package pages.login;

import config.ConfigurationManager;
import helpers.ElementHelper;
import helpers.StepLogger;
import helpers.ToastHelper;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileBy;
import io.qameta.allure.Allure;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pages.BasePage;
import utils.SessionManager;

import java.time.Duration;

public class LoginPage extends BasePage {
    private static final Logger logger = LoggerFactory.getLogger(ToastHelper.class);
    private final By phoneField = getLocator("phoneInputField.text");
    private final By SmsOtp = getLocator("SmsOtp.text");
    private final By loginTitle = getLocator("loginPageTitle.text");
    private final By otpTitle = getLocator("verifyOtpTitle.text");
    private final By guestButton = MobileBy.AccessibilityId(ConfigurationManager.get("continueAsGuest.accessibility"));
    private final By networkError = getLocator("internalError.text");

    public LoginPage(AppiumDriver driver) {
        super(driver);
    }

    public WebElement getLoginPageElement() {
        return driver.findElement(phoneField);
    }

    public WebElement getLoginPageTitleElement() {
        return driver.findElement(loginTitle);
    }

    public void enterPhoneNumber(String phoneNumber) {
        logger.info("📱 Entering phone number: " + phoneNumber);
        Allure.step("📱 Entering phone number: " + phoneNumber);
        driver.findElement(phoneField).sendKeys(phoneNumber);
    }
    public void clickContinue() {
        StepLogger.step("➡️ Clicking Continue button");
        driver.findElement(SmsOtp).click();

        try {
            // ✅ Wait for OTP screen to appear quickly (e.g., within 6–8 seconds)
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(8));
            wait.until(ExpectedConditions.visibilityOfElementLocated(otpTitle));

            StepLogger.step("✅ OTP screen appeared.");
        } catch (TimeoutException e) {
            StepLogger.warn("⏳ OTP screen not visible within expected time. Checking for errors...");

            // Fallback: check for error toast or message
            try {
                WebElement errorElement = driver.findElement(networkError);
                String errorText = errorElement.getText().trim();
                StepLogger.error("❌ Network or app error: " + errorText);
            } catch (NoSuchElementException ex) {
                StepLogger.step("⚠️ No network error found, but OTP screen is still missing.");
            }
        }
    }

    public void continueAsGuest() {
        final String homeIndicatorKey = "selfCollectBtn.text";
        final String guestButtonKey = "continueAsGuest.accessibility";

        By homeIndicatorLocator = ElementHelper.getLocator(homeIndicatorKey);
        By guestButtonLocator = ElementHelper.getLocator(guestButtonKey);

        // ✅ If already on Home screen, assume guest session
        if (ElementHelper.isElementPresent(homeIndicatorLocator, 3)) {
            logger.info("🟢 Already on Home screen. Guest login assumed.");
            Allure.step("🟢 Already on Home screen. Guest login assumed.");
            return;
        }

        // ✅ If on login page and guest button is visible, click it
        if (ElementHelper.waitForElementToBeVisible(guestButtonLocator, 5)) {
            logger.info("👉 Guest button visible. Clicking...");
            Allure.step("👉 Guest button visible. Clicking...");
            ElementHelper.clickElement(guestButtonLocator);
        } else {
            throw new RuntimeException("❌ Guest button not found. App may have skipped login page.");
        }
    }

    public boolean isLoginPageDisplayed() {
        logger.info("🔍 Checking if login page is displayed...");
        Allure.step("🔍 Checking if login page is displayed...");
        try {
            WebElement titleElement = ElementHelper.waitUntilVisible(loginTitle, 5);
            boolean isDisplayed = titleElement != null && titleElement.isDisplayed();
            logger.info(isDisplayed ? "✅ Login page is visible." : "❌ Login page not visible.");
            Allure.step(isDisplayed ? "✅ Login page is visible." : "❌ Login page not visible.");
            return isDisplayed;
        } catch (Exception e) {
            logger.error("💥 Error checking login page: " + e.getMessage());
            Allure.step("💥 Error checking login page: " + e.getMessage());
            return false;
        }
    }

    public boolean waitForPageLoad(int timeoutSeconds) {
        WebElement title = ElementHelper.waitUntilVisible(loginTitle, timeoutSeconds);
        return title != null;
    }

}