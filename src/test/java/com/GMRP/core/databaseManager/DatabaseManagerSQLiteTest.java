package com.GMRP.core.databaseManager;
import static org.junit.jupiter.api.Assertions.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import org.junit.jupiter.api.Test;

public class DatabaseManagerSQLiteTest {
    @Test
    void getConnection_shouldReturnValidConnection() throws SQLException {
        DatabaseManagerSQLite manager =
                new DatabaseManagerSQLite(":memory:");

        try (Connection connection = manager.getConnection()) {
            assertNotNull(connection);
            assertFalse(connection.isClosed());
        }
    }
    @Test
    void testConnection_shouldReturnTrueForValidDatabase() {
        DatabaseManagerSQLite manager =
                new DatabaseManagerSQLite(":memory:");

        assertTrue(manager.testConnection());
    }
    @Test
    void getConnection_shouldAllowSqlExecution() throws SQLException {
        DatabaseManagerSQLite manager =
                new DatabaseManagerSQLite(":memory:");

        try (Connection connection = manager.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT 1")) {

            assertTrue(resultSet.next());
            assertEquals(1, resultSet.getInt(1));
        }
    }
    @Test
    void testConnection_shouldReturnFalseWhenConnectionFails() {
        DatabaseManagerSQLite manager = new DatabaseManagerSQLite(":memory:") {
            @Override
            public Connection getConnection() throws SQLException {
                throw new SQLException("Database connection failed");
            }
        };

        assertFalse(manager.testConnection());
    }
}
