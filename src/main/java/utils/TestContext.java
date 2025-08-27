package utils;

import drivers.DriverManager;
import helpers.NavigationHelper;
import io.appium.java_client.AppiumDriver;
import io.cucumber.java.Scenario;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class TestContext {

    private AppiumDriver driver;
    private WebDriverWait wait;
    private Scenario scenario;
    private NavigationHelper navigationHelper;

    private static final Duration TIMEOUT = Duration.ofSeconds(30);

    // ==== Shared Payment Context ====
    public static String paymentMethod;
    public static String referenceNumber;
    public static String paymentAmount;
    public static String paymentDateTime;

    // === Driver & Wait Management ===

    public AppiumDriver getDriver() {
        if (driver == null) {
            driver = DriverManager.initializeDriver(); // Uses config-driven settings
            DriverManager.setDriver(driver);
            wait = new WebDriverWait(driver, TIMEOUT);
        }
        return driver;
    }

    public void setDriver(AppiumDriver driver) {
        if (driver != null) {
            this.driver = driver;
            this.wait = new WebDriverWait(driver, TIMEOUT);
        }
    }

    public WebDriverWait getWait() {
        if (wait == null && driver != null) {
            wait = new WebDriverWait(driver, TIMEOUT);
        }
        return wait;
    }

    public void tearDown() {
        if (driver != null) {
            try {
                driver.quit();
                System.out.println("🧹 Driver terminated");
            } catch (Exception e) {
                System.err.println("⚠️ Failed to quit driver: " + e.getMessage());
            } finally {
                driver = null;
                wait = null;
                DriverManager.setDriver(null);
            }
        }
    }

    public Scenario getScenario() {
        return scenario;
    }

    public void setScenario(Scenario scenario) {
        this.scenario = scenario;
    }

    public NavigationHelper getNavigationHelper() {
        return navigationHelper;
    }

    public void setNavigationHelper(NavigationHelper navigationHelper) {
        this.navigationHelper = navigationHelper;
    }

    // === Debug Info ===

    public static void logPaymentContext() {
        System.out.println("📦 Payment Context:");
        System.out.println("  📌 Method: " + paymentMethod);
        System.out.println("  💰 Amount: " + paymentAmount);
        System.out.println("  🧾 Reference: " + referenceNumber);
        System.out.println("  🕒 DateTime: " + paymentDateTime);
    }
}