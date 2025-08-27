package pages.accountpage;

import config.ConfigurationManager;
import helpers.LoggerHelper;
import helpers.OrderCardHelper;
import helpers.PropertiesLoader;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileBy;
import io.appium.java_client.android.AndroidDriver;
import io.qameta.allure.Allure;
import io.qameta.allure.model.Status;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pages.BasePage;

import java.time.Duration;
import java.util.Properties;

public class OrdersPage extends BasePage {
    private static final Logger logger = LoggerFactory.getLogger(OrdersPage.class);
    private final Properties configProperties;
    private final AppiumDriver driver;
    private final OrderCardHelper orderCardHelper;

    public OrdersPage(AppiumDriver driver) {
        super(driver);
        this.driver = driver;
        this.configProperties = PropertiesLoader.loadProperties("src/main/resources/config.properties");
        this.orderCardHelper = new OrderCardHelper(driver);
    }

    public boolean isOrdersPageDisplayed() {
        String locatorValue = ConfigurationManager.getProperty("ordersPageTitle.text");
        By ordersPageTitleLocator = MobileBy.AndroidUIAutomator("new UiSelector().text(\"" + locatorValue + "\")");

        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
            boolean isDisplayed = wait.until(ExpectedConditions.visibilityOfElementLocated(ordersPageTitleLocator)).isDisplayed();
            logger.info("✅ Orders page title is displayed.");
            Allure.step("✅ Orders page is displayed.");
            return isDisplayed;
        } catch (Exception e) {
            logger.error("❌ Error checking orders page visibility: {}", e.getMessage());
            Allure.step("❌ Failed to verify Orders page.", Status.FAILED);
            return false;
        }
    }

}