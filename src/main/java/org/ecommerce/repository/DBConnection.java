package org.ecommerce.repository;

import org.ecommerce.util.EnvConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String host;
    private static final String port;
    private static final String dbName;
    private static final String user;
    private static final String password;
    private static final String url;
    private static final String urlNoDB;

    static {
        host     = EnvConfig.get(EnvConfig.ENV_DB_HOST,     "db.host",     "localhost");
        port     = EnvConfig.get(EnvConfig.ENV_DB_PORT,     "db.port",     "3306");
        dbName   = EnvConfig.get(EnvConfig.ENV_DB_NAME,     "db.name",     "ecommerce_db");
        user     = EnvConfig.get(EnvConfig.ENV_DB_USER,     "db.user",     "root");
        password = EnvConfig.get(EnvConfig.ENV_DB_PASSWORD, "db.password", "");

        String params = "useSSL=false&serverTimezone=UTC"
                      + "&allowPublicKeyRetrieval=true"
                      + "&useUnicode=true&characterEncoding=UTF-8";

        url     = "jdbc:mysql://" + host + ":" + port + "/" + dbName + "?" + params;
        urlNoDB = "jdbc:mysql://" + host + ":" + port + "/?" + params;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC driver not found", e);
        }
    }

    /** Standard connection — requires the database to already exist. */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    /**
     * Connection without a selected database.
     * Used by DatabaseInitializer to CREATE DATABASE IF NOT EXISTS.
     */
    public static Connection getConnectionWithoutDb() throws SQLException {
        return DriverManager.getConnection(urlNoDB, user, password);
    }

    public static String getDbName() {
        return dbName;
    }
}
