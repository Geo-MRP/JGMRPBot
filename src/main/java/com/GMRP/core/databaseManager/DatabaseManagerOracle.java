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
 * Database Manager for Oracle (UCP)
 */
public class DatabaseManagerOracle implements IDatabaseManager {

	private final PoolDataSource oraclePool; // null when using SQLite

	public DatabaseManagerOracle() throws SQLException {
		this.oraclePool = createOraclePool();
	}

	@Override
	public Connection getConnection() throws SQLException {
		return oraclePool.getConnection();
	}

	@Override
	public boolean testConnection() {
		try (Connection conn = getConnection();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery("SELECT 1 FROM DUAL")) {

			if (rs.next()) {
				System.out.println("Database connection OK (Oracle). Test result: " + rs.getInt(1));
				return true;
			}
		} catch (SQLException e) {
			System.err.println("Database connection FAILED (Oracle): " + e.getMessage());
		}
		return false;
	}

	@Override
	public void close() {
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
}