package utils;

import drivers.DriverManager;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.Platform;
import org.openqa.selenium.remote.CapabilityType;

public class PlatformHelper {

    public static boolean isAndroid() {
        AppiumDriver driver = DriverManager.getDriver();
        if (driver == null) return false;

        Platform platform = (Platform) driver.getCapabilities().getCapability(CapabilityType.PLATFORM_NAME);
        return platform != null && platform.name().equalsIgnoreCase("android");
    }

    public static boolean isIOS() {
        AppiumDriver driver = DriverManager.getDriver();
        if (driver == null) return false;

        Platform platform = (Platform) driver.getCapabilities().getCapability(CapabilityType.PLATFORM_NAME);
        return platform != null && platform.name().equalsIgnoreCase("ios");
    }

    public static String getPlatformName() {
        AppiumDriver driver = DriverManager.getDriver();
        if (driver == null) return "unknown";

        Object platform = driver.getCapabilities().getCapability(CapabilityType.PLATFORM_NAME);
        return platform != null ? platform.toString().toLowerCase() : "unknown";
    }
}