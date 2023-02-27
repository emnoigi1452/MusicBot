package me.stella.Radio;

import me.stella.Bot.BotModules;
import net.dv8tion.jda.api.audio.hooks.ConnectionListener;
import net.dv8tion.jda.api.audio.hooks.ConnectionStatus;
import net.dv8tion.jda.api.entities.User;

public class AudioListener implements ConnectionListener {

	@Override
	public void onPing(final long ping) {
		//
	}

	@Override
	public void onStatusChange(final ConnectionStatus status) {
		final Runnable asyncHandle = (() -> {
			if(status == ConnectionStatus.DISCONNECTED_KICKED_FROM_CHANNEL || status == ConnectionStatus.DISCONNECTED_CHANNEL_DELETED) {
				if(BotModules.getAudioPanel() != null) {
					BotModules.getAudioPanel().getPlayerChannel().getGuild().getAudioManager().closeAudioConnection();
					BotModules.getAudioPanel().getLogging().sendMessage(BotModules.getLocale().getMessage("disconnect")).queue();
					try {
						BotModules.getAudioPanel()._clean();
					} catch (Exception e) {
						e.printStackTrace();
					} BotModules.setAudioPanel(null);
				}
			}
		}); new Thread(asyncHandle).start();
	}

	@Override
	public void onUserSpeaking(User user, boolean speaking) {
		// 
	}

}
