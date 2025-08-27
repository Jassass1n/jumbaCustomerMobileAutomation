package helpers;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileBy;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public class ToastHelper {
    private static final Logger logger = LoggerFactory.getLogger(ToastHelper.class);
    private final AppiumDriver driver;

    public ToastHelper(AppiumDriver driver) {
        this.driver = driver;
    }

    public boolean verifyToastVisible(String partialText, int timeoutSeconds) {
        StepLogger.step("🔍 Verifying toast visibility: " + partialText);
        try {
            By toastLocator = MobileBy.AndroidUIAutomator(
                    "new UiSelector().textContains(\"" + partialText + "\")"
            );

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
            WebElement toast = wait.until(ExpectedConditions.presenceOfElementLocated(toastLocator));

            String toastText = toast.getText();
            logger.info("✅ Toast verified: {}", toastText);
            StepLogger.step("✅ Toast found with text: " + toastText);
            return true;

        } catch (TimeoutException e) {
            logger.warn("⚠️ Toast not found within {} seconds: {}", timeoutSeconds, partialText);
            StepLogger.warn("⚠️ Toast not found within timeout for text: " + partialText);
            return false;

        } catch (Exception e) {
            logger.error("❌ Error during toast verification", e);
            StepLogger.error("❌ Exception during toast verification: " + e.getMessage());
            return false;
        }
    }

    public void waitForToastToDisappear(String partialText, int timeoutSeconds) {
        StepLogger.step("⏳ Waiting for toast to disappear: " + partialText);
        try {
            By toastLocator = MobileBy.AndroidUIAutomator(
                    "new UiSelector().textContains(\"" + partialText + "\")"
            );

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
            wait.until(ExpectedConditions.invisibilityOfElementLocated(toastLocator));

            logger.info("✅ Toast disappeared: {}", partialText);
            StepLogger.step("✅ Toast disappeared: " + partialText);

        } catch (TimeoutException e) {
            logger.warn("⚠️ Toast did not disappear in {} seconds: {}", timeoutSeconds, partialText);
            StepLogger.warn("⚠️ Toast did not disappear in time: " + partialText);

        } catch (Exception e) {
            logger.error("❌ Error waiting for toast to disappear", e);
            StepLogger.error("❌ Exception while waiting for toast to disappear: " + e.getMessage());
        }
    }

    public boolean verifyAndWaitForToast(String partialText, int visibleTimeout, int disappearTimeout) {
        StepLogger.step("📋 Verifying and waiting for toast: " + partialText);
        boolean found = verifyToastVisible(partialText, visibleTimeout);
        if (found) {
            waitForToastToDisappear(partialText, disappearTimeout);
            return true;
        }
        return false;
    }
}