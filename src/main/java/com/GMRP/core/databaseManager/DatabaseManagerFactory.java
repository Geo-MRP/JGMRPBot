/* SPDX-License-Identifier: AGPL-3.0-or-later */

package com.GMRP.core.databaseManager;

import com.GMRP.BotConfig;
import com.GMRP.core.databaseManager.exception.DatabaseManagerException;

public final class DatabaseManagerFactory {

	private DatabaseManagerFactory() {
	}

	public static IDatabaseManager create() throws DatabaseManagerException {
		String dbType = BotConfig.getInstance().getDbType().toLowerCase();
		if ("oracle".equals(dbType)) {
			return new DatabaseManagerOracle();
		}
		// Default / fallback = SQLite
		return new DatabaseManagerSQLite();
	}
}