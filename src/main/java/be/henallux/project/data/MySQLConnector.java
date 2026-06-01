package main.java.be.henallux.project.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class MySQLConnector {
    private static volatile MySQLConnector instance;
    private Connection connection;

    /**
     * Creates an instance based on .env file variables.
     * @require .env file at ../../.env
     * @require MYSQL_ROOT_PASSWORD in .env file
     * @require MYSQL_DATABASE in .env file
     * @require MYSQL_USER in .env file
     * @require MYSQL_PASSWORD in .env file
     */

    private final String url;
    private final String user;
    private final String password;

    private MySQLConnector() {
        String address  = System.getenv("MYSQL_ADDRESS")  != null ? System.getenv("MYSQL_ADDRESS")  : "localhost";
        String port     = System.getenv("MYSQL_PORT")     != null ? System.getenv("MYSQL_PORT")     : "3306";
        String database = System.getenv("MYSQL_DATABASE") != null ? System.getenv("MYSQL_DATABASE") : "superdatabasename";
        this.user       = System.getenv("MYSQL_USER")     != null ? System.getenv("MYSQL_USER")     : "superadminname";
        this.password   = System.getenv("MYSQL_PASSWORD") != null ? System.getenv("MYSQL_PASSWORD") : "supersuperadminpassword";
        this.url        = String.format("jdbc:mysql://%s:%s/%s", address, port, database);
    }

    @SuppressWarnings("DoubleCheckedLocking") // I would rather check two times than fuck up my DB!
    public static MySQLConnector getInstance() {
        if (instance == null) {
            synchronized (MySQLConnector.class) {
                if (instance == null) {
                    instance = new MySQLConnector();
                }
            }
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
    // FIX: closeConnection() removed — there is no longer a shared connection
    // to close. Each caller closes its own connection via try-with-resources.
}