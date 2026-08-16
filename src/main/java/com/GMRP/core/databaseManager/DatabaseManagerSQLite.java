/* SPDX-License-Identifier: AGPL-3.0-or-later */

package com.GMRP.core.databaseManager;

import java.sql.*;

import com.GMRP.BotConfig;
import com.GMRP.core.databaseManager.exception.DatabaseManagerException;

/**
 * Database access point supporting SQLite.
 */
public class DatabaseManagerSQLite implements IDatabaseManager {

	private final String sqliteUrl;

	public DatabaseManagerSQLite() {
		this.sqliteUrl = "jdbc:sqlite:" + BotConfig.getInstance().getDbSqlitePath();
	}

	public DatabaseManagerSQLite(String sqlitePath) {
		this.sqliteUrl = "jdbc:sqlite:" + sqlitePath;
	}

	private Connection getConnection() throws DatabaseManagerException {
		try {
			return java.sql.DriverManager.getConnection(sqliteUrl);
		} catch (SQLException e) {
			throw new DatabaseManagerException("Failed to connect to SQLite database", e);
		}
	}

	@Override
	public boolean testConnection() {
		try (Connection conn = getConnection();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery("SELECT 1")) {

			if (rs.next()) {
				System.out.println("Database connection OK (SQLite). Test result: " + rs.getInt(1));
				return true;
			}
		} catch (DatabaseManagerException | SQLException e) {
			System.err.println("Database connection FAILED (SQLite): " + e.getMessage());
		}
		return false;
	}

	@Override
	public void close() {
		// SQLite has no pool to destroy
	}

	@Override
	public String getConfigKey(String key) throws DatabaseManagerException {
		try (Connection connection = this.getConnection();
				PreparedStatement preparedStatement = connection
						.prepareStatement("SELECT CONFIG_VALUE FROM CONFIG WHERE CONFIG_KEY = ?")) {
			preparedStatement.setString(1, key);
			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (!resultSet.next())
					throw new DatabaseManagerException(key + " not found in the database.");
				return resultSet.getString(1);
			}
		} catch (DatabaseManagerException | SQLException e) {
			throw new DatabaseManagerException("Failed to get config value from database", e);
		}
	}
}