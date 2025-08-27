package utils;

import io.appium.java_client.AppiumDriver;

/**
 * Utility class for handling various in-app popups.
 */
public class PopupHandler {

    private final AppiumDriver driver;

    public PopupHandler(AppiumDriver driver) {
        this.driver = driver;
    }

    /**
     * Placeholder for handling any future in-app popups (e.g., discount banners).
     */
    public void handleDiscountPopupIfPresent() {
        // Add logic for handling in-app popups like discount banners here.
        System.out.println("ℹ️ No popup handling required at this time.");
    }
}