package helpers;

import config.ConfigurationManager;
import drivers.DriverManager;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileBy;
import io.qameta.allure.Allure;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ElementHelper {
    private static final int DEFAULT_WAIT_TIME = 10;
    private static final Logger logger = LoggerFactory.getLogger(ElementHelper.class);

    private ElementHelper() {
        throw new UnsupportedOperationException("Utility class — do not instantiate");
    }

    public static By getLocator(String locatorKey) {
        return LocatorHelper.resolveLocator(locatorKey);
    }

    public static WebElement waitForElementVisible(AppiumDriver driver, By locator, int timeoutInSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public static WebElement waitForElementVisible(AppiumDriver driver, By locator, int timeout, WebElement parent) {
        Logger logger = LoggerFactory.getLogger(ElementHelper.class);

        try {
            logger.debug("🔎 Waiting for element [{}] inside parent...", locator);
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
            WebElement element = wait.until(driver1 -> {
                List<WebElement> elements = parent.findElements(locator);
                for (WebElement el : elements) {
                    if (el.isDisplayed()) {
                        return el;
                    }
                }
                return null;
            });

            if (element != null) {
                logger.info("✅ Found and visible: [{}] inside parent", locator);
                return element;
            } else {
                logger.warn("⚠️ Element [{}] not found or not visible in parent within {}s", locator, timeout);
            }
        } catch (Exception e) {
            logger.error("❌ Failed to find visible element [{}] in parent: {}", locator, e.getMessage());
        }

        throw new NoSuchElementException("Element " + locator.toString() + " not visible in parent");
    }

    public static List<WebElement> waitForElementsVisible(AppiumDriver driver, By locator, int timeoutInSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
        return wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
    }

    public static WebElement getElement(By locator) {
        return new WebDriverWait(getDriverSafely(), Duration.ofSeconds(DEFAULT_WAIT_TIME))
                .until(ExpectedConditions.presenceOfElementLocated(locator));
    }



    public static WebElement getClickableElement(By locator) {
        return new WebDriverWait(getDriverSafely(), Duration.ofSeconds(DEFAULT_WAIT_TIME))
                .until(ExpectedConditions.elementToBeClickable(locator));
    }

    public static boolean isElementDisplayed(String locatorKey) {
        return isElementDisplayed(getLocator(locatorKey));
    }

    public static void clickElement(String locatorKey) {
        clickElement(getLocator(locatorKey));
    }

    public static boolean isElementDisplayed(By locator) {
        try {
            return getElement(locator).isDisplayed();
        } catch (Exception e) {
            logger.warn("❌ Element not displayed: {} → {}", locator, e.getMessage());
            return false;
        }
    }

    public static void clickElement(By locator) {
        try {
            getClickableElement(locator).click();
            logger.info("✅ Clicked element: {}", locator);
        } catch (Exception e) {
            logger.error("❌ Failed to click element: {} → {}", locator, e.getMessage());
            throw e;
        }
    }

    public static boolean waitForAnyElementVisible(By[] locators, int timeoutInSeconds) {
        WebDriverWait wait = new WebDriverWait(getDriverSafely(), Duration.ofSeconds(timeoutInSeconds));
        try {
            return wait.until(driver -> {
                for (By locator : locators) {
                    try {
                        if (driver.findElement(locator).isDisplayed()) return true;
                    } catch (Exception ignored) {}
                }
                return false;
            });
        } catch (TimeoutException e) {
            logger.warn("❌ None of the elements became visible within timeout.");
            return false;
        }
    }

    public static boolean isElementDisplayed(By locator, int timeoutInSeconds) {
        try {
            WebDriverWait wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(timeoutInSeconds));
            wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    public static boolean waitForElementToBeClickable(By locator, int timeoutInSeconds) {
        try {
            new WebDriverWait(getDriverSafely(), Duration.ofSeconds(timeoutInSeconds))
                    .until(ExpectedConditions.elementToBeClickable(locator));
            return true;
        } catch (TimeoutException e) {
            logger.warn("❌ Element not clickable: {}", locator);
            return false;
        }
    }

    public static boolean isElementPresent(By locator, int timeoutInSeconds) {
        try {
            new WebDriverWait(getDriverSafely(), Duration.ofSeconds(timeoutInSeconds))
                    .until(ExpectedConditions.presenceOfElementLocated(locator));
            return true;
        } catch (TimeoutException e) {
            logger.warn("❌ Element not present: {}", locator);
            return false;
        }
    }

    public static boolean isElementPresent(String locatorKey) {
        By locator = LocatorHelper.resolveLocator(locatorKey);
        return isElementPresent(locator, 5);
    }

    public static boolean waitForElementToBeVisible(By locator, int timeoutInSeconds) {
        try {
            new WebDriverWait(getDriverSafely(), Duration.ofSeconds(timeoutInSeconds))
                    .until(ExpectedConditions.visibilityOfElementLocated(locator));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    public static void clickElement(WebElement element) {
        try {
            new WebDriverWait(getDriverSafely(), Duration.ofSeconds(DEFAULT_WAIT_TIME))
                    .until(ExpectedConditions.elementToBeClickable(element)).click();
            logger.info("✅ Clicked WebElement.");
        } catch (Exception e) {
            logger.error("❌ Failed to click WebElement: {}", e.getMessage());
            throw e;
        }
    }

    // ✅ Original method (kept for backward compatibility)
    public static boolean scrollToText(AppiumDriver driver, String visibleText) {
        try {
            String uiSelector = "new UiScrollable(new UiSelector().scrollable(true))" +
                    ".scrollIntoView(new UiSelector().text(\"" + visibleText + "\"))";

            logger.info("🌀 Attempting to scroll to text: '{}'", visibleText);
            driver.findElement(AppiumBy.androidUIAutomator(uiSelector));
            logger.info("✅ Successfully scrolled to '{}'", visibleText);
            return true;

        } catch (Exception e) {
            logger.error("❌ Could not scroll to text '{}'. Reason: {}", visibleText, e.getMessage());
            return false;
        }
    }

    // ✅ New version with boolean return
    public static boolean scrollToTextAndReturn(String text) {
        int maxRetries = 3;
        int attempt = 0;

        while (attempt < maxRetries) {
            try {
                String platform = getDriverSafely().getCapabilities().getPlatformName().toString();

                if ("Android".equalsIgnoreCase(platform)) {
                    String scrollable = "new UiScrollable(new UiSelector().scrollable(true))" +
                            ".scrollIntoView(new UiSelector().text(\"" + text + "\"))";
                    getDriverSafely().findElement(MobileBy.AndroidUIAutomator(scrollable));
                } else if ("iOS".equalsIgnoreCase(platform)) {
                    getDriverSafely().findElement(MobileBy.iOSNsPredicateString("label == '" + text + "' || name == '" + text + "'"));
                }

                logger.info("✅ Successfully scrolled to text: {}", text);
                return true;

            } catch (Exception e) {
                attempt++;
                logger.warn("⚠️ Attempt {} to scroll to '{}' failed: {}", attempt, text, e.getMessage());

                if (attempt >= maxRetries) {
                    logger.error("❌ Failed to scroll to text after {} attempts: {}", maxRetries, text);
                    return false;
                }

                delay(1000); // optional pause between retries
            }
        }

        return false; // fallback
    }

    public static void tapElementByBounds(String bounds) {
        Matcher matcher = Pattern.compile("\\[(\\d+),(\\d+)]\\[(\\d+),(\\d+)]").matcher(bounds);
        if (matcher.find()) {
            int x1 = Integer.parseInt(matcher.group(1));
            int y1 = Integer.parseInt(matcher.group(2));
            int x2 = Integer.parseInt(matcher.group(3));
            int y2 = Integer.parseInt(matcher.group(4));
            int centerX = (x1 + x2) / 2;
            int centerY = (y1 + y2) / 2;

            PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
            Sequence tap = new Sequence(finger, 1);
            tap.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), centerX, centerY));
            tap.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
            tap.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

            getDriverSafely().perform(Collections.singletonList(tap));
            logger.info("✅ Tapped at: ({}, {})", centerX, centerY);
        } else {
            throw new IllegalArgumentException(String.format("❌ Invalid bounds format: %s", bounds));
        }
    }

    public static void takeScreenshot(String screenshotName, String folderName) {
        try {
            File src = ((TakesScreenshot) getDriverSafely()).getScreenshotAs(OutputType.FILE);
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String filePath = System.getProperty("user.dir") + "/screenshots/" + folderName + "/" + screenshotName + "_" + timestamp + ".png";
            FileUtils.copyFile(src, new File(filePath));
            logger.info("✅ Screenshot saved at: {}", filePath);
        } catch (Exception e) {
            logger.error("❌ Failed to take screenshot: {}", e.getMessage());
        }
    }

    public static WebElement waitUntilClickable(String locatorKey, int timeoutSeconds) {
        By locator = resolveLocator(locatorKey);
        return new WebDriverWait(getDriverSafely(), Duration.ofSeconds(timeoutSeconds))
                .until(ExpectedConditions.elementToBeClickable(locator));
    }

    private static By resolveLocator(String locatorKey) {
        String raw = ConfigurationManager.getProperty(locatorKey);

        if (raw == null) {
            throw new IllegalArgumentException(String.format("❌ Locator not found for key: %s", locatorKey));
        }

        raw = raw.trim();

        if (raw.startsWith("new UiSelector()")) {
            return MobileBy.AndroidUIAutomator(raw);
        } else if (raw.startsWith("//") || raw.startsWith("(//")) {
            return By.xpath(raw);
        } else if (raw.contains(":id/")) {
            return By.id(raw);
        } else if (!raw.contains(" ")) {
            return MobileBy.AccessibilityId(raw);
        } else {
            throw new IllegalArgumentException(String.format("❌ Unrecognized locator format: %s", raw));
        }
    }

    public static boolean isElementDisplayed(String locatorKey, int timeoutInSeconds) {
        try {
            By locator = getLocator(locatorKey);
            return isElementDisplayed(locator, timeoutInSeconds);
        } catch (Exception e) {
            logger.warn("❌ Element '{}' not visible: {}", locatorKey, e.getMessage());
            return false;
        }
    }

    public static WebElement waitUntilVisible(By locator, int timeoutSeconds) {
        try {
            return new WebDriverWait(getDriverSafely(), Duration.ofSeconds(timeoutSeconds))
                    .until(ExpectedConditions.visibilityOfElementLocated(locator));
        } catch (TimeoutException e) {
            logger.debug("⏱️ Element not visible after {}s: {}", timeoutSeconds, locator);
            return null;
        }
    }

    public static AppiumDriver getDriverSafely() {
        AppiumDriver driver = DriverManager.getDriver();
        if (driver == null || driver.getSessionId() == null) {
            throw new IllegalStateException("❌ Driver is null or session is dead. Cannot proceed.");
        }
        return driver;
    }

    public static void safeAllureStep(String message) {
        try {
            Allure.step(message);
        } catch (Exception e) {
            logger.debug("⚠️ Skipped Allure step '{}'. No test context active.", message);
        }
    }

    public static void delay(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
            safeAllureStep("⏱ Delay for " + milliseconds + " ms");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("❌ Delay interrupted: {}", e.getMessage());
        }
    }
    public static void clickElementByText(AppiumDriver driver, String text) {
        try {
            WebElement element = driver.findElement(MobileBy.AndroidUIAutomator(
                    "new UiSelector().text(\"" + text + "\")"));
            element.click();
            logger.info("✅ Clicked element with text: {}", text);
        } catch (Exception e) {
            logger.error("❌ Could not click element with text '{}': {}", text, e.getMessage());
            throw new RuntimeException(e);
        }
    }
    public static void tapBelowElement(By locator, int offsetY) {
        try {
            WebElement element = getDriverSafely().findElement(locator);
            Point location = element.getLocation();
            Dimension size = element.getSize();

            int tapX = location.getX() + size.getWidth() / 2;
            int tapY = location.getY() + size.getHeight() + offsetY;

            PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
            Sequence tap = new Sequence(finger, 1);
            tap.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), tapX, tapY));
            tap.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
            tap.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

            getDriverSafely().perform(Collections.singletonList(tap));
            logger.info("✅ Tapped below element at ({}, {})", tapX, tapY);
        } catch (Exception e) {
            logger.error("❌ Failed to tap below element: {}", e.getMessage());
            throw e;
        }
    }

    // -------------------------- 📜 Scrolling --------------------------
    public static void scrollToText(String text) {
        try {
            String platform = getDriverSafely().getCapabilities().getPlatformName().toString();
            if ("Android".equalsIgnoreCase(platform)) {
                String scrollable = "new UiScrollable(new UiSelector().scrollable(true)).scrollIntoView(new UiSelector().text(\"" + text + "\"))";
                getDriverSafely().findElement(MobileBy.AndroidUIAutomator(scrollable));
                System.out.println("✅ Scrolled to text: " + text);
            } else if ("iOS".equalsIgnoreCase(platform)) {
                WebElement element = getDriverSafely().findElement(MobileBy.iOSNsPredicateString("label == '" + text + "' || name == '" + text + "'"));
                System.out.println("✅ Found iOS element: " + text);
            } else {
                System.out.println("⚠️ Unsupported platform: " + platform);
            }
        } catch (Exception e) {
            System.out.println("❌ Scroll to text failed: " + text + " → " + e.getMessage());
        }
    }




}