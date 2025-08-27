package helpers;

import helpers.ElementHelper;
import helpers.LocatorHelper;
import io.qameta.allure.Allure;
import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CartHelper {

    private static final Logger logger = LoggerFactory.getLogger(CartHelper.class);

    public static void clearCartItems() {
        By deleteBtnLocator = LocatorHelper.resolveLocator("deleteItemBtn.automator");
        By emptyCartTextLocator = LocatorHelper.resolveLocator("cartDescription.text");

        logger.info("🧹 Starting to clear cart items...");
        Allure.step("🧹 Starting to clear cart items...");

        int attempts = 0;
        int maxAttempts = 10;

        while (attempts < maxAttempts) {
            attempts++;

            // Stop if cart is already empty
            if (ElementHelper.isElementDisplayed(emptyCartTextLocator, 2)) {
                logger.info("🛒 Cart is empty — no more items to delete.");
                Allure.step("🛒 Cart is empty.");
                break;
            }

            // If delete button is found, click it
            if (ElementHelper.isElementPresent(deleteBtnLocator, 3)) {
                logger.info("❌ Deleting item #{}", attempts);
                Allure.step("❌ Deleting item #" + attempts);
                ElementHelper.clickElement(deleteBtnLocator);

                // Optional: small wait to let the cart update
                ElementHelper.delay(500);
            } else {
                logger.warn("⚠️ No delete button found on attempt #{}", attempts);
                break;
            }
        }

        if (!ElementHelper.isElementDisplayed(emptyCartTextLocator, 3)) {
            logger.warn("❌ Cart may still have items after {} attempts", attempts);
            Allure.step("❌ Cart may still have items after " + attempts + " attempts");
        } else {
            logger.info("✅ Cart cleared successfully");
            Allure.step("✅ Cart cleared successfully");
        }
    }
}