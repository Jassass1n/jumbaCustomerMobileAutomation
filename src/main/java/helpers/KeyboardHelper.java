package helpers;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KeyboardHelper {
    private static final Logger logger = LoggerFactory.getLogger(ElementHelper.class);
    public static void hideKeyboard(AppiumDriver driver) {
        try {
            if (driver instanceof AndroidDriver) {
                ((AndroidDriver) driver).hideKeyboard();
            } else if (driver instanceof IOSDriver) {
                ((IOSDriver) driver).hideKeyboard();
            }
        } catch (Exception e) {
            logger.error("⚠️ Could not hide keyboard: " + e.getMessage());
        }
    }
}
