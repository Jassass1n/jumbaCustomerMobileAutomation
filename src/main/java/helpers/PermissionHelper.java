package helpers;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileBy;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

public class PermissionHelper {
    private static final Logger logger = LoggerFactory.getLogger(PermissionHelper.class);
    private final AppiumDriver driver;

    private final List<By> allowLocators = Arrays.asList(
            By.id("com.android.permissioncontroller:id/permission_allow_button"),
            By.id("com.android.permissioncontroller:id/permission_allow_foreground_only_button"),
            By.id("com.android.permissioncontroller:id/permission_allow_media_button"),
            MobileBy.AndroidUIAutomator("new UiSelector().text(\"Allow\")"),
            MobileBy.AndroidUIAutomator("new UiSelector().text(\"While using the app\")"),
            MobileBy.AndroidUIAutomator("new UiSelector().text(\"Only this time\")")
    );

    public PermissionHelper(AppiumDriver driver) {
        this.driver = driver;
    }

    public void allowAllPermissionsIfPresent() {
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(2));
        logger.info("👀 Checking for permission dialogs...");

        try {
            for (int attempt = 0; attempt < 3; attempt++) {
                boolean found = false;

                for (By locator : allowLocators) {
                    List<WebElement> buttons = driver.findElements(locator);

                    if (!buttons.isEmpty()) {
                        WebElement btn = buttons.get(0);
                        try {
                            btn.click();
                            logger.info("✅ Clicked permission button: {}", locator);
                        } catch (Exception e) {
                            try {
                                String bounds = btn.getAttribute("bounds");
                                ElementHelper.tapElementByBounds(bounds);
                                logger.info("✅ Tapped permission button at bounds: {}", bounds);
                            } catch (Exception tapEx) {
                                logger.warn("❌ Failed to tap by bounds: {}", tapEx.getMessage());
                            }
                        }

                        ElementHelper.waitUntilVisible(locator, 2);
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    logger.info("ℹ️ No more permission dialogs after {} pass(es).", attempt + 1);
                    break;
                }
            }
        } catch (Exception e) {
            logger.error("⚠️ Permission handling failed: {}", e.getMessage(), e);
        } finally {
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        }
    }

    public boolean allowAllPermissionsIfPresentWithResult() {
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(2));
        logger.info("👀 Checking for permission dialogs...");

        try {
            for (int attempt = 0; attempt < 3; attempt++) {
                boolean found = false;

                for (By locator : allowLocators) {
                    List<WebElement> buttons = driver.findElements(locator);

                    if (!buttons.isEmpty()) {
                        WebElement btn = buttons.get(0);
                        try {
                            btn.click();
                            logger.info("✅ Clicked permission button: {}", locator);
                        } catch (Exception e) {
                            try {
                                String bounds = btn.getAttribute("bounds");
                                ElementHelper.tapElementByBounds(bounds);
                                logger.info("✅ Tapped permission button at bounds: {}", bounds);
                            } catch (Exception tapEx) {
                                logger.warn("❌ Failed to tap by bounds: {}", tapEx.getMessage());
                            }
                        }

                        ElementHelper.waitUntilVisible(locator, 2);
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    logger.info("ℹ️ No more permission dialogs after {} pass(es).", attempt + 1);
                    return attempt > 0; // Return true if at least one permission was handled
                }
            }
        } catch (Exception e) {
            logger.error("⚠️ Permission handling failed: {}", e.getMessage(), e);
        } finally {
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        }

        return false;
    }
    public static void waitUntilNoPermissionDialogs(AppiumDriver driver, int maxWaitSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(maxWaitSeconds));
        wait.until(d -> {
            boolean hasPermissionPopup = !d.findElements(By.id("com.android.permissioncontroller:id/permission_allow_button")).isEmpty();
            boolean hasToastDialog = !d.findElements(By.id("com.jumba.custmobile.dev:id/alertTitle")).isEmpty();
            return !hasPermissionPopup && !hasToastDialog;
        });
    }

}