package helpers;

import config.ConfigurationManager;
import io.appium.java_client.MobileBy;
import io.qameta.allure.Allure;
import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.PlatformHelper;

public class LocatorHelper {
    private static final Logger logger = LoggerFactory.getLogger(LocatorHelper.class);

    public static By resolveLocator(String locatorKey) {
        String locatorValue = ConfigurationManager.getProperty(locatorKey);

        if (locatorValue == null || locatorValue.trim().isEmpty()) {
            throw new IllegalArgumentException("❌ Locator value is missing for key: " + locatorKey);
        }

        String key = locatorKey.toLowerCase();
        String raw = locatorValue.trim();
        boolean isAndroid = PlatformHelper.isAndroid();

        // 🔹 Suffix-based strategy (based on locatorKey)
        if (key.endsWith(".id") || key.endsWith(".resourceid")) return By.id(raw);
        if (key.endsWith(".xpath")) return By.xpath(raw);
        if (key.endsWith(".accessibility") || key.endsWith(".accessibilityid"))
            return MobileBy.AccessibilityId(raw);
        if (key.endsWith(".classname")) return By.className(raw);

        if (key.endsWith(".text")) {
            return isAndroid
                    ? MobileBy.AndroidUIAutomator("new UiSelector().text(\"" + raw + "\")")
                    : MobileBy.AccessibilityId(raw); // Fallback for iOS
        }

        if (key.endsWith(".containstext") && isAndroid)
            return MobileBy.AndroidUIAutomator("new UiSelector().textContains(\"" + raw + "\")");

        if (key.endsWith(".indextext") && isAndroid)
            return MobileBy.AndroidUIAutomator("new UiSelector().text(\"" + raw + "\").instance(0)");

        if (key.endsWith(".uiautomator") && isAndroid)
            return MobileBy.AndroidUIAutomator(raw);

        // 🔹 Prefix-based strategy (based on locatorValue)
        if (raw.startsWith("xpath=")) return By.xpath(raw.substring(6));
        if (raw.startsWith("id=")) return By.id(raw.substring(3));
        if (raw.startsWith("accessibilityId=")) return MobileBy.AccessibilityId(raw.substring(16));

        if (raw.startsWith("text=")) {
            String value = raw.substring(5);
            return isAndroid
                    ? MobileBy.AndroidUIAutomator("new UiSelector().text(\"" + value + "\")")
                    : MobileBy.AccessibilityId(value); // iOS fallback
        }

        if (raw.startsWith("new UiSelector") && isAndroid)
            return MobileBy.AndroidUIAutomator(raw);

        // ❌ Unsupported fallback
        String type = key.contains(".") ? key.substring(key.lastIndexOf('.') + 1) : "unknown";
        String message = "❌ Unsupported locator type for key: " + locatorKey + " (type: " + type + ")";

        logger.error(message);
        Allure.step(message);
        throw new IllegalArgumentException(message);
    }
}