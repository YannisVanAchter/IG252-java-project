package main.java.be.henallux.project.data;

import io.github.cdimascio.dotenv.Dotenv;

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
    private MySQLConnector() {
        try {
            Dotenv dotenv = Dotenv.load();
            String url = String.format("jdbc:mysql://%s:%S/%s", dotenv.get("MYSQL_ADDRESS"), dotenv.get("MYSQL_PORT"), dotenv.get("MYSQL_DATABASE"));
            String user = dotenv.get("MYSQL_USER");
            String password = dotenv.get("MYSQL_PASSWORD");
            this.connection = DriverManager.getConnection(url, user, password);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("DoubleCheckedLocking") //  I would rather check two times than fuck up my DB !
    public MySQLConnector getInstance() {
        if (instance == null ) {
            synchronized (MySQLConnector.class) {
                if (instance == null) {
                    instance = new MySQLConnector();
                }
            }
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
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
