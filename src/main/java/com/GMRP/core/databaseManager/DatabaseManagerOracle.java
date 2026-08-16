/* SPDX-License-Identifier: AGPL-3.0-or-later */

package com.GMRP.core.databaseManager;

import java.sql.*;

import com.GMRP.BotConfig;

import com.GMRP.core.databaseManager.exception.DatabaseManagerException;
import oracle.ucp.admin.UniversalConnectionPoolManager;
import oracle.ucp.admin.UniversalConnectionPoolManagerImpl;
import oracle.ucp.jdbc.PoolDataSource;
import oracle.ucp.jdbc.PoolDataSourceFactory;

/**
 * Database Manager for Oracle (UCP)
 */
public class DatabaseManagerOracle implements IDatabaseManager {

	private final PoolDataSource oraclePool; // null when using SQLite

	public DatabaseManagerOracle() throws DatabaseManagerException {
		try {
			this.oraclePool = createOraclePool();
		} catch (SQLException e) {
			throw new DatabaseManagerException("Failed to create Oracle connection pool", e);
		}
	}

	private Connection getConnection() throws DatabaseManagerException {
		try {
			return oraclePool.getConnection();
		} catch (SQLException e) {
			throw new DatabaseManagerException("Failed to connect to Oracle database", e);
		}
	}

	@Override
	public boolean testConnection() {
		try (Connection conn = getConnection();
				Statement statement = conn.createStatement();
				ResultSet resultSet = statement.executeQuery("SELECT 1 FROM DUAL")) {

			if (resultSet.next()) {
				System.out.println("Database connection OK (Oracle). Test result: " + resultSet.getInt(1));
				return true;
			}
		} catch (DatabaseManagerException | SQLException e) {
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