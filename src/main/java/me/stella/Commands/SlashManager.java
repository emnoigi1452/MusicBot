package me.stella.Commands;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import me.stella.Bot.BotModules;
import me.stella.Commands.Implementation.LoopCommand;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public class SlashManager extends ListenerAdapter {
	
	private Map<String, BaseSlash> commands;
	
	public SlashManager() {
		this.commands = Collections.synchronizedMap(new HashMap<String, BaseSlash>());	
	}
	
	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent e) {
		if(containsCommand(e.getName()))
			executeCommand(e);
	}
	
	@Override
	public void onCommandAutoCompleteInteraction(final CommandAutoCompleteInteractionEvent event) {
		if(event.getName().equals("loop") && event.getFocusedOption().getName().equals("mode")) {
			List<Command.Choice> options = Stream.of(LoopCommand.autoComplete)
					.filter(word -> word.startsWith(event.getFocusedOption().getValue()))
					.map(word -> new Command.Choice(word, word))
					.collect(Collectors.toList());
			event.replyChoices(options).queue();
		}
	}
	
	@Override
	public void onGuildReady(GuildReadyEvent e) {
		Guild guild = e.getGuild();
		Set<CommandData> commands = new HashSet<CommandData>();
		this.commands.values().stream().forEach(base -> {
			commands.add(BaseSlash.buildSlashCommand(base));
		});
		try {
			guild.updateCommands().addCommands(commands).queue();
			Thread.sleep(3000L);
		} catch(Exception err) { err.printStackTrace(); }
	}
	
	@Override
	public void onGuildJoin(GuildJoinEvent e) {
		Guild guild = e.getGuild();
		BotModules.getSettings().appendGuild(guild.getId());
		Set<CommandData> commands = new HashSet<CommandData>();
		this.commands.values().stream().forEach(base -> {
			commands.add(BaseSlash.buildSlashCommand(base));
		});
		try {
			guild.updateCommands().addCommands(commands).queue();
			Thread.sleep(3000L);
		} catch(Exception err) { err.printStackTrace(); }
	}
	
	public synchronized void addCommand(BaseSlash command) {
		this.commands.put(command.getName(), command);
	}
	
	public synchronized void clearCommands() {
		this.commands.clear();
	}
	
	public boolean containsCommand(String name) {
		return commands.containsKey(name);
	}
	
	public synchronized void executeCommand(SlashCommandInteractionEvent e) {
		this.commands.get(e.getName()).execute(e);
	}

}
