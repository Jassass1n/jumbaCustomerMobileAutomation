package pages.components;

import config.ConfigurationManager;
import drivers.DriverManager;
import helpers.ElementHelper;
import helpers.KeyboardHelper;
import helpers.LocatorHelper;
import helpers.PermissionHelper;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import io.appium.java_client.ios.IOSDriver;
import io.cucumber.java.Scenario;
import io.qameta.allure.Allure;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import stepdefinitions.LoginSteps;
import utils.ScreenshotUtil;
import utils.TestContext;
import utils.AndroidUtils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class BankTransferComponent {

    private final AppiumDriver driver;
    private final TestContext testContext;
    private final WebDriverWait wait;
    private final boolean isAndroid;
    private final Scenario scenario;

    private final String bankTransferTextStr = ConfigurationManager.get("payment.bankTransfer.text");
    private static final Logger logger = LoggerFactory.getLogger(LoginSteps.class);

    private final By headsUpToast;
    private final By referenceNumberInput;
    private final By uploadButton;
    private final By confirmPaymentButton;
    private final By uploadImageButton;

    public BankTransferComponent(TestContext context) {
        this.testContext = context;
        this.driver = context.getDriver();
        this.scenario = context.getScenario();
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        this.isAndroid = driver.getCapabilities().getPlatformName().toString().equalsIgnoreCase("Android");

        headsUpToast = byId(ConfigurationManager.get("headsUpNotificationOkBtn.id"));
        referenceNumberInput = getLocator("payment.referenceField.text");
        uploadButton = getLocator("bankTransferUploadBtn.text");
        confirmPaymentButton = MobileBy.AccessibilityId(ConfigurationManager.get("payment.confirmPaymentBtn.accessibilityId"));
        uploadImageButton = getLocator("uploadImageOption.text");
    }


    public void selectRandomBankFromDropdown() {
        // Step 1: Click on dropdown (using accessibility ID from config)
        String dropdownAccId = ConfigurationManager.get("payment.bank.dropdown");
        WebElement dropdown = ElementHelper.waitForElementVisible(driver, AppiumBy.accessibilityId(dropdownAccId), 10);
        dropdown.click();

        // Step 2: Parse options from config and pick one at random
        String[] bankOptions = ConfigurationManager.get("payment.bank.options").split(",");
        String selectedBank = bankOptions[new Random().nextInt(bankOptions.length)].trim();

        System.out.println("🔘 Attempting to select bank: " + selectedBank);

        // Step 3: Scroll to and tap the option using visible text
        ElementHelper.scrollToText(selectedBank); // scroll helper can be platform-specific
        By optionLocator = By.xpath("//*[@text='" + selectedBank + "']");
        WebElement bankOption = ElementHelper.waitForElementVisible(driver, optionLocator, 10);
        bankOption.click();

        System.out.println("✅ Selected bank: " + selectedBank);
    }

    public void expandBankTransferSectionIfNeeded() {
        try {
            ElementHelper.scrollToText(bankTransferTextStr);

            if (!ElementHelper.isElementDisplayed(referenceNumberInput)) {
                System.out.println("ℹ️ Expanding Bank Transfer section...");
                ElementHelper.clickElement(ElementHelper.getLocator("payment.bankTransfer.text"));

                Thread.sleep(1000); // optional wait
                new PermissionHelper(driver).allowAllPermissionsIfPresent();
                handleHeadsUpToast();

                ElementHelper.scrollToText(ConfigurationManager.get("payment.confirmPaymentBtn"));
                System.out.println("✅ Expanded Bank Transfer section");
            } else {
                System.out.println("✅ Bank Transfer section already visible");
            }
        } catch (Exception e) {
            Assert.fail("❌ Failed to expand Bank Transfer section: " + e.getMessage());
        }
    }


    public void enterReferenceNumber(String referenceNumber) {
        if (referenceNumber == null || referenceNumber.trim().isEmpty()) {
            System.out.println("⚠️ Reference number is empty. Skipping input.");
            return;
        }

        try {
            System.out.println("➡️ Starting to enter reference number: " + referenceNumber);

            // Scroll to the label text to bring field into view
            String refLabelText = ConfigurationManager.get("payment.referenceField.text"); // "Enter Payment Reference Number"
            ElementHelper.scrollToText(driver, refLabelText);

            // Locate input field using precise XPath (editable field)
            By inputLocator = By.xpath(ConfigurationManager.get("payment.referenceField.xpath"));
            List<WebElement> inputs = driver.findElements(inputLocator);

            WebElement inputField = null;
            for (WebElement el : inputs) {
                if (el.isDisplayed() && el.isEnabled()) {
                    inputField = el;
                    break;
                }
            }

            if (inputField == null) {
                throw new RuntimeException("❌ No visible and enabled input field found.");
            }

            System.out.println("📍 Element ready - Displayed: true, Enabled: true");

            inputField.click(); // Ensure focus
            Thread.sleep(500);

            // Clear field (via End + multiple DEL keys)
            ((AndroidDriver) driver).pressKey(new KeyEvent(AndroidKey.MOVE_END));
            for (int i = 0; i < 12; i++) {
                ((AndroidDriver) driver).pressKey(new KeyEvent(AndroidKey.DEL));
            }

            // Type reference using Android keys
            for (char c : referenceNumber.toCharArray()) {
                ((AndroidDriver) driver).pressKey(new KeyEvent(getAndroidKey(c)));
            }

            Thread.sleep(500); // Wait for UI update
            // Hide keyboard after typing
            KeyboardHelper.hideKeyboard(driver);

            // Verify input using getAttribute("text")
            String actual = inputField.getAttribute("text");
            System.out.println("📋 Retrieved input field text: " + actual);


            System.out.println("📋 Retrieved input field text: " + actual);
            if (actual == null || !actual.equalsIgnoreCase(referenceNumber)) {
                ScreenshotUtil.captureAndAttachScreenshot(driver, scenario, "RefNumber_Mismatch", true);
                throw new AssertionError("❌ Input verification failed: expected '" + referenceNumber + "', but found '" + actual + "'");
            }

            ScreenshotUtil.captureAndAttachScreenshot(driver, scenario, "RefNumber_Entry_Success", false);
            System.out.println("✅ Successfully entered reference number: " + referenceNumber);

        } catch (Exception e) {
            ScreenshotUtil.captureAndAttachScreenshot(driver, scenario, "RefNumber_Entry_Error", true);
            System.out.println("❌ Failed to enter reference number: " + e.getMessage());
            Assert.fail("❌ Failed to enter reference number: " + e.getMessage());
        }
    }

    public static void hideKeyboard(AppiumDriver driver) {
        try {
            if (driver instanceof AndroidDriver) {
                ((AndroidDriver) driver).hideKeyboard();
            } else if (driver instanceof IOSDriver) {
                ((IOSDriver) driver).hideKeyboard();
            }
        } catch (Exception e) {
            System.out.println("⚠️ Could not hide keyboard: " + e.getMessage());
        }
    }

    private AndroidKey getAndroidKey(char c) {
        switch (c) {
            case '0': return AndroidKey.DIGIT_0;
            case '1': return AndroidKey.DIGIT_1;
            case '2': return AndroidKey.DIGIT_2;
            case '3': return AndroidKey.DIGIT_3;
            case '4': return AndroidKey.DIGIT_4;
            case '5': return AndroidKey.DIGIT_5;
            case '6': return AndroidKey.DIGIT_6;
            case '7': return AndroidKey.DIGIT_7;
            case '8': return AndroidKey.DIGIT_8;
            case '9': return AndroidKey.DIGIT_9;
            case 'A': return AndroidKey.A;
            case 'B': return AndroidKey.B;
            case 'C': return AndroidKey.C;
            case 'D': return AndroidKey.D;
            case 'E': return AndroidKey.E;
            case 'F': return AndroidKey.F;
            case 'G': return AndroidKey.G;
            case 'H': return AndroidKey.H;
            case 'I': return AndroidKey.I;
            case 'J': return AndroidKey.J;
            case 'K': return AndroidKey.K;
            case 'L': return AndroidKey.L;
            case 'M': return AndroidKey.M;
            case 'N': return AndroidKey.N;
            case 'O': return AndroidKey.O;
            case 'P': return AndroidKey.P;
            case 'Q': return AndroidKey.Q;
            case 'R': return AndroidKey.R;
            case 'S': return AndroidKey.S;
            case 'T': return AndroidKey.T;
            case 'U': return AndroidKey.U;
            case 'V': return AndroidKey.V;
            case 'W': return AndroidKey.W;
            case 'X': return AndroidKey.X;
            case 'Y': return AndroidKey.Y;
            case 'Z': return AndroidKey.Z;
            default:
                throw new IllegalArgumentException("❌ Unsupported character for AndroidKey: '" + c + "'");
        }
    }

    public void uploadProofOfPayment(String folderName, int waitSeconds) {
        try {
            clickUploadButton();
            clickUploadImageOption();
            new PermissionHelper(driver).allowAllPermissionsIfPresent();

            AndroidUtils.openAndroidFolder(driver, folderName, waitSeconds); // ✅ Moved to utility
            Thread.sleep(waitSeconds * 1000L);

            boolean success = AndroidUtils.selectImageWithFallback(driver); // ✅ Also moved
            if (!success) {
                ScreenshotUtil.captureAndAttachScreenshot(driver, scenario, "UploadProofError", true);
                throw new RuntimeException("❌ No selectable image found in the folder, all strategies failed.");
            }
        } catch (Exception e) {
            ScreenshotUtil.captureAndAttachScreenshot(driver, scenario, "UploadProofError", true);
            throw new RuntimeException("❌ Failed to upload image: " + e.getMessage(), e);
        }
    }

    public boolean confirmPaymentIfEnabled() {
        try {
            WebElement button = wait.until(ExpectedConditions.visibilityOfElementLocated(confirmPaymentButton));
            ElementHelper.scrollToTextAndReturn(ConfigurationManager.get("payment.confirmPaymentBtn"));

            boolean enabled = "true".equalsIgnoreCase(button.getAttribute("enabled"));
            if (enabled) {
                button.click();
                System.out.println("🟢 Clicked Confirm Payment");
                return true;
            } else {
                System.out.println("⚠️ Confirm Payment button is disabled.");
                return false;
            }
        } catch (Exception e) {
            System.out.println("❌ Confirm Payment failed: " + e.getMessage());
            return false;
        }
    }

    private void handleHeadsUpToast() {
        try {
            wait.withTimeout(Duration.ofSeconds(5)).until(ExpectedConditions.elementToBeClickable(headsUpToast)).click();
            System.out.println("ℹ️ Dismissed heads-up toast");
        } catch (Exception ignored) {
            System.out.println("✅ No heads-up toast appeared");
        }
    }

    private void clickUploadButton() {
        wait.until(ExpectedConditions.elementToBeClickable(uploadButton)).click();
        System.out.println("📤 Clicked Upload button");
    }

    private void clickUploadImageOption() {
        wait.until(ExpectedConditions.elementToBeClickable(uploadImageButton)).click();
        System.out.println("🖼️ Selected 'Upload Image'");
    }

    private boolean selectImageBasedOnAndroidVersion() {
        String version = driver.getCapabilities().getCapability("platformVersion").toString();
        if (version.startsWith("12")) {
            return clickFirstThumbnailImageAndroid12();
        } else {
            return selectImageWithFallbackAndroid14();
        }
    }

    private boolean clickFirstThumbnailImageAndroid12() {
        try {
            List<WebElement> thumbs = driver.findElements(By.id("com.google.android.documentsui:id/icon_thumb"));
            for (WebElement thumb : thumbs) {
                if (thumb.isDisplayed()) {
                    String desc = thumb.getAttribute("content-desc");
                    if (desc != null && desc.toLowerCase().contains("image")) {
                        thumb.click();
                        System.out.println("✅ Clicked thumbnail with content-desc: " + desc);
                        return true;
                    }
                    thumb.click(); // fallback
                    System.out.println("✅ Clicked visible thumbnail (fallback)");
                    return true;
                }
            }
            System.out.println("❌ No visible thumbnails found.");
        } catch (Exception e) {
            System.out.println("❌ Failed in Android 12 thumbnail logic: " + e.getMessage());
        }
        return false;
    }

    private boolean selectImageWithFallbackAndroid14() {
        try {
            WebElement image = driver.findElement(MobileBy.AndroidUIAutomator(
                    "new UiSelector().className(\"android.view.View\").instance(9)"));
            image.click();
            System.out.println("✅ Image clicked via instance(9)");
            return true;
        } catch (Exception ignored) {
            System.out.println("⚠️ Fallback to clickable images...");
        }

        List<WebElement> images = driver.findElements(By.xpath(
                "//android.widget.ImageView[@clickable='true'] | //android.view.ViewGroup[@clickable='true']"));

        if (!images.isEmpty()) {
            images.get(0).click();
            System.out.println("✅ Image clicked via fallback clickable element");
            return true;
        }

        for (WebElement el : driver.findElements(By.xpath("//*"))) {
            if ("true".equalsIgnoreCase(el.getAttribute("displayed"))) {
                String bounds = el.getAttribute("bounds");
                ElementHelper.tapElementByBounds(bounds);
                System.out.println("✅ Tapped element via bounds: " + bounds);
                return true;
            }
        }

        System.out.println("❌ No image found with Android 14 fallback strategy.");
        return false;
    }

    private void openAndroidFolder(String folderName, int waitSeconds) {
        try {
            WebDriverWait folderWait = new WebDriverWait(driver, Duration.ofSeconds(waitSeconds));

            List<By> tabs = List.of(
                    MobileBy.xpath("//android.widget.TextView[@text='Albums']"),
                    MobileBy.xpath("//android.widget.TextView[@text='Collections']")
            );
            for (By tab : tabs) {
                if (!driver.findElements(tab).isEmpty()) {
                    driver.findElement(tab).click();
                    System.out.println("📁 Switched to folder tab: " + tab);
                    break;
                }
            }

            By showRoots = MobileBy.AccessibilityId("Show roots");
            if (!driver.findElements(showRoots).isEmpty()) {
                driver.findElement(showRoots).click();
                System.out.println("☰ Clicked 'Show roots'");
            }

            String lower = folderName.toLowerCase();
            List<By> folderLocators = List.of(
                    MobileBy.xpath("//*[contains(translate(@text, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '" + lower + "')]"),
                    MobileBy.AccessibilityId(folderName)
            );

            for (By locator : folderLocators) {
                if (!driver.findElements(locator).isEmpty()) {
                    driver.findElement(locator).click();
                    System.out.println("📂 Opened folder: " + folderName);
                    return;
                }
            }

            throw new RuntimeException("Folder not found: " + folderName);
        } catch (Exception e) {
            throw new RuntimeException("Failed to open folder: " + e.getMessage(), e);
        }
    }

    private By getLocator(String key) {
        String value = ConfigurationManager.get(key);
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("❌ Locator key '" + key + "' is null or empty.");
        }
        return isAndroid ? MobileBy.AndroidUIAutomator("new UiSelector().text(\"" + value + "\")") : MobileBy.AccessibilityId(value);
    }

    private By byId(String id) {
        return MobileBy.id(id);
    }
}