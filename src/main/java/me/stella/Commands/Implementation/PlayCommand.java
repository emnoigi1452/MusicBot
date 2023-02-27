package me.stella.Commands.Implementation;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import me.stella.Bot.BotModules;
import me.stella.Bot.MusicBot;
import me.stella.Commands.BaseSlash;
import me.stella.Discord.AsyncPlayerExecutor;
import me.stella.Radio.AudioListener;
import me.stella.Radio.AudioPanel;
import me.stella.Radio.AudioPlayerSendHandler;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.managers.AudioManager;

public class PlayCommand extends BaseSlash {
	
	public static List<String> extensions = Arrays.asList(new String[] { "MP3", "WAV", "FLAC", "OGG", "M4A" });

	public PlayCommand() {
		super("play", "Plays an audio file", Permission.USE_APPLICATION_COMMANDS, true, PlayCommand.initParameters());
	}
	
	public static List<CommandOption> initParameters() {
		List<CommandOption> options = new LinkedList<CommandOption>();
		options.add(new CommandOption(OptionType.ATTACHMENT, "audio", "The specified audio file", true));
		return options;
	}

	@Override
	public void execute(SlashCommandInteractionEvent e) {
		MusicBot.logger.log(Level.INFO, "Play command called!");
		Member member = e.getMember();
		if(_checkIntrovert(member)) {
			Attachment audioFile = e.getOption("audio").getAsAttachment();
			if(!(_checkExtensions(audioFile.getFileExtension()))) {
				e.reply(BotModules.getLocale().getMessage("invalid_audio_file")).setEphemeral(true).queue();
				return;
			}
			try {
				MusicBot.logger.log(Level.INFO, "Preparing the audio panel...");
				if(BotModules.getAudioPanel() == null) {
					MusicBot.buildNewAudioManager();
					BotModules.setAudioPanel(new AudioPanel(
							member, e.getChannel().asTextChannel(), member.getVoiceState().getChannel().asVoiceChannel()));
					Guild g = e.getGuild();
					VoiceChannel vc = member.getVoiceState().getChannel().asVoiceChannel();
					AudioManager guildAudioManager = g.getAudioManager();
					MusicBot.logger.log(Level.INFO, "Connecting to voice channel...");
					guildAudioManager.setSendingHandler(new AudioPlayerSendHandler(BotModules.getAudioPanel().getPlayer()));
					guildAudioManager.setConnectionListener(new AudioListener());
					guildAudioManager.openAudioConnection(vc);
				}
				if(!(_checkAudioChannel(member)) && !(_checkVoiceHook(member, BotModules.getAudioPanel().getPlayerChannel()))) {
					e.reply(BotModules.getLocale().getMessage("not_joined")).submit();
					return;
				}
				e.deferReply().delay(3, TimeUnit.SECONDS).queue();
				e.getHook().sendMessage(BotModules.getLocale().getMessage("loading")).queue();
				Thread.sleep(2000L);
				MusicBot.logger.log(Level.INFO, "Downloading file to vault...");
				new Thread(new AsyncPlayerExecutor(audioFile)).start();
			} catch(Throwable t) { t.printStackTrace(); e.reply(BotModules.getLocale().getMessage("error")).queue(); }
		} else e.reply(BotModules.getLocale().getMessage("no_permission")).queue();
	}
	
	private boolean _checkExtensions(String extension) {
		extension = extension.toUpperCase();
		return PlayCommand.extensions.contains(extension);
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
