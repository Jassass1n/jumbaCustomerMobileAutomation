package utils;

import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriverException;

public class DriverUtils {

    /**
     * Checks if the driver is invalid or its session is not active.
     *
     * @param driver the AppiumDriver instance
     * @return true if driver is null or session is invalid
     */
    public static boolean isDriverInvalid(AppiumDriver driver) {
        try {
            return driver == null || driver.getSessionId() == null;
        } catch (WebDriverException e) {
            System.err.println("⚠️ Driver session check failed: " + e.getMessage());
            return true;
        }
    }

    /**
     * Determines whether the current screen is the login screen
     * by checking for the presence of the "Continue as Guest" button.
     *
     * @param driver the AppiumDriver instance
     * @return true if the login element is visible
     */
    public static boolean isOnLoginScreen(AppiumDriver driver) {
        try {
            return driver.findElement(By.xpath("//*[@content-desc='Continue as Guest']")).isDisplayed();
        } catch (Exception e) {
            System.out.println("🔍 Not on login screen: " + e.getMessage());
            return false;
        }
    }
}