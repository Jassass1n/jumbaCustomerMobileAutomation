package pages.login;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.qameta.allure.Allure;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pages.BasePage;
import utils.AndroidUtils;

import java.time.Duration;

public class OTPPage extends BasePage {
    private static final Logger logger = LoggerFactory.getLogger(OTPPage.class);

    private final By errorMessage = getLocator("errorMessage.resourceId");
    private final By verifyOtpTitle = getLocator("verifyOtpTitle.text");
    private final By verifyButton = getLocator("verifyButton.accessibility");
    private final By verifyingText = getLocator("verifyingOTP.text");
    private final By loader = getLocator("otpLoader.uiautomator");
    private final By homeScreenElement = getLocator("selfCollectBtn.text");
    private final By otpField = MobileBy.AndroidUIAutomator("new UiSelector().resourceId(\"otp-input\").instance(0)");

    public OTPPage(AppiumDriver driver) {
        super(driver);
    }

    public boolean isOTPPageDisplayed() {
        long startTime = System.currentTimeMillis();
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.presenceOfElementLocated(verifyButton));

            boolean isDisplayed = driver.findElement(verifyButton).isDisplayed();
            long duration = System.currentTimeMillis() - startTime;

            logger.info("✅ OTP Page is displayed in {} ms", duration);
            Allure.step("✅ OTP Page is displayed in " + duration + " ms");
            return isDisplayed;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("❌ OTP Page NOT displayed after {} ms: {}", duration, e.getMessage());
            Allure.step("❌ OTP Page NOT displayed: " + e.getMessage());
            return false;
        }
    }

    public void enterOTP(String otp) {
        if (!isOTPPageDisplayed()) {
            throw new RuntimeException("❌ OTP page not visible. Cannot enter OTP.");
        }

        AndroidDriver androidDriver = (AndroidDriver) this.driver;

        try {
            logger.info("⌨️ Entering OTP: {}", otp);
            Allure.step("⌨️ Entering OTP: " + otp);

            // Focus the field (optional)
            WebElement otpInput = new WebDriverWait(driver, Duration.ofSeconds(5))
                    .until(ExpectedConditions.elementToBeClickable(otpField));
            otpInput.click();

            for (char digit : otp.toCharArray()) {
                int digitValue = Character.getNumericValue(digit);
                if (digitValue < 0 || digitValue > 9) {
                    throw new IllegalArgumentException("Invalid OTP digit: " + digit);
                }

                AndroidKey key = AndroidKey.valueOf("DIGIT_" + digitValue);
                AndroidUtils.pressKeyWithRetry(androidDriver, key, 2, 200);
            }

            logger.info("✅ OTP entered successfully.");
            Allure.step("✅ OTP entered successfully.");
        } catch (Exception e) {
            logger.error("❌ Failed to enter OTP: {}", e.getMessage(), e);
            throw new RuntimeException("❌ Failed to enter OTP: " + e.getMessage(), e);
        }
    }

    public void waitForVerificationAndHomeRedirect() {
        try {
            long start = System.currentTimeMillis();
            logger.info("⏱️ Waiting for OTP verification and redirection to Home...");
            Allure.step("⏱️ Waiting for OTP verification and redirection to Home...");

            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(3));
            WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(15));

            // Step 1: Optional verifying text
            try {
                shortWait.until(ExpectedConditions.visibilityOfElementLocated(verifyingText));
                logger.info("⏳ 'Verifying...' appeared.");
                Allure.step("⏳ 'Verifying...' appeared.");
            } catch (Exception ignored) {
                logger.info("⚠️ 'Verifying...' text skipped or disappeared quickly.");
            }

            // Step 2: Loader
            try {
                shortWait.until(ExpectedConditions.presenceOfElementLocated(loader));
                logger.info("⏳ Loader appeared.");
                Allure.step("⏳ Loader appeared.");
                longWait.until(ExpectedConditions.invisibilityOfElementLocated(loader));
                logger.info("✅ Loader dismissed.");
                Allure.step("✅ Loader dismissed.");
            } catch (Exception ignored) {
                logger.info("⚠️ Loader skipped or already gone.");
            }

            // Step 3: Wait for Home
            longWait.until(ExpectedConditions.presenceOfElementLocated(homeScreenElement));
            long duration = System.currentTimeMillis() - start;

            logger.info("✅ Home Page detected after OTP. Time taken: {} ms", duration);
            Allure.step("✅ Home Page detected after OTP. Time taken: " + duration + " ms");
        } catch (Exception e) {
            logger.error("❌ Verification or redirect to Home failed: {}", e.getMessage(), e);
            throw new RuntimeException("❌ Verification or redirect to Home failed: " + e.getMessage(), e);
        }
    }

    public String getErrorMessage() {
        try {
            return new WebDriverWait(driver, Duration.ofSeconds(5))
                    .until(ExpectedConditions.visibilityOfElementLocated(errorMessage))
                    .getText();
        } catch (Exception e) {
            return "❌ Error message not found or not visible.";
        }
    }
}