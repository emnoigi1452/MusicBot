package me.stella.Bot;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;

import me.stella.Commands.SlashManager;
import me.stella.Discord.Application;
import me.stella.Radio.AudioPanel;
import me.stella.Settings.JSONInternal;
import me.stella.Settings.Locale;

public class BotModules {
	
	private static JSONInternal settings;
	private static Locale locale;
	private static Application application;
	private static SlashManager manager;
	private static AudioPanel panel;
	private static AudioPlayerManager audioManager;

	public static void setSettings(JSONInternal param) {
		settings = param;
	}
	
	public static JSONInternal getSettings() {
		return settings;
	}
	
	public static void setLocale(Locale param) {
		locale = param;
	}
	
	public static Locale getLocale() {
		return locale;
	}
	
	public static void setApplication(Application param) {
		application = param;
	}
	
	public static Application getApplication() {
		return application;
	}

	public static void setSlashManager(SlashManager slashManager) {
		manager = slashManager;
	}
	
	public static SlashManager getSlashManager() {
		return manager;
	}
	
	public static void setAudioPanel(AudioPanel panelInput) {
		if(panelInput == null)
			panel.getManager().shutdown();
		panel = panelInput;
	}
	
	public static AudioPanel getAudioPanel() {
		return panel;
	}
	
	public static AudioPlayerManager getAudioPlayerManager() {
		return audioManager;
	}
	
	public static void setAudioPlayerManager(AudioPlayerManager manager) {
		audioManager = manager;
	}
}
