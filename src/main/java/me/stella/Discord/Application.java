package me.stella.Discord;

import java.io.File;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import me.stella.Bot.BotModules;
import me.stella.Bot.MusicBot;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Application {
	
	private JDA app;
	
	public Application(String token) throws Exception {
		JDABuilder appBuilder = JDABuilder.create(token, MusicBot.prepareDefaultIntents());
		appBuilder.setActivity(BotModules.getSettings().buildActivity());
		appBuilder.addEventListeners(BotModules.getSlashManager());
		this.app = appBuilder.build(); this.app.awaitReady();
		MusicBot.logger.log(Level.INFO, "JDA successfully loaded! Guilds = " + this.app.getGuilds().size());
		Object[] params = BotModules.getSettings().getBootParameters();
		if(Boolean.parseBoolean(String.valueOf(params[0]))) {
			Guild guild = this.app.getGuildById(String.valueOf(params[1]));
			MessageChannel channel = guild.getChannelById(MessageChannel.class, String.valueOf(params[2]));
			channel.sendMessage(BotModules.getLocale().getMessage("boot")).submit();
		}
	}
	
	public void addListener(ListenerAdapter adapter) {
		this.app.addEventListener(adapter);
	}
	
	public JDA getJDA() {
		return this.app;
	}
	
	public void kill() {
		try {
			this.app.retrieveCommands().submit().get().stream().forEach(cmd -> {
				try {
					cmd.delete().delay(3, TimeUnit.SECONDS).queue();
					Thread.sleep(2000L);
					MusicBot.logger.log(Level.INFO, "Removed " + cmd + " from hooked commands!");
				} catch(Exception e) { e.printStackTrace(); }
			});
			this.app.removeEventListener(BotModules.getSlashManager());
			MusicBot.logger.log(Level.INFO, "Cleaning up the audio vault...");
			for(File audioFile: MusicBot.resource.listFiles()) {
				if(audioFile.isFile())
					audioFile.delete();
			}
			BotModules.getAudioPlayerManager().shutdown();
			MusicBot.logger.log(Level.INFO, "Saving the configuration...");
			new Thread(() -> {
				try {
					BotModules.getSettings().rewrite();
				} catch(Exception e) { e.printStackTrace(); }
			}).start();
			Thread.sleep(2000L);
		} catch(Exception e) { e.printStackTrace(); }
		this.app.shutdown();
	}

}
