/* SPDX-License-Identifier: AGPL-3.0-or-later */
package com.GMRP.core.databaseManager;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Database access point. Implementations exist for Oracle (UCP) and SQLite.
 * Designed for constructor-based dependency injection.
 */
public interface IDatabaseManager extends AutoCloseable {

	/**
	 * Returns a Connection. Always close it (preferably with try-with-resources).
	 */
	Connection getConnection() throws SQLException;

	/**
	 * Simple connectivity check. Useful at startup or in tests.
	 */
	boolean testConnection();

	/**
	 * Releases any resources (e.g. Oracle connection pool). No-op for SQLite.
	 */
	@Override
	void close();
}