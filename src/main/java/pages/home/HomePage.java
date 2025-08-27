package pages.home;

import drivers.DriverManager;
import helpers.ElementHelper;
import io.appium.java_client.AppiumDriver;
import io.qameta.allure.Allure;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import pages.BasePage;

import java.time.Duration;

public class HomePage extends BasePage {

    private static final Logger logger = LoggerFactory.getLogger(HomePage.class);

    public HomePage(AppiumDriver driver) {
        super(driver);
    }

    /**
     * Waits for home page to be visible by checking for the Self Collect button.
     * @return true if home page loaded, false if timeout
     */
    public boolean waitForHomePageToLoad() {
        String locatorKey = "selfCollectBtn.text";  // config entry
        try {
            By selfCollectBtn = ElementHelper.getLocator(locatorKey);
            WebElement element = ElementHelper.waitUntilVisible(selfCollectBtn, 15);

            if (element != null) {
                logger.info("✅ Home page loaded successfully.");
                ElementHelper.safeAllureStep("✅ Home page loaded successfully.");
                return true;
            } else {
                logger.error("❌ Home page element not visible.");
                ElementHelper.safeAllureStep("❌ Home page element not visible.");
                return false;
            }
        } catch (Exception e) {
            logger.error("❌ Home page did not load: {}", e.getMessage());
            ElementHelper.safeAllureStep("❌ Home page did not load: " + e.getMessage());
            return false;
        }
    }

    public boolean isHomePageDisplayed() {
        boolean visible = ElementHelper.isElementDisplayed("selfCollectBtn.text");
        logger.info("🏠 Home page visible: {}", visible);
        Allure.step("🏠 Home page visible: " + visible);
        return visible;
    }

    public void switchToDeliveryTab() {
        ElementHelper.clickElement("deliveryBtn.text");
        logger.info("🔄 Switched to Delivery tab");
        Allure.step("🔄 Switched to Delivery tab");
    }

    public boolean isDeliveryPageDisplayed() {
        boolean visible = ElementHelper.isElementDisplayed("deliveryPageTitle.text");
        logger.info("📦 Delivery page visible: {}", visible);
        Allure.step("📦 Delivery page visible: " + visible);
        return visible;
    }

    public void switchToSelfCollectTab() {
        final By selfCollectTab = ElementHelper.getLocator("selfCollectBtn.text");
        final By selfCollectTitle = ElementHelper.getLocator("selfCollectPageTitle.text");

        try {
            WebElement tab = ElementHelper.waitForElementVisible(DriverManager.getDriver(), selfCollectTab, 10);
            if (tab == null) {
                logger.error("🔴 'Self Collect' tab element not found for locator: {}", selfCollectTab);
                Allure.step("🔴 'Self Collect' tab element not found");
                Assert.fail("❌ 'Self Collect' tab is not clickable or missing.");
            }

            tab.click();
            logger.info("🟢 Clicked on 'Self Collect' tab");
            Allure.step("🟢 Clicked on 'Self Collect' tab");

            boolean isTitleVisible = ElementHelper.isElementDisplayed(selfCollectTitle, 5);
            if (isTitleVisible) {
                logger.info("🟢 Self Collect page title is visible");
                Allure.step("🟢 Self Collect page title is visible");
            } else {
                logger.error("🔴 Self Collect page title is not visible for locator: {}", selfCollectTitle);
                Allure.step("🔴 Self Collect page title is not visible");
                Assert.fail("❌ Failed to load Self Collect page.");
            }

        } catch (IllegalArgumentException e) {
            logger.error("❌ Invalid locator format in config: {}", e.getMessage(), e);
            Allure.step("❌ Invalid locator format in config: " + e.getMessage());
            Assert.fail("❌ Unrecognized locator. Check the config key and value.");
        } catch (Exception e) {
            logger.error("❌ Exception in switchToSelfCollectTab: {}", e.getMessage(), e);
            Allure.step("❌ Exception in switchToSelfCollectTab: " + e.getMessage());
            Assert.fail("❌ Unexpected error while switching to Self Collect tab.");
        }
    }

    public boolean isSelfCollectPageDisplayed() {
        boolean visible = ElementHelper.isElementDisplayed("selfCollectPageTitle.text");
        logger.info("🏬 Self Collect page visible: {}", visible);
        Allure.step("🏬 Self Collect page visible: " + visible);
        return visible;
    }

    public void tapSearchField() {
        ElementHelper.clickElement("searchField.text");
        logger.info("🔍 Tapped on search field");
        Allure.step("🔍 Tapped on search field");
    }

    public void enterSearchQuery(String productName) {
        try {
            WebElement searchInput = ElementHelper.waitUntilClickable("productSearchField.xpath", 10);
            searchInput.click();
            searchInput.clear();
            searchInput.sendKeys(productName);
            logger.info("✅ Entered search query: {}", productName);
            Allure.step("✅ Entered search query: " + productName);
        } catch (Exception e) {
            logger.error("❌ Failed to enter search query: {}", e.getMessage(), e);
            Allure.step("❌ Failed to enter search query: " + e.getMessage());
            throw e;
        }
    }

    public boolean isProductsPageDisplayed() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
            boolean isTitleVisible = wait
                    .until(ExpectedConditions.visibilityOfElementLocated(getLocator("ProductsPageTitle.text")))
                    .isDisplayed();

            boolean hasActions = wait.until(driver -> {
                try {
                    return driver.findElement(getLocator("buyNowBtn.text")).isDisplayed()
                            || driver.findElement(getLocator("addToCartBtn.text")).isDisplayed();
                } catch (Exception ignored) {
                    return false;
                }
            });

            boolean result = isTitleVisible && hasActions;
            logger.info("🛒 Products page displayed: {}", result);
            Allure.step("🛒 Products page displayed: " + result);
            return result;

        } catch (Exception e) {
            logger.error("❌ Product Details Page not displayed correctly: {}", e.getMessage(), e);
            Allure.step("❌ Product Details Page not displayed correctly: " + e.getMessage());
            return false;
        }
    }

    public boolean waitForPageLoad(int timeoutSeconds) {
        boolean visible = ElementHelper.waitUntilVisible(getLocator("searchField.text"), timeoutSeconds) != null;
        logger.info("⏳ Home page search field visible: {}", visible);
        Allure.step("⏳ Home page search field visible: " + visible);
        return visible;
    }

    public void switchToAccountPage() {
        try {
            WebElement accountButton = ElementHelper.waitUntilClickable("accountPageButtonFromHome.uiautomator", 10);
            ElementHelper.clickElement(accountButton);
            logger.info("👤 Tapped on Account page button");
            Allure.step("👤 Tapped on Account page button");

            WebElement accountPage = ElementHelper.waitUntilVisible(
                    ElementHelper.getLocator("accountPageTitle.uiautomator"), 5);

            if (accountPage != null && accountPage.isDisplayed()) {
                logger.info("✅ Account page displayed");
                Allure.step("✅ Account page displayed");
            } else {
                logger.warn("⚠️ Account page not visible after click");
                Allure.step("⚠️ Account page not visible after click");
            }

        } catch (Exception e) {
            logger.error("❌ Unable to open Account Page: {}", e.getMessage(), e);
            Allure.step("❌ Unable to open Account Page: " + e.getMessage());
        }
    }

    public void viewOrderAgain() {
        By locator = getLocator("reorderButton.text");
        ElementHelper.clickElement(locator);
        logger.info("🔁 Clicked View Order Again button");
        Allure.step("🔁 Clicked View Order Again button");
    }
}