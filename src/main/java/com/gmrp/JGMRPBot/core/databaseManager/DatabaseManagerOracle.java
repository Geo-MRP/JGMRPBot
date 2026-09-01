/* SPDX-License-Identifier: AGPL-3.0-or-later */

package com.gmrp.JGMRPBot.core.databaseManager;

import java.sql.*;

import com.gmrp.JGMRPBot.BotConfig;

import com.gmrp.JGMRPBot.core.databaseManager.exception.DatabaseManagerException;
import oracle.ucp.admin.UniversalConnectionPoolManager;
import oracle.ucp.admin.UniversalConnectionPoolManagerImpl;
import oracle.ucp.jdbc.PoolDataSource;
import oracle.ucp.jdbc.PoolDataSourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Database Manager for Oracle (UCP)
 */
public class DatabaseManagerOracle implements IDatabaseManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseManagerOracle.class);

	private final PoolDataSource oraclePool; // null when using SQLite

	public DatabaseManagerOracle() throws DatabaseManagerException {
		LOGGER.info("Initializing Oracle Database Manager");
		try {
			this.oraclePool = createOraclePool();
			LOGGER.info("Oracle Database Manager initialized successfully");
		} catch (SQLException e) {
			LOGGER.error("Failed to create Oracle connection pool", e);
			throw new DatabaseManagerException("Failed to create Oracle connection pool", e);
		}
	}

	private Connection getConnection() throws DatabaseManagerException {
		try {
			LOGGER.trace("Attempting to get Oracle database connection");
			Connection conn = oraclePool.getConnection();
			LOGGER.trace("Oracle database connection obtained successfully");
			return conn;
		} catch (SQLException e) {
			LOGGER.error("Failed to connect to Oracle database", e);
			throw new DatabaseManagerException("Failed to connect to Oracle database", e);
		}
	}

	@Override
	public boolean testConnection() {
		LOGGER.info("Testing Oracle database connection");
		try (Connection conn = getConnection();
				Statement statement = conn.createStatement();
				ResultSet resultSet = statement.executeQuery("SELECT 1 FROM DUAL")) {

			if (resultSet.next()) {
				int result = resultSet.getInt(1);
				LOGGER.info("Database connection OK (Oracle). Test result: {}", result);
				return true;
			}
		} catch (DatabaseManagerException | SQLException e) {
			LOGGER.error("Database connection FAILED (Oracle): {}", e.getMessage(), e);
		}
		return false;
	}

	@Override
	public void close() {
		LOGGER.debug("Closing Oracle database manager and destroying connection pool");
		try {
			String poolName = oraclePool.getConnectionPoolName();
			if (poolName != null && !poolName.isBlank()) {
				LOGGER.debug("Destroying Oracle connection pool: {}", poolName);
				UniversalConnectionPoolManager mgr = UniversalConnectionPoolManagerImpl
						.getUniversalConnectionPoolManager();
				mgr.destroyConnectionPool(poolName);
				LOGGER.info("Oracle connection pool '{}' destroyed successfully", poolName);
			} else {
				LOGGER.warn("Oracle connection pool name is null or blank, cannot destroy pool");
			}
		} catch (Exception e) {
			LOGGER.error("Error while destroying Oracle connection pool: {}", e.getMessage(), e);
		}
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
			LOGGER.error("Failed to get config value for key '{}' from Oracle database", key, e);
			throw new DatabaseManagerException("Failed to get config value from database", e);
		}
	}

	private PoolDataSource createOraclePool() throws SQLException {
		LOGGER.info("Creating Oracle connection pool with UCP");

		PoolDataSource pool = PoolDataSourceFactory.getPoolDataSource();
		pool.setConnectionFactoryClassName("oracle.jdbc.replay.OracleConnectionPoolDataSourceImpl");

		String connectString = BotConfig.getInstance().getDbConnectString();
		String user = BotConfig.getInstance().getDbUser();
		String password = BotConfig.getInstance().getDbPassword();

		pool.setURL("jdbc:oracle:thin:@" + connectString);
		pool.setUser(user);
		pool.setPassword(password);
		pool.setConnectionPoolName("JGMRP_UCP_POOL");

		// Reasonable defaults – adjust later if needed
		pool.setInitialPoolSize(2);
		pool.setMinPoolSize(2);
		pool.setMaxPoolSize(10);

		LOGGER.debug(
				"Oracle connection pool configured with URL: jdbc:oracle:thin:@{}, User: {}, Pool size: initial=2, min=2, max=10",
				connectString, user);
		LOGGER.info("Oracle connection pool created successfully with name: JGMRP_UCP_POOL");

		return pool;
	}
}