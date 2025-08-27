package stepdefinitions;

import helpers.ElementHelper;
import io.cucumber.java.en.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import pages.ActiveOrders;
import pages.home.HomePage;
import pages.ProductsPage;
import utils.TestContext;

public class HomeSteps {

    private static final Logger logger = LoggerFactory.getLogger(HomeSteps.class);

    private final HomePage homePage;
    private final ProductsPage productsPage;
    private final ActiveOrders activeOrders;

    public HomeSteps(TestContext context) {
        this.homePage = new HomePage(context.getDriver());
        this.productsPage = new ProductsPage(context.getDriver());
        this.activeOrders = new ActiveOrders(context.getDriver());
    }

    @Given("the user is on the Home page")
    public void userIsOnHomePage() {
        logger.info("🔍 Waiting for Home page to load...");
        boolean isLoaded = homePage.waitForPageLoad(10);
        boolean isDisplayed = homePage.waitForHomePageToLoad();
        logger.info("✅ Home page loaded: {}, displayed: {}", isLoaded, isDisplayed);
        Assert.assertTrue(isLoaded && isDisplayed, "Home page should be fully loaded and displayed");
    }

    // ==== Delivery Tab Flow ====

    @When("the user switches to the Delivery tab")
    public void userSwitchesToDeliveryTab() {
        logger.info("🔄 Switching to Delivery tab...");
        try {
            homePage.switchToDeliveryTab();
            logger.info("✅ Switched to Delivery tab");
        } catch (Exception e) {
            logger.error("❌ Error switching to Delivery tab: {}", e.getMessage());
            Assert.fail("Failed to switch to Delivery tab");
        }
    }

    @Then("the Delivery page should be displayed")
    public void verifyDeliveryPageDisplayed() {
        boolean displayed = homePage.isDeliveryPageDisplayed();
        logger.info("📦 Delivery page displayed: {}", displayed);
        Assert.assertTrue(displayed, "Delivery page should be displayed");
    }

    // ====== View Active Orders List ======
    @When("user clicks Track Order button")
    public void userClicksTrackOrderButton() {
        logger.info("🔄 Clicking Track Order button...");
        try {
            activeOrders.viewTrackOrderlist();
            logger.info("✅ Clicked Track Order button");
        } catch (Exception e) {
            logger.error("❌ Error clicking Track Order button: {}", e.getMessage());
            Assert.fail("Failed to click Track Order button");
        }
    }

    // ====== Select any order from the list ======
    @And("Selects any order from the list")
    public void selectsAnyOrderFromTheList() {
        logger.info("🔄 Selecting any order from the list...");
        try {
            activeOrders.selectOrderByPriority();
            logger.info("✅ Selected any order from the list");
        } catch (Exception e) {
            logger.error("❌ Error selecting order: {}", e.getMessage());
            Assert.fail("Failed to select any order from the list");
        }
    }

    // ==== Self Collect Tab Flow ====

    @When("the user switches to the Self Collect tab")
    public void userSwitchesToSelfCollectTab() {
        logger.info("🔄 Switching to Self Collect tab...");
        try {
            homePage.switchToSelfCollectTab();
            logger.info("✅ Switched to Self Collect tab");
        } catch (Exception e) {
            logger.error("❌ Error switching to Self Collect tab: {}", e.getMessage(), e);
            Assert.fail("Failed to switch to Self Collect tab");
        }
    }

    @Then("the order tracking page should be displayed")
    public void verifyOrderTrackingPageDisplayed() {
        boolean displayed = activeOrders.isOrderTrackingPageDisplayed();
        logger.info("📦 Order Tracking page displayed: {}", displayed);
        Assert.assertTrue(displayed, "Order Tracking page should be displayed");
    }

    @Then("the Self Collect page should be displayed")
    public void verifySelfCollectPageDisplayed() {
        boolean isLoaded = homePage.waitForPageLoad(10);
        boolean isDisplayed = homePage.isSelfCollectPageDisplayed();
        logger.info("✅ Self Collect page loaded: {}, displayed: {}", isLoaded, isDisplayed);
        ElementHelper.takeScreenshot("self_collect_tab", "screenshots");
        Assert.assertTrue(isDisplayed, "Self Collect page should be displayed");
    }

    // ==== Product Search Flow ====

    @When("the user taps on the Search field")
    public void tapSearchField() {
        logger.info("🔎 Tapping on the search field...");
        try {
            homePage.tapSearchField();
            logger.info("✅ Search field tapped");
        } catch (Exception e) {
            logger.error("❌ Failed to tap search field: {}", e.getMessage());
            Assert.fail("Search field tap failed");
        }
    }
}