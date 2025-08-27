package stepdefinitions.orders;

import helpers.LoggerHelper;
import helpers.NavigationHelper;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.qameta.allure.Allure;
import org.slf4j.Logger;
import pages.CartPage;
import pages.orders.OrderDetails;
import pages.orders.Reorder;
import pages.home.HomePage;
import utils.TestContext;

public class ReorderSteps {

    private final HomePage homePage;
    private final Reorder reorder;
    private final OrderDetails orderDetails;
    private final CartPage cartPage;
    private final NavigationHelper navigationHelper;

    private static final Logger logger = LoggerHelper.getLogger(ReorderSteps.class);

    public ReorderSteps(TestContext context) {
        this.homePage = new HomePage(context.getDriver());
        this.reorder = new Reorder(context.getDriver());
        this.cartPage = new CartPage(context.getDriver());
        this.navigationHelper = context.getNavigationHelper();
        this.orderDetails = new OrderDetails(context.getDriver());
    }

    @When("the user click Order Again button")
    public void theUserClickOrderAgainButton() {
        homePage.switchToDeliveryTab();
        homePage.viewOrderAgain();
        logger.info("🔁 Clicked View Order Again button");
        Allure.step("🔁 Clicked View Order Again button");
    }

    @Then("completed recent orders page should be displayed")
    public void completedRecentOrdersPageShouldBeDisplayed() {
        reorder.isorderAgainPageDisplayed();
        logger.info("✅ Reorder Page Displayed");
        Allure.step("✅ Reorder Page Displayed");
    }

    @When("user clicks reorder button of the most recent order")
    public void userClicksReorderButton(){
        reorder.clickReorderButton();
        logger.info("✅ Clicked Reorder button");
        Allure.step("✅ Clicked Reorder button");
    }

    @And("cart page should be displayed with the same items as the original order")
    public void cartPageShouldBeDisplayedWithTheSameItemsAsTheOriginalOrder() {
        cartPage.isCartPageDisplayed();
        logger.info("🛒 Cart page displayed");
        Allure.step("🛒 Cart page displayed");
    }

    @Then("user should be able to complete the selfcollect order")
    public void userShouldBeAbleToCompleteselfcollectTheOrder() {
        logger.info("🚀 Completing self-collect reorder flow via NavigationHelper...");
        Allure.step("🚀 Self-collect reorder flow started");
        navigationHelper.selfCollectReorderFlow();
    }

    @Then("user should be able to complete the delivery order")
    public void userShouldBeAbleToCompleteTheDeliveryOrder() {
        logger.info("🚀 Completing delivery reorder flow via NavigationHelper...");
        Allure.step("🚀 Delivery reorder flow started");
        navigationHelper.deliveryReorderFlow();
    }



    @When("user clicks re-order button")
    public void userClicksReOrderButton() {
        orderDetails.clickReorderButton();
        logger.info("✅ Clicked Reorder button");
        Allure.step("✅ Clicked Reorder button");
    }

    @When("track order button should be visible")
    public void trackOrderButtonShouldBeVisible() {
        orderDetails.trackOrderButtonisVisible();
    }
}