package pages.popups;

import config.ConfigurationManager;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileBy;
import io.appium.java_client.remote.SupportsContextSwitching;
import io.qameta.allure.Allure;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

public class NotificationPopupPage {
    private static final Logger logger = LoggerFactory.getLogger(NotificationPopupPage.class);

    private final AppiumDriver driver;
    private final WebDriverWait shortWait;

    public NotificationPopupPage(AppiumDriver driver) {
        this.driver = driver;
        this.shortWait = new WebDriverWait(driver, Duration.ofSeconds(3));
    }

    public boolean handleNotificationIfPresent() {
        long start = System.currentTimeMillis();
        long maxDurationMs = 4000;

        try {
            switchToNativeContext();

            List<By> allowButtons = Arrays.asList(
                    getOptionalLocator("allowNotificationsBtn.id"),
                    By.id("com.android.permissioncontroller:id/permission_allow_button"),
                    By.id("com.android.permissioncontroller:id/permission_allow_foreground_only_button"),
                    MobileBy.AndroidUIAutomator("new UiSelector().text(\"Allow\")"),
                    MobileBy.AndroidUIAutomator("new UiSelector().text(\"Allow once\")"),
                    MobileBy.AndroidUIAutomator("new UiSelector().text(\"While using the app\")")
            );

            for (By locator : allowButtons) {
                if (locator == null) continue;

                if (System.currentTimeMillis() - start > maxDurationMs) {
                    logger.warn("⏱️ Time budget exceeded, skipping remaining locators.");
                    Allure.step("⏱️ Time budget exceeded.");
                    break;
                }

                Wait<AppiumDriver> fastWait = new FluentWait<>(driver)
                        .withTimeout(Duration.ofMillis(500))
                        .pollingEvery(Duration.ofMillis(100))
                        .ignoring(NoSuchElementException.class);

                try {
                    WebElement allowBtn = fastWait.until(ExpectedConditions.elementToBeClickable(locator));
                    allowBtn.click();
                    logger.info("✅ Clicked allow notification button via: {}", locator);
                    Allure.step("✅ Notification allowed via: " + locator);

                    waitForMainAppElement();
                    logger.info("⏱️ Notification handled in {} ms", System.currentTimeMillis() - start);
                    return true;

                } catch (TimeoutException ignored) {
                    // Continue to next locator
                }
            }

            logger.info("ℹ️ No notification popup appeared.");
            Allure.step("ℹ️ No notification popup appeared.");
            return false;

        } catch (Exception e) {
            logger.error("❌ Failed to handle notification", e);
            Allure.step("❌ Failed to handle notification: " + e.getMessage());
            return false;
        }
    }

    public void waitForMainAppElement() {
        try {
            switchToNativeContext();

            By loginPageIndicator = getOptionalLocator("continueAsGuest.accessibility");
            if (driver.findElements(loginPageIndicator).size() > 0) {
                logger.info("✅ Login page element already visible, skipping wait.");
                return;
            }

            new WebDriverWait(driver, Duration.ofSeconds(3))
                    .until(ExpectedConditions.presenceOfElementLocated(loginPageIndicator));

            logger.info("✅ Login page confirmed after notification.");

        } catch (TimeoutException e) {
            logger.warn("⚠️ Login page didn't load in time.");
        } catch (Exception e) {
            logger.error("❌ Error while waiting for login page", e);
        }
    }

    private void switchToNativeContext() {
        if (driver instanceof SupportsContextSwitching) {
            ((SupportsContextSwitching) driver).context("NATIVE_APP");
            logger.debug("🔄 Switched to NATIVE_APP context");
        }
    }

    private By getOptionalLocator(String key) {
        try {
            String locatorId = ConfigurationManager.getProperty(key);
            return (locatorId != null && !locatorId.isEmpty()) ? MobileBy.id(locatorId) : null;
        } catch (Exception ignored) {
            return null;
        }
    }
}