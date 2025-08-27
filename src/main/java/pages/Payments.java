package pages;

import config.ConfigurationManager;
import helpers.ElementHelper;
import helpers.ToastHelper;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileBy;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import pages.components.BankTransferComponent;
import pages.home.HomePage;
import utils.TestContext;

import io.qameta.allure.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.NoSuchElementException;

public class Payments extends BasePage {

    private static final Logger logger = LoggerFactory.getLogger(Payments.class);

    private final HomePage homePage;
    private final WebDriverWait wait;
    private final BankTransferComponent bankTransferComponent;
    private final By confirmPaymentButton = MobileBy.AccessibilityId(ConfigurationManager.get("payment.confirmPaymentBtn"));
    private final By completeOrderButton = getLocator("completeOrderBtn.text");
    private final By backToHomeBtn = getLocator("backToHomeBtn.text");
    private final ToastHelper toastHelper;
    private final TestContext testContext;
    private final AppiumDriver driver;

    public Payments(TestContext testContext) {
        super(testContext.getDriver());
        this.testContext = testContext;
        this.driver = testContext.getDriver();
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        this.bankTransferComponent = new BankTransferComponent(testContext);
        this.homePage = new HomePage(driver);
        this.toastHelper = new ToastHelper(driver);
    }

    public BankTransferComponent getBankTransferComponent() {
        return bankTransferComponent;
    }

    @Step("Verify Payments page is displayed")
    public boolean isPaymentsDisplayed() {
        try {
            boolean isTitlePresent = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    getLocator("paymentsTitle.text"))).isDisplayed();

            boolean hasDetailElements = wait.until(driver -> {
                try {
                    boolean hasCompleteOrderBtn = driver.findElement(completeOrderButton).isDisplayed();
                    boolean hasPayBalanceLaterBtn = driver.findElement(getLocator("payBalanceLaterBtn.text")).isDisplayed();
                    return hasCompleteOrderBtn || hasPayBalanceLaterBtn;
                } catch (Exception e) {
                    return false;
                }
            });

            logger.info("📄 Payment Details Page - Title: {}, Detail Elements: {}", isTitlePresent, hasDetailElements);
            return isTitlePresent && hasDetailElements;

        } catch (Exception e) {
            logger.error("❌ Error checking Payment details page: {}", e.getMessage());
            return false;
        }
    }

    @Step("Verify payment section amount: {expectedAmount} and status Pending")
    public boolean verifyPaymentSectionUpdated(String expectedAmount) {
        try {
            WebElement amountElement = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    getLocator("payment.totalAmount.text")));
            WebElement pendingStatusElement = driver.findElement(getLocator("payment.status.text"));

            String actualAmount = amountElement.getText().replaceAll("[^0-9.]", "");
            String status = pendingStatusElement.getText();

            boolean isAmountMatched = actualAmount.equals(expectedAmount);
            boolean isPending = status.equalsIgnoreCase("Pending");

            logger.info("💰 Amount displayed: {} | Expected: {}", actualAmount, expectedAmount);
            logger.info("📄 Payment Status: {}", status);

            return isAmountMatched && isPending;
        } catch (Exception e) {
            logger.error("❌ Payment section verification failed: {}", e.getMessage());
            return false;
        }
    }

    @Step("Click Confirm Payment button and verify toast")
    public void clickConfirmPayment() {
        if (!bankTransferComponent.confirmPaymentIfEnabled()) {
            Assert.fail("❌ Confirm Payment button is not enabled or not clickable.");
        }

        try {
            driver.findElement(confirmPaymentButton).click();
            logger.info("🖱️ Clicked Confirm Payment");

            boolean success = toastHelper.verifyAndWaitForToast(
                    "Proof of payment submitted successfully", 5, 3);

            if (!success) {
                Assert.fail("❌ Toast message not found or did not disappear.");
            }

        } catch (Exception e) {
            Assert.fail("❌ Error during Confirm Payment toast check: " + e.getMessage());
        }
    }

    @Step("Click Complete Order button with retry")
    public void clickCompleteOrder1() {
        int maxRetries = 3;
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                wait.until(ExpectedConditions.elementToBeClickable(completeOrderButton)).click();
                logger.info("🖱️ Clicked Complete Order!");
                return;
            } catch (Exception e) {
                logger.warn("⚠️ Attempt {} to click Complete Order failed: {}", attempt, e.getMessage());
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        Assert.fail("❌ Failed to click Complete Order button after " + maxRetries + " attempts");
    }

    @Step("Confirm payment if enabled and verify redirection")
    public boolean confirmPaymentIfEnabled() {
        try {
            WebElement button = wait.until(ExpectedConditions.visibilityOfElementLocated(completeOrderButton));

            boolean enabled = wait.until(driver -> {
                String enabledAttr = driver.findElement(completeOrderButton).getAttribute("enabled");
                logger.debug("🔄 Confirm button 'enabled': {}", enabledAttr);
                return "true".equalsIgnoreCase(enabledAttr);
            });

            if (enabled) {
                button.click();
                logger.info("🖱️ Clicked Confirm Payment");

                By submittedOrderPageElement = ElementHelper.getLocator("orderSubmittedTitle.text");
                boolean redirected = wait.until(ExpectedConditions.visibilityOfElementLocated(submittedOrderPageElement)) != null;

                logger.info("✅ Redirected to Order Submitted page: {}", redirected);
                return redirected;
            } else {
                logger.warn("⚠️ Confirm Payment button is still disabled after wait.");
                return false;
            }
        } catch (Exception e) {
            logger.error("❌ Error confirming payment: {}", e.getMessage());
            return false;
        }
    }

    @Step("Click Pay Balance Later button and handle optional KRA popup")
    public void clickPayBalanceLater() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(getLocator("payBalanceLaterBtn.text"))).click();
            logger.info("🖱️ Clicked Pay Balance Later");

            if (ElementHelper.isElementPresent(getLocator("kraInputLabel.text"), 3)) {
                logger.info("ℹ️ KRA popup detected, clicking Skip...");
                wait.until(ExpectedConditions.elementToBeClickable(getLocator("kraPinSkipBtn.text"))).click();
                logger.info("🖱️ Clicked Skip on KRA popup");
            } else {
                logger.info("ℹ️ No KRA popup detected.");
            }

        } catch (Exception e) {
            logger.error("❌ Failed during Pay Later flow: {}", e.getMessage());
            Assert.fail("❌ Pay Later flow failed: " + e.getMessage());
        }
    }

    private boolean isElementDisplayed(By locator) {
        try {
            return driver.findElement(locator).isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    @Step("Click Back to Home and verify redirection")
    public void clickBackToHome() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(backToHomeBtn)).click();
            if (homePage.waitForHomePageToLoad()) {
                logger.info("✅ Back to Home button clicked and redirected to Home page.");
            }
        } catch (Exception e) {
            logger.error("❌ Error waiting for Back to Home button: {}", e.getMessage());
            Assert.fail("❌ Back to Home failed: " + e.getMessage());
        }
    }

    @Step("Verify Order Submitted page is visible")
    public boolean isOrderSubmittedPageVisible() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(getLocator("orderSubmittedTitle.text")));
            logger.info("✅ Order Submitted page is displayed");
            return true;
        } catch (Exception e) {
            logger.warn("⚠️ Order Submitted page not displayed: {}", e.getMessage());
            return false;
        }
    }

    public boolean isStillOnPaymentPage() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(confirmPaymentButton));
            logger.warn("⚠️ Still on Payment page.");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Step("Click View Order if Order Submitted page is visible")
    public void clickViewOrder() {
        try {
            if (isOrderSubmittedPageVisible()) {
                wait.until(ExpectedConditions.elementToBeClickable(getLocator("viewOrderBtn.text"))).click();
                logger.info("✅ Clicked View Order button");
            } else {
                logger.warn("⚠️ Order Submitted page is not visible. Skipping View Order click.");
            }
        } catch (Exception e) {
            logger.error("❌ Error clicking View Order: {}", e.getMessage());
        }
    }

    @Step("Check if Order Details page is visible")
    public boolean isOrderDetailsPageVisible() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(getLocator("orderDetailsTitle.text")));
            logger.info("✅ Order Details page is displayed.");
            return true;
        } catch (Exception e) {
            logger.error("❌ Order Details page not visible: {}", e.getMessage());
            return false;
        }
    }

    @Step("Attempt to complete order if button is enabled")
    public boolean completeOrderIfEnabled() {
        try {
            logger.info("➡️ Waiting for Complete Order button...");
            WebElement button = wait.until(ExpectedConditions.visibilityOfElementLocated(completeOrderButton));

            boolean enabled = wait.until(driver -> {
                String enabledAttr = driver.findElement(completeOrderButton).getAttribute("enabled");
                logger.debug("🔍 Button 'enabled' attribute: {}", enabledAttr);
                return "true".equalsIgnoreCase(enabledAttr);
            });

            if (enabled) {
                button.click();
                logger.info("🖱️ Clicked Complete Order");
                return true;
            } else {
                logger.warn("⚠️ Complete Order button is disabled.");
                return false;
            }

        } catch (Exception e) {
            logger.error("❌ Exception in completeOrderIfEnabled(): {}", e.getMessage());
            return false;
        }
    }

    @Step("Click Complete Order and verify order submission")
    public void clickCompleteOrder() {
        try {
            toastHelper.waitForToastToDisappear("Proof of payment submitted successfully", 3);

            if (!completeOrderIfEnabled()) {
                Assert.fail("❌ Complete Order button not enabled.");
            }

            driver.findElement(completeOrderButton).click();
            logger.info("🖱️ Clicked Complete Order");

            if (ElementHelper.isElementDisplayed(getLocator("kraInputLabel.text"), 3)) {
                logger.info("ℹ️ KRA popup displayed, clicking Skip...");
                driver.findElement(getLocator("kraPinSkipBtn.text")).click();
                logger.info("🖱️ Clicked Skip on KRA popup");
            } else {
                logger.info("ℹ️ KRA popup not displayed.");
            }

            boolean isOrderSubmitted = ElementHelper.isElementDisplayed(getLocator("orderSubmittedTitle.text"), 5);
            Assert.assertTrue(isOrderSubmitted, "❌ Order Submitted screen was not displayed.");
            logger.info("✅ Order Submitted screen verified.");

        } catch (Exception e) {
            Assert.fail("❌ Failed to complete order flow: " + e.getMessage());
        }
    }

    @Step("Wait for Payments page to load")
    public boolean waitForPageLoad(int timeoutSeconds) {
        try {
            wait.withTimeout(Duration.ofSeconds(timeoutSeconds))
                    .until(ExpectedConditions.visibilityOfElementLocated(getLocator("paymentsTitle.text")));
            return true;
        } catch (Exception e) {
            logger.error("❌ Timeout waiting for Payment page to load: {}", e.getMessage());
            return false;
        }
    }
}