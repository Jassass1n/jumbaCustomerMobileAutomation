package helpers;

import pages.*;
import pages.accountpage.*;
import pages.components.PickupLocationPage;
import pages.home.HomePage;
import pages.orders.Reorder;
import stepdefinitions.HomeSteps;
import stepdefinitions.OrderSteps;
import stepdefinitions.ProductSteps;
import stepdefinitions.accountpage.AccountPageSteps;
import stepdefinitions.payments.PaymentPageSteps;
import utils.SessionManager;

public class NavigationHelper {

    private final HomeSteps homeSteps;
    private final ProductSteps productSteps;
    private final ProductsPage productsPage;
    private final OrderSteps orderSteps;
    private final FulfilmentDetails fulfilmentDetails;
    private final HomePage homePage;
    private final AccountPage accountPage;
    private final OrdersPage ordersPage;
    private final AccountPageSteps accountPageSteps;
    private final ProfilePage profilePage;
    private final PickupLocationPage pickupLocationPage;
    private final CartPage cartPage;
    private final Payments paymentsPage;
    private final PaymentPageSteps paymentPageSteps;
    private final Reorder reorder;

    private NavigationHelper(Builder builder) {
        this.homeSteps = builder.homeSteps;
        this.productSteps = builder.productSteps;
        this.productsPage = builder.productsPage;
        this.orderSteps = builder.orderSteps;
        this.fulfilmentDetails = builder.fulfilmentDetails;
        this.homePage = builder.homePage;
        this.accountPage = builder.accountPage;
        this.ordersPage = builder.ordersPage;
        this.accountPageSteps = builder.accountPageSteps;
        this.profilePage = builder.profilePage;
        this.pickupLocationPage = builder.pickupLocationPage;
        this.cartPage = builder.cartPage;
        this.paymentsPage = builder.paymentsPage;
        this.paymentPageSteps = builder.paymentPageSteps;
        this.reorder = builder.reorder;
    }

    // ✅ Builder class
    public static class Builder {
        private HomeSteps homeSteps;
        private ProductSteps productSteps;
        private ProductsPage productsPage;
        private OrderSteps orderSteps;
        private FulfilmentDetails fulfilmentDetails;
        private HomePage homePage;
        private AccountPage accountPage;
        private OrdersPage ordersPage;
        private AccountPageSteps accountPageSteps;
        private ProfilePage profilePage;
        private PickupLocationPage pickupLocationPage;
        private CartPage cartPage;
        private Payments paymentsPage;
        private PaymentPageSteps paymentPageSteps;
        private Reorder reorder;

        public Builder withHomeSteps(HomeSteps homeSteps) {
            this.homeSteps = homeSteps;
            return this;
        }

        public Builder withProductSteps(ProductSteps productSteps) {
            this.productSteps = productSteps;
            return this;
        }

        public Builder withProductsPage(ProductsPage productsPage) {
            this.productsPage = productsPage;
            return this;
        }

        public Builder withOrderSteps(OrderSteps orderSteps) {
            this.orderSteps = orderSteps;
            return this;
        }

        public Builder withFulfilmentDetails(FulfilmentDetails fulfilmentDetails) {
            this.fulfilmentDetails = fulfilmentDetails;
            return this;
        }

        public Builder withHomePage(HomePage homePage) {
            this.homePage = homePage;
            return this;
        }

        public Builder withAccountPage(AccountPage accountPage) {
            this.accountPage = accountPage;
            return this;
        }

        public Builder withOrdersPage(OrdersPage ordersPage) {
            this.ordersPage = ordersPage;
            return this;
        }

        public Builder withAccountPageSteps(AccountPageSteps accountPageSteps) {
            this.accountPageSteps = accountPageSteps;
            return this;
        }

        public Builder withProfilePage(ProfilePage profilePage) {
            this.profilePage = profilePage;
            return this;
        }

        public Builder withPickupLocationPage(PickupLocationPage pickupLocationPage) {
            this.pickupLocationPage = pickupLocationPage;
            return this;
        }

        public Builder withCartPage(CartPage cartPage) {
            this.cartPage = cartPage;
            return this;
        }

        public Builder withPaymentsPage(Payments paymentsPage) {
            this.paymentsPage = paymentsPage;
            return this;
        }

        public Builder withPaymentPageSteps(PaymentPageSteps paymentPageSteps) {
            this.paymentPageSteps = paymentPageSteps;
            return this;
        }

        public Builder withReorder(Reorder reorder) {
            this.reorder = reorder;
            return this;
        }

        public NavigationHelper build() {
            return new NavigationHelper(this);
        }
    }

    // === Reusable navigation steps ===

    public void goToProductDetailsViaSearch() {
        homeSteps.userIsOnHomePage();
        homeSteps.userSwitchesToSelfCollectTab();
        homeSteps.verifySelfCollectPageDisplayed();
        homeSteps.tapSearchField();
        productSteps.enterSearchProduct();
        productSteps.selectFirstSearchResult();
        productSteps.verifyProductsPageDisplayed();
    }

    public void viewProductDetails() {
        homeSteps.userIsOnHomePage();
        homeSteps.tapSearchField();
        productSteps.enterSearchProduct();
        productSteps.selectFirstSearchResult();
        productSteps.verifyProductsPageDisplayed();

    }



    public void goToCartPageViaSearch() {
        viewProductDetails();
        pickupLocationPage.selectValidPickupLocation();
        orderSteps.userUpdatesQuantityFromConfig();
        orderSteps.userClicksViewCartButton();
        orderSteps.userVerifyCartPageHasLoaded();
    }

    public void goToFulfilmentDetailsPageViaSearch() {
        goToCartPageViaSearch();
        orderSteps.userClicksProceedToFulfillmentButton();
        orderSteps.fulfilmentDetailsIsDisplayed();
    }

    public void goToAccountPage() {
        homeSteps.userIsOnHomePage();
        homePage.switchToAccountPage();
    }

    public void goToOrderListingPageViaAccount() {
        goToAccountPage();
        accountPage.clickOrdersButton();
        ordersPage.isOrdersPageDisplayed();
    }

    public void goToProfilePageViaAccount() {
        goToAccountPage();
        profilePage.clickProfileButton();
        profilePage.isProfilePageDisplayed();
    }

    public void logout() {
        goToProfilePageViaAccount();
        accountPage.clickLogoutButton();
        accountPage.clickLoginButton();
        SessionManager.setLoggedIn(false);
    }

    public void reReorderFlow() {
        cartPage.clickProceedToFulfilment();
        orderSteps.fulfilmentDetailsIsDisplayed();
    }

    public void selfCollectReorderFlow() {
        reReorderFlow();
        fulfilmentDetails.fillPlateNumberAndProceed();
        fulfilmentDetails.clickProceedToPayment();
        paymentPageSteps.paymentPageShouldBeDisplayed();
        paymentsPage.clickPayBalanceLater();
        paymentsPage.clickViewOrder();
        paymentsPage.isOrderDetailsPageVisible();
    }

    public void deliveryReorderFlow() {
        reReorderFlow();
        fulfilmentDetails.clickProceedToPayment();
        paymentPageSteps.paymentPageShouldBeDisplayed();
        paymentsPage.clickPayBalanceLater();
        paymentsPage.clickBackToHome();
    }

}