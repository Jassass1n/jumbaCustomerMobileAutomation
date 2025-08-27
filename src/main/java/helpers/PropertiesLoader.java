package helpers;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PropertiesLoader {
    private static final Logger logger = LoggerFactory.getLogger(PermissionHelper.class);
    public static Properties loadProperties(String filePath) {
        Properties properties = new Properties();
        try (FileInputStream configFile = new FileInputStream(filePath)) {
            properties.load(configFile);
        } catch (IOException e) {
            logger.error("Error loading properties from " + filePath + ": " + e.getMessage());
        }
        return properties;
    }
}
