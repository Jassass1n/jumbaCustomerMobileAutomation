package helpers;

import config.ConfigurationManager;
import drivers.DriverManager;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.SessionManager;

public class AppStateDetector {
    private static final Logger logger = LoggerFactory.getLogger(AppStateDetector.class);
    private static final AppiumDriver driver = DriverManager.getDriver();

    public enum AppState {
        NOTIFICATION_POPUP,
        LOGIN_SCREEN,
        OTP_SCREEN,
        HOME_SCREEN_GUEST,
        HOME_SCREEN_OTP,
        UNKNOWN
    }

    public static AppState detectAppState() {
        logger.info("🔍 Detecting app state...");

        try {
            if (ElementHelper.isElementPresent("allowNotificationsBtn.id")) {
                logger.info("📍 Detected: Notification popup");
                return AppState.NOTIFICATION_POPUP;
            }

            By guestButton = ElementHelper.getLocator("continueAsGuest.accessibility");
            By selfCollectButton = ElementHelper.getLocator("selfCollectBtn.text");

            if (ElementHelper.waitUntilVisible(guestButton, 2) != null) {
                logger.info("📍 Detected: Login screen");
                return AppState.LOGIN_SCREEN;
            }

            if (ElementHelper.isElementPresent("verifyOtpTitle.text")) {
                logger.info("📍 Detected: OTP screen");
                return AppState.OTP_SCREEN;
            }

            if (ElementHelper.waitUntilVisible(selfCollectButton, 2) != null) {
                boolean hasOtpOnlyElements =
                        ElementHelper.isElementPresent("orderTrackingTitle.text") ||
                                ElementHelper.isElementPresent("activeOrderBtn.text");

                logger.info("🧠 [State Detection] Visible: selfCollectBtn");
                logger.info("🧠 [State Detection] OTP-only elements present: {}", hasOtpOnlyElements);

                if (hasOtpOnlyElements) {
                    logger.info("📍 Detected: Home screen (OTP logged in)");
                    return AppState.HOME_SCREEN_OTP;
                } else {
                    logger.info("📍 Detected: Home screen (Guest)");
                    return AppState.HOME_SCREEN_GUEST;
                }
            }
        } catch (Exception e) {
            logger.warn("⚠️ Error while detecting app state: {}", e.getMessage(), e);
        }

        logger.warn("❓ App state unknown");
        return AppState.UNKNOWN;
    }
}