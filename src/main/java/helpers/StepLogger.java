package helpers;

import io.qameta.allure.Allure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StepLogger {
    private static final Logger logger = LoggerFactory.getLogger(StepLogger.class);

    public static void step(String message) {
        logger.info(message);
        try {
            if (Allure.getLifecycle().getCurrentTestCase().isPresent()) {
                Allure.step(message);
            }
        } catch (Exception ignored) {}
    }

    public static void warn(String message) {
        logger.warn(message);
        try {
            if (Allure.getLifecycle().getCurrentTestCase().isPresent()) {
                Allure.step("⚠️ " + message);
            }
        } catch (Exception ignored) {}
    }

    public static void error(String message) {
        logger.error(message);
        try {
            if (Allure.getLifecycle().getCurrentTestCase().isPresent()) {
                Allure.step("❌ " + message);
            }
        } catch (Exception ignored) {}
    }
}