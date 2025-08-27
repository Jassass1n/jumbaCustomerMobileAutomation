package pages;

import config.ConfigurationManager;
import helpers.ElementHelper;
import io.appium.java_client.AppiumDriver;
import io.qameta.allure.Allure;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;

import static helpers.ElementHelper.*;

public class ActiveOrders extends BasePage {

    private static final Logger logger = LoggerFactory.getLogger(ActiveOrders.class);
    private final WebDriverWait wait;

    public ActiveOrders(AppiumDriver driver) {
        super(driver);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    /** Checks that the home page root element is on-screen */
    public boolean waitForHomePageToLoad() {
        By locator = getLocator("homePageElement.text");
        boolean displayed = isElementDisplayed(locator);
        logAndReport("Home Page", displayed);
        return displayed;
    }

    /** Opens the Active Orders tab from Home */
    public void viewActiveOrders() {
        By locator = getLocator("activeOrderBtn.text");
        Allure.step("Clicking on Active Orders button");
        clickElement(locator);
        logger.info("Clicked Active Orders button, waiting for Active Orders page to load...");
    }

    /** Verifies that at least the first active order card is present */
    public boolean isActiveOrdersDisplayed() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(getLocator("firstActiveOrderStatus.xpath")));
            logger.info("Active Orders page loaded");
            Allure.step("✅ Active Orders page loaded");
            return true;
        } catch (Exception e) {
            logger.error("Error checking Active Orders page", e);
            Allure.step("❌ Active Orders page NOT loaded: " + e.getMessage());
            return false;
        }
    }

    /** Opens the details page of the first active order */
    public void viewOrderDetails() {
        By locator = getLocator("firstActiveOrderStatus.xpath");
        Allure.step("Clicking the first Active Order to view details");
        clickElement(locator);
        logger.info("Clicked the first Active Order to view details");
    }

    /** Opens the track order list */
    public void viewTrackOrderlist() {
        By locator = getLocator("trackOrderBtn.xpath");
        Allure.step("Clicking Track Order button");
        clickElement(locator);
        logger.info("Clicked Track Order button");
    }

    /** Selects the first available order based on status priority */
    public void selectOrderByPriority() {
        String[] statuses = {
                ConfigurationManager.get("order.status.priority1"),
                ConfigurationManager.get("order.status.priority2"),
                ConfigurationManager.get("order.status.priority3")
        };
        String xpathTemplate = ConfigurationManager.get("order.status.xpath.template");

        for (String status : statuses) {
            try {
                Allure.step("Searching for order with status: " + status);
                logger.info("Searching for order with status: {}", status);
                ElementHelper.scrollToTextAndReturn(status);
                String xpath = String.format(xpathTemplate, status, status);
                List<WebElement> orders = driver.findElements(By.xpath(xpath));
                if (!orders.isEmpty()) {
                    orders.get(0).click();
                    logger.info("Clicked order with status: {}", status);
                    Allure.step("✅ Clicked order with status: " + status);
                    return;
                }
            } catch (Exception e) {
                logger.warn("Scroll or find failed for status '{}'", status, e);
                Allure.step("⚠️ Failed for status '" + status + "': " + e.getMessage());
            }
        }

        // Fallback
        try {
            Allure.step("Fallback: Clicking first Submitted order");
            logger.info("Fallback: Clicking first Submitted order");
            clickElement(getLocator("firstActiveOrderStatus.xpath"));
        } catch (Exception e) {
            logger.error("Fallback also failed", e);
            Allure.step("❌ Even fallback failed: " + e.getMessage());
            throw e;
        }
    }

    /** Checks that the Order Tracking page is displayed */
    public boolean isOrderTrackingPageDisplayed() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(getLocator("orderTrackingTitle.text")));
            logger.info("Order Tracking page displayed");
            Allure.step("✅ Order Tracking page displayed");
            return true;
        } catch (Exception e) {
            logger.error("Error checking Order Tracking page", e);
            Allure.step("❌ Error checking Order Tracking page: " + e.getMessage());
            return false;
        }
    }

    /** Wait for Payments page to load */
    public boolean waitForPageLoad(int timeoutSeconds) {
        try {
            wait.withTimeout(Duration.ofSeconds(timeoutSeconds))
                    .until(ExpectedConditions.visibilityOfElementLocated(getLocator("paymentsTitle.text")));
            logger.info("Payments page loaded");
            Allure.step("✅ Payments page loaded");
            return true;
        } catch (Exception e) {
            logger.error("Timeout waiting for Payment page", e);
            Allure.step("❌ Timeout waiting for Payment page: " + e.getMessage());
            return false;
        }
    }

    /** Utility for dual logging */
    private void logAndReport(String context, boolean success) {
        String msg = success
                ? "✅ " + context + " displayed"
                : "❌ " + context + " NOT displayed";
        logger.info(msg);
        Allure.step(msg);
    }
}