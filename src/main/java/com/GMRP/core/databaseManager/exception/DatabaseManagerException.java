package com.GMRP.core.databaseManager.exception;

/**
 * Root exception for all database manager-related errors.
 */
public class DatabaseManagerException extends Exception {
	public DatabaseManagerException(String message) {
		super(message);
	}

	public DatabaseManagerException(String message, Throwable cause) {
		super(message, cause);
	}
}