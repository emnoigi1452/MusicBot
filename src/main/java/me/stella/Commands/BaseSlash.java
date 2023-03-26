package me.stella.Commands;

import java.util.List;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public abstract class BaseSlash {
	
	private String name;
	private String description;
	private Permission permission;
	private boolean guildOnly;
	private List<CommandOption> options;
	
	public BaseSlash(String name, String description, Permission permission, boolean guildOnly, List<CommandOption> options) {
		this.name = name;
		this.description = description;
		this.permission = permission;
		this.guildOnly = guildOnly;
		this.options = options;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getDescription() {
		return this.description;
	}
	
	public Permission getDefaultPermission() {
		return this.permission;
	}
	
	public boolean isGuildOnly() {
		return this.guildOnly;
	}
	
	public List<CommandOption> getOptions() {
		return this.options;
	}
	
	public abstract void execute(SlashCommandInteractionEvent e);
	
	public static SlashCommandData buildSlashCommand(BaseSlash info) {
		SlashCommandData init = Commands.slash(info.getName(), info.getDescription());
		init.setDefaultPermissions(DefaultMemberPermissions.enabledFor(info.getDefaultPermission()));
		init.setGuildOnly(info.isGuildOnly());
		if(info.getOptions() != null) {
			info.getOptions().stream().forEach(option -> {
				init.addOption(option.getType(), option.getName(), option.getDescription(), option.isRequired(), option.isAutoCompleteSupported());
			});
		}
		return init;
	}
	
	public static class CommandOption {
		private OptionType type;
		private String name;
		private String description;
		private boolean required;
		private boolean autoComplete;
		
		public CommandOption(OptionType type, String name, String desc, boolean required) {
			this(type, name, desc, required, false);
		}
		
		public CommandOption(OptionType type, String name, String desc, boolean required, boolean autoComplete) {
			this.type = type;
			this.name = name;
			this.description = desc;
			this.required = required;
			this.autoComplete = autoComplete;
		}

		public OptionType getType() {
			return this.type;
		}
		
		public String getName() {
			return this.name;
		}
		
		public String getDescription() {
			return this.description;
		}
		
		public boolean isRequired() {
			return this.required;
		}
		
		public boolean isAutoCompleteSupported() {
			return this.autoComplete;
		}
	}

}
