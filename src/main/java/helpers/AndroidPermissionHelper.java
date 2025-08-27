package helpers;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileBy;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

public class AndroidPermissionHelper {

    private final AppiumDriver driver;
    private final WebDriverWait wait;

    public AndroidPermissionHelper(AppiumDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(5));
    }

    private final List<By> allowLocators = Arrays.asList(
            By.id("com.android.permissioncontroller:id/permission_allow_button"),
            By.id("com.android.permissioncontroller:id/permission_allow_foreground_only_button"),
            By.id("com.android.permissioncontroller:id/permission_allow_media_button"),
            MobileBy.AndroidUIAutomator("new UiSelector().text(\"Allow\")"),
            MobileBy.AndroidUIAutomator("new UiSelector().text(\"While using the app\")"),
            MobileBy.AndroidUIAutomator("new UiSelector().text(\"Only this time\")")
    );

    public boolean acceptIfPresent() {
        try {
            for (int attempt = 0; attempt < 3; attempt++) {
                for (By locator : allowLocators) {
                    List<WebElement> buttons = driver.findElements(locator);
                    if (!buttons.isEmpty()) {
                        buttons.get(0).click();
                        System.out.println("✅ Accepted permission popup via: " + locator);
                        Thread.sleep(1000);
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("⚠️ Failed while handling Android permission: " + e.getMessage());
        }
        return false;
    }
}