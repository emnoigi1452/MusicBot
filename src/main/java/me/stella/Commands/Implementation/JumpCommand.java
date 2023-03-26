package me.stella.Commands.Implementation;

import java.util.LinkedList;
import java.util.List;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;

import me.stella.Bot.BotModules;
import me.stella.Commands.BaseSlash;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;

public class JumpCommand extends BaseSlash {
	
	public JumpCommand() {
		super("jump", "Skips to a specific song in the queue.", Permission.USE_APPLICATION_COMMANDS,
				true, JumpCommand.initParameters());
	}
	
	public static List<CommandOption> initParameters() {
		List<CommandOption> options = new LinkedList<CommandOption>();
		options.add(new CommandOption(OptionType.STRING, "position", "The position of the song in the queue", true));
		return options;
	}
	
	@Override
	public void execute(SlashCommandInteractionEvent e) {
		Member member = e.getMember(); 
		if(_checkIntrovert(member)) {
			try {
				if(BotModules.getAudioPanel() != null) {
					if(_checkAudioChannel(member) && _checkVoiceHook(member, BotModules.getAudioPanel().getPlayerChannel())) {
						final AudioPlayer player = BotModules.getAudioPanel().getPlayer();
						try {
							int pos = Integer.valueOf(e.getOption("position").getAsString());
							new Thread(() -> {
								boolean b = BotModules.getAudioPanel().getQueue()._jump(pos);
								if(!b) {
									e.reply(BotModules.getLocale().getMessage("jump_fail")).queue();
									return;
								}
								if(player.getPlayingTrack() != null)
									player.stopTrack();
								BotModules.getAudioPanel().perform();
								e.reply(BotModules.getLocale().getMessage("jumped").replace("{pos}", String.valueOf(pos))).queue();
							}).start();
						} catch(NumberFormatException err) {
							e.reply(BotModules.getLocale().getMessage("not_integer")).queue();
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
