package me.stella.Discord;

public class OsuAudioMod {
	
	public static String[] MODS = new String[] { "DT", "NC", "HT", "NM" };
	
	private String name;
	private double pitch;
	private double speed;
	
	public OsuAudioMod(String name, double a, double b) {
		this.name = name;
		this.pitch = a;
		this.speed = b;
	}
	
	public String getMod() {
		return this.name;
	}
	
	public double getAudioSpeed() {
		return this.speed;
	}
	
	public double getAudioPitch() {
		return this.pitch;
	}
	
	public static OsuAudioMod _HalfTime_() {
		return new OsuAudioMod("HT", 1.0D, 0.75D);
	}
	
	public static OsuAudioMod _DoubleTime_() {
		return new OsuAudioMod("DT", 1.0D, 1.5D);
	}
	
	public static OsuAudioMod _Nightcore_() {
		return new OsuAudioMod("NC", 1.5D, 1.0D);
	}
	
	public static OsuAudioMod _NoMod_() {
		return new OsuAudioMod("NM", 1.0D, 1.0D);
	}
	
}
