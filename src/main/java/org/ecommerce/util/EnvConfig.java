package org.ecommerce.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Centralized configuration resolver.
 * Priority: system environment variables > .env file > db.properties defaults.
 */
public class EnvConfig {

    public static final String ENV_DB_HOST     = "DB_HOST";
    public static final String ENV_DB_PORT     = "DB_PORT";
    public static final String ENV_DB_NAME     = "DB_NAME";
    public static final String ENV_DB_USER     = "DB_USER";
    public static final String ENV_DB_PASSWORD = "DB_PASSWORD";
    public static final String ENV_JWT_SECRET  = "JWT_SECRET";
    public static final String ENV_MAIL_USER   = "MAIL_USER";
    public static final String ENV_MAIL_PASS   = "MAIL_PASSWORD";

    private static final Properties fileProps = new Properties();
    private static final Map<String, String> dotEnv = new HashMap<>();

    static {
        // 1. Load db.properties as baseline
        try (InputStream in = EnvConfig.class.getClassLoader()
                                             .getResourceAsStream("db.properties")) {
            if (in != null) fileProps.load(in);
        } catch (IOException ignored) {}

        // 2. Load .env file (optional — never fail if absent)
        loadDotEnv();
    }

    /**
     * Resolve a config value: system env var > .env file > db.properties > hardcoded fallback.
     */
    public static String get(String envKey, String propKey, String fallback) {
        // 1. System environment variable
        String val = System.getenv(envKey);
        if (val != null && !val.isBlank()) return val.trim();

        // 2. .env file
        val = dotEnv.get(envKey);
        if (val != null && !val.isBlank()) return val.trim();

        // 3. db.properties
        val = fileProps.getProperty(propKey);
        if (val != null && !val.isBlank()) return val.trim();

        // 4. hardcoded fallback
        return fallback;
    }

    private static void loadDotEnv() {
        String[] candidates = {
            System.getProperty("user.dir"),
            System.getProperty("catalina.home"),
            System.getProperty("catalina.base")
        };
        for (String dir : candidates) {
            if (dir == null) continue;
            Path envFile = Paths.get(dir, ".env");
            if (Files.exists(envFile)) {
                parseDotEnvFile(envFile);
                return;
            }
        }
    }

    private static void parseDotEnvFile(Path file) {
        try (BufferedReader reader = Files.newBufferedReader(file)) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                if (line.startsWith("export ")) line = line.substring(7).trim();

                int eq = line.indexOf('=');
                if (eq < 1) continue;

                String key   = line.substring(0, eq).trim();
                String value = line.substring(eq + 1).trim();

                // Strip surrounding quotes
                if (value.length() >= 2 &&
                    ((value.startsWith("\"") && value.endsWith("\"")) ||
                     (value.startsWith("'")  && value.endsWith("'")))) {
                    value = value.substring(1, value.length() - 1);
                }

                // Strip inline comments (unquoted # preceded by space)
                int commentIdx = value.indexOf(" #");
                if (commentIdx >= 0) value = value.substring(0, commentIdx).trim();

                if (!key.isEmpty()) dotEnv.put(key, value);
            }
        } catch (IOException ignored) {}
    }
}
