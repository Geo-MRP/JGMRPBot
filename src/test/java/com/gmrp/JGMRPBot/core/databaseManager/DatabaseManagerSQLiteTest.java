/* SPDX-License-Identifier: AGPL-3.0-or-later */

package com.gmrp.JGMRPBot.core.databaseManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.gmrp.JGMRPBot.core.databaseManager.exception.DatabaseManagerException;

public class DatabaseManagerSQLiteTest {
	@TempDir
	Path temporaryDirectory;

	@Test
	void testConnection_shouldReturnTrueForValidDatabase() {
		DatabaseManagerSQLite manager = new DatabaseManagerSQLite(":memory:");

		assertTrue(manager.testConnection());
	}
	@Test
	void getConfigKey_shouldReturnConfiguredValue() throws Exception {
		Path databasePath = temporaryDirectory.resolve("config.db");
		try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + databasePath);
				var statement = connection.createStatement()) {
			statement.executeUpdate("CREATE TABLE CONFIG (CONFIG_KEY TEXT, CONFIG_VALUE TEXT)");
			statement.executeUpdate("INSERT INTO CONFIG VALUES ('OWNER', '12345')");
		}

		DatabaseManagerSQLite manager = new DatabaseManagerSQLite(databasePath.toString());

		assertEquals("12345", manager.getConfigKey("OWNER"));
	}

	@Test
	void getConfigKey_shouldThrowDatabaseManagerExceptionWhenKeyIsMissing() throws Exception {
		Path databasePath = temporaryDirectory.resolve("config.db");
		try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + databasePath);
				var statement = connection.createStatement()) {
			statement.executeUpdate("CREATE TABLE CONFIG (CONFIG_KEY TEXT, CONFIG_VALUE TEXT)");
		}

		DatabaseManagerSQLite manager = new DatabaseManagerSQLite(databasePath.toString());

		DatabaseManagerException exception = assertThrows(DatabaseManagerException.class,
				() -> manager.getConfigKey("MISSING"));
		assertEquals("Failed to get config value from database", exception.getMessage());
	}
}
