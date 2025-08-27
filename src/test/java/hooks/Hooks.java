package hooks;

import config.ConfigurationManager;
import config.EmailConfigManager;
import drivers.DriverManager;
import helpers.*;
import io.appium.java_client.AppiumDriver;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.qameta.allure.Allure;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriverException;
import org.slf4j.Logger;
import pages.*;
import pages.accountpage.*;
import pages.components.PickupLocationPage;
import pages.home.HomePage;
import pages.login.LoginPage;
import pages.login.OTPPage;
import pages.orders.Reorder;
import pages.popups.NotificationPopupPage;
import pages.popups.PermissionHandler;
import stepdefinitions.*;
import stepdefinitions.accountpage.AccountPageSteps;
import stepdefinitions.payments.PaymentPageSteps;
import utils.*;

import java.io.ByteArrayInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

import static helpers.AppStateDetector.AppState;

public class Hooks {

    private NavigationHelper navigationHelper;
    private static final Logger logger = LoggerHelper.getLogger(Hooks.class);
    private final TestContext testContext;
    private static String lastFeatureFile = null;
    private static boolean reportSent = false;

    public Hooks(TestContext context) {
        this.testContext = context;
    }

    @Before
    public void beforeScenario(Scenario scenario) {
        testContext.setScenario(scenario);
        Set<String> tags = new HashSet<>(scenario.getSourceTagNames());
        String currentFeatureFile = extractFeatureFileName(scenario);
        boolean shouldResetApp = shouldReset(tags, currentFeatureFile);

        AppiumDriver driver = DriverManager.getDriver();

        if (shouldResetApp || DriverUtils.isDriverInvalid(driver)) {
            logger.info("🔄 Resetting session before scenario: {}", scenario.getName());
            SessionManager.resetSession();
            DriverManager.quitDriver();
            initializeDriverWithRetry();
            driver = DriverManager.getDriver();
            testContext.setDriver(driver);
            lastFeatureFile = currentFeatureFile;
            handleStartupPopups(driver);
            buildNavigationHelper(driver);
        } else {
            if (DriverUtils.isDriverInvalid(driver)) {
                logger.warn("⚠️ Reused driver is invalid. Reinitializing...");
                DriverManager.quitDriver();
                initializeDriverWithRetry();
            }
            driver = DriverManager.getDriver();
            testContext.setDriver(driver);
            buildNavigationHelper(driver);
        }

        if (DriverUtils.isDriverInvalid(driver)) {
            String errorMsg = "❌ Appium driver is not initialized before scenario: " + scenario.getName();
            logger.error(errorMsg);
            Allure.addAttachment("Driver Initialization Error", errorMsg);
            throw new IllegalStateException(errorMsg);
        }

        AppState currentState = AppStateDetector.detectAppState();

        if (tags.contains("@guestSession")) {
            handleGuestSession(currentState);
        } else if (tags.contains("@otpSession")) {
            handleOtpSession(currentState);
        } else if (tags.contains("@notification") || tags.contains("@resetSession") || tags.contains("@freshLaunch")) {
            logger.info("🌱 Fresh launch or notification-only scenario — skipping login.");
        } else {
            logger.warn("❓ No session tag found. Defaulting to OTP session.");
            handleOtpSession(currentState);
        }

        if (tags.contains("@notifications")) {
            logger.info("🔔 Handling notification popup...");
            new PopupHandler(driver).handleDiscountPopupIfPresent();
        }
    }

    @After
    public void afterScenario(Scenario scenario) {
        AppiumDriver driver = DriverManager.getDriver();

        try {
            if (driver != null && driver.getSessionId() != null) {
                driver.getPageSource(); // refresh layout
                byte[] screenshot = driver.getScreenshotAs(OutputType.BYTES);
                String title = scenario.isFailed() ? "❌ Failure Screenshot" : "✅ Success Screenshot";
                scenario.attach(screenshot, "image/png", title);
                Allure.addAttachment(title, new ByteArrayInputStream(screenshot));
                logger.info("📸 Screenshot attached to Allure report.");
            }
        } catch (Exception e) {
            logger.error("❌ Error capturing screenshot: {}", e.getMessage(), e);
            Allure.addAttachment("Screenshot Error", e.toString());
        }

        Set<String> tags = new HashSet<>(scenario.getSourceTagNames());
        boolean shouldCleanup = scenario.isFailed() || tags.stream().anyMatch(tag ->
                tag.equals("@logout") || tag.equals("@selfcollect") ||
                        tag.equals("@delivery") || tag.equals("@order"));

        if (shouldCleanup) {
            logger.info("🧹 Tearing down session after scenario: {}", scenario.getName());
            DriverManager.quitDriver();
            testContext.tearDown();
            logger.info("✅ Session ended.");
        }

        if (!reportSent) {
            EmailConfigManager.loadProperties();
            if (Boolean.parseBoolean(EmailConfigManager.get("email.enabled").trim())) {
                synchronized (Hooks.class) {
                    if (!reportSent) {
                        try {
                            logger.info("📦 Zipping and emailing Allure report...");
                            ReportZipper.zipFolder("allure-report", "allure-report.zip");
                            EmailReportSender.sendReportEmail("allure-report.zip");
                            reportSent = true;
                            Allure.addAttachment("✅ Report Sent", "Allure report emailed.");
                        } catch (Exception e) {
                            logger.error("❌ Failed to email report: {}", e.getMessage(), e);
                            Allure.addAttachment("Email Error", e.toString());
                        }
                    }
                }
            } else {
                Allure.addAttachment("Email Skipped", "email.enabled=false");
                logger.info("🚫 Email sending is disabled via config");
            }
        }
    }

    private void handleGuestSession(AppState state) {
        AppiumDriver driver = DriverManager.getDriver();
        LoginPage loginPage = new LoginPage(driver);
        NotificationPopupPage popup = new NotificationPopupPage(driver);

        if (state == AppState.HOME_SCREEN_GUEST) {
            logger.info("✅ Already in guest session");
            return;
        }

        logger.info("🧠 Ensuring guest session from state: {}", state);
        switch (state) {
            case NOTIFICATION_POPUP -> {
                popup.handleNotificationIfPresent();
                loginPage.continueAsGuest();
            }
            case LOGIN_SCREEN -> loginPage.continueAsGuest();
            case HOME_SCREEN_OTP -> {
                navigationHelper.logout();
                loginPage.continueAsGuest();
            }
            default -> {
                logger.warn("🔄 Unknown state. Restarting app...");
                DriverManager.quitDriver();
                initializeDriverWithRetry();
                new NotificationPopupPage(DriverManager.getDriver()).handleNotificationIfPresent();
                new LoginPage(DriverManager.getDriver()).continueAsGuest();
            }
        }
    }

    private void handleOtpSession(AppState state) {
        AppiumDriver driver = DriverManager.getDriver();
        LoginPage loginPage = new LoginPage(driver);
        OTPPage otpPage = new OTPPage(driver);
        NotificationPopupPage popup = new NotificationPopupPage(driver);
        String phone = ConfigurationManager.get("phoneNumber");
        String otp = ConfigurationManager.get("otp");

        logger.info("🧠 Ensuring OTP login session from state: {}", state);
        switch (state) {
            case NOTIFICATION_POPUP -> {
                popup.handleNotificationIfPresent();
                loginPage.enterPhoneNumber(phone);
                loginPage.clickContinue();
                otpPage.enterOTP(otp);
                otpPage.waitForVerificationAndHomeRedirect();
            }
            case LOGIN_SCREEN -> {
                loginPage.enterPhoneNumber(phone);
                loginPage.clickContinue();
                otpPage.enterOTP(otp);
                otpPage.waitForVerificationAndHomeRedirect();
            }
            case HOME_SCREEN_OTP -> logger.info("✅ Already in OTP session");
            case HOME_SCREEN_GUEST -> {
                navigationHelper.logout();
                loginPage.enterPhoneNumber(phone);
                loginPage.clickContinue();
                otpPage.enterOTP(otp);
                otpPage.waitForVerificationAndHomeRedirect();
            }
            default -> {
                logger.warn("🔄 Unknown state. Restarting app...");
                DriverManager.quitDriver();
                initializeDriverWithRetry();
                AppiumDriver retryDriver = DriverManager.getDriver();
                new NotificationPopupPage(retryDriver).handleNotificationIfPresent();
                loginPage.enterPhoneNumber(phone);
                loginPage.clickContinue();
                otpPage.enterOTP(otp);
                otpPage.waitForVerificationAndHomeRedirect();
            }
        }
    }

    private AppiumDriver initializeDriverWithRetry() {
        for (int i = 0; i < 2; i++) {
            try {
                AppiumDriver driver = DriverManager.initializeDriver();
                if (!DriverUtils.isDriverInvalid(driver)) {
                    logger.info("✅ Driver created successfully.");
                    return driver;
                }
            } catch (WebDriverException e) {
                logger.warn("⚠️ Driver init failed. Retrying...");
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException ignored) {}
            }
        }
        String msg = "❌ Failed to initialize Appium driver after retries.";
        logger.error(msg);
        Allure.addAttachment("Driver Retry Failure", msg);
        throw new RuntimeException(msg);
    }

    private void handleStartupPopups(AppiumDriver driver) {
        try {
            new PermissionHandler(driver).handleStartupPermissions();
        } catch (Exception e) {
            logger.warn("⚠️ Error during permission popup handling: {}", e.getMessage(), e);
            Allure.addAttachment("Permission Handler Error", e.toString());
        }
    }

    private boolean shouldReset(Set<String> tags, String currentFeatureFile) {
        return tags.contains("@resetSession") ||
                tags.contains("@guest") || tags.contains("@guestSession") ||
                tags.contains("@productSearch") || tags.contains("@selfcollect") ||
                tags.contains("@delivery") || tags.contains("@order") ||
                tags.contains("@logout") ||
                currentFeatureFile.contains("login") ||
                !currentFeatureFile.equals(lastFeatureFile);
    }

    private String extractFeatureFileName(Scenario scenario) {
        try {
            Path path = Paths.get(scenario.getUri().getPath());
            return path.getFileName().toString();
        } catch (Exception e) {
            logger.warn("⚠️ Failed to extract feature file name: {}", e.getMessage());
            return "unknown.feature";
        }
    }

    private void buildNavigationHelper(AppiumDriver driver) {
        HomeSteps homeSteps = new HomeSteps(testContext);
        ProductSteps productSteps = new ProductSteps(testContext);
        OrderSteps orderSteps = new OrderSteps(testContext);
        ProductsPage productsPage = new ProductsPage(driver);
        FulfilmentDetails fulfilmentDetails = new FulfilmentDetails(driver);
        HomePage homePage = new HomePage(driver);
        AccountPage accountPage = new AccountPage(driver);
        OrdersPage ordersPage = new OrdersPage(driver);
        AccountPageSteps accountPageSteps = new AccountPageSteps(testContext);
        ProfilePage profilePage = new ProfilePage(driver);
        PickupLocationPage pickupLocationPage = new PickupLocationPage(driver);
        CartPage cartPage = new CartPage(driver);
        Payments paymentsPage = new Payments(testContext);
        PaymentPageSteps paymentPageSteps = new PaymentPageSteps(testContext);
        Reorder reorder = new Reorder(driver);

        navigationHelper = new NavigationHelper.Builder()
                .withHomeSteps(homeSteps)
                .withProductSteps(productSteps)
                .withProductsPage(productsPage)
                .withOrderSteps(orderSteps)
                .withFulfilmentDetails(fulfilmentDetails)
                .withHomePage(homePage)
                .withAccountPage(accountPage)
                .withOrdersPage(ordersPage)
                .withAccountPageSteps(accountPageSteps)
                .withProfilePage(profilePage)
                .withPickupLocationPage(pickupLocationPage)
                .withCartPage(cartPage)
                .withPaymentsPage(paymentsPage)
                .withPaymentPageSteps(paymentPageSteps)
                .withReorder(reorder)
                .build();

        testContext.setNavigationHelper(navigationHelper);
    }
}