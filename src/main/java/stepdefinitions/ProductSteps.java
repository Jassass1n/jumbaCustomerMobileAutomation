package stepdefinitions;
import config.ConfigurationManager;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;
import pages.home.HomePage;
import pages.components.PickupLocationPage;
import pages.ProductsPage;
import utils.LogUtils;
import utils.TestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class ProductSteps {

    private final ProductsPage productsPage;
    private final LogUtils log = new LogUtils(ProductSteps.class);
    private final HomePage homePage;
    private final PickupLocationPage pickupLocationPage;
    private static final Logger logger = LoggerFactory.getLogger(HomeSteps.class);

    public ProductSteps(TestContext context) {
        this.productsPage = new ProductsPage(context.getDriver());
        this.homePage = new HomePage(context.getDriver());
        this.pickupLocationPage = new PickupLocationPage(context.getDriver());
    }

    @When("user clicks products button")
    public void userClicksProductsButton() {
        System.out.println("🖱️ Clicking on products button...");
        try {
            productsPage.clickProductsButton();
            System.out.println("✅ Clicked on products button.");
        } catch (Exception e) {
            System.out.println("❌ Failed to click products button: " + e.getMessage());
            Assert.fail("❌ Could not click on products button: " + e.getMessage());
        }
    }

    @When("the user switches to the products Delivery tab")
    public void theUserSwitchesToTheProductsDeliveryTab() {
        System.out.println("🔄 Switching to Products Delivery tab...");
        try {
            productsPage.switchToProductsDeliveryTabAndVerifyFlow();
            System.out.println("✅ Successfully switched to Products Delivery tab and verified flow.");
        } catch (Exception e) {
            System.out.println("❌ Failed during Products Delivery tab flow: " + e.getMessage());
            Assert.fail("❌ Could not switch to Products Delivery tab: " + e.getMessage());
        }
    }

    @Then("the user switches back to Self Collect tab")
    public void theUserSwitchesBackToSelfCollectTab() {
        System.out.println("🔁 Switching back to Self Collect...");
        try {
            productsPage.switchBackToSelfCollectAndVerify();
            System.out.println("✅ Successfully switched back to Self Collect.");
        } catch (Exception e) {
            System.out.println("❌ Failed to switch back to Self Collect: " + e.getMessage());
            Assert.fail("❌ Could not switch back to Self Collect: " + e.getMessage());
        }
    }

    @When("the user enters a valid product name")
    public void enterSearchProduct() {
        String product = ConfigurationManager.getProperty("productName");
        logger.info("⌨️ Entering product name: {}", product);

        if (product == null || product.trim().isEmpty()) {
            logger.error("❌ Product name from config is null or empty");
            Assert.fail("Product name must be provided in config.properties");
        }

        try {
            homePage.enterSearchQuery(product);
            logger.info("✅ Product name entered");
        } catch (Exception e) {
            logger.error("❌ Failed to enter search query: {}", e.getMessage());
            Assert.fail("Failed to enter product name");
        }
    }

    @When("the user taps on the first product in the search results")
    public void selectFirstSearchResult() {
        logger.info("🛒 Selecting the first product in the search results...");
        try {
            productsPage.selectFirstSearchResult();
            Thread.sleep(1000); // can be replaced with wait helper
            productsPage.selectFirstSearchResult();
            logger.info("✅ Product selected: {}", ConfigurationManager.getProperty("productName"));
        } catch (Exception e) {
            logger.error("❌ Failed to select product: {}", e.getMessage());
            Assert.fail("Failed to select first product in search results");
        }
    }

    @When("user selects a pickup location")
    public void selectPickupLocation() {
        logger.info("📍 Selecting pickup location...");
        try {
            pickupLocationPage.selectValidPickupLocation();
            logger.info("✅ Pickup location selected");
        } catch (Exception e) {
            logger.error("❌ Failed to select pickup location: {}", e.getMessage());
            Assert.fail("Failed to select pickup location");
        }
    }

    @Then("the Product Details page should be displayed")
    public void verifyProductsPageDisplayed() {
        boolean displayed = productsPage.isProductsPageDisplayed();
        logger.info("📄 Product Details page displayed: {}", displayed);
        Assert.assertTrue(displayed, "Product Details page should be displayed");
    }

}