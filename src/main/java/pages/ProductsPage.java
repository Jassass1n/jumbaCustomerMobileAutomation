package pages;

import config.ConfigurationManager;
import helpers.ElementHelper;
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
import java.util.List;

import static helpers.ElementHelper.clickElement;

public class ProductsPage extends BasePage {
    private static final Logger logger = LoggerFactory.getLogger(ProductsPage.class);
    private final WebDriverWait wait;

    public ProductsPage(AppiumDriver driver) {
        super(driver);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    protected void scrollToText(String text) {
        String uiAutomatorString = "new UiScrollable(new UiSelector().scrollable(true))"
                + ".scrollIntoView(new UiSelector().textContains(\"" + text + "\"))";
        driver.findElement(MobileBy.AndroidUIAutomator(uiAutomatorString));
    }

    public boolean isProductsPageDisplayed() {
        String expectedText = ConfigurationManager.getProperty("ProductsPageTitle.text");
        try {
            Allure.step("🔍 Checking if Products page is displayed with title: " + expectedText);
            logger.info("🔍 Checking if Products page is displayed with title: {}", expectedText);

            scrollToText(expectedText);
            logger.info("🔃 Scrolled to text: {}", expectedText);

            By descriptionLocator = MobileBy.AndroidUIAutomator("new UiSelector().textContains(\"" + expectedText + "\")");
            boolean isVisible = wait.until(ExpectedConditions.visibilityOfElementLocated(descriptionLocator)).isDisplayed();

            logger.info("✅ Products page is visible with text: {}", expectedText);
            Allure.step("✅ Products page is visible with text: " + expectedText);
            return isVisible;
        } catch (Exception e) {
            String msg = "❌ Failed to locate Products page with title: " + expectedText;
            logger.error(msg, e);
            Allure.step(msg + " - " + e.getMessage());
            return false;
        }
    }

    public void enterProductQuantity(String quantity) {
        try {
            logger.info("🔢 Trying to enter quantity: {}", quantity);
            String qtyLocator = ConfigurationManager.getProperty("productQtyInput.className");

            WebElement qtyField = wait.until(ExpectedConditions.elementToBeClickable(MobileBy.AndroidUIAutomator(qtyLocator)));
            qtyField.click();
            qtyField.clear();
            qtyField.sendKeys(quantity);
            logger.info("✅ Quantity entered successfully: {}", quantity);

            try {
                ((AndroidDriver) driver).hideKeyboard();
                logger.info("⌨️ Keyboard hidden successfully");
            } catch (Exception e) {
                logger.warn("ℹ️ Could not hide keyboard: {}", e.getMessage());
            }
        } catch (Exception e) {
            logger.error("❌ Error while entering quantity: {}", e.getMessage());
            throw e;
        }
    }

    public void clickAddToCart() {
        try {
            logger.info("🛒 Attempting to click 'Add to Cart'...");
            WebElement addToCartBtn = wait.until(ExpectedConditions.elementToBeClickable(getLocator("addToCartBtn.accessibilityId")));
            addToCartBtn.click();
            logger.info("✅ Clicked 'Add to Cart'.");
        } catch (Exception e) {
            logger.error("❌ Failed to click 'Add to Cart': {}", e.getMessage());
            throw e;
        }
    }

    public void clickViewCart() {
        try {
            logger.info("🛒 Waiting for 'View Cart' button...");

            By viewCartBtnLocator = ElementHelper.getLocator("viewCartBtn.text");
            WebElement viewCartBtn = wait.until(ExpectedConditions.elementToBeClickable(viewCartBtnLocator));

            logger.info("🛒 Found 'View Cart' button. Attempting to click...");
            viewCartBtn.click();

            logger.info("✅ Clicked 'View Cart'. Waiting to verify navigation...");

            // Try waiting for Cart Page, then fall back to Home Page check
            By cartPageTitleLocator = ElementHelper.getLocator("cartPageTitle.text");
            By homePageTitleLocator = ElementHelper.getLocator("homePageElement.text");

            try {
                wait.until(ExpectedConditions.visibilityOfElementLocated(cartPageTitleLocator));
                logger.info("🛒 Confirmed navigation to Cart Page.");
            } catch (TimeoutException e) {
                if (ElementHelper.isElementDisplayed(homePageTitleLocator)) {
                    logger.warn("⚠️ 'View Cart' clicked but redirected to Home Page instead.");
                } else {
                    logger.warn("⚠️ 'View Cart' clicked but current page couldn't be identified.");
                }
            }

        } catch (Exception e) {
            logger.error("❌ Failed to click 'View Cart': {}", e.getMessage());
            throw e;
        }
    }

    public boolean isCartConfirmationToastVisible() {
        try {
            WebElement toast = wait.until(ExpectedConditions.visibilityOfElementLocated(getLocator("cartToastMessage.text")));
            logger.info("✅ Cart confirmation toast is visible.");
            return toast.isDisplayed();
        } catch (TimeoutException e) {
            logger.warn("❌ Cart toast not found within timeout.");
            return false;
        } catch (Exception e) {
            logger.error("❌ Error checking for cart toast: {}", e.getMessage());
            return false;
        }
    }

    public void clickProductsButton() {
        clickElement(getLocator("browseAllProductsBtn.text"));
    }

    public void switchToProductsDeliveryTabAndVerifyFlow() {
        clickProductsDeliveryTab();
        handleChangeAnywayButtonIfPresent();

        boolean hasEstDeliveryCost = verifyEstimatedDeliveryCostIsDisplayed();
        if (!hasEstDeliveryCost) {
            logger.error("❌ Estimated delivery cost NOT found.");
            throw new AssertionError("Estimated delivery cost not found on any product.");
        }
        logger.info("✅ Estimated delivery cost is displayed.");
    }


    public void switchBackToSelfCollectAndVerify() {
        try {
            logger.info("🔁 Attempting to switch back to Self Collect...");

            // Click Self Collect tab
            clickElement(getLocator("selfCollectBtn.text"));
            logger.info("✅ Clicked Self Collect tab.");

            // Handle "Change Anyway" toast if shown
            handleChangeAnywayButtonIfPresent();

            // Verify that delivery-specific content is NOT visible anymore
            boolean deliveryCostVisible = verifyEstimatedDeliveryCostIsDisplayed();
            if (deliveryCostVisible) {
                throw new AssertionError("❌ Estimated Delivery Cost should NOT be visible in Self Collect mode.");
            } else {
                logger.info("✅ Estimated Delivery Cost is not visible — correctly switched to Self Collect.");
            }

        } catch (Exception e) {
            logger.error("❌ Error while switching to Self Collect: {}", e.getMessage());
            throw e;
        }
    }

    public void clickProductsDeliveryTab() {
        clickElement(getLocator("productsDeliveryBtn.text"));
        logger.info("✅ Clicked Delivery tab.");
    }

    public void clickSelfCollectTab() {
        clickElement(getLocator("selfCollectBtn.text"));
        logger.info("✅ Clicked Self Collect tab.");
    }

    public void handleChangeAnywayButtonIfPresent() {
        try {
            By changeAnywayBtn = getLocator("productsChangeAnywayBtn.text");
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
            WebElement button = wait.until(ExpectedConditions.visibilityOfElementLocated(changeAnywayBtn));

            if (button.isDisplayed()) {
                button.click();
                logger.info("✅ Clicked 'Change Anyway' button.");
            }
        } catch (TimeoutException e) {
            logger.info("ℹ️ 'Change Anyway' toast not shown. No action needed.");
        } catch (Exception e) {
            logger.error("❌ Failed to click 'Change Anyway' button: {}", e.getMessage());
            throw new RuntimeException("Failed to handle 'Change Anyway' button", e);
        }
    }

    public void clickChangeAnywayButton() {
        try {
            clickElement(getLocator("productsChangeAnywayBtn.text"));
            logger.info("✅ Clicked 'Change Anyway' button.");
        } catch (Exception e) {
            logger.error("❌ Failed to click 'Change Anyway' button: {}", e.getMessage());
        }
    }

    public boolean verifyEstimatedDeliveryCostIsDisplayed() {
        try {
            By locator = getLocator("estDeliveryCost.xpath");
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

            List<WebElement> elements = wait.until(driver -> {
                List<WebElement> found = driver.findElements(locator);
                for (WebElement element : found) {
                    if (element.isDisplayed()) {
                        return found;
                    }
                }
                return null;
            });

            if (elements != null && !elements.isEmpty()) {
                logger.info("✅ Estimated delivery cost element is visible.");
                return true;
            }

            logger.warn("⚠️ Estimated delivery cost elements found but not visible.");
            return false;

        } catch (TimeoutException e) {
            logger.warn("⏳ Timed out waiting for estimated delivery cost element to be visible.");
            return false;
        } catch (Exception e) {
            logger.error("❌ Error verifying estimated delivery cost: {}", e.getMessage());
            return false;
        }
    }

    public boolean isSearchProductsPageDisplayed() {
        try {
            boolean isTitlePresent = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    getLocator("searchProductsPageTitle.text"))).isDisplayed();

            boolean searchProductsField = wait.until(driver -> {
                try {
                    return driver.findElement(getLocator("searchField.className")).isDisplayed();
                } catch (Exception e) {
                    return false;
                }
            });

            logger.info("📄 Search Product Page Check - Title: {}, Search Field: {}", isTitlePresent, searchProductsField);
            return isTitlePresent && searchProductsField;
        } catch (Exception e) {
            logger.error("❌ Error checking product details page: {}", e.getMessage());
            return false;
        }
    }

    public void enterSearchQuery(String productName) {
        try {
            WebElement searchInput = ElementHelper.waitUntilClickable("productSearchField.xpath", 10);
            searchInput.click();
            searchInput.clear();
            searchInput.sendKeys(productName);
            logger.info("✅ Entered search query: {}", productName);
        } catch (Exception e) {
            logger.error("❌ Failed to enter search query: {}", e.getMessage());
            throw e;
        }
    }

    public void selectFirstSearchResult() {
        try {
            String locator = ConfigurationManager.getProperty("firstSearchResult.className");
            WebElement firstProduct = wait.until(ExpectedConditions.elementToBeClickable(MobileBy.AndroidUIAutomator(locator)));
            firstProduct.click();
            logger.info("✅ Selected first search result");
        } catch (Exception e) {
            logger.error("❌ Failed to select first search result: {}", e.getMessage());
        }
    }

    public boolean isProductsDeliveryTabVisible() {
        try {
            return driver.findElement(getLocator("deliveryAddress.text")).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void userClicksHomeButton() {
        try {
            String automator = ConfigurationManager.getProperty("homeBtn.automator");
            By locator = MobileBy.AndroidUIAutomator(automator);

            clickElement(locator);

            logger.info("✅ Clicked Home button.");
            Allure.step("✅ Clicked Home button.");
        } catch (Exception e) {
            logger.error("❌ Failed to click Home button: {}", e.getMessage());
            Allure.step("❌ Failed to click Home button: " + e.getMessage());
            throw e;
        }
    }
}