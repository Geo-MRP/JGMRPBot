/* SPDX-License-Identifier: AGPL-3.0-or-later */

package com.GMRP.core.databaseManager;

import java.sql.*;

import com.GMRP.BotConfig;
import com.GMRP.core.databaseManager.exception.DatabaseManagerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Database access point supporting SQLite.
 */
public class DatabaseManagerSQLite implements IDatabaseManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseManagerSQLite.class);

	private final String sqliteUrl;

	public DatabaseManagerSQLite() {
		this.sqliteUrl = "jdbc:sqlite:" + BotConfig.getInstance().getDbSqlitePath();
		LOGGER.debug("DatabaseManagerSQLite initialized with default SQLite path: {}",
				BotConfig.getInstance().getDbSqlitePath());
	}

	public DatabaseManagerSQLite(String sqlitePath) {
		this.sqliteUrl = "jdbc:sqlite:" + sqlitePath;
		LOGGER.debug("DatabaseManagerSQLite initialized with custom SQLite path: {}", sqlitePath);
	}

	private Connection getConnection() throws DatabaseManagerException {
		try {
			LOGGER.trace("Attempting to get database connection");
			return java.sql.DriverManager.getConnection(sqliteUrl);
		} catch (SQLException e) {
			LOGGER.error("Failed to connect to SQLite database at URL: {}", sqliteUrl, e);
			throw new DatabaseManagerException("Failed to connect to SQLite database", e);
		}
	}

	@Override
	public boolean testConnection() {
		LOGGER.info("Testing SQLite database connection");
		try (Connection conn = getConnection();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery("SELECT 1")) {

			if (rs.next()) {
				int result = rs.getInt(1);
				LOGGER.info("Database connection OK (SQLite). Test result: {}", result);
				System.out.println("Database connection OK (SQLite). Test result: " + result);
				return true;
			}
		} catch (DatabaseManagerException | SQLException e) {
			LOGGER.error("Database connection FAILED (SQLite): {}", e.getMessage(), e);
			System.err.println("Database connection FAILED (SQLite): " + e.getMessage());
		}
		return false;
	}

	@Override
	public void close() {
		LOGGER.debug("Closing SQLite database manager (no pool to destroy)");
		// SQLite has no pool to destroy
	}

	@Override
	public String getConfigKey(String key) throws DatabaseManagerException {
		LOGGER.debug("Retrieving config value for key: {}", key);
		try (Connection connection = this.getConnection();
				PreparedStatement preparedStatement = connection
						.prepareStatement("SELECT CONFIG_VALUE FROM CONFIG WHERE CONFIG_KEY = ?")) {
			preparedStatement.setString(1, key);
			LOGGER.trace("Executing SQL query for config key: {}", key);
			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (!resultSet.next()) {
					LOGGER.warn("Config key '{}' not found in the database", key);
					throw new DatabaseManagerException(key + " not found in the database.");
				}
				String value = resultSet.getString(1);
				LOGGER.debug("Successfully retrieved config value for key: {}", key);
				return value;
			}
		} catch (DatabaseManagerException | SQLException e) {
			LOGGER.error("Failed to get config value for key '{}' from database", key, e);
			throw new DatabaseManagerException("Failed to get config value from database", e);
		}
	}
}