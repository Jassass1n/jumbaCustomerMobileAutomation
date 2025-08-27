package stepdefinitions.orders;

import io.appium.java_client.AppiumDriver;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;

import pages.CartPage;
import pages.FulfilmentDetails;
import pages.ProductsPage;
import pages.Payments;
import stepdefinitions.HomeSteps;
import utils.TestContext;
import config.ConfigurationManager;

public class DeliverySteps {

    private final ProductsPage ProductsPage;
    private final CartPage cartPage;
    private final FulfilmentDetails fulfilmentDetails;
    private final HomeSteps homeSteps;
    private final Payments payments;
    private String deliveryMethod;
    private final TestContext testContext;
    private final AppiumDriver driver;


    public DeliverySteps(TestContext context) {
        this.ProductsPage = new ProductsPage(context.getDriver());
        this.cartPage = new CartPage(context.getDriver());
        this.fulfilmentDetails = new FulfilmentDetails(context.getDriver());
        this.homeSteps = new HomeSteps(context); // Reuse HomeSteps
        this.driver = context.getDriver();
        this.testContext = context;
        this.payments = new Payments(testContext);
    }

    @When("the user switches to the Delivery method page")
    public void theUserSwitchesToTheDeliveryMethodPage() {
        System.out.println("🔄 Switching to Products Delivery tab...");
        try {
            fulfilmentDetails.switchToDeliveryMethod();
            System.out.println("✅ Successfully switched to Products Delivery tab and verified flow.");
        } catch (Exception e) {
            System.out.println("❌ Failed during Products Delivery tab flow: " + e.getMessage());
            Assert.fail("❌ Could not switch to Products Delivery tab: " + e.getMessage());
        }
    }


}