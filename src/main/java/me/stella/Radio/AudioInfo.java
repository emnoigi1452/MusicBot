package me.stella.Radio;

import java.math.BigInteger;
import java.util.Base64;

public class AudioInfo {
	
	private String name;
	private String payload;
	
	public AudioInfo(String name, long i) {
		this.name = name; String temp = name + "_" + String.valueOf(i);
		this.payload = handleBytes(Base64.getEncoder().encode(temp.getBytes()));
	}
	
	public String getAudioName() {
		return this.name;
	}
	
	public String getAudioData() {
		return this.payload;
	}
	
	private String handleBytes(byte[] input) {
		return String.format("%0" + ((input.length) << 1) + "x", new BigInteger(1, input));
	}

}
