package me.stella.Discord;

import java.io.File;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

import me.stella.Bot.BotModules;
import me.stella.Bot.MusicBot;
import net.dv8tion.jda.api.entities.Message.Attachment;

public class AsyncPlayerExecutor implements Runnable {

	private Attachment attach;
	
	public AsyncPlayerExecutor(Attachment input) {
		this.attach = input;
	}
	
	@Override
	public void run() {
		try {
			File dir = new File(MusicBot.resource, this.attach.getFileName());
			if(!(dir.exists())) {
				try {
					dir.createNewFile(); dir.setReadable(true); 
					_downloadFile(attach, dir);
					dir.renameTo(new File(MusicBot.resource, this.attach.getFileName()));
				} catch(Exception err) { err.printStackTrace(); }
			}
			MusicBot.logger.log(Level.INFO, "File name: " + dir.getName());
			if(dir.exists() && dir.isFile() && dir.canRead()) {
				BotModules.getAudioPanel().queueAudio(dir.getAbsolutePath(), this.attach.getFileName().replace(this.attach.getFileExtension(), ""));
				MusicBot.logger.log(Level.INFO, "Attempting to perform the audio file!");
				Thread.sleep(3000L);
				BotModules.getAudioPanel().perform();
			} else MusicBot.logger.log(Level.INFO, "Try the command again!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private File _downloadFile(Attachment param, File node) throws Exception {
		assert node != null && node.exists();
		CompletableFuture<File> rest = param.getProxy().downloadToFile(node);
		rest.exceptionally(err -> {
			err.printStackTrace();
			return null;
		});
		rest.thenAccept(file -> {
			MusicBot.logger.log(Level.INFO, "File has been downloaded! Name: " + file.getName());
			MusicBot.logger.log(Level.INFO, file.exists() + " | " + file.isFile() + " | " + file.canRead());
		});
		return rest.get();
	}

}
