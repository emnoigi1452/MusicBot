package me.stella.Commands.Implementation;

import java.util.ArrayList;
import java.util.List;

import me.stella.Bot.BotModules;
import me.stella.Commands.BaseSlash;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;

public class PauseCommand extends BaseSlash {

	public PauseCommand() {
		super("pause", "Set the pause option for the player", Permission.USE_APPLICATION_COMMANDS, true, PauseCommand.initParameters());
		// TODO Auto-generated constructor stub
	}
	
	public static List<CommandOption> initParameters() {
		List<CommandOption> option = new ArrayList<CommandOption>();
		option.add(new CommandOption(OptionType.STRING, "paused", "Whether to pause the player.", true));
		return option;
	}
	
	@Override
	public void execute(SlashCommandInteractionEvent e) {
		Member member = e.getMember();
		if(_checkIntrovert(member)) {
			try {
				if(BotModules.getAudioPanel() != null) {
					if(_checkAudioChannel(member) && _checkVoiceHook(member, BotModules.getAudioPanel().getPlayerChannel())) {
						boolean value = Boolean.valueOf(e.getOption("paused").getAsString());
						boolean cur = BotModules.getAudioPanel().getQueue()._paused();
						if(value == cur)
							e.reply(BotModules.getLocale().getMessage("already").replace("{mode}", parseMode(cur))).queue();
						else {
							if(value) {
								BotModules.getAudioPanel().getQueue()._pause();
								e.reply(BotModules.getLocale().getMessage("paused")).queue();
							} else {
								BotModules.getAudioPanel().getQueue()._unpause();
								e.reply(BotModules.getLocale().getMessage("resumed")).queue();
							}
						}
					} else e.reply(BotModules.getLocale().getMessage("not_joined"));
				} else e.reply(BotModules.getLocale().getMessage("no_player")).queue();
			} catch(Throwable t) { t.printStackTrace(); e.reply(BotModules.getLocale().getMessage("error")).queue(); }
		} else e.reply(BotModules.getLocale().getMessage("no_permission"));
	}
	
	private String parseMode(boolean param) {
		return param ? "PAUSED" : "PLAYING";
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
