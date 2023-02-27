package me.stella.Radio;

import java.nio.Buffer;
import java.nio.ByteBuffer;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame;

import net.dv8tion.jda.api.audio.AudioSendHandler;

public class AudioPlayerSendHandler implements AudioSendHandler {

	private final AudioPlayer player;
	private ByteBuffer buffer;
	private MutableAudioFrame frame;
	
	public AudioPlayerSendHandler(AudioPlayer player) {
		this.player = player;
		this.buffer = ByteBuffer.allocate(1024);
		this.frame = new MutableAudioFrame();
		this.frame.setBuffer(this.buffer);
	}
	
	@Override
	public boolean canProvide() {
		return this.player.provide(this.frame);
	}

	@Override
	public ByteBuffer provide20MsAudio() {
	 	final Buffer packet = (Buffer) this.buffer;
	 	return ((ByteBuffer)packet.flip());
	}
	
	@Override
	public boolean isOpus() {
		return true;
	}

}
