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

public class LoopCommand extends BaseSlash {
	
	public static final String[] autoComplete = {"ONE", "ALL", "OFF"};

	public LoopCommand() {
		super("loop", "Set the loop mode for the audio player", Permission.USE_APPLICATION_COMMANDS, true, LoopCommand.initParameters());
	}
	
	public static List<CommandOption> initParameters() {
		List<CommandOption> option = new ArrayList<CommandOption>();
		option.add(new CommandOption(OptionType.STRING, "mode", "The loop mode for the audio player", false));
		return option;
	}

	@Override
	public void execute(SlashCommandInteractionEvent e) {
		Member member = e.getMember();
		if(_checkIntrovert(member)) {
			try {
				if(BotModules.getAudioPanel() != null) {
					if(_checkAudioChannel(member) && _checkVoiceHook(member, BotModules.getAudioPanel().getPlayerChannel())) {
						if(e.getOptions().size() == 0) {
							e.reply(LoopCommand.parseLoopMode(BotModules.getAudioPanel().getQueue()._loop())).queue();
							return;
						} else {
							byte extract = LoopMode.eval(e.getOption("mode").getAsString().toUpperCase());
							if(extract == -1) {
								e.reply(BotModules.getLocale().getMessage("invalid_mode")).queue();
								return;
							}
							byte ret = BotModules.getAudioPanel().getQueue()._loopMode(extract);
							BotModules.getAudioPanel().perform();
							e.reply(LoopCommand.parseLoopMode(ret)).queue();
						}
					} else e.reply(BotModules.getLocale().getMessage("not_joined"));
				} else e.reply(BotModules.getLocale().getMessage("no_player")).queue();
			} catch(Throwable t) { t.printStackTrace(); e.reply(BotModules.getLocale().getMessage("error")).queue(); }
		} else e.reply(BotModules.getLocale().getMessage("no_permission"));
	}
	
	public static String parseLoopMode(byte param) {
		LoopMode mode = LoopMode.parse(param);
		return BotModules.getLocale().getMessage("loop_mode").replace("{mode}", String.valueOf(mode));
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
	
	public enum LoopMode {
		ONE,
		ALL,
		OFF;
		static byte eval(String a) {
			LoopMode b = null;
			try {
				b = LoopMode.valueOf(a);
			} catch(Throwable t) { t.printStackTrace(); }
			if(b == LoopMode.OFF)
				return 1;
			else if(b == LoopMode.ONE)
				return 2;
			else if(b == LoopMode.ALL)
				return 3;
			else return -1;
		}
		static LoopMode parse(byte o) {
			switch(o) {
				case 2: return LoopMode.ONE;
				case 3: return LoopMode.ALL;
				default: return LoopMode.OFF;
			}
		}
	}

}
