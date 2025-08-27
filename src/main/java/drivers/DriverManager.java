package drivers;

import config.ConfigurationManager;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.MalformedURLException;
import java.net.URL;

public class DriverManager {

    private static final ThreadLocal<AppiumDriver> threadLocalDriver = new ThreadLocal<>();

    public static AppiumDriver getDriver() {
        return threadLocalDriver.get();
    }

    public static void setDriver(AppiumDriver driver) {
        threadLocalDriver.set(driver);
    }

    public static void resetDriver() {
        quitDriver();
    }

    public static AppiumDriver initializeDriver() {
        String platform = ConfigurationManager.get("platformName").toUpperCase();
        boolean fullReset = Boolean.parseBoolean(ConfigurationManager.get("appium.fullReset"));
        boolean noReset = Boolean.parseBoolean(ConfigurationManager.get("noReset"));
        boolean dontStopAppOnReset = Boolean.parseBoolean(ConfigurationManager.get("dontStopAppOnReset"));

        System.out.println("📱 Initializing driver for platform: " + platform + " | fullReset=" + fullReset + ", noReset=" + noReset + ", dontStopAppOnReset=" + dontStopAppOnReset);

        AppiumDriver driver;
        long start = System.currentTimeMillis();

        switch (platform) {
            case "ANDROID":
                driver = initAndroidDriver(fullReset, noReset, dontStopAppOnReset);
                break;
            case "IOS":
                driver = initIOSDriver(fullReset, noReset);
                break;
            default:
                throw new IllegalArgumentException("❌ Unsupported platform: " + platform);
        }

        long duration = System.currentTimeMillis() - start;
        System.out.println("✅ Driver created successfully in " + duration + " ms");

        setDriver(driver);
        return driver;
    }

    public static void quitDriver() {
        AppiumDriver driver = getDriver();
        if (driver != null) {
            try {
                driver.quit();
                System.out.println("✅ Driver session quit successfully.");
            } catch (Exception e) {
                System.err.println("⚠️ Failed to quit driver: " + e.getMessage());
            }
        }
        threadLocalDriver.remove();
    }

    private static AndroidDriver initAndroidDriver(boolean fullReset, boolean noReset, boolean dontStopAppOnReset) {
        DesiredCapabilities caps = new DesiredCapabilities();

        caps.setCapability("platformName", ConfigurationManager.get("platformName"));
        caps.setCapability("automationName", ConfigurationManager.get("automationName"));
        caps.setCapability("deviceName", ConfigurationManager.get("deviceName"));
        caps.setCapability("udid", ConfigurationManager.get("udid"));
        caps.setCapability("platformVersion", ConfigurationManager.get("platformVersion"));
        caps.setCapability("appPackage", ConfigurationManager.get("appPackage"));
        caps.setCapability("appActivity", ConfigurationManager.get("appActivity"));
        caps.setCapability("fullReset", fullReset);
        caps.setCapability("noReset", noReset);
        caps.setCapability("dontStopAppOnReset", dontStopAppOnReset);
        caps.setCapability("newCommandTimeout", 300);

        System.out.println("📦 Android Capabilities:");
        caps.asMap().forEach((k, v) -> System.out.println("  ➤ " + k + ": " + v));

        try {
            URL serverUrl = new URL(ConfigurationManager.get("appiumServerURL"));
            return new AndroidDriver(serverUrl, caps);
        } catch (MalformedURLException e) {
            throw new RuntimeException("❌ Invalid Appium server URL: " + e.getMessage(), e);
        }
    }

    private static IOSDriver initIOSDriver(boolean fullReset, boolean noReset) {
        DesiredCapabilities caps = new DesiredCapabilities();

        caps.setCapability("platformName", "iOS");
        caps.setCapability("automationName", "XCUITest");
        caps.setCapability("deviceName", ConfigurationManager.get("deviceName"));
        caps.setCapability("udid", ConfigurationManager.get("udid"));
        caps.setCapability("platformVersion", ConfigurationManager.get("platformVersion"));
        caps.setCapability("bundleId", ConfigurationManager.get("bundleId"));
        caps.setCapability("useNewWDA", true);
        caps.setCapability("newCommandTimeout", 300);
        caps.setCapability("fullReset", fullReset);
        caps.setCapability("noReset", noReset);

        System.out.println("📦 iOS Capabilities:");
        caps.asMap().forEach((k, v) -> System.out.println("  ➤ " + k + ": " + v));

        try {
            URL serverUrl = new URL(ConfigurationManager.get("appiumServerURL"));
            return new IOSDriver(serverUrl, caps);
        } catch (MalformedURLException e) {
            throw new RuntimeException("❌ Invalid Appium server URL: " + e.getMessage(), e);
        }
    }


}