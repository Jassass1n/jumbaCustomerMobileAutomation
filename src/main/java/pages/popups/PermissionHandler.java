package pages.popups;

import config.ConfigurationManager;
import helpers.AndroidPermissionHelper;
import helpers.ElementHelper;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileBy;
import io.appium.java_client.remote.SupportsContextSwitching;
import io.qameta.allure.Allure;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.Duration;

public class PermissionHandler {

    private static final Logger logger = LoggerFactory.getLogger(PermissionHandler.class);
    private final By guestButton = MobileBy.AccessibilityId(ConfigurationManager.get("continueAsGuest.accessibility"));
    private final AppiumDriver driver;

    public PermissionHandler(AppiumDriver driver) {
        this.driver = driver;
    }

    public void handleStartupPermissions() {
        switchToNativeContext();

        if (isLoginPageVisible()) {
            logger.info("ℹ️ Login page already visible. Skipping permission handling.");
            Allure.step("ℹ️ Login page already visible. Skipping permission handling.");
            return;
        }

        boolean locationHandled = handleLocationPermission();
        boolean additionalPermissionHandled = new AndroidPermissionHelper(driver).acceptIfPresent();

        if (locationHandled || additionalPermissionHandled) {
            waitForLoginPage();
        } else {
            logger.info("ℹ️ No permission popups appeared.");
            Allure.step("ℹ️ No permission popups appeared.");
        }
    }

    private boolean handleLocationPermission() {
        By locationAllowBtn = getLocatorFromConfig("giveLocationAccessBtn.id");

        if (ElementHelper.isElementPresent(locationAllowBtn, 2)) {
            driver.findElement(locationAllowBtn).click();
            logger.info("✅ Location permission allowed.");
            Allure.step("✅ Location permission allowed.");
            return true;
        }

        return false;
    }

    private boolean isLoginPageVisible() {
        return ElementHelper.isElementPresent(guestButton, 2);
    }

    private void waitForLoginPage() {
        try {
            ElementHelper.waitUntilVisible(guestButton, 6);
            logger.info("✅ Login page detected after permissions.");
            Allure.step("✅ Login page detected after permissions.");
        } catch (TimeoutException e) {
            logger.warn("⚠️ Login page not detected after permission handling: {}", e.getMessage());
            Allure.step("⚠️ Login page not detected after permission handling: " + e.getMessage());
        }
    }

    private void switchToNativeContext() {
        if (driver instanceof SupportsContextSwitching) {
            ((SupportsContextSwitching) driver).context("NATIVE_APP");
            logger.debug("🔄 Switched to NATIVE_APP context.");
        }
    }


    private By getLocatorFromConfig(String key) {
        String id = ConfigurationManager.getProperty(key);
        return MobileBy.id(id);
    }
}