package org.ecommerce.repository;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {

    private static String url;
    private static String user;
    private static String password;

    static {
        try (InputStream in = DBConnection.class.getClassLoader()
                .getResourceAsStream("db.properties")) {
            if (in == null) throw new RuntimeException("db.properties not found in classpath");
            Properties props = new Properties();
            props.load(in);
            url      = props.getProperty("db.url");
            user     = props.getProperty("db.user");
            password = props.getProperty("db.password");
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Failed to initialize DBConnection: " + e.getMessage(), e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}
