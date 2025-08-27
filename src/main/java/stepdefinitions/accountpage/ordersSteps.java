package stepdefinitions.accountpage;

import helpers.OrderCardHelper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import helpers.NavigationHelper;
import io.cucumber.java.en.When;
import pages.accountpage.AccountPage;
import pages.accountpage.OrdersPage;
import pages.home.HomePage;
import pages.orders.OrderDetails;
import stepdefinitions.HomeSteps;
import utils.TestContext;

public class ordersSteps {
    private final NavigationHelper navigationHelper;
    private final OrdersPage ordersPage;
    private final OrderCardHelper orderCardHelper;
    private final OrderDetails orderDetails;

    public ordersSteps(TestContext context) {
        HomeSteps homeSteps = new HomeSteps(context);
        HomePage homePage = new HomePage(context.getDriver());
        AccountPage accountPage = new AccountPage(context.getDriver());
        OrderCardHelper orderCardHelper = new OrderCardHelper(context.getDriver());
        this.ordersPage = new OrdersPage(context.getDriver());
        this.orderCardHelper = orderCardHelper;
        this.orderDetails = new OrderDetails(context.getDriver());

        this.navigationHelper = new NavigationHelper.Builder()
                .withHomeSteps(homeSteps)
                .withHomePage(homePage)
                .withAccountPage(accountPage)
                .withOrdersPage(ordersPage)
                .build();
    }

    @Given("the user is on order listing page")
    public void theUserIsOnOrderListingPage() {
        navigationHelper.goToOrderListingPageViaAccount();
    }

    @When("user opens the first Self-Collect order with highest priority status")
    public void userOpensHighestPriorityOrder() {
        orderCardHelper.openFirstMatchingOrderCard("Self-Collect", "Submitted");

    }

    @When("user opens the first Delivery order with highest priority status")
    public void userOpensDeliveryOrder() {
        orderCardHelper.openFirstMatchingOrderCard("Delivery", "Submitted");
    }

    @Then("the order details page should be displayed")
    public void theOrderDetailsPageIsDisplayed() {
        orderDetails.isOrderDetailsDisplayed();
    }
}