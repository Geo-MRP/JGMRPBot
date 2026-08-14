/* SPDX-License-Identifier: AGPL-3.0-or-later */
package com.GMRP.core.databaseManager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.GMRP.BotConfig;

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

	public Connection getConnection() throws SQLException {
		return java.sql.DriverManager.getConnection(sqliteUrl);
	}

	public boolean testConnection() {
		try (Connection conn = getConnection();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery("SELECT 1")) {

			if (rs.next()) {
				System.out.println("Database connection OK (SQLite). Test result: " + rs.getInt(1));
				return true;
			}
		} catch (SQLException e) {
			System.err.println("Database connection FAILED (SQLite): " + e.getMessage());
		}
		return false;
	}

	@Override
	public void close() {
		// SQLite has no pool to destroy
	}
}