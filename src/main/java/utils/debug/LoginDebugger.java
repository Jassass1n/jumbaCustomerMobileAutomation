package utils.debug;

import config.ConfigurationManager;
import drivers.DriverManager;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileBy;
import io.cucumber.java.Scenario;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.ScreenshotUtil;

import java.time.Duration;
import java.util.List;

public class LoginDebugger {

    private static final WebDriver driver = DriverManager.getDriver();
    private static final WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

    public static void debugGuestLogin(Scenario scenario) {
        String guestText = ConfigurationManager.getProperty("continueAsGuest.accessibility");
        System.out.println("🔍 Debugging Guest Login: [" + guestText + "]");
        tryLocateAndClick(guestText, scenario, "guest_login_debug");
    }

    public static void debugOTPLogin(Scenario scenario) {
        String loginText = ConfigurationManager.getProperty("phoneInputField.text");
        System.out.println("🔍 Debugging OTP Login: [" + loginText + "]");
        tryLocateAndClick(loginText, scenario, "otp_login_debug");
    }

    private static void tryLocateAndClick(String visibleText, Scenario scenario, String screenshotName) {
        try {
            // 1. Try UIAutomator exact match
            By exactBy = MobileBy.AndroidUIAutomator("new UiSelector().text(\"" + visibleText + "\")");
            List<WebElement> elements = driver.findElements(exactBy);
            System.out.println("🔍 UIAutomator Exact Match found: " + elements.size());

            if (!elements.isEmpty() && elements.get(0).isDisplayed()) {
                elements.get(0).click();
                System.out.println("✅ Clicked via UIAutomator exact match.");
                return;
            }

            // 2. Try UIAutomator contains
            String keyword = visibleText.split(" ")[0];
            By containsBy = MobileBy.AndroidUIAutomator("new UiSelector().textContains(\"" + keyword + "\")");
            elements = driver.findElements(containsBy);
            System.out.println("🔍 UIAutomator Contains Match found: " + elements.size());

            if (!elements.isEmpty() && elements.get(0).isDisplayed()) {
                elements.get(0).click();
                System.out.println("✅ Clicked via UIAutomator contains match.");
                return;
            }

            // 3. Try XPath fallback
            By xpathBy = By.xpath("//*[contains(@text, '" + keyword + "')]");
            WebElement fallbackBtn = wait.until(ExpectedConditions.visibilityOfElementLocated(xpathBy));
            fallbackBtn.click();
            System.out.println("✅ Clicked via XPath fallback.");

        } catch (TimeoutException te) {
            System.out.println("⏰ Timeout: Element not found for text: " + visibleText);
        } catch (Exception e) {
            System.out.println("❌ Exception during login debug: " + e.getMessage());
        }

        ScreenshotUtil.captureAndAttachScreenshot((AppiumDriver) driver, scenario, screenshotName, true);
        System.out.println("📄 Page Source:\n" + driver.getPageSource());
    }
}