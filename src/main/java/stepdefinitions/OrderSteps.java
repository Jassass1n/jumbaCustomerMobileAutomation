package stepdefinitions;

import config.ConfigurationManager;
import helpers.NavigationHelper;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;
import pages.CartPage;
import pages.FulfilmentDetails;
import pages.ProductsPage;
import pages.components.PickupLocationPage;
import stepdefinitions.payments.PaymentPageSteps;
import utils.TestContext;

public class OrderSteps {

    private final TestContext context;
    private final NavigationHelper navigationHelper;
    private final ProductsPage productsPage;
    private final CartPage cartPage;
    private final FulfilmentDetails fulfilmentDetails;
    private final PickupLocationPage pickupLocationPage;

    public OrderSteps(TestContext context) {
        this.context = context;
        this.productsPage = new ProductsPage(context.getDriver());
        this.cartPage = new CartPage(context.getDriver());
        this.fulfilmentDetails = new FulfilmentDetails(context.getDriver());
        this.pickupLocationPage = new PickupLocationPage(context.getDriver());

        this.navigationHelper = context.getNavigationHelper();
    }

    // === Precondition Navigation ===

    @Given("the user is on the product details page via search")
    public void userOnProductsPageViaSearch() {
        navigationHelper.viewProductDetails();
    }

    @Given("the user is on the cart page")
    public void userIsOnCartPage() {
        navigationHelper.goToCartPageViaSearch();
    }

    @Given("the user is on fulfilment details page")
    public void userOnFulfilmentDetailsPage() {
        navigationHelper.goToFulfilmentDetailsPageViaSearch();
    }

    @When("user clicks add to cart button")
    public void userClicksAddToCartButton() {
        productsPage.clickAddToCart();
    }

    @And("user updates the quantity from config")
    public void userUpdatesQuantityFromConfig() {
        String quantity = ConfigurationManager.getProperty("product.quantity");
        productsPage.enterProductQuantity(quantity);
    }

    @When("the user selects valid pickup location and clicks add to cart")
    public void theUserSelectsValidPickupLocation() {
        pickupLocationPage.selectValidPickupLocation();
    }

    @And("user updates the quantity of the product")
    public void userUpdatesQuantityAndClicksAddToCart() {
        String quantity = ConfigurationManager.getProperty("product.quantity");
        productsPage.enterProductQuantity(quantity);
    }

    @When("user clicks View Cart button")
    public void userClicksViewCartButton() {
        productsPage.clickViewCart();
    }

    @And("user verify cart page has loaded")
    public void userVerifyCartPageHasLoaded() {
        boolean isLoaded = cartPage.waitForPageLoad(10);
        boolean isDisplayed = cartPage.isCartPageDisplayed();
        Assert.assertTrue(isLoaded && isDisplayed, "Cart page should be displayed");
    }

    @When("user clicks Proceed to fulfilment button")
    public void userClicksProceedToFulfillmentButton() {
        cartPage.clickProceedToFulfilment();
    }

    @Then("user should see the Fulfilment Details page")
    public void fulfilmentDetailsIsDisplayed() {
        boolean isLoaded = fulfilmentDetails.waitForPageLoad(10);
        boolean isDisplayed = fulfilmentDetails.isFulfilmentDetailsDisplayed();
        Assert.assertTrue(isLoaded && isDisplayed, "Fulfilment Details page should be displayed");
    }


}