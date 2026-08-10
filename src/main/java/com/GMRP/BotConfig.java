package com.GMRP;

import org.json.JSONObject;

public class BotConfig {
	private static BotConfig instance;
	private JSONObject config;

	private BotConfig() {
		this("");
	}

	private BotConfig(String path) {
		if (path == null || path.isEmpty()) {
			loadFromEnv();
		} else {
			loadFromFile(path);
		}
	}

	public static void init(String path) {
		if (instance == null) {
			instance = new BotConfig(path);
		}
	}

	public static void init() {
		if (instance == null) {
			instance = new BotConfig();
		}
	}

	public static BotConfig getInstance() {
		if (instance == null) {
			throw new IllegalStateException(
					"CRITICAL: Tried to access BotConfig before it was initialized in Main.java!");
		}
		return instance;
	}

	public void loadFromFile(String path) {
		// not implemented yet
	}

	public void loadFromEnv() {
		config = new JSONObject();
		config.put("TOKEN", System.getenv("TOKEN"));
		config.put("OWNER", System.getenv("OWNER"));
		config.put("SERVER", System.getenv("SERVER"));
	}

	public String getToken() {
		return config.getString("TOKEN");
	}

	public String getOwnerId() {
		return config.getString("OWNER");
	}

	public String getServerID() {
		return config.getString("SERVER");
	}

}
