package utils;

import io.appium.java_client.AppiumDriver;
import io.qameta.allure.Allure;
import io.cucumber.java.Scenario;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ScreenshotUtil {

    private static final Logger logger = LoggerFactory.getLogger(ScreenshotUtil.class);

    private static final String BASE_SCREENSHOT_DIR = "screenshots";
    private static final String DATE_FOLDER = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

    private ScreenshotUtil() {
        // Prevent instantiation
    }

    /**
     * Capture screenshot, save to disk under pass/ or fail/, attach to Allure and optionally to Cucumber.
     */
    public static void captureAndAttachScreenshot(AppiumDriver driver, Scenario scenario, String label, boolean isFailed) {
        if (!isDriverValid(driver)) {
            logger.error("❌ AppiumDriver is null or session is invalid. Screenshot not taken.");
            attachFallbackMessage(scenario, label);
            return;
        }

        try {
            byte[] screenshotBytes = captureScreenshotBytes(driver);
            if (screenshotBytes == null) {
                logger.error("❌ Screenshot bytes are null. Cannot proceed.");
                attachFallbackMessage(scenario, label);
                return;
            }

            String statusFolder = isFailed ? "fail" : "pass";
            String filePath = saveScreenshotToDisk(screenshotBytes, label, statusFolder);
            logger.info("📸 Screenshot saved: {}", filePath);

            attachToAllure(label, screenshotBytes);
            attachToCucumber(scenario, label, screenshotBytes);

        } catch (Exception e) {
            logger.error("⚠️ Screenshot capture failed: {}", e.getMessage());
            attachFallbackMessage(scenario, label);
        }
    }

    /**
     * Capture and attach screenshot to Allure and Cucumber without saving to file.
     */
    public static void captureAndAttachWithoutSaving(AppiumDriver driver, Scenario scenario, String label) {
        if (!isDriverValid(driver)) {
            logger.error("❌ AppiumDriver is null or session is invalid. Screenshot not taken.");
            attachFallbackMessage(scenario, label);
            return;
        }

        try {
            byte[] screenshotBytes = captureScreenshotBytes(driver);
            if (screenshotBytes == null) {
                logger.error("❌ Screenshot bytes are null. Cannot proceed.");
                attachFallbackMessage(scenario, label);
                return;
            }

            attachToAllure(label, screenshotBytes);
            attachToCucumber(scenario, label, screenshotBytes);

        } catch (Exception e) {
            logger.error("⚠️ Screenshot capture failed: {}", e.getMessage());
            attachFallbackMessage(scenario, label);
        }
    }

    private static boolean isDriverValid(AppiumDriver driver) {
        return driver != null && driver.getSessionId() != null;
    }

    private static byte[] captureScreenshotBytes(AppiumDriver driver) {
        try {
            File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            return FileUtils.readFileToByteArray(srcFile);
        } catch (IOException e) {
            logger.error("❌ Failed to read screenshot bytes: {}", e.getMessage());
            return null;
        } catch (Exception e) {
            logger.error("❌ Unexpected error while capturing screenshot: {}", e.getMessage());
            return null;
        }
    }

    private static String saveScreenshotToDisk(byte[] bytes, String label, String statusFolder) throws IOException {
        String sanitizedLabel = label.replaceAll("[^a-zA-Z0-9\\-_]", "_");
        File folder = new File(BASE_SCREENSHOT_DIR + File.separator + DATE_FOLDER + File.separator + statusFolder);
        if (!folder.exists() && !folder.mkdirs()) {
            throw new IOException("Failed to create directory: " + folder.getAbsolutePath());
        }

        File file = new File(folder, sanitizedLabel + ".png");
        FileUtils.writeByteArrayToFile(file, bytes);
        return file.getAbsolutePath();
    }

    private static void attachToAllure(String label, byte[] bytes) {
        try {
            Allure.addAttachment(label, new ByteArrayInputStream(bytes));
            logger.info("📎 Screenshot attached to Allure report");
        } catch (Exception e) {
            logger.warn("⚠️ Failed to attach screenshot to Allure: {}", e.getMessage());
        }
    }

    private static void attachToCucumber(Scenario scenario, String label, byte[] bytes) {
        if (scenario != null) {
            try {
                scenario.attach(bytes, "image/png", label);
                logger.info("📎 Screenshot attached to Cucumber scenario");
            } catch (Exception e) {
                logger.warn("⚠️ Failed to attach screenshot to Cucumber: {}", e.getMessage());
            }
        }
    }

    private static void attachFallbackMessage(Scenario scenario, String label) {
        if (scenario != null) {
            String message = "⚠️ Screenshot for '" + label + "' could not be captured due to driver issues.";
            scenario.attach(message.getBytes(), "text/plain", label + "_screenshot_failed");
            logger.warn(message);
        }
    }
}