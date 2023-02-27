package me.stella.Commands.Implementation;

import java.util.List;

import me.stella.Bot.BotModules;
import me.stella.Commands.BaseSlash;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class QueueCommand extends BaseSlash {

	public QueueCommand() {
		super("queue", "Display the current queue of the bot.", Permission.USE_APPLICATION_COMMANDS, true, null);
	}

	@Override
	public void execute(SlashCommandInteractionEvent e) {
		Member member = e.getMember();
		if(_checkIntrovert(member)) {
			try {
				if(BotModules.getAudioPanel() != null) {
					if(_checkAudioChannel(member) && _checkVoiceHook(member, BotModules.getAudioPanel().getPlayerChannel())) {
						List<String> queue = BotModules.getAudioPanel().getQueue().pullTracks();
						String reply = "```{data}```"; StringBuilder listBuilder = new StringBuilder();
						for(int j = 0; j < queue.size(); j++) {
							if(j != 0)
								listBuilder.append("\n");
							listBuilder.append(String.valueOf(j+1) + ". " + queue.get(j));
						}
						e.reply(reply.replace("{data}", listBuilder.toString())).setEphemeral(true).queue();
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
