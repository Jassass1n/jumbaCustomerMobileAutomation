package stepdefinitions;

import config.ConfigurationManager;
import helpers.ElementHelper;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileBy;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.*;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriverException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pages.home.HomePage;
import pages.login.LoginPage;
import pages.login.OTPPage;
import pages.popups.NotificationPopupPage;
import utils.SessionManager;
import utils.ScreenshotUtil;
import utils.TestContext;

import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

public class LoginSteps {

    private static final Logger logger = LoggerFactory.getLogger(LoginSteps.class);

    private final TestContext context;
    private final AppiumDriver driver;
    private final Scenario scenario;
    private final LoginPage loginPage;
    private final OTPPage otpPage;
    private final HomePage homePage;
    private final NotificationPopupPage notificationPopupPage;

    public LoginSteps(TestContext context) {
        this.context = context;
        this.driver = context.getDriver();

        if (this.driver == null) {
            throw new IllegalStateException("❌ Driver is null in LoginSteps. Hooks may have failed.");
        }

        this.loginPage = new LoginPage(driver);
        this.otpPage = new OTPPage(driver);
        this.scenario = context.getScenario();
        this.homePage = new HomePage(driver);
        this.notificationPopupPage = new NotificationPopupPage(driver);
    }

    @When("the user allows notifications")
    public void allowNotifications() {
        boolean handled = notificationPopupPage.handleNotificationIfPresent();
        logger.info(handled ? "🔔 Notification popup handled." : "🔕 No notification popup appeared.");
    }

    @Then("the login page should be displayed")
    public void verifyLoginPage() {
        long start = System.currentTimeMillis();
        boolean isDisplayed = loginPage.waitForPageLoad(5);
        long duration = System.currentTimeMillis() - start;
        logger.info("⏱️ Login page load time: {}ms", duration);

        assertTrue(isDisplayed, "❌ Login page was not displayed in time.");
    }

    @Given("the user is on the login page")
    public void theUserIsOnLoginPage() {
        if (SessionManager.isLoggedIn()) {
            logger.info("ℹ️ Skipping login page check — user already logged in via Hooks.");
            return;
        }

        assertTrue(loginPage.isLoginPageDisplayed(), "❌ Login page was not displayed after app launch");
    }

    @When("the user selects {string}")
    public void selectLoginOption(String option) {
        if ("Continue as Guest".equalsIgnoreCase(option)) {
            if (!SessionManager.isLoggedIn()) {
                if (driver == null || driver.getSessionId() == null) {
                    throw new IllegalStateException("❌ Driver is not initialized or already quit before selecting guest.");
                }

                logger.info("🔐 Guest login - Session ID: {}", driver.getSessionId());
                loginPage.continueAsGuest();
                SessionManager.setLoggedIn(true);
            } else {
                logger.info("ℹ️ Already logged in as guest. Skipping guest login.");
            }
        } else {
            throw new IllegalArgumentException("❌ Unsupported login option: " + option);
        }
    }

    @When("the user enters valid phone number {string}")
    public void enterPhoneNumber(String number) {
        if (homePage.isHomePageDisplayed()) {
            logger.info("✅ Already on Home Page. Skipping phone number entry.");
            SessionManager.setLoggedIn(true);
            return;
        }

        loginPage.enterPhoneNumber(number);
        loginPage.clickContinue();
    }
    @Then("the user enters valid OTP {string} and is redirected to the home page")
    public void enterOTP(String otp) {
        if (homePage.isHomePageDisplayed()) {
            logger.info("✅ Already on Home Page. Skipping OTP entry.");
            SessionManager.setLoggedIn(true);
            return;
        }

        try {
            takeScreenshot("before_otp_entry");

            otpPage.enterOTP(otp);

            // Optional wait for home page after entering OTP
            if (!homePage.waitForHomePageToLoad()) {
                throw new RuntimeException("❌ Home Page did not appear after OTP.");
            }

            takeScreenshot("after_otp_entry");
            SessionManager.setLoggedIn(true);

        } catch (Exception e) {
            takeScreenshot("otp_input_failure");
            throw new RuntimeException("❌ Failed to enter OTP: " + e.getMessage(), e);
        }
    }

    @Then("verify error message contains {string}")
    public void verifyOtpErrorMessage(String expectedError) {
        String actual = otpPage.getErrorMessage();
        assertTrue(actual.contains(expectedError), "❌ Expected: " + expectedError + " | Actual: " + actual);
    }

    @Given("the user is logged in")
    public void ensureUserIsLoggedIn() {
        if (SessionManager.isLoggedIn()) {
            logger.info("✅ Already logged in.");
            return;
        }

        try {
            notificationPopupPage.handleNotificationIfPresent();
            loginPage.enterPhoneNumber(ConfigurationManager.getProperty("phoneNumber"));
            loginPage.clickContinue();

            if (!otpPage.isOTPPageDisplayed()) {
                ScreenshotUtil.captureAndAttachScreenshot(driver, context.getScenario(), "otp_page_not_displayed", true);
                throw new IllegalStateException("❌ OTP page was not displayed.");
            }

            otpPage.enterOTP(ConfigurationManager.getProperty("otp"));
            otpPage.waitForVerificationAndHomeRedirect();
            assertTrue(homePage.waitForHomePageToLoad(), "❌ Home page was not displayed after login.");
            SessionManager.setLoggedIn(true);

        } catch (Exception e) {
            ScreenshotUtil.captureAndAttachScreenshot(driver, context.getScenario(), "login_failure", true);
            fail("❌ Login failed: " + e.getMessage());
        }
    }

    @Given("the user continues as guest")
    public void continueAsGuest() {
        if (SessionManager.isLoggedIn()) {
            logger.info("🔓 Already logged in. Skipping guest login.");
            return;
        }

        try {
            String continueAsGuestText = ConfigurationManager.getProperty("continueAsGuest.accessibility");
            String loginPageTitleText = ConfigurationManager.getProperty("loginPageTitle.text");

            if (continueAsGuestText == null || continueAsGuestText.isEmpty()) {
                throw new IllegalStateException("❌ Missing 'continueAsGuest.accessibility' in config.properties");
            }
            if (loginPageTitleText == null || loginPageTitleText.isEmpty()) {
                throw new IllegalStateException("❌ Missing 'loginPageTitle.text' in config.properties");
            }

            By guestBtnBy = MobileBy.AndroidUIAutomator("new UiSelector().text(\"" + continueAsGuestText + "\")");
            By loginTitleBy = MobileBy.AndroidUIAutomator("new UiSelector().text(\"" + loginPageTitleText + "\")");

            boolean isReady = ElementHelper.waitForAnyElementVisible(
                    new By[]{guestBtnBy, loginTitleBy}, 20
            );

            if (!isReady) {
                throw new IllegalStateException("❌ Login screen did not load in time.");
            }

            notificationPopupPage.handleNotificationIfPresent();
            loginPage.continueAsGuest();

            assertTrue(homePage.waitForHomePageToLoad(), "❌ Home page did not load.");
            SessionManager.setLoggedIn(false);

        } catch (Exception e) {
            ScreenshotUtil.captureAndAttachScreenshot(driver, context.getScenario(), "guest_login_failed", true);
            fail("❌ Guest login failed: " + e.getMessage());
        }
    }

    @Then("the home page should be displayed")
    public void verifyHomePage() {
        assertTrue(homePage.waitForHomePageToLoad(), "❌ Home page was not displayed after login.");
    }

    private void takeScreenshot(String label) {
        if (driver != null) {
            try {
                byte[] screenshot = driver.getScreenshotAs(OutputType.BYTES);
                scenario.attach(screenshot, "image/png", label);
            } catch (WebDriverException ex) {
                logger.warn("⚠️ Could not take screenshot '{}': {}", label, ex.getMessage());
            }
        }
    }
}