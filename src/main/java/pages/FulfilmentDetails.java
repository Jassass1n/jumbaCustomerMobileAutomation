package pages;

import config.ConfigurationManager;
import drivers.DriverManager;
import helpers.ElementHelper;
import helpers.LocatorHelper;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileBy;
import io.appium.java_client.android.AndroidDriver;
import io.qameta.allure.Allure;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public class FulfilmentDetails extends BasePage {
    private static final Logger logger = LoggerFactory.getLogger(FulfilmentDetails.class);
    private final ProductsPage productsPage;

    public FulfilmentDetails(AppiumDriver driver) {
        super(driver);
        this.productsPage = new ProductsPage(driver);
    }

    public boolean isFulfilmentDetailsDisplayed() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

            boolean isTitlePresent = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    getLocator("cartPageTitle.text"))).isDisplayed();

            boolean hasDetailElements = wait.until(driver -> {
                try {
                    boolean hasProceedToFulfilmentBtn = driver.findElement(getLocator("proceedToFulfilmentBtn.text")).isDisplayed();
                    boolean totalAmountLabel = driver.findElement(getLocator("totalAmountLabel.text")).isDisplayed();
                    return hasProceedToFulfilmentBtn || totalAmountLabel;
                } catch (Exception e) {
                    return false;
                }
            });

            logger.info("📄 Fulfilment Details Displayed - Title: {}, Detail Elements: {}", isTitlePresent, hasDetailElements);
            Allure.step("📄 Fulfilment Details Displayed");

            return isTitlePresent && hasDetailElements;

        } catch (Exception e) {
            logger.error("❌ Error checking Fulfilment Details page: {}", e.getMessage());
            Allure.step("❌ Error checking Fulfilment Details page: " + e.getMessage());
            return false;
        }
    }

    public void fillPlateNumberAndProceed() {
        try {
            String plateNumber = ConfigurationManager.getProperty("plateNumberValue", "KAA123A");
            String plateLocator = ConfigurationManager.getProperty("plateNumber.id");
            String proceedButtonText = ConfigurationManager.getProperty("proceedToPaymentBtn.text");

            logger.info("🚗 Entering plate number: {}", plateNumber);
            Allure.step("🚗 Entering plate number: " + plateNumber);

            WebElement plateInput = new WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(ExpectedConditions.elementToBeClickable(MobileBy.AndroidUIAutomator(plateLocator)));

            plateInput.click();
            plateInput.clear();
            plateInput.sendKeys(plateNumber);

            try {
                ((AndroidDriver) driver).hideKeyboard();
                logger.info("⌨️ Keyboard hidden after entering plate number");
            } catch (Exception e) {
                logger.info("ℹ️ No keyboard to hide: {}", e.getMessage());
            }

            WebElement proceedBtn = new WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(ExpectedConditions.elementToBeClickable(
                            MobileBy.AndroidUIAutomator("new UiSelector().text(\"" + proceedButtonText + "\")")));

            proceedBtn.click();
            logger.info("✅ Proceeded to payment");
            Allure.step("✅ Clicked 'Proceed to Payment'");

        } catch (Exception e) {
            logger.error("❌ Failed to proceed from Fulfilment page: {}", e.getMessage());
            Allure.step("❌ Failed to proceed from Fulfilment page: " + e.getMessage());
            throw e;
        }
    }

    public boolean waitForPageLoad(int timeoutSeconds) {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds))
                    .until(ExpectedConditions.visibilityOfElementLocated(getLocator("cartPageTitle.text")));
            logger.info("✅ Fulfilment page loaded within {} seconds", timeoutSeconds);
            Allure.step("✅ Fulfilment page loaded");
            return true;
        } catch (Exception e) {
            logger.error("❌ Fulfilment page did not load in time: {}", e.getMessage());
            Allure.step("❌ Fulfilment page did not load");
            return false;
        }
    }

    public boolean isDeliveryPageDisplayed() {
        try {
            String expectedText = ConfigurationManager.getProperty("deliveryAddress.text");
            By deliveryTitle = MobileBy.AndroidUIAutomator("new UiSelector().textContains(\"" + expectedText + "\")");

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
            return wait.until(ExpectedConditions.visibilityOfElementLocated(deliveryTitle)).isDisplayed();
        } catch (Exception e) {
            logger.error("❌ Error checking delivery page: {}", e.getMessage());
            return false;
        }
    }

    public boolean isSelfCollectPageDisplayed() {
        try {
            String expectedText = ConfigurationManager.getProperty("selfCollect.text");
            By selfCollectTitle = MobileBy.AndroidUIAutomator("new UiSelector().textContains(\"" + expectedText + "\")");

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
            return wait.until(ExpectedConditions.visibilityOfElementLocated(selfCollectTitle)).isDisplayed();
        } catch (Exception e) {
            logger.error("❌ Error checking self collect page: {}", e.getMessage());
            return false;
        }
    }

    public boolean waitUntilSelfCollectPageIsDisplayed() {
        try {
            By selfCollectTitle = LocatorHelper.resolveLocator("selfCollectBtn.text");

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.visibilityOfElementLocated(selfCollectTitle));
            return true;
        } catch (TimeoutException e) {
            logger.warn("⚠️ Self Collect page did not appear within timeout.");
            return false;
        }
    }

    public boolean isUpdatingProductPricingPopupVisible() {
        String popupText = ConfigurationManager.getProperty("products.pricingPopupText");

        try {
            By popupLocator = MobileBy.AndroidUIAutomator("new UiSelector().textContains(\"" + popupText + "\")");
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
            return wait.until(ExpectedConditions.visibilityOfElementLocated(popupLocator)).isDisplayed();
        } catch (TimeoutException e) {
            logger.info("ℹ️ Pricing popup not visible.");
            return false;
        } catch (Exception e) {
            logger.error("❌ Error checking for Pricing popup: {}", e.getMessage());
            return false;
        }
    }

    public void waitForPricingPopupToDismiss() {
        String popupText = ConfigurationManager.getProperty("products.pricingPopupText");
        By popupLocator = MobileBy.AndroidUIAutomator("new UiSelector().textContains(\"" + popupText + "\")");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        wait.until(ExpectedConditions.invisibilityOfElementLocated(popupLocator));
    }

    public void clickProceedToPayment() {
        logger.info("🔍 Checking if 'Proceed to Payment' button is visible and enabled...");

        try {
            By proceedToPaymentBtn = LocatorHelper.resolveLocator("proceedToPaymentBtn.text");

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement button = wait.until(ExpectedConditions.presenceOfElementLocated(proceedToPaymentBtn));

            if (button.isDisplayed() && button.isEnabled()) {
                logger.info("✅ 'Proceed to Payment' is visible and enabled. Clicking...");
                button.click();
                logger.info("✅ Clicked 'Proceed to Payment' successfully.");
            } else {
                logger.warn("⚠️ 'Proceed to Payment' is present but not clickable (disabled or hidden).");
            }

        } catch (TimeoutException e) {
            logger.error("❌ 'Proceed to Payment' button was not found within 10 seconds.");
        } catch (Exception e) {
            logger.error("❌ Unexpected error when trying to click 'Proceed to Payment': {}", e.getMessage());
        }
    }

    public void switchToDeliveryMethod() {
        try {
            logger.info("🔄 Switching to Products Delivery tab...");

            if (isUpdatingProductPricingPopupVisible()) {
                ElementHelper.safeAllureStep("⏳ Waiting for pricing popup...");
                waitForPricingPopupToDismiss();
                logger.info("✅ Pricing popup dismissed");
            }

            productsPage.clickProductsDeliveryTab();
            logger.info("✅ Clicked Delivery tab");

            productsPage.handleChangeAnywayButtonIfPresent();

            if (isUpdatingProductPricingPopupVisible()) {
                ElementHelper.safeAllureStep("⏳ Waiting for pricing popup...");
                waitForPricingPopupToDismiss();
                logger.info("✅ Pricing popup dismissed");
            }

            if (!isDeliveryPageDisplayed()) {
                throw new AssertionError("❌ Delivery method page not visible.");
            }

            logger.info("✅ Delivery method page is visible.");

        } catch (Exception e) {
            logger.error("❌ Error while switching to delivery method", e);
            throw new AssertionError("❌ Could not switch to Delivery tab: " + e.getMessage(), e);
        }
    }

    public void switchToSelfCollectMethod() {
        try {
            logger.info("🔄 Switching to Self Collect tab...");
            Allure.step("🔄 Switching to Self Collect tab...");

            // STEP 1: Handle initial pricing popup (on page load)
            if (isUpdatingProductPricingPopupVisible()) {
                ElementHelper.safeAllureStep("⏳ Waiting for initial 'Updating Product Pricing' popup to disappear...");
                waitForPricingPopupToDismiss();
                logger.info("✅ Initial 'Updating Product Pricing' popup dismissed");
            }

            // STEP 2: Click Self Collect Tab (with retry)
            By selfCollectTabLocator = LocatorHelper.resolveLocator("selfCollectBtn.text");
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            boolean tabClicked = false;
            for (int attempt = 1; attempt <= 2; attempt++) {
                try {
                    WebElement tab = wait.until(ExpectedConditions.elementToBeClickable(selfCollectTabLocator));
                    tab.click();
                    logger.info("✅ Clicked Self Collect tab (Attempt {})", attempt);
                    Allure.step("✅ Clicked Self Collect tab (Attempt " + attempt + ")");
                    tabClicked = true;
                    break;
                } catch (Exception e) {
                    logger.warn("⚠️ Attempt {} to click Self Collect tab failed: {}", attempt, e.getMessage());
                    if (attempt == 2) throw e;
                }
            }

            if (!tabClicked) {
                throw new RuntimeException("❌ Failed to click Self Collect tab after retries.");
            }

            // STEP 3: Handle toast if shown
            productsPage.handleChangeAnywayButtonIfPresent();

            // STEP 4: Wait for second pricing popup after tab click
            if (isUpdatingProductPricingPopupVisible()) {
                ElementHelper.safeAllureStep("⏳ Waiting for second 'Updating Product Pricing' popup to disappear...");
                waitForPricingPopupToDismiss();
                logger.info("✅ Second 'Updating Product Pricing' popup dismissed");
            }

            // STEP 5: Confirm Self Collect page is displayed
            if (!waitUntilSelfCollectPageIsDisplayed()) {
                throw new AssertionError("❌ Self Collect page not visible after clicking tab.");
            }

            logger.info("✅ Self Collect page is visible.");
            Allure.step("✅ Self Collect page is visible.");

        } catch (Exception e) {
            logger.error("❌ Error while switching to Self Collect method", e);
            Allure.step("❌ Failed to switch to Self Collect: " + e.getMessage());
            throw new AssertionError("❌ Could not switch to Self Collect tab: " + e.getMessage(), e);
        }
    }
}