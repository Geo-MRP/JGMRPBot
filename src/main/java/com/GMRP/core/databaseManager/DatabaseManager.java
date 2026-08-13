/* SPDX-License-Identifier: AGPL-3.0-or-later */
package com.GMRP.core.databaseManager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.GMRP.BotConfig;

import oracle.ucp.admin.UniversalConnectionPoolManager;
import oracle.ucp.admin.UniversalConnectionPoolManagerImpl;
import oracle.ucp.jdbc.PoolDataSource;
import oracle.ucp.jdbc.PoolDataSourceFactory;

/**
 * Database access point supporting Oracle (UCP) and SQLite. Designed for
 * constructor-based dependency injection.
 */
public class DatabaseManager implements AutoCloseable {

	private final String dbType;
	private final PoolDataSource oraclePool; // null when using SQLite
	private final String sqliteUrl; // null when using Oracle

	/**
	 * Preferred constructor – reads everything from BotConfig.
	 */
	public DatabaseManager() throws SQLException {
		this.dbType = BotConfig.getInstance().getDbType().toLowerCase();

		if ("oracle".equals(dbType)) {
			this.oraclePool = createOraclePool();
			this.sqliteUrl = null;
		} else {
			// Default / fallback = SQLite
			this.oraclePool = null;
			this.sqliteUrl = "jdbc:sqlite:" + BotConfig.getInstance().getDbSqlitePath();
		}
	}

	/**
	 * Convenience constructor for tests or special cases where you want to force
	 * SQLite without touching BotConfig.
	 */
	public DatabaseManager(String sqlitePath) {
		this.dbType = "sqlite";
		this.oraclePool = null;
		this.sqliteUrl = "jdbc:sqlite:" + sqlitePath;
	}

	// -------------------------------------------------------------------------
	// Public API
	// -------------------------------------------------------------------------

	/**
	 * Returns a Connection. Always close it (preferably with try-with-resources).
	 */
	public Connection getConnection() throws SQLException {
		if ("oracle".equals(dbType)) {
			return oraclePool.getConnection();
		}
		return java.sql.DriverManager.getConnection(sqliteUrl);
	}

	/**
	 * Simple connectivity check. Useful at startup or in tests.
	 */
	public boolean testConnection() {
		try (Connection conn = getConnection();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(getTestQuery())) {

			if (rs.next()) {
				System.out.println("Database connection OK (" + dbType + "). Test result: " + rs.getInt(1));
				return true;
			}
		} catch (SQLException e) {
			System.err.println("Database connection FAILED (" + dbType + "): " + e.getMessage());
		}
		return false;
	}

	public String getDbType() {
		return dbType;
	}

	/**
	 * Closes the Oracle pool if one exists. Call this on application shutdown.
	 */
	@Override
	public void close() {
		if (oraclePool == null) {
			return; // SQLite – nothing to close
		}

		try {
			String poolName = oraclePool.getConnectionPoolName();
			if (poolName != null && !poolName.isBlank()) {
				UniversalConnectionPoolManager mgr = UniversalConnectionPoolManagerImpl
						.getUniversalConnectionPoolManager();
				mgr.destroyConnectionPool(poolName);
			}
		} catch (Exception e) {
			System.err.println("Error while destroying Oracle connection pool: " + e.getMessage());
		}
	}

	// -------------------------------------------------------------------------
	// Private helpers
	// -------------------------------------------------------------------------

	private PoolDataSource createOraclePool() throws SQLException {
		PoolDataSource pool = PoolDataSourceFactory.getPoolDataSource();
		pool.setConnectionFactoryClassName("oracle.jdbc.replay.OracleConnectionPoolDataSourceImpl");
		pool.setURL("jdbc:oracle:thin:@" + BotConfig.getInstance().getDbConnectString());
		pool.setUser(BotConfig.getInstance().getDbUser());
		pool.setPassword(BotConfig.getInstance().getDbPassword());
		pool.setConnectionPoolName("JGMRP_UCP_POOL");

		// Reasonable defaults – adjust later if needed
		pool.setInitialPoolSize(2);
		pool.setMinPoolSize(2);
		pool.setMaxPoolSize(10);

		return pool;
	}

	private String getTestQuery() {
		return "oracle".equals(dbType) ? "SELECT 1 FROM DUAL" : "SELECT 1";
	}
}