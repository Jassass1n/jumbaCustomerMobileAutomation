package utils;

import helpers.ElementHelper;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileBy;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

public class AndroidUtils {
    private static final Logger logger = LoggerFactory.getLogger(AndroidUtils.class);

    public static boolean selectImageWithFallback(AppiumDriver driver) {
        try {
            List<WebElement> thumbnails = driver.findElements(By.xpath(
                    "//android.widget.ImageView[@resource-id='com.android.documentsui:id/icon_thumb']"));

            for (WebElement img : thumbnails) {
                if (img.isDisplayed()) {
                    img.click();
                    logger.info("✅ Clicked visible thumbnail image.");
                    return true;
                }
            }

            logger.warn("⚠️ No thumbnails found. Trying fallback...");

            // Fallback: Try tapping any large ImageView
            List<WebElement> candidates = driver.findElements(By.className("android.widget.ImageView"));
            for (WebElement el : candidates) {
                if (el.isDisplayed() && el.getSize().getHeight() > 100 && el.getSize().getWidth() > 100) {
                    el.click();
                    logger.info("✅ Clicked fallback ImageView (size-based).");
                    return true;
                }
            }

            logger.error("❌ No image found using fallback strategy.");
            return false;

        } catch (Exception e) {
            logger.error("❌ Error selecting image: {}", e.getMessage());
            return false;
        }
    }

    public static void openAndroidFolder(AppiumDriver driver, String folderName, int waitSeconds) {
        try {
            List<By> tabs = List.of(
                    MobileBy.xpath("//android.widget.TextView[@text='Albums']"),
                    MobileBy.xpath("//android.widget.TextView[@text='Collections']")
            );
            for (By tab : tabs) {
                if (!driver.findElements(tab).isEmpty()) {
                    driver.findElement(tab).click();
                    logger.info("📁 Switched to folder tab: {}", tab);
                    break;
                }
            }

            By showRoots = MobileBy.AccessibilityId("Show roots");
            if (!driver.findElements(showRoots).isEmpty()) {
                driver.findElement(showRoots).click();
                logger.info("☰ Clicked 'Show roots'");
            }

            String lower = folderName.toLowerCase();
            List<By> folderLocators = List.of(
                    MobileBy.xpath("//*[contains(translate(@text, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '" + lower + "')]"),
                    MobileBy.AccessibilityId(folderName)
            );

            for (By locator : folderLocators) {
                if (!driver.findElements(locator).isEmpty()) {
                    driver.findElement(locator).click();
                    logger.info("📂 Opened folder: {}", folderName);
                    return;
                }
            }

            logger.error("❌ Folder not found: {}", folderName);
            throw new RuntimeException("Folder not found: " + folderName);
        } catch (Exception e) {
            logger.error("❌ Failed to open folder: {} → {}", folderName, e.getMessage());
            throw new RuntimeException("Failed to open folder: " + e.getMessage(), e);
        }
    }

    public static void scrollToText(AndroidDriver driver, String text) {
        String uiSelector = "new UiScrollable(new UiSelector().scrollable(true)).scrollIntoView(new UiSelector().textContains(\"" + text + "\"))";
        try {
            driver.findElement(MobileBy.AndroidUIAutomator(uiSelector));
            logger.info("✅ Scrolled to element with text: {}", text);
        } catch (Exception e) {
            logger.warn("❌ Failed to scroll to element with text '{}': {}", text, e.getMessage());
        }
    }

    public static void swipeUp(AndroidDriver driver) {
        Dimension size = driver.manage().window().getSize();
        int startX = size.width / 2;
        int startY = (int) (size.height * 0.8);
        int endY = (int) (size.height * 0.3);

        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence swipe = new Sequence(finger, 1);

        swipe.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), startX, startY));
        swipe.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        swipe.addAction(finger.createPointerMove(Duration.ofMillis(500), PointerInput.Origin.viewport(), startX, endY));
        swipe.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        driver.perform(Collections.singletonList(swipe));
    }

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("⚠️ Sleep interrupted: " + e.getMessage());
        }
    }

    public static void pressKeyWithRetry(AndroidDriver driver, AndroidKey key, int retries, long intervalMs) {
        for (int i = 0; i < retries; i++) {
            try {
                driver.pressKey(new KeyEvent(key));
                Thread.sleep(intervalMs);
                logger.info("✅ Pressed key: {}", key.name());
                return;
            } catch (Exception e) {
                if (i == retries - 1) {
                    logger.error("❌ Key press failed for: {} → {}", key.name(), e.getMessage());
                    throw new RuntimeException("Key press failed for: " + key.name(), e);
                }
            }
        }
    }
}