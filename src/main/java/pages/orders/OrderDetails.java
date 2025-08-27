package pages.orders;

import config.ConfigurationManager;
import helpers.ElementHelper;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileBy;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import pages.BasePage;
import utils.TestContext;
import utils.PlatformHelper;
import io.qameta.allure.Allure;

import java.time.Duration;

import static helpers.ElementHelper.*;

public class OrderDetails extends BasePage {
    private static final Logger logger = LoggerFactory.getLogger(OrderDetails.class);
    private final WebDriverWait wait;

    public OrderDetails(AppiumDriver driver) {
        super(driver);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    public boolean isOrderDetailsDisplayed() {
        return isElementDisplayed("orderDetailsPageTitle.text");
    }

    /**
     * ✅ Verifies transaction details using TestContext values and dynamic UIAutomator selectors.
     */
    public boolean verifyTransactionDetails() {
        try {
            logger.info("🔍 Verifying transaction details on Order Details screen...");
            Allure.step("🔍 Verifying transaction details on Order Details screen");

            String methodLocator = "new UiSelector().textContains(\"" + TestContext.paymentMethod + "\")";
            String amountLocator = "new UiSelector().textContains(\"" + TestContext.paymentAmount + "\")";
            String refLocator = "new UiSelector().textContains(\"" + TestContext.referenceNumber + "\")";

            String datePart = TestContext.paymentDateTime.split(",")[0];
            String timeLocator = "new UiSelector().textContains(\"" + datePart + "\")";

            String actualMethod = driver.findElement(MobileBy.AndroidUIAutomator(methodLocator)).getText();
            String actualAmount = driver.findElement(MobileBy.AndroidUIAutomator(amountLocator)).getText();
            String actualRef = driver.findElement(MobileBy.AndroidUIAutomator(refLocator)).getText();
            String actualTime = driver.findElement(MobileBy.AndroidUIAutomator(timeLocator)).getText();

            logger.info("🧾 Found values: Method={}, Amount={}, Ref={}, Time={}", actualMethod, actualAmount, actualRef, actualTime);

            boolean result = actualMethod.contains(TestContext.paymentMethod)
                    && actualAmount.contains(TestContext.paymentAmount)
                    && actualRef.equals(TestContext.referenceNumber)
                    && actualTime.contains(datePart);

            if (result) {
                logger.info("✅ Transaction details match expected values.");
                Allure.step("✅ Transaction details match expected values.");
            } else {
                logger.warn("❌ Transaction details mismatch.");
                Allure.step("❌ Transaction details mismatch.");
            }

            return result;

        } catch (Exception e) {
            logger.error("❌ Failed to verify transaction details", e);
            Allure.step("❌ Exception verifying transaction details: " + e.getMessage());
            return false;
        }
    }

    public void clickPayNowLink() {
        logger.info("➡️ Clicking 'Pay Now' link");
        Allure.step("➡️ Clicking 'Pay Now' link");
        clickElement("payNowLink.text");
    }

    /**
     * ✅ Waits for Payments page to load within a custom timeout.
     */
    public boolean waitForPageLoad(int timeoutSeconds) {
        try {
            logger.info("⏳ Waiting for Payments page to load...");
            Allure.step("⏳ Waiting for Payments page to load...");
            wait.withTimeout(Duration.ofSeconds(timeoutSeconds))
                    .until(ExpectedConditions.visibilityOfElementLocated(getLocator("paymentsTitle.text")));

            logger.info("✅ Payments page loaded successfully");
            Allure.step("✅ Payments page loaded successfully");
            return true;

        } catch (Exception e) {
            logger.error("❌ Timeout waiting for Payments page to load", e);
            Allure.step("❌ Timeout waiting for Payments page to load: " + e.getMessage());
            return false;
        }
    }
    public void clickReorderButton() {
        String reorderBtnText = ConfigurationManager.getProperty("reorderBtn.text");

        try {
            logger.info("➡️ Scrolling to 'Reorder' button with text: {}", reorderBtnText);
            Allure.step("➡️ Scrolling to 'Reorder' button with text: " + reorderBtnText);

            // Step 1: Scroll to the text
            ElementHelper.scrollToTextAndReturn(reorderBtnText);

            // Step 2: Check if it's visible
            By reorderBtnLocator = ElementHelper.getLocator("reorderBtn.text");
            if (ElementHelper.isElementDisplayed(reorderBtnLocator)) {
                logger.info("✅ 'Reorder' button is visible, clicking...");
                Allure.step("✅ 'Reorder' button is visible, clicking...");

                // Step 3: Click it
                ElementHelper.clickElement(reorderBtnLocator);

                logger.info("✅ Clicked 'Reorder' button");
                Allure.step("✅ Clicked 'Reorder' button");
            } else {
                logger.error("❌ 'Reorder' button is not visible after scrolling.");
                Allure.step("❌ 'Reorder' button is not visible after scrolling.");
                Assert.fail("❌ 'Reorder' button is not visible after scrolling.");
            }

        } catch (Exception e) {
            logger.error("❌ Failed to click 'Reorder' button: {}", e.getMessage());
            Allure.step("❌ Failed to click 'Reorder' button");
            Assert.fail("❌ Failed to click 'Reorder' button: " + e.getMessage());
        }
    }

    public void trackOrderButtonisVisible() {
        String trackOrderBtnText = ConfigurationManager.getProperty("trackOrderBtn.text");

        try {
            logger.info("➡️ Scrolling to 'Track Order' button with text: {}", trackOrderBtnText);
            Allure.step("➡️ Scrolling to 'Track Order' button with text: " + trackOrderBtnText);

            // Step 1: Scroll to the text
            ElementHelper.scrollToTextAndReturn(trackOrderBtnText);

            // Step 2: Check if it's visible
            By trackOrderBtnLocator = ElementHelper.getLocator("trackOrderBtn.text");
            if (ElementHelper.isElementDisplayed(trackOrderBtnLocator)) {
                logger.info("✅ 'Track Order' button is visible");
                Allure.step("✅ 'Track Order' button is visible");
            } else {
                logger.error("❌ 'Track Order' button is not visible after scrolling.");
                Allure.step("❌ 'Track Order' button is not visible after scrolling.");
                Assert.fail("❌ 'Track Order' button is not visible after scrolling.");
            }

        } catch (Exception e) {
            logger.error("❌ Failed to scroll to 'Track Order' button: {}", e.getMessage());
            Allure.step("❌ Failed to scroll to 'Track Order' button");
            Assert.fail("❌ Failed to scroll to 'Track Order' button: " + e.getMessage());
        }
    }

}