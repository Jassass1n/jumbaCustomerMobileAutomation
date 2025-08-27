package pages.components;

import config.ConfigurationManager;
import helpers.ElementHelper;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileBy;
import io.qameta.allure.Allure;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pages.BasePage;

import java.util.List;

public class PickupLocationPage extends BasePage {

    private static final Logger logger = LoggerFactory.getLogger(PickupLocationPage.class);
    private final AppiumDriver driver;

    private final By pickupDropdown = MobileBy.AccessibilityId(ConfigurationManager.get("pickup.dropdown.accessibilityId"));
    private final By addToCartBtn = MobileBy.AccessibilityId(ConfigurationManager.get("addToCart.button.accessibilityId"));
    private final By selectLocationLabel = MobileBy.AccessibilityId(ConfigurationManager.get("pickup.dropdown.accessibilityId"));
    private final By pickupErrorText = MobileBy.AndroidUIAutomator("new UiSelector().textContains(\"Please select your pickup location\")");

    public PickupLocationPage(AppiumDriver driver) {
        super(driver);
        this.driver = driver;
    }

    public void selectValidPickupLocation() {
        logger.info("📍 Selecting pickup location via dropdown + screen-relative tap");
        Allure.step("Select pickup location");

        openDropdownAndTap();

        logger.info("🛒 Clicking Add to cart");
        ElementHelper.clickElement(addToCartBtn);
        ElementHelper.delay(1000);

        if (isPickupErrorVisible()) {
            logger.warn("⚠️ Pickup error detected, retrying dropdown selection");
            openDropdownAndTap();
            ElementHelper.clickElement(addToCartBtn);
            ElementHelper.delay(1000);
        }

        if (isPickupStillUnselected()) {
            String errorMsg = "❌ Pickup location was not selected. 'Select a location' still visible.";
            logger.error(errorMsg);
            Allure.addAttachment("Pickup Location Error", errorMsg);
            throw new RuntimeException(errorMsg);
        }

        Allure.step("✅ Pickup location selected successfully");
        logger.info("✅ Pickup location selection complete");
    }

    private void openDropdownAndTap() {
        ElementHelper.clickElement(pickupDropdown);
        ElementHelper.delay(500);
        ElementHelper.tapBelowElement(pickupDropdown, 100); // Adjust offset as needed
    }

    private boolean isPickupErrorVisible() {
        List<WebElement> errors = driver.findElements(pickupErrorText);
        return !errors.isEmpty() && errors.get(0).isDisplayed();
    }

    private boolean isPickupStillUnselected() {
        try {
            return driver.findElement(selectLocationLabel).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}