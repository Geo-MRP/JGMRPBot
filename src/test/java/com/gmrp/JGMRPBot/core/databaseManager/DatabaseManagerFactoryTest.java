/* SPDX-License-Identifier: AGPL-3.0-or-later */

package com.gmrp.JGMRPBot.core.databaseManager;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import com.gmrp.JGMRPBot.core.databaseManager.exception.DatabaseManagerException;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import static org.mockito.Mockito.mockConstruction;
import com.gmrp.JGMRPBot.BotConfig;

public class DatabaseManagerFactoryTest {
	@Test
	void create_shouldReturnSQLiteManagerForSQLiteConfig() throws DatabaseManagerException {
		BotConfig config = mock(BotConfig.class);

		when(config.getDbType()).thenReturn("sqlite");

		try (MockedStatic<BotConfig> botConfig = mockStatic(BotConfig.class)) {

			botConfig.when(BotConfig::getInstance)
					.thenReturn(config);

			DatabaseManager manager = DatabaseManagerFactory.create();

			assertInstanceOf(DatabaseManagerSQLite.class, manager);
		}
	}
	@Test
	void create_shouldReturnSQLiteManagerForUnknownDatabaseType() throws DatabaseManagerException {
		BotConfig config = mock(BotConfig.class);

		when(config.getDbType()).thenReturn("unknown");

		try (MockedStatic<BotConfig> botConfig = mockStatic(BotConfig.class)) {

			botConfig.when(BotConfig::getInstance)
					.thenReturn(config);

			DatabaseManager manager = DatabaseManagerFactory.create();

			assertInstanceOf(DatabaseManagerSQLite.class, manager);
		}
	}
	@Test
	void create_shouldReturnOracleManagerForOracleConfig() throws DatabaseManagerException {
		BotConfig config = mock(BotConfig.class);

		when(config.getDbType()).thenReturn("oracle");

		try (MockedStatic<BotConfig> botConfig = mockStatic(BotConfig.class);
				MockedConstruction<DatabaseManagerOracle> oracleConstruction = mockConstruction(
						DatabaseManagerOracle.class)) {

			botConfig.when(BotConfig::getInstance)
					.thenReturn(config);

			DatabaseManager manager = DatabaseManagerFactory.create();

			assertInstanceOf(DatabaseManagerOracle.class, manager);
		}
	}
}
