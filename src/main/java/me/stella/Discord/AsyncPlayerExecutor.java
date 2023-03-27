package me.stella.Discord;

import java.io.File;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

import me.stella.Bot.BotModules;
import me.stella.Bot.MusicBot;
import net.dv8tion.jda.api.entities.Message.Attachment;

public class AsyncPlayerExecutor implements Runnable {

	public static boolean PATCH_DONE = false;
	
	private Attachment attach;
	private double speed;
	private double pitch;
	private String mod;
	
	public AsyncPlayerExecutor(Attachment input) {
		this(input, OsuAudioMod._NoMod_());
	}
	
	public AsyncPlayerExecutor(Attachment input, OsuAudioMod mod) {
		this.attach = input;
		this.pitch = mod.getAudioPitch();
		this.speed = mod.getAudioSpeed();
		this.mod = mod.getMod();
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
				String fileName = this.attach.getFileName().replace("." + this.attach.getFileExtension(), "");
				if(this.mod.equals("NM")) {
					MusicBot.logger.log(Level.INFO, "Attempting to perform the audio file!");
					BotModules.getAudioPanel().queueAudio(dir.getAbsolutePath(), fileName);
					Thread.sleep(1500L);
					BotModules.getAudioPanel().perform();
				} else {
					MusicBot.logger.log(Level.INFO, "Applying audio patches on the song... This might take a while...");
					buildFFMPEGProcess(dir.getAbsoluteFile().getAbsolutePath());
					MusicBot.logger.log(Level.INFO, dir.getAbsolutePath());
					final String output = buildModOutput(dir.getAbsoluteFile().getAbsolutePath());
					MusicBot.logger.log(Level.INFO, output);
					new Thread(() -> {
						while(true) {
							File file = new File(output);
							if(file.exists() && file.length() > 0)
								break;
							try {
								Thread.sleep(1000L);
							} catch(InterruptedException err) { err.printStackTrace(); }
						}
						AsyncPlayerExecutor.PATCH_DONE = true;
						MusicBot.logger.log(Level.INFO, "Generation complete! File can now be played!");
					}).start();
					while(!(AsyncPlayerExecutor.PATCH_DONE))
						Thread.sleep(1000L);
					dir.delete();
					dir = new File(output);
					fileName = fileName.concat(" +" + this.mod);
					BotModules.getAudioPanel().queueAudio(output, fileName);
					Thread.sleep(1500L);
					BotModules.getAudioPanel().perform();
				}
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
	
	private void buildFFMPEGProcess(String file) throws Exception {
		String handle = "ffmpeg -i \"{input}\" -filter:a \"aresample={bitRate}\",\"atempo={speed}\",\"asetrate={pitch}\" -y \"{output}\"";
		handle = handle.replace("{input}", file)
				.replace("{output}", buildModOutput(file))
				.replace("{bitRate}", BotModules.getSettings().getAudioBitRate())
				.replace("{speed}", String.valueOf(this.speed))
				.replace("{pitch}", String.valueOf(this.pitch) + "*" + BotModules.getSettings().getAudioBitRate());
		MusicBot.logger.log(Level.INFO, handle);
		String preset = "cmd /c start cmd.exe /K \"{command} && exit\"".replace("{command}", handle);
		Runtime.getRuntime().exec(preset); AsyncPlayerExecutor.PATCH_DONE = false;
	}
	
	private String buildModOutput(String file) {
		int extensionPos = file.lastIndexOf(".");
		StringBuilder fileBuilder = new StringBuilder();
		fileBuilder.append(file.substring(0, extensionPos));
		fileBuilder.append("_" + this.mod + "." + file.substring(extensionPos+1));
		return fileBuilder.toString();
	}

}
