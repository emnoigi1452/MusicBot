package me.stella.Settings;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Activity.ActivityType;

public class JSONInternal {
	
	private File backup;
	private JSONObject internal;
	private boolean change;
	
	public JSONInternal(File f) {
		assert (f != null) && (f.exists()) && (f.isFile());
		this.backup = f;
		this.change = false;
		BufferedReader input = null;
		this.internal = null;
		try {
			input = new BufferedReader(new FileReader(f));
			String line; StringBuilder json = new StringBuilder();
			while((line = input.readLine()) != null)
				json.append(line.concat("\n"));
			this.internal = (JSONObject) (new JSONParser().parse(json.toString()));
			input.close();
		} catch(Exception e) { e.printStackTrace(); }
	}
	
	private void check() {
		assert (this.internal != null);
	}
	
	public String getToken() {
		check();
		return String.valueOf(this.internal.get("token"));
	}
	
	public String getDefaultPermission() {
		check();
		return String.valueOf(this.internal.get("permission"));
	}
	
	public String getLocaleFile() {
		check();
		return String.valueOf(this.internal.get("locale"));
	}
	
	public String getAudioBitRate() {
		check();
		return String.valueOf(this.internal.get("bit_rate"));
	}
	
	public String getPrivateRoleID(String guildID) {
		check();
		JSONObject guildConfig = (JSONObject) this.internal.get("private_role");
		if(!(guildConfig.containsKey(guildID)))
			return "-1";
		return String.valueOf(guildConfig.get(guildID));
	}
	
	@SuppressWarnings("unchecked")
	public void appendGuild(String guildID) {
		check();
		JSONObject guildConfig = (JSONObject) this.internal.get("private_role");
		guildConfig.put(guildID, "-1");
		this.internal.put("private_role", guildConfig);
		this.change = true;
	}
	
	public void rewrite() throws Exception {
		if(!change)
			return;
		OutputStreamWriter output = new OutputStreamWriter(new FileOutputStream(this.backup), StandardCharsets.UTF_8);
		output.write(this.internal.toJSONString());
		output.flush(); output.close();
	}
	
	public Activity buildActivity() {
		JSONObject activityConfig = (JSONObject) this.internal.get("activity");
		ActivityType type = null;
		try {
			type = ActivityType.valueOf(String.valueOf(activityConfig.get("type")));
			if(type == null)
				type = ActivityType.PLAYING;
			return Activity.of(type, String.valueOf(activityConfig.get("value")));
		} catch(Exception e) { e.printStackTrace(); }
		return null;
	}
	
	public Object[] getBootParameters() {
		check();
		JSONObject bootConfig = (JSONObject) this.internal.get("boot");
		return (new Object[] {
			bootConfig.get("enabled"),
			bootConfig.get("serverID"),
			bootConfig.get("channelID")
		});
		
	}

}
