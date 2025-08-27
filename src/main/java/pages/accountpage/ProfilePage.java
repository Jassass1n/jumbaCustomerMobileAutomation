package pages.accountpage;
import helpers.ElementHelper;
import helpers.PropertiesLoader;
import io.appium.java_client.AppiumDriver;
import io.qameta.allure.Allure;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pages.BasePage;
import pages.FulfilmentDetails;

import java.time.Duration;
import java.util.Properties;

import static helpers.ElementHelper.clickElement;


public class ProfilePage extends BasePage {
    private static final Logger logger = LoggerFactory.getLogger(FulfilmentDetails.class);
    private final Properties configProperties;
    private final WebDriverWait wait;

    public ProfilePage(AppiumDriver driver) {
        super(driver);
        this.configProperties = PropertiesLoader.loadProperties("src/main/resources/config.properties");
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }



    public boolean isProfilePageDisplayed() {
        By locator = ElementHelper.getLocator("profilePageTitle.text");
        return ElementHelper.isElementDisplayed(locator);
    }

    /**
     * Clicks login button on account page and verifies if login page is displayed
     * @return true if login page is displayed, false otherwise
     */
    public void clickProfileButton() {
        By profileButtonLocator = getLocator("profileButton.accessibility");

        try {
            logger.info(" 👤 Clicking Profile button...");
            clickElement(profileButtonLocator);

            if (isProfilePageDisplayed()) {
                logger.info("✅ Profile page displayed after click.");
                Allure.step("✅ Profile page loaded after clicking Profile button.");
            } else {
                logger.warn("⚠️ Profile page not visible after clicking.");
                Allure.step("⚠️ Profile page did not appear.");
            }
        } catch (Exception e) {
            logger.error("❌ Failed to click Profile button: {}", e.getMessage());
            Allure.step("❌ Failed to click Profile button.", () -> {
                throw e;
            });
        }
    }

    public boolean waitForProfilePageLoad(int timeoutSeconds) {
        return ElementHelper.waitUntilVisible(getLocator("profilePageTitle.text"), timeoutSeconds) != null;
    }
}
