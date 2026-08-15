package com.GMRP.core.databaseManager;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import java.sql.Connection;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import com.GMRP.BotConfig;
import oracle.ucp.jdbc.PoolDataSource;
import oracle.ucp.jdbc.PoolDataSourceFactory;
import static org.mockito.Mockito.verify;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManagerOracleTest {
	@Test
	void getConnection_shouldReturnConnectionFromPool() throws Exception {
		PoolDataSource pool = mock(PoolDataSource.class);
		Connection connection = mock(Connection.class);
		BotConfig config = mock(BotConfig.class);

		when(config.getDbConnectString()).thenReturn("test");
		when(config.getDbUser()).thenReturn("user");
		when(config.getDbPassword()).thenReturn("password");
		when(pool.getConnection()).thenReturn(connection);

		try (MockedStatic<PoolDataSourceFactory> poolFactory = mockStatic(PoolDataSourceFactory.class);
				MockedStatic<BotConfig> botConfig = mockStatic(BotConfig.class)) {

			poolFactory.when(PoolDataSourceFactory::getPoolDataSource)
					.thenReturn(pool);

			botConfig.when(BotConfig::getInstance)
					.thenReturn(config);

			DatabaseManagerOracle manager = new DatabaseManagerOracle();

			assertSame(connection, manager.getConnection());
		}
	}

	@Test
	void testConnection_shouldReturnTrueWhenQuerySucceeds() throws Exception {
		PoolDataSource pool = mock(PoolDataSource.class);
		Connection connection = mock(Connection.class);
		Statement statement = mock(Statement.class);
		ResultSet resultSet = mock(ResultSet.class);
		BotConfig config = mock(BotConfig.class);

		when(config.getDbConnectString()).thenReturn("test");
		when(config.getDbUser()).thenReturn("user");
		when(config.getDbPassword()).thenReturn("password");

		when(pool.getConnection()).thenReturn(connection);
		when(connection.createStatement()).thenReturn(statement);
		when(statement.executeQuery("SELECT 1 FROM DUAL")).thenReturn(resultSet);
		when(resultSet.next()).thenReturn(true);

		try (MockedStatic<PoolDataSourceFactory> poolFactory = mockStatic(PoolDataSourceFactory.class);
				MockedStatic<BotConfig> botConfig = mockStatic(BotConfig.class)) {

			poolFactory.when(PoolDataSourceFactory::getPoolDataSource)
					.thenReturn(pool);

			botConfig.when(BotConfig::getInstance)
					.thenReturn(config);

			DatabaseManagerOracle manager = new DatabaseManagerOracle();

			assertTrue(manager.testConnection());
		}
	}
	@Test
	void testConnection_shouldReturnFalseWhenQueryFails() throws Exception {
		PoolDataSource pool = mock(PoolDataSource.class);
		Connection connection = mock(Connection.class);
		Statement statement = mock(Statement.class);
		BotConfig config = mock(BotConfig.class);

		when(config.getDbConnectString()).thenReturn("test");
		when(config.getDbUser()).thenReturn("user");
		when(config.getDbPassword()).thenReturn("password");

		when(pool.getConnection()).thenReturn(connection);
		when(connection.createStatement()).thenReturn(statement);
		when(statement.executeQuery("SELECT 1 FROM DUAL"))
				.thenThrow(new SQLException("Database query failed"));

		try (MockedStatic<PoolDataSourceFactory> poolFactory = mockStatic(PoolDataSourceFactory.class);
				MockedStatic<BotConfig> botConfig = mockStatic(BotConfig.class)) {

			poolFactory.when(PoolDataSourceFactory::getPoolDataSource)
					.thenReturn(pool);

			botConfig.when(BotConfig::getInstance)
					.thenReturn(config);

			DatabaseManagerOracle manager = new DatabaseManagerOracle();

			assertFalse(manager.testConnection());
		}
	}
	@Test
	void close_shouldNotThrowWhenPoolNameIsBlank() throws Exception {
		PoolDataSource pool = mock(PoolDataSource.class);
		BotConfig config = mock(BotConfig.class);

		when(config.getDbConnectString()).thenReturn("test");
		when(config.getDbUser()).thenReturn("user");
		when(config.getDbPassword()).thenReturn("password");
		when(pool.getConnectionPoolName()).thenReturn("");

		try (MockedStatic<PoolDataSourceFactory> poolFactory = mockStatic(PoolDataSourceFactory.class);
				MockedStatic<BotConfig> botConfig = mockStatic(BotConfig.class)) {

			poolFactory.when(PoolDataSourceFactory::getPoolDataSource)
					.thenReturn(pool);

			botConfig.when(BotConfig::getInstance)
					.thenReturn(config);

			DatabaseManagerOracle manager = new DatabaseManagerOracle();

			assertDoesNotThrow(manager::close);
		}
	}
	@Test
	void testConnection_shouldCloseResourcesAfterExecution() throws Exception {
		PoolDataSource pool = mock(PoolDataSource.class);
		Connection connection = mock(Connection.class);
		Statement statement = mock(Statement.class);
		ResultSet resultSet = mock(ResultSet.class);
		BotConfig config = mock(BotConfig.class);

		when(config.getDbConnectString()).thenReturn("test");
		when(config.getDbUser()).thenReturn("user");
		when(config.getDbPassword()).thenReturn("password");

		when(pool.getConnection()).thenReturn(connection);
		when(connection.createStatement()).thenReturn(statement);
		when(statement.executeQuery("SELECT 1 FROM DUAL"))
				.thenReturn(resultSet);
		when(resultSet.next()).thenReturn(true);

		try (MockedStatic<PoolDataSourceFactory> poolFactory = mockStatic(PoolDataSourceFactory.class);
				MockedStatic<BotConfig> botConfig = mockStatic(BotConfig.class)) {

			poolFactory.when(PoolDataSourceFactory::getPoolDataSource)
					.thenReturn(pool);

			botConfig.when(BotConfig::getInstance)
					.thenReturn(config);

			DatabaseManagerOracle manager = new DatabaseManagerOracle();

			assertTrue(manager.testConnection());

			verify(resultSet).close();
			verify(statement).close();
			verify(connection).close();
		}
	}
}
