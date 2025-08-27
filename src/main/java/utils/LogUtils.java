package utils;

import io.qameta.allure.Allure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;

public class LogUtils {

    private final Logger logger;

    // Default static logger (optional fallback)
    private static final Logger staticLogger = LoggerFactory.getLogger("StaticLogger");

    public LogUtils(Class<?> clazz) {
        this.logger = LoggerFactory.getLogger(clazz);
    }

    // ========== 🔹 Instance Methods ==========
    public void infoInstance(String message) {
        logger.info(message);
        logAllureStep("ℹ️ " + message);
    }

    public void infoInstance(String message, Object... args) {
        String formatted = format(message, args);
        logger.info(formatted);
        logAllureStep("ℹ️ " + formatted);
    }

    public void warnInstance(String message) {
        logger.warn(message);
        logAllureStep("⚠️ " + message);
    }

    public void errorInstance(String message) {
        logger.error(message);
        logAllureStep("❌ " + message);
    }

    public void errorInstance(String message, Object... args) {
        String formatted = format(message, args);
        logger.error(formatted);
        logAllureStep("❌ " + formatted);
    }

    public void debugInstance(String message) {
        logger.debug(message);
    }

    public void stepInstance(String message) {
        logAllureStep("📌 " + message);
    }

    // ========== 🔹 Static Convenience Methods ==========
    public static void info(String message) {
        staticLogger.info(message);
        logAllureStep("ℹ️ " + message);
    }

    public static void info(String message, Object... args) {
        String formatted = formatStatic(message, args);
        staticLogger.info(formatted);
        logAllureStep("ℹ️ " + formatted);
    }

    public static void warn(String message) {
        staticLogger.warn(message);
        logAllureStep("⚠️ " + message);
    }

    public static void error(String message) {
        staticLogger.error(message);
        logAllureStep("❌ " + message);
    }

    public static void error(String message, Object... args) {
        String formatted = formatStatic(message, args);
        staticLogger.error(formatted);
        logAllureStep("❌ " + formatted);
    }

    public static void debug(String message) {
        staticLogger.debug(message);
    }

    public static void step(String message) {
        logAllureStep("📌 " + message);
    }

    // ========== 🔹 Formatter ==========
    private String format(String message, Object... args) {
        for (Object arg : args) {
            message = message.replaceFirst("\\{\\}", Matcher.quoteReplacement(String.valueOf(arg)));
        }
        return message;
    }

    private static String formatStatic(String message, Object... args) {
        for (Object arg : args) {
            message = message.replaceFirst("\\{\\}", Matcher.quoteReplacement(String.valueOf(arg)));
        }
        return message;
    }

    // ========== 🔹 Safe Allure Logging ==========
    private static void logAllureStep(String message) {
        try {
            Allure.step(message);
        } catch (IllegalStateException ignored) {
            // No test is currently running, ignore
        }
    }
}