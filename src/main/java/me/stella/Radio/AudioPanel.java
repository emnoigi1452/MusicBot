package me.stella.Radio;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import me.stella.Bot.BotModules;
import me.stella.Bot.MusicBot;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;

public class AudioPanel extends AudioEventAdapter {
	
	public static Set<String> existingPayload;
	
	private final AudioPlayerManager manager;
	private final AudioPlayer player;
	private final MessageChannel channel;
	private final VoiceChannel vc;
	private final Member host;
	private final SimpleQueueArray queue;
	
	public AudioPanel(Member member, MessageChannel channel, VoiceChannel vc) {
		this.manager = BotModules.getAudioPlayerManager();
		this.player = this.manager.createPlayer();
		this.host = member;
		this.channel = channel;
		this.vc = vc;
		this.player.addListener(this);
		this.player.setVolume(100);
		this.queue = new SimpleQueueArray(this.player);
		existingPayload = Collections.synchronizedSet(new HashSet<String>());
	}
	
	public synchronized void _clean() throws Exception {
		if(this.player.getPlayingTrack() != null) {
			this.player.stopTrack();
		}
		Thread.sleep(250L);
		this.player.stopTrack();
		this.player.destroy();
		existingPayload = Collections.synchronizedSet(new HashSet<String>());
		for(File audioFile: MusicBot.resource.listFiles()) {
			if(audioFile.isFile())
				audioFile.delete();
		}
	}
	
	public synchronized void perform() {
		if(this.player.getPlayingTrack() != null)
			return;
		AudioTrack track = getQueue()._next();
		MusicBot.logger.log(Level.INFO, "Is null: " + String.valueOf(track == null));
		if(track == null) {
			MusicBot.logger.log(Level.INFO, "Index: " + getQueue()._now() + " - Size: " + getQueue()._list());
			return;
		}
		MusicBot.logger.log(Level.INFO, "Player started! Audio length: " + track.getDuration());
		this.player.playTrack(track);
	}
	
	private void logTrackInfo(AudioTrack track) {
		String original = BotModules.getLocale().getMessage("start");
		assert (track != null);
		String trackName = AudioPanel.parseAudioName(track);
		assert (trackName != null && original != null);
		getLogging().sendMessage(original.replace("{name}", trackName)).queue();
	}
	
	@Override
	public void onPlayerPause(AudioPlayer player) {
		MusicBot.logger.log(Level.INFO, "Audio player has been paused!");
	}
	
	@Override
	public void onPlayerResume(AudioPlayer player) {
		MusicBot.logger.log(Level.INFO, "Audio player has resumed its course!");
	}
	
	@Override
	public void onTrackStart(AudioPlayer player, AudioTrack track) {
		try {
			logTrackInfo(track);
		} catch(Throwable t) { t.printStackTrace(); }
	}
	
	@Override
	public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason reason) {
		assert (player != null);
		try {
			if(reason == AudioTrackEndReason.LOAD_FAILED)
				getLogging().sendMessage(BotModules.getLocale().getMessage("error_audio")).submit();
			this.player.stopTrack();
			if(reason.mayStartNext)
				this.player.playTrack(this.queue._next());
		} catch(Throwable t) { t.printStackTrace(); }
	}
	
	public Member getHost() {
		return this.host;
	}
	
	public VoiceChannel getPlayerChannel() {
		return this.vc;
	}
	
	public MessageChannel getLogging() {
		return this.channel;
	}
	
	public SimpleQueueArray getQueue() {
		return this.queue;
	}
	
	public AudioPlayerManager getManager() {
		return this.manager;
	}
	
	public AudioPlayer getPlayer() {
		return this.player;
	}
	
	private String buildSongName(AudioTrack track, String backup) {
		assert (track != null);
		AudioTrackInfo meta = track.getInfo(); String parsed = meta.author + " - " + meta.title;
		return parsed.contains("Unknown artist") || parsed.contains("Unknown title") ? backup : parsed;
	}
	
	public synchronized void queueAudio(final String path, String fileName) {
		final SimpleQueueArray vcQueue = getQueue();
		final String formatDiscordName = fileName.replace("_", " ");
		final MessageChannel reference = getLogging();
		this.manager.loadItem(path, new AudioLoadResultHandler() {
			@Override
			public void trackLoaded(AudioTrack track) {
				AudioInfo build = new AudioInfo(buildSongName(track, formatDiscordName), track.getDuration());
				track.setUserData(build);
				if(!(existingPayload.contains(build.getAudioData()))) {
					vcQueue._add(track);
					existingPayload.add(build.getAudioData());
				}
				reference.sendMessage(BotModules.getLocale().getMessage("loaded_track").replace("{name}", 
						AudioPanel.parseAudioName(track))).submit();
			}
			@Override
			public void playlistLoaded(AudioPlaylist playlist) {
				playlist.getTracks().stream().forEach(track -> {
					AudioInfo build = new AudioInfo(buildSongName(track, formatDiscordName), track.getDuration());
					track.setUserData(build);
					if(!(existingPayload.contains(build.getAudioData()))) {
						vcQueue._add(track);
						existingPayload.add(build.getAudioData());
					}
					reference.sendMessage(BotModules.getLocale().getMessage("loaded_track").replace("{name}", 
							AudioPanel.parseAudioName(track))).submit();
				});
			}
			@Override
			public void noMatches() {
				reference.sendMessage(BotModules.getLocale().getMessage("not_found")).submit();
			}
			@Override
			public void loadFailed(FriendlyException exception) {
				exception.printStackTrace();
				reference.sendMessage(BotModules.getLocale().getMessage("error_audio")).submit();
			}
		});
	}
	
	public static String parseAudioName(AudioTrack track) {
		assert (track != null);
		AudioInfo internal = ((AudioInfo)track.getUserData());
		return internal.getAudioName();
	}
}
