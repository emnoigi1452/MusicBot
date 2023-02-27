package me.stella.Settings;

import java.io.File;
import java.nio.file.Files;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Locale {
	
	public static final String SPLIT = "=";
	private Map<String, String> nodes = Collections.synchronizedMap(new HashMap<String, String>());
	
	public Locale(File f) {
		try {
			List<String> lines = Files.readAllLines(f.toPath());
			StringBuilder pars;
			for(String line: lines) {
				if(!(_checkLine(line)))
					continue;
				String[] factors = line.split(SPLIT);
				pars = new StringBuilder();
				for(int i = 1; i < factors.length; i++)
					pars.append((i > 1 ? SPLIT : "") + factors[i]);
				this.nodes.put(factors[0], pars.toString());
			}
		} catch (Exception e) { e.printStackTrace(); }
	}
	
	public synchronized String getMessage(String param) {
		return "> " + this.nodes.getOrDefault(param, "Error: There\'s an error in the localization file! Trace: **MESSAGE_NOT_FOUND**" + " - Param: " + param);
	}
	
	private boolean _checkLine(String param0) {
		if(param0 == null)
			return false;
		return (!(param0.trim().startsWith("#")));
	}

}
