/* SPDX-License-Identifier: AGPL-3.0-or-later */

package com.GMRP.core.databaseManager;

import java.sql.SQLException;

import com.GMRP.BotConfig;

public final class DatabaseManagerFactory {

	private DatabaseManagerFactory() {
	}

	public static IDatabaseManager create() throws SQLException {
		String dbType = BotConfig.getInstance().getDbType().toLowerCase();
		if ("oracle".equals(dbType)) {
			return new DatabaseManagerOracle();
		}
		// Default / fallback = SQLite
		return new DatabaseManagerSQLite();
	}
}