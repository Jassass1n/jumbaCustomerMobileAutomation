package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {

    private static Properties properties = new Properties();

    static {
        loadProperties();
    }

    private static void loadProperties() {
        String relativePath = "src/main/resources/config.properties";  // Update path to main/resources
        try (FileInputStream input = new FileInputStream(relativePath)) {
            properties.load(input);
            System.out.println("Loaded config.properties from relative path: " + relativePath);
        } catch (IOException e) {
            System.err.println("Failed to load config.properties from relative path: " + relativePath);
            throw new RuntimeException("Failed to load config.properties file.");
        }
    }

    public static String getProperty(String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            System.err.println("Warning: Property key '" + key + "' not found in config.properties");
        }
        return value;
    }

    // Alias for easier access
    public static String get(String key) {
        return getProperty(key);
    }

    // Reload properties at runtime if needed
    public static void reload() {
        loadProperties();
    }

    /**
     * Returns the absolute path by resolving the relative path from project root.
     * If the path in config is already absolute, returns it as is.
     *
     * @param key property key that holds the file path (relative or absolute)
     * @return absolute file path as String
     */
    public static String getAbsolutePath(String key) {
        String path = get(key);
        if (path == null) {
            throw new RuntimeException("Property key '" + key + "' not found in config.properties");
        }
        File file = new File(path);
        if (file.isAbsolute()) {
            return path;  // Already absolute path
        } else {
            // Resolve relative to project root directory
            String projectRoot = System.getProperty("user.dir");
            return new File(projectRoot, path).getAbsolutePath();
        }
    }
}