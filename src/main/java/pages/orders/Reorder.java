package pages.orders;

import io.appium.java_client.AppiumDriver;
import io.qameta.allure.Allure;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pages.BasePage;

import java.time.Duration;

public class Reorder extends BasePage {

    private static final Logger logger = LoggerFactory.getLogger(Reorder.class);
    private final WebDriverWait wait;

    private final By orderAgainTitle = getLocator("orderAgainTitle.text");
    private final By completedStatus = getLocator("completedStatus.text");
    private final By reorderButton = getLocator("reorderBtn.automator");

    public Reorder(AppiumDriver driver) {
        super(driver);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    public boolean isorderAgainPageDisplayed() {
        try {
            logger.info("🔍 Checking if Order Again title is visible...");
            boolean isTitlePresent = wait.until(ExpectedConditions
                    .visibilityOfElementLocated(orderAgainTitle)).isDisplayed();

            logger.info("🔍 Checking for Completed status and Reorder button...");
            boolean hasCompletedOrReorderBtn = wait.until(driver -> {
                try {
                    boolean completedVisible = driver.findElement(completedStatus).isDisplayed();
                    boolean reorderVisible = driver.findElement(reorderButton).isDisplayed();
                    return completedVisible || reorderVisible;
                } catch (Exception e) {
                    logger.warn("⚠️ Element missing during check: {}", e.getMessage());
                    return false;
                }
            });

            logger.info("✅ Reorder Page Check - Title: {}, Buttons: {}", isTitlePresent, hasCompletedOrReorderBtn);
            Allure.step("✅ Reorder Page Displayed - Title: " + isTitlePresent + ", Buttons: " + hasCompletedOrReorderBtn);

            return isTitlePresent && hasCompletedOrReorderBtn;

        } catch (WebDriverException e) {
            logger.error("💥 Driver/UiAutomator crash or timeout: {}", e.getMessage());
            Allure.step("💥 Driver crash or timeout while checking Reorder page: " + e.getMessage());
            return false;
        } catch (Exception e) {
            logger.error("❌ Error checking Reorder page: {}", e.getMessage());
            Allure.step("❌ General error checking Reorder page: " + e.getMessage());
            return false;
        }
    }

    public void clickReorderButton() {
        Allure.step("Clicking Reorder button...");
        driver.findElement(reorderButton).click();
        logger.info("✅ Clicked Reorder button");
    }
}