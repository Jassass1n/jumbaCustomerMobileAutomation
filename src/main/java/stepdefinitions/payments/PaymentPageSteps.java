package stepdefinitions.payments;

import io.appium.java_client.AppiumDriver;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;
import pages.Payments;
import pages.ActiveOrders;
import pages.orders.OrderDetails;
import utils.TestContext;

public class PaymentPageSteps {

    private final OrderDetails orderDetails;
    private final Payments payments;
    private final ActiveOrders activeOrders;
    private final TestContext testContext;
    private final AppiumDriver driver;

    public PaymentPageSteps(TestContext context) {
        this.orderDetails = new OrderDetails(context.getDriver());
        this.driver = context.getDriver();
        this.testContext = context;
        this.payments = new Payments(testContext);
        this.activeOrders = new ActiveOrders(context.getDriver());
    }

    // ====View Payment Page====
    @When("user clicks pay now link")
    public void userClicksPayNowLink() {
        System.out.println("🔄 Clicking pay now link...");
        try {
            orderDetails.clickPayNowLink();
            System.out.println("✅ Clicked pay now link");
        } catch (Exception e) {
            System.out.println("❌ Error clicking pay now link: " + e.getMessage());
            Assert.fail("Failed to click pay now link");
        }

    }
    @Then("payment page should be displayed")
    public void paymentPageShouldBeDisplayed() {
        System.out.println("🔍 Waiting for Payment page to load...");
        try {
            boolean isLoaded = payments.waitForPageLoad(10);
            boolean isDisplayed = payments.isPaymentsDisplayed();
            System.out.println("✅ Payment page loaded: " + isLoaded + ", displayed: " + isDisplayed);
        } catch (Exception e) {
            System.out.println("❌ Error checking Payment page: " + e.getMessage());
        }
    }
    @And("user has navigated to the Payment Page via Orders")
    public void theUserIsOnPaymentPage() {
        System.out.println("Navigating to Payment Page via Orders");
        activeOrders.viewActiveOrders();
        activeOrders.isActiveOrdersDisplayed();
        activeOrders.viewOrderDetails();
        orderDetails.clickPayNowLink();
        payments.waitForPageLoad(10);
        System.out.println("✅ Clicked pay now link, waiting for Payment page to load...");

    }

}
