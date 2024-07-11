package ua.edu.dnu.booksales.dao;

import ua.edu.dnu.booksales.util.PropertiesManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Service {
    public static Connection getConnection() throws SQLException {
        Properties props = new Properties();
        PropertiesManager propertiesManager = new PropertiesManager("db.properties");
        propertiesManager.load(props);

        System.setProperty("driver", props.getProperty("driver"));
        try {
            Class.forName(props.getProperty("driver"));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException();
        }
        String url = props.getProperty("url");
        String username = props.getProperty("username");
        String password = props.getProperty("password");
        return DriverManager.getConnection(url, username, password);
    }
}
