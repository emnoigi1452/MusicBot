package me.stella.Commands.Implementation;

import java.util.logging.Level;

import me.stella.Bot.BotModules;
import me.stella.Bot.MusicBot;
import me.stella.Commands.BaseSlash;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class StopCommand extends BaseSlash {

	public StopCommand() {
		super("stop", "Stops the audio player, effectively ending the audio manager.", 
				Permission.USE_APPLICATION_COMMANDS, true, null);
	}

	@Override
	public void execute(SlashCommandInteractionEvent e) {
		Member member = e.getMember(); 
		if(_checkIntrovert(member)) {
			try {
				if(BotModules.getAudioPanel() != null) {
					Member host = BotModules.getAudioPanel().getHost();
					if(_checkAudioChannel(member) && _checkVoiceHook(member, BotModules.getAudioPanel().getPlayerChannel())) {
						if(!(member.equals(host))) {
							MusicBot.logger.log(Level.INFO, "Some dude tried to stop the BOT. Host: " + String.valueOf(host));
							e.reply(BotModules.getLocale().getMessage("only_owner").replace("{mention}", host.getAsMention())).setEphemeral(true).queue();
							return;
						}
						host.getGuild().getAudioManager().closeAudioConnection();
						BotModules.getAudioPanel()._clean(); Thread.sleep(1000L);
						BotModules.setAudioPanel(null);
						e.reply(BotModules.getLocale().getMessage("player_stop")).queue();
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
