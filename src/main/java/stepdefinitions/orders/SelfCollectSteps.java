package stepdefinitions.orders;

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

public class SelfCollectSteps {

    private final ProductsPage ProductsPage;
    private final CartPage cartPage;
    private final FulfilmentDetails fulfilmentDetails;
    private final HomeSteps homeSteps;
    private final Payments payments;
    private final TestContext testContext;


    public SelfCollectSteps(TestContext context) {
        this.ProductsPage = new ProductsPage(context.getDriver());
        this.cartPage = new CartPage(context.getDriver());
        this.fulfilmentDetails = new FulfilmentDetails(context.getDriver());
        this.homeSteps = new HomeSteps(context); // Reuse HomeSteps
        this.testContext = context;
        this.payments = new Payments(testContext);

    }

    @When("user inputs plate number")
    public void userInputsPlateNumber() {
        fulfilmentDetails.fillPlateNumberAndProceed();
    }

    @And("user clicks Proceed to payment button")
    public void userClicksProceedToPaymentButton() {
        fulfilmentDetails.clickProceedToPayment();
    }



}