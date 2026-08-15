/* SPDX-License-Identifier: AGPL-3.0-or-later */

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
		config.put("DB_TYPE", System.getenv("DB_TYPE")); // "oracle" or "sqlite"
		config.put("DB_USER", System.getenv("DB_USER"));
		config.put("DB_PASSWORD", System.getenv("DB_PASSWORD"));
		config.put("DB_CONNECT_STRING", System.getenv("DB_CONNECT_STRING")); // Oracle only
		config.put("DB_SQLITE_PATH", System.getenv("DB_SQLITE_PATH")); // e.g. "data/database.db"
	}

	public String getToken() {
		return config.getString("TOKEN");
	}

	public String getDbType() {
		return config.optString("DB_TYPE", "sqlite");
	}

	public String getDbUser() {
		return config.optString("DB_USER", "");
	}

	public String getDbPassword() {
		return config.optString("DB_PASSWORD", "");
	}

	public String getDbConnectString() {
		return config.optString("DB_CONNECT_STRING", "");
	}

	public String getDbSqlitePath() {
		return config.optString("DB_SQLITE_PATH", "data/database.db");
	}
}
