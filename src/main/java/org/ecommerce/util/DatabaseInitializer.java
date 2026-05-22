package org.ecommerce.util;

import org.ecommerce.repository.DBConnection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Initializes the database schema on application startup.
 * Reads sql/schema.sql from the classpath and executes it against MySQL.
 * Safe to run on every startup — all statements are idempotent
 * (CREATE DATABASE IF NOT EXISTS, CREATE TABLE IF NOT EXISTS, INSERT IGNORE).
 */
public class DatabaseInitializer {

    private static final Logger LOG = Logger.getLogger(DatabaseInitializer.class.getName());

    public static void initialize() {
        LOG.info("[FashionHub] Starting database initialization...");
        try {
            String sql = loadSqlFile();
            List<String> statements = parseSqlStatements(sql);
            executeStatements(statements);
            LOG.info("[FashionHub] Database initialization complete.");
        } catch (Exception e) {
            LOG.log(Level.SEVERE,
                "[FashionHub] Database initialization FAILED — check your DB credentials in .env or db.properties.", e);
        }
    }

    private static String loadSqlFile() throws IOException {
        InputStream in = DatabaseInitializer.class.getClassLoader()
                             .getResourceAsStream("sql/schema.sql");
        if (in == null) throw new IOException("sql/schema.sql not found on classpath");

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(in, StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                // Strip single-line comments
                int commentIdx = line.indexOf("--");
                if (commentIdx >= 0) line = line.substring(0, commentIdx);
                sb.append(line).append('\n');
            }
            return sb.toString();
        }
    }

    private static List<String> parseSqlStatements(String sql) {
        // Remove block comments /* ... */
        sql = sql.replaceAll("/\\*[\\s\\S]*?\\*/", " ");

        List<String> result = new ArrayList<>();
        for (String stmt : sql.split(";")) {
            stmt = stmt.trim();
            if (stmt.isEmpty()) continue;
            // Skip USE statements — catalog switching is handled in code
            if (stmt.toUpperCase().startsWith("USE ")) continue;
            result.add(stmt);
        }
        return result;
    }

    private static void executeStatements(List<String> statements) throws SQLException {
        String dbName = DBConnection.getDbName();

        try (Connection con = DBConnection.getConnectionWithoutDb()) {
            con.setAutoCommit(true);

            for (String stmt : statements) {
                String upper = stmt.toUpperCase().trim();

                if (upper.startsWith("CREATE DATABASE")) {
                    // Execute CREATE DATABASE on the no-DB connection, then switch catalog
                    try (Statement s = con.createStatement()) {
                        s.execute(stmt);
                        LOG.info("[FashionHub] Database ensured: " + dbName);
                    }
                    con.setCatalog(dbName);
                    continue;
                }

                try (Statement s = con.createStatement()) {
                    s.execute(stmt);
                    String preview = stmt.replace('\n', ' ').trim();
                    if (preview.length() > 80) preview = preview.substring(0, 80) + "...";
                    LOG.fine("[FashionHub] Executed: " + preview);
                } catch (SQLException e) {
                    LOG.log(Level.WARNING, "[FashionHub] Statement skipped (" + e.getMessage() + "): "
                        + stmt.replace('\n', ' ').trim().substring(0, Math.min(60, stmt.length())));
                }
            }
        }
    }
}
