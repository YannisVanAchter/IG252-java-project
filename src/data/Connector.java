package data;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class Connector {
    private Connector instance;
    private Connection connection;

    /**
     * Creates an instance based on .env file variables.
     * @require .env file at ../../.env
     * @require MYSQL_ROOT_PASSWORD in .env file
     * @require MYSQL_DATABASE in .env file
     * @require MYSQL_USER in .env file
     * @require MYSQL_PASSWORD in .env file
     */
    private Connector() {
        try {
            Dotenv dotenv = Dotenv.load();
            String url = "jdbc:mysql://localhost:3306/" + dotenv.get("MYSQL_DATABASE");
            String user = dotenv.get("MYSQL_USER");
            String password = dotenv.get("MYSQL_PASSWORD");
            Connection connection = DriverManager.getConnection(url, user, password);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Connector getInstance() {
        if (instance == null) {
            instance = new Connector();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    public void resetConnection() {
        closeConnection();
        instance = new Connector();
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
