package pages;

import config.ConfigurationManager;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileBy;
import io.appium.java_client.android.AndroidDriver;
import io.qameta.allure.Allure;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import java.time.Duration;
import java.util.List;

public class CartPage extends BasePage {
    private static final Logger logger = LoggerFactory.getLogger(CartPage.class);

    public CartPage(AppiumDriver driver) {
        super(driver);
    }

    public boolean isCartPageDisplayed() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

            boolean isTitleVisible = wait
                    .until(ExpectedConditions.visibilityOfElementLocated(getLocator("cartPageTitle.text")))
                    .isDisplayed();

            boolean detailElementsVisible = wait.until(driver -> {
                try {
                    boolean fulfilBtnVisible = driver.findElement(getLocator("proceedToFulfilmentBtn.text")).isDisplayed();
                    boolean totalLabelVisible = driver.findElement(getLocator("totalAmountLabel.text")).isDisplayed();
                    return fulfilBtnVisible || totalLabelVisible;
                } catch (Exception e) {
                    return false;
                }
            });

            logger.info("📄 Cart page check - Title: {}, Detail elements: {}", isTitleVisible, detailElementsVisible);
            Allure.step("📄 Cart page verified");

            return isTitleVisible && detailElementsVisible;

        } catch (Exception e) {
            logger.error("❌ Error checking cart page: {}", e.getMessage());
            Allure.step("❌ Cart page check failed: " + e.getMessage());
            return false;
        }
    }

    public void clickProceedToFulfilment() {
        try {
            WebElement btn = new WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(ExpectedConditions.elementToBeClickable(getLocator("proceedToFulfilmentBtn.text")));
            btn.click();
            logger.info("✅ Clicked 'Proceed to Fulfilment'");
            Allure.step("✅ Clicked 'Proceed to Fulfilment'");
        } catch (Exception e) {
            logger.error("❌ Failed to click 'Proceed to Fulfilment': {}", e.getMessage());
            Allure.step("❌ Failed to click 'Proceed to Fulfilment': " + e.getMessage());
            throw e;
        }
    }

    public void updateQuantity(String quantity) {
        try {
            logger.info("🔢 Entering quantity: {}", quantity);
            Allure.step("🔢 Updating quantity to: " + quantity);

            String qtyLocator = ConfigurationManager.getProperty("cartQtyInput.className");

            WebElement qtyField = new WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(ExpectedConditions.elementToBeClickable(MobileBy.AndroidUIAutomator(qtyLocator)));

            qtyField.click();
            qtyField.clear();
            qtyField.sendKeys(quantity);

            try {
                ((AndroidDriver) driver).hideKeyboard();
                logger.debug("⌨️ Keyboard hidden");
            } catch (Exception ignored) {
                logger.debug("⌨️ Keyboard could not be hidden");
            }

            Thread.sleep(300);

            logger.info("✅ Quantity updated to {}", quantity);
            Allure.step("✅ Quantity updated to " + quantity);

        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            logger.error("⚠️ Sleep interrupted: {}", ie.getMessage());
            throw new RuntimeException(ie);
        } catch (Exception e) {
            logger.error("❌ Failed to update quantity: {}", e.getMessage());
            Allure.step("❌ Failed to update quantity: " + e.getMessage());
            throw e;
        }
    }

    public boolean waitForPageLoad(int timeoutSeconds) {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds))
                    .until(ExpectedConditions.visibilityOfElementLocated(getLocator("cartPageTitle.text")));
            logger.info("✅ Cart page loaded");
            Allure.step("✅ Cart page loaded");
            return true;
        } catch (Exception e) {
            logger.warn("❌ Cart page did not load in {} seconds", timeoutSeconds);
            Allure.step("❌ Cart page load failed: " + e.getMessage());
            return false;
        }
    }

    public void clickAddMoreItems() {
        try {
            String text = ConfigurationManager.get("addMoreItemsBtn.text");

            if (text == null || text.isEmpty()) {
                throw new RuntimeException("❌ Missing config: 'addMoreItemsBtn.text'");
            }

            String selector = "new UiSelector().text(\"" + text + "\")";
            WebElement element = new WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(ExpectedConditions.elementToBeClickable(MobileBy.AndroidUIAutomator(selector)));

            element.click();
            logger.info("✅ Clicked 'Add More Items'");
            Allure.step("✅ Clicked 'Add More Items'");

        } catch (Exception e) {
            logger.error("❌ Failed to click 'Add More Items': {}", e.getMessage());
            Allure.step("❌ Failed to click 'Add More Items': " + e.getMessage());
            throw e;
        }
    }

    public boolean verifyCartItemQuantity(String expectedItemName, int expectedQuantity) {
        try {
            String itemSelector = "new UiSelector().text(\"" + expectedItemName + "\")";
            WebElement item = driver.findElement(MobileBy.AndroidUIAutomator(itemSelector));

            String qtySelector = "new UiSelector().text(\"" + expectedQuantity + "\")";
            List<WebElement> matches = driver.findElements(MobileBy.AndroidUIAutomator(qtySelector));

            if (item != null && !matches.isEmpty()) {
                logger.info("✅ Verified item '{}' with quantity {}", expectedItemName, expectedQuantity);
                Allure.step("✅ Verified cart item quantity for " + expectedItemName);
                return true;
            } else {
                logger.warn("❌ Quantity {} not found for item '{}'", expectedQuantity, expectedItemName);
                Allure.step("❌ Quantity mismatch for " + expectedItemName);
                return false;
            }
        } catch (Exception e) {
            logger.error("❌ Error verifying cart item '{}': {}", expectedItemName, e.getMessage());
            Allure.step("❌ Verification failed for " + expectedItemName);
            return false;
        }
    }

    public void verifyCartDeliveryMethod(String deliveryMethod) {
        String text = driver.findElement(By.id(ConfigurationManager.getProperty("cart.deliveryMethod"))).getText().toLowerCase();

        if (deliveryMethod.equalsIgnoreCase("delivery")) {
            Assert.assertTrue(text.contains("transport"), "Expected 'transport' for delivery method.");
        } else if (deliveryMethod.equalsIgnoreCase("self collect")) {
            Assert.assertFalse(text.contains("transport"), "'Transport' should not be present for self collect.");
        } else {
            throw new IllegalArgumentException("Invalid delivery method: " + deliveryMethod);
        }

        logger.info("✅ Verified cart delivery method: {}", deliveryMethod);
        Allure.step("✅ Verified delivery method: " + deliveryMethod);
    }

    public void clickStartShoppingButton() {
        try {
            String buttonText = ConfigurationManager.getProperty("cartDescription.text");
            String selector = "new UiSelector().text(\"" + buttonText + "\")";
            WebElement element = new WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(ExpectedConditions.elementToBeClickable(MobileBy.AndroidUIAutomator(selector)));

            element.click();
            logger.info("✅ Clicked '{}'", buttonText);
            Allure.step("✅ Clicked '" + buttonText + "'");

        } catch (Exception e) {
            String buttonText = ConfigurationManager.getProperty("cartDescription.text");
            logger.error("❌ Failed to click '{}': {}", buttonText, e.getMessage());
            Allure.step("❌ Failed to click '" + buttonText + "': " + e.getMessage());
            throw e;
        }
    }

}