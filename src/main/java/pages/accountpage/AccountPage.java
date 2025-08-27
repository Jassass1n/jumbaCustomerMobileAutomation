package pages.accountpage;

import config.ConfigurationManager;
import helpers.ElementHelper;
import helpers.PropertiesLoader;
import helpers.ToastHelper;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileBy;
import io.qameta.allure.Allure;
import org.openqa.selenium.By;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pages.BasePage;
import pages.FulfilmentDetails;
import pages.login.LoginPage;
import java.time.Duration;
import java.util.Properties;

import static helpers.ElementHelper.clickElement;

public class AccountPage extends BasePage {
    private static final Logger logger = LoggerFactory.getLogger(FulfilmentDetails.class);
    private final Properties configProperties;
    private final WebDriverWait wait;
    private final LoginPage loginPage;
    private final OrdersPage ordersPage;

    public AccountPage(AppiumDriver driver) {
        super(driver);
        this.configProperties = PropertiesLoader.loadProperties("src/main/resources/config.properties");
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        this.loginPage = new LoginPage(driver);
        this.ordersPage = new OrdersPage(driver);
    }

    public boolean isAccountPageDisplayed() {
        String locatorValue = configProperties.getProperty("accountPageTitle.uiautomator");
        return ElementHelper.isElementDisplayed(locatorValue);
    }

    public boolean waitForPageLoad(int timeoutSeconds) {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds))
                    .until(d -> {
                        WebElement element = d.findElement(getLocator("accountPageTitle.uiautomator"));
                        return element != null && element.isDisplayed();
                    });
            return true;
        } catch (Exception e) {
            logger.error("❌ Account Page not loaded: " + e.getMessage());
            Allure.step("❌ Account Page not loaded: " + e.getMessage());
            return false;
        }
    }
    /** Wait for Notifications page to load */
    public boolean waitForNotificationsPageLoad(int timeoutSeconds) {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds))
                    .until(d -> {
                        WebElement element = d.findElement(getLocator("notificationsPreferencePageTitle.text"));
                        return element != null && element.isDisplayed();
                    });
            return true;
        } catch (Exception e) {
            logger.error("❌ Notifications Page not loaded: " + e.getMessage());
            Allure.step("❌ Notifications Page not loaded: " + e.getMessage());
            return false;
        }
    }
    /** Profile page should be displayed */
    public boolean isProfilePageDisplayed() {
        By locator = getLocator("profilePageTitle.text");
        return ElementHelper.isElementDisplayed(locator);
    }

    /** Opens the Notification Preferences page */
    public void clickNotificationPreferencesButton() {
        By locator = getLocator("notificationsPreference.text");
        clickElement(locator);
    }

    /** Notification Preferences page should be displayed */
    public boolean isNotificationPreferencesPageDisplayed() {
        By locator = getLocator("notificationsPreferencePageTitle.text");
        return ElementHelper.isElementDisplayed(locator);
    }

    /** Toggles all notification preferences */
    public void toggleAllNotificationToggles() {
        String[] toggleKeys = {
                "accountVerifiedBtn.xpath",
                "orderStatusBtn.xpath",
                "paymentNotificationBtn.xpath",
                "jengwaUpdatesBtn.xpath"
        };

        for (String key : toggleKeys) {
            try {
                System.out.println("🔁 Toggling: " + key);
                By locator = getLocator(key);
                clickElement(locator);
            } catch (Exception e) {
                System.out.println("❌ Failed to toggle " + key + ": " + e.getMessage());
            }
        }
    }

    /** Clicks Update Preferences and verifies the toast */
    public boolean clickUpdatePreferencesButtonAndVerifyToast() {
        String expectedToast = configProperties.getProperty("notificationSuccessMessage.text");
        By buttonLocator = getLocator("updatePreferences.text");

        try {
            Allure.step("🔘 Clicking 'Update Preferences' button");
            clickElement(buttonLocator);

            ToastHelper toastHelper = new ToastHelper(driver);
            boolean isToastDisplayed = toastHelper.verifyToastVisible(expectedToast, 5);

            if (isToastDisplayed) {
                logger.info("✅ Toast displayed after updating preferences: {}", expectedToast);
                Allure.step("✅ Toast displayed: " + expectedToast);
                return true;
            } else {
                logger.warn("❌ Expected toast not displayed: {}", expectedToast);
                Allure.step("❌ Expected toast not found: " + expectedToast);
                return false;
            }
        } catch (Exception e) {
            logger.error("❌ Error verifying toast after clicking update preferences", e);
            Allure.step("❌ Exception occurred while checking toast: " + e.getMessage());
            return false;
        }
    }

    /** Logs out and verifies redirect to login page */
    public void clickLogoutButton() {
        By logoutBtn = getLocator("logoutBtn.text");
        By loginButton = getLocator("loginButton.text");
        By loginPageTitle = getLocator("loginPageTitle.text"); // Ensure this is in your config

        try {
            Allure.step("🔘 Clicking Logout button");
            wait.until(ExpectedConditions.elementToBeClickable(logoutBtn)).click();
            logger.info("✅ Clicked Logout button");

            Allure.step("🔎 Verifying Login button is visible after logout");
            if (wait.until(ExpectedConditions.visibilityOfElementLocated(loginButton)).isDisplayed()) {
                logger.info("✅ Login button is displayed after logout");

                Allure.step("🔘 Clicking Login button");
                wait.until(ExpectedConditions.elementToBeClickable(loginButton)).click();
                logger.info("✅ Clicked Login button");

                Allure.step("🔎 Verifying Login page is displayed");
                if (wait.until(ExpectedConditions.visibilityOfElementLocated(loginPageTitle)).isDisplayed()) {
                    logger.info("✅ Verified login page is displayed");
                    Allure.step("✅ Login page displayed successfully");
                } else {
                    logger.error("❌ Login page not displayed after clicking login button");
                    Allure.step("❌ Login page not displayed after clicking login button");
                    throw new RuntimeException("Login page not displayed after logout");
                }
            } else {
                logger.error("❌ Login button not visible after logout");
                Allure.step("❌ Login button not visible after logout");
                throw new RuntimeException("Login button not visible after logout");
            }
        } catch (Exception e) {
            logger.error("❌ Logout and login redirection failed", e);
            Allure.step("❌ Exception during logout/login redirection: " + e.getMessage());
            throw e;
        }
    }

    private By getProfilePhoneNumberLocator() {
        String rawNumber = ConfigurationManager.getProperty("phoneNumber");
        String fullNumber = "+254" + rawNumber;

        return MobileBy.AndroidUIAutomator("new UiSelector().text(\"" + fullNumber + "\")");
    }

    /**
     * Verifies if the logged-in phone number shown on profile page matches expected full number
     * @return true if matches, false otherwise
     */
    public boolean isLoggedInPhoneNumberCorrect() {
        try {
            By phoneNumberLocator = getProfilePhoneNumberLocator();
            String actualPhoneNumber = driver.findElement(phoneNumberLocator).getText().trim();

            String rawNumber = ConfigurationManager.getProperty("phoneNumber");
            String expectedPhoneNumber = "+254" + rawNumber;
            logger.info("📱 Expected: " + expectedPhoneNumber + " | Found: " + actualPhoneNumber);
            Allure.step("📱 Expected: " + expectedPhoneNumber + " | Found: " + actualPhoneNumber);

            return actualPhoneNumber.equals(expectedPhoneNumber);
        } catch (Exception e) {
            logger.error("❌ Error verifying phone number", e);
            Allure.step("❌ Exception occurred while verifying phone number: " + e.getMessage());
            return false;
        }
    }

    /**
     * Checks if profile page is displayed by checking if phone number element is visible
     * @return true if displayed, false otherwise
     */
    public boolean isAccountProfileDisplayed() {
        By phoneNumberLocator = getProfilePhoneNumberLocator();
        return ElementHelper.isElementDisplayed(phoneNumberLocator);
    }
    /**
     * Clicks login button on account page and verifies if login page is displayed
     * @return true if login page is displayed, false otherwise
     */
    public boolean clickLoginButton() {
        By loginButtonLocator = getLocator("loginButton.text");
        clickElement(loginButtonLocator);
        return loginPage.isLoginPageDisplayed();
    }



    public void clickOrdersButton() {
        By ordersButtonLocator = getLocator("ordersButton.accessibility");

        try {
            logger.info("🛒 Clicking Orders button...");
            clickElement(ordersButtonLocator);

            if (ordersPage.isOrdersPageDisplayed()) {
                logger.info("✅ Orders page displayed after click.");
                Allure.step("✅ Orders page loaded after clicking Orders button.");
            } else {
                logger.warn("⚠️ Orders page not visible after clicking.");
                Allure.step("⚠️ Orders page did not appear.");
            }
        } catch (Exception e) {
            logger.error("❌ Failed to click Orders button: {}", e.getMessage());
            Allure.step("❌ Failed to click Orders button.", () -> {
                throw e;
            });
        }
    }


}

