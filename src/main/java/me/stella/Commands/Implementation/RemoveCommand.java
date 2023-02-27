package me.stella.Commands.Implementation;

import java.util.ArrayList;
import java.util.List;

import me.stella.Bot.BotModules;
import me.stella.Commands.BaseSlash;
import me.stella.Radio.AudioInfo;
import me.stella.Radio.SimpleQueueArray.TrackResponse;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;

public class RemoveCommand extends BaseSlash {

	public RemoveCommand() {
		super("remove", "Remove a song from the queue.", Permission.USE_APPLICATION_COMMANDS, true, RemoveCommand.initParameters());
	}
	
	public static List<CommandOption> initParameters() {
		List<CommandOption> options = new ArrayList<CommandOption>();
		options.add(new CommandOption(OptionType.STRING, "index", "The index of the song in the queue.", true));
		return options;
	}

	@Override
	public void execute(SlashCommandInteractionEvent e) {
		Member member = e.getMember(); 
		if(_checkIntrovert(member)) {
			try {
				if(BotModules.getAudioPanel() != null) {
					if(_checkAudioChannel(member) && _checkVoiceHook(member, BotModules.getAudioPanel().getPlayerChannel())) {
						int input = Integer.valueOf(e.getOption("index").getAsString()).intValue();
						TrackResponse response = BotModules.getAudioPanel().getQueue()._remove((input-1));
						if(response.isSuccess()) {
							e.reply(BotModules.getLocale().getMessage("removed").replace("{name}", 
									((AudioInfo)response.getTrack().getUserData()).getAudioName())).queue();
							BotModules.getAudioPanel().perform();
						}
						else e.reply(BotModules.getLocale().getMessage("remove_fail")).queue();
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
