package utils;

import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.HashMap;
import java.util.Map;

public class ScrollHelper {

    private AndroidDriver driver;

    public ScrollHelper(AndroidDriver driver) {
        this.driver = driver;
    }

    public boolean scrollUntilVisible(By locator, int maxScrolls) {
        for (int i = 0; i < maxScrolls; i++) {
            try {
                WebElement element = driver.findElement(locator);
                if (element.isDisplayed()) {
                    System.out.println("✅ Element is now visible after " + (i + 1) + " scroll(s).");
                    return true;
                }
            } catch (Exception ignored) {}

            // Perform swipe up
            performSwipeUp();
        }
        System.out.println("❌ Failed to make element visible after " + maxScrolls + " scrolls.");
        return false;
    }

    private void performSwipeUp() {
        Map<String, Object> params = new HashMap<>();
        params.put("direction", "up");
        params.put("percent", 0.75);
        params.put("left", 100);
        params.put("top", 300);
        params.put("width", 600);
        params.put("height", 1200);

        try {
            driver.executeScript("mobile: swipeGesture", params);
            Thread.sleep(800); // Slight wait after scroll
            System.out.println("↕️ Performed swipe up.");
        } catch (Exception e) {
            System.out.println("❌ Swipe failed: " + e.getMessage());
        }
    }
}