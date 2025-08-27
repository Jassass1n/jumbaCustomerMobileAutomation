package helpers;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileBy;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import helpers.ElementHelper;

public class GuestLoginHelper {

    private final AppiumDriver driver;

    private static final Logger logger = LoggerFactory.getLogger(ElementHelper.class);
    private final By allowNotificationBtn = MobileBy.id("com.android.permissioncontroller:id/permission_allow_button");
    private final By continueAsGuestBtn = MobileBy.AccessibilityId("Continue as Guest");

    public GuestLoginHelper(AppiumDriver driver) {
        this.driver = driver;
    }

    public static void loginAsGuest(AppiumDriver driver) {
        new GuestLoginHelper(driver).performGuestLogin();
    }

    public void performGuestLogin() {
        try {
            // 🔔 Handle notification permission if shown
            if (ElementHelper.isElementPresent(allowNotificationBtn, 5)) {
                logger.info("ℹ️ Notification permission popup shown.");
                ElementHelper.clickElement(allowNotificationBtn);
            } else {
                System.out.println("ℹ️ Notification permission popup not shown.");
            }

            // 👤 Tap 'Continue as Guest'
            logger.info("👤 Tapping 'Continue as Guest'");
            ElementHelper.clickElement(continueAsGuestBtn);

        } catch (NoSuchElementException e) {
            throw new RuntimeException("Guest login failed: Element not found", e);
        } catch (Exception e) {
            throw new RuntimeException("Guest login encountered an error", e);
        }
    }
}