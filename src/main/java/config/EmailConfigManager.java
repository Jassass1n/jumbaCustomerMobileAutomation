package config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class EmailConfigManager {
    private static Properties properties = new Properties();

    static {
        loadProperties();
    }

    public static void loadProperties() {
        try (InputStream input = new FileInputStream("src/main/resources/email.properties")) {
            properties.load(input);
            System.out.println("✅ Reloaded email.properties.");
        } catch (IOException e) {
            System.err.println("❌ Failed to load email.properties: " + e.getMessage());
        }
    }

    public static String get(String key) {
        String value = properties.getProperty(key);
        System.out.println("🔍 config[" + key + "] = '" + value + "'");
        return value;
    }
}