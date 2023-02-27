package me.stella.Commands.Implementation;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import me.stella.Bot.BotModules;
import me.stella.Commands.BaseSlash;
import me.stella.Radio.AudioInfo;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class SkipCommand extends BaseSlash {

	public SkipCommand() {
		super("skip", "Skips over the currently playing song.", Permission.USE_APPLICATION_COMMANDS, true, null);
	}

	@Override
	public void execute(SlashCommandInteractionEvent e) {
		Member member = e.getMember(); 
		if(_checkIntrovert(member)) {
			try {
				if(BotModules.getAudioPanel() != null) {
					if(_checkAudioChannel(member) && _checkVoiceHook(member, BotModules.getAudioPanel().getPlayerChannel())) {
						AudioPlayer player = BotModules.getAudioPanel().getPlayer();
						AudioTrack track = player.getPlayingTrack();
						if(track == null)
							e.reply(BotModules.getLocale().getMessage("no_playing_song")).queue();
						else {
							player.stopTrack();
							BotModules.getAudioPanel().perform();
							e.reply(BotModules.getLocale().getMessage("skipped").replace("{name}", 
									((AudioInfo)track.getUserData()).getAudioName())).queue();
						}
					} else e.reply(BotModules.getLocale().getMessage("not_joined"));
				} else e.reply(BotModules.getLocale().getMessage("no_player")).queue();
			} catch(Throwable t) { t.printStackTrace(); e.reply(BotModules.getLocale().getMessage("error")).queue(); }
		} else e.reply(BotModules.getLocale().getMessage("no_permission"));
	}
	
	public boolean _checkIntrovert(Member member) {
		Role introvert = member.getGuild().getRoleById(
				BotModules.getSettings().getPrivateRoleID(member.getGuild().getId()));
		return member.getRoles().contains(introvert);
	}
	
	private boolean _checkAudioChannel(Member member) {
		return member.getVoiceState().inAudioChannel();
	}
	
	private boolean _checkVoiceHook(Member member, VoiceChannel hook) {
		return member.getVoiceState().getChannel().asVoiceChannel().equals(hook);
	}
	

}
