package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Database connection utility for SQL Server using JDBC.
 * Update the connection parameters below to match your environment.
 */
public class DBContext {

    // ===== UPDATE THESE VALUES TO MATCH YOUR SQL SERVER =====
    private static final String SERVER_NAME = "localhost";
    private static final String DB_NAME = "AssignmentManageDB";
    private static final String PORT = "1433";
    private static final String USER_NAME = "sa";
    private static final String PASSWORD = "123";
    // ========================================================

    private static final String URL = "jdbc:sqlserver://" + SERVER_NAME + ":" + PORT
            + ";databaseName=" + DB_NAME
            + ";encrypt=true;trustServerCertificate=true";

    /**
     * Get a connection to the SQL Server database.
     * @return Connection object
     * @throws SQLException if connection fails
     * @throws ClassNotFoundException if JDBC driver not found
     */
    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        return DriverManager.getConnection(URL, USER_NAME, PASSWORD);
    }

    /**
     * Close a connection safely.
     * @param conn the connection to close
     */
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
