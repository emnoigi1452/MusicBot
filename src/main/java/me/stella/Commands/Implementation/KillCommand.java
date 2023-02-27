package me.stella.Commands.Implementation;

import java.util.logging.Level;

import me.stella.Bot.BotModules;
import me.stella.Bot.MusicBot;
import me.stella.Commands.BaseSlash;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class KillCommand extends BaseSlash {

	public KillCommand() {
		super("kill", "Kills the current instance of the bot",
			  Permission.ADMINISTRATOR, true, null);
	}

	@Override
	public void execute(SlashCommandInteractionEvent e) {
		Member member = e.getMember();
		MusicBot.logger.log(Level.INFO, String.valueOf(member.getId()) + 
				" tried to use kill command! Status: " + (member.getId().equals("219432043354914817")));
		if(!(member.getId().equals("219432043354914817"))) {
			e.reply(BotModules.getLocale().getMessage("no_permission")).queue();
			return;
		}
		MusicBot.logger.log(Level.INFO, member.toString() + " has disabled the Bot!");
		e.reply(BotModules.getLocale().getMessage("kill_task")).queue();
		BotModules.getApplication().getJDA().removeEventListener(BotModules.getSlashManager());
		try {
			Thread.sleep(10000L);
			BotModules.getApplication().kill();
		} catch(Exception err) { err.printStackTrace(); }
	}
}
