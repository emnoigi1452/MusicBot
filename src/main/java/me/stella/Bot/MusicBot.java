package me.stella.Bot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;

import me.stella.Commands.SlashManager;
import me.stella.Commands.Implementation.JumpCommand;
import me.stella.Commands.Implementation.KillCommand;
import me.stella.Commands.Implementation.LoopCommand;
import me.stella.Commands.Implementation.PauseCommand;
import me.stella.Commands.Implementation.PlayCommand;
import me.stella.Commands.Implementation.QueueCommand;
import me.stella.Commands.Implementation.RemoveCommand;
import me.stella.Commands.Implementation.SkipCommand;
import me.stella.Commands.Implementation.StopCommand;
import me.stella.Discord.Application;
import me.stella.Settings.JSONInternal;
import me.stella.Settings.Locale;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class MusicBot {
	
	public static Logger logger = Logger.getLogger(MusicBot.class.getName());
	public static File resource;
	public static File lib;
	public static MusicBot main;
	
	public MusicBot() throws Exception {
		BotModules.setSettings(new JSONInternal(MusicBot.extract("settings.json")));
		BotModules.setLocale(new Locale(MusicBot.extract(BotModules.getSettings().getLocaleFile())));
		MusicBot.logger.log(Level.INFO, "Finished setting up internal configurations! Attempting to load the JDA...");
		BotModules.setSlashManager(new SlashManager()); setupCommands();
		MusicBot.logger.log(Level.INFO, "Setting up commands... Allocating 3 seconds");
		Thread.sleep(3000L);
		BotModules.setApplication(new Application(BotModules.getSettings().getToken()));
		MusicBot.logger.log(Level.INFO, "Added new slash command listener!");
		MusicBot.logger.log(Level.INFO, "Added new music manager! Ready to play music!");
	}
	
	public static void buildNewAudioManager() {
		AudioPlayerManager newManager = new DefaultAudioPlayerManager();
		BotModules.setAudioPlayerManager(newManager);
		AudioSourceManagers.registerLocalSource(newManager);
	}
	
	private void setupCommands() {
		SlashManager manager = BotModules.getSlashManager();
		manager.addCommand(new KillCommand());
		manager.addCommand(new PlayCommand());
		manager.addCommand(new LoopCommand());
		manager.addCommand(new PauseCommand());
		manager.addCommand(new StopCommand());
		manager.addCommand(new QueueCommand());
		manager.addCommand(new SkipCommand());
		manager.addCommand(new RemoveCommand());
		manager.addCommand(new JumpCommand());
	}
	
	public static void main(String[] args) {
		try {
			logger = new BotLogger("MusicBotLogger");
			lib = new File(System.getProperty("user.dir"));
			resource = new File(lib, "AudioVault");
			if(!(resource.exists()))
				resource.mkdir();
			main = new MusicBot();
		} catch(Exception e) { e.printStackTrace(); }
	}
	
	public static List<GatewayIntent> prepareDefaultIntents() {
		List<GatewayIntent> intents = new LinkedList<GatewayIntent>();
		intents.add(GatewayIntent.GUILD_MEMBERS);
		intents.add(GatewayIntent.GUILD_PRESENCES);
		intents.add(GatewayIntent.MESSAGE_CONTENT);
		intents.add(GatewayIntent.GUILD_VOICE_STATES);
		intents.add(GatewayIntent.GUILD_EMOJIS_AND_STICKERS);
		intents.add(GatewayIntent.GUILD_MESSAGES);
		intents.add(GatewayIntent.SCHEDULED_EVENTS);
		return intents;
	}
	
	public static File extract(String param1) {
		assert (param1 != null);
		try {
			ClassLoader loader = MusicBot.class.getClassLoader();
			InputStream stream = loader.getResourceAsStream(param1);
			BufferedReader utfReader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
			LinkedList<String> content = new LinkedList<String>(); String s;
			while((s = utfReader.readLine()) != null)
				content.add(s);
			utfReader.close();
			File extract = new File(lib, param1);
			if(!(extract.exists()))
				extract.createNewFile();
			else
				return extract;
			final OutputStreamWriter output = new OutputStreamWriter(new FileOutputStream(extract), StandardCharsets.UTF_8);
			content.stream().forEach(line -> {
				try {
					output.write(line.concat("\n"));
				} catch(Exception e) { e.printStackTrace(); }
			}); output.flush(); output.close();
			MusicBot.logger.log(Level.INFO, "Generated file with name \'[name]\'".replace("[name]", param1));
			return extract;
		} catch(Exception e) { e.printStackTrace(); }
		return null;
		
	}

}
