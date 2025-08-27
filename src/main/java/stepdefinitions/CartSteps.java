package stepdefinitions;

import config.ConfigurationManager;
import helpers.CartHelper;
import io.appium.java_client.AppiumDriver;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;
import pages.CartPage;
import pages.FulfilmentDetails;
import pages.home.HomePage;
import pages.ProductsPage;
import utils.ScreenshotUtil;
import utils.TestContext;

public class CartSteps {
    private final AppiumDriver driver;
    private final TestContext context;
    private final HomePage homePage;
    private final ProductsPage ProductsPage;
    private final HomeSteps homeSteps;
    private final CartPage cartPage;
    private final FulfilmentDetails fulfilmentDetails;



    public CartSteps(TestContext context) {
        this.context = context;
        this.driver = context.getDriver();
        this.homePage = new HomePage(context.getDriver());
        this.ProductsPage = new ProductsPage(context.getDriver());
        this.homeSteps = new HomeSteps(context); // Reuse HomeSteps
        this.cartPage = new CartPage(context.getDriver());
        this.fulfilmentDetails = new FulfilmentDetails(context.getDriver());
    }

    @Then ("the cart page should be displayed")
    public void theCartPageShouldBeDisplayed() {
        boolean displayed = cartPage.isCartPageDisplayed();
        System.out.println("🛒 Cart page displayed: " + displayed);
        Assert.assertTrue(displayed, "Cart page should be displayed");
    }

    @When("user attempts to update the quantity")
    public void userAttemptsToUpdateTheQuantity() {
        String quantity = ConfigurationManager.getProperty("cart.quantity");
        cartPage.updateQuantity(quantity);
    }

    @When("user clicks Add More Items button")
    public void userClicksAddMoreItemsButton() {
        cartPage.clickAddMoreItems();
    }

    @When("the user searches for a product")
    public void theUserSearchesForAProduct() {
        String product = ConfigurationManager.getProperty("secondProductToSearch");
        homePage.enterSearchQuery(product);
    }

    @When("user clicks Add to cart button and updates the quantity")
    public void userClicksAddToCartButtonAndUpdatesTheQuantity() {
        ProductsPage.clickAddToCart();
        String quantity = ConfigurationManager.getProperty("cart.quantity");
        ProductsPage.enterProductQuantity(quantity);
    }

    @And("the cart page should be displayed with the added item")
    public void theCartPageShouldBeDisplayedWithTheAddedItem() {
        try {
            // Step 1: Verify cart page is visible
            boolean isDisplayed = cartPage.isCartPageDisplayed();
            System.out.println("🛒 Cart page displayed: " + isDisplayed);
            Assert.assertTrue(isDisplayed, "❌ Cart page should be displayed");

            // Step 2: Parse item names and quantities from config
            String[] itemNames = ConfigurationManager.get("cart.items").split(",");
            String[] quantities = ConfigurationManager.get("cart.quantities").split(",");

            Assert.assertEquals(itemNames.length, quantities.length,
                    "❌ Mismatch between number of cart items and quantities");

            // Step 3: Loop and verify each item
            for (int i = 0; i < itemNames.length; i++) {
                String item = itemNames[i].trim();
                int qty = Integer.parseInt(quantities[i].trim());

                boolean result = cartPage.verifyCartItemQuantity(item, qty);
                System.out.println("🧾 Verifying '" + item + "' with quantity " + qty + ": " + result);
                Assert.assertTrue(result, "❌ '" + item + "' with quantity " + qty + " not found in cart");
            }

            // Step 4: Screenshot on success
            ScreenshotUtil.captureAndAttachScreenshot(driver, context.getScenario(), "cart_page_verification_success", true);

        } catch (AssertionError | Exception e) {
            // Screenshot on failure
            ScreenshotUtil.captureAndAttachScreenshot(driver, context.getScenario(), "cart_page_verification_failed_" + System.currentTimeMillis(), true);
            throw e;
        }
    }
    @Then("the search product page should be displayed")
    public void theSearchProductPageShouldBeDisplayed() {
        boolean displayed = ProductsPage.isSearchProductsPageDisplayed();
        System.out.println("📄 Search product page displayed: " + displayed);
        Assert.assertTrue(displayed, "Search product page should be displayed");
    }

    @And("the user clears the cart items")
    public void theUserClearsTheCartItems() {
        CartHelper.clearCartItems();
    }

    @When("the user clicks start shopping button")
    public void theUserClickStartShoppingButton() {
        cartPage.clickStartShoppingButton();
    }

    @And("the user clicks home button")
    public void theUserClicksHomeButton() {
        ProductsPage.userClicksHomeButton();
    }
}
