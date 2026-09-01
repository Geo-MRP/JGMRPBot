/* SPDX-License-Identifier: AGPL-3.0-or-later */

package com.gmrp.JGMRPBot.core.databaseManager;

import com.gmrp.JGMRPBot.BotConfig;
import com.gmrp.JGMRPBot.core.databaseManager.exception.DatabaseManagerException;

public final class DatabaseManagerFactory {

	private DatabaseManagerFactory() {
	}

	public static DatabaseManager create() throws DatabaseManagerException {
		String dbType = BotConfig.getInstance().getDbType().toLowerCase();
		if ("oracle".equals(dbType)) {
			return new DatabaseManagerOracle();
		}
		// Default / fallback = SQLite
		return new DatabaseManagerSQLite();
	}
}