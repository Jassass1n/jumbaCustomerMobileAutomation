package config;

import helpers.LoggerHelper;
import io.qameta.allure.Allure;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigurationManager {

    private static final String CONFIG_FILE = "config.properties";
    private static final Properties properties = new Properties();
    private static final Logger logger = LoggerHelper.getLogger(ConfigurationManager.class);
    private static volatile ConfigurationManager instance;

    static {
        try {
            InputStream input = ConfigurationManager.class.getClassLoader().getResourceAsStream(CONFIG_FILE);
            logger.info("Attempting to load configuration file: {}", CONFIG_FILE);

            if (input == null) {
                File fallback = new File("src/test/resources/" + CONFIG_FILE);
                if (!fallback.exists()) {
                    String errorMsg = CONFIG_FILE + " not found in classpath or fallback path.";
                    logger.error(errorMsg);
                    Allure.addAttachment("❌ Config Load Failure", errorMsg);
                    throw new RuntimeException(errorMsg);
                }
                input = new FileInputStream(fallback);
                logger.info("Loaded config from fallback path: {}", fallback.getAbsolutePath());
            } else {
                logger.info("Loaded config from classpath.");
            }

            properties.load(input);
            String successMsg = "Configuration loaded with " + properties.size() + " entries.";
            logger.info(successMsg);
            Allure.addAttachment("✅ Config Loaded", successMsg);

        } catch (IOException e) {
            String errorMsg = "Failed to load " + CONFIG_FILE + ": " + e.getMessage();
            logger.error(errorMsg, e);
            Allure.addAttachment("❌ Config Load Exception", errorMsg);
            throw new RuntimeException(errorMsg, e);
        }
    }

    public static ConfigurationManager getInstance() {
        if (instance == null) {
            synchronized (ConfigurationManager.class) {
                if (instance == null) {
                    instance = new ConfigurationManager();
                }
            }
        }
        return instance;
    }

    public static String get(String key) {
        return getProperty(key);
    }

    public static String getProperty(String key) {
        String value = properties.getProperty(key);
        if (value == null || value.trim().isEmpty()) {
            String warnMsg = "⚠️ Property missing or empty: " + key;
            logger.warn(warnMsg);
            Allure.addAttachment("⚠️ Missing Config Key", key);
            return null;
        }
        return value.trim();
    }

    public static String getProperty(String key, String defaultValue) {
        String value = getProperty(key);
        return (value == null || value.isEmpty()) ? defaultValue : value;
    }

    public static String requireProperty(String key) {
        String value = getProperty(key);
        if (value == null) {
            String errorMsg = "❌ Required config key missing: " + key;
            logger.error(errorMsg);
            Allure.addAttachment("❌ Missing Required Config Key", key);
            throw new IllegalStateException(errorMsg);
        }
        return value;
    }

    public static boolean getBoolean(String key) {
        return Boolean.parseBoolean(getProperty(key));
    }

    public static int getInt(String key) {
        try {
            return Integer.parseInt(getProperty(key));
        } catch (NumberFormatException e) {
            String errorMsg = "❌ Invalid integer for key: " + key;
            logger.error(errorMsg, e);
            Allure.addAttachment("❌ Config Integer Parse Error", errorMsg);
            throw new IllegalArgumentException(errorMsg, e);
        }
    }

    public static String getQuantity() {
        return getProperty("product.quantity", "100");
    }

    public static String getPaymentReferenceNumber() {
        return getProperty("payment.refNumber", "0000");
    }

    public static String getBankTransferText() {
        return getProperty("bankTransferSection.text", "Bank Transfer");
    }

    public static String getHeadsUpToastId() {
        return getProperty("headsUpNotification.id");
    }

    public static String getHeadsUpToastOkBtnId() {
        return getProperty("headsUpNotificationOkBtn.id");
    }

    public static void debugPrintAllProperties() {
        logger.info("All loaded config properties:");
        StringBuilder sb = new StringBuilder();
        properties.forEach((k, v) -> {
            String line = String.format(" - %s = %s%n", k, v);
            sb.append(line);
            logger.info(line.trim());
        });
        Allure.addAttachment("🔍 All Config Properties", sb.toString());
    }
}