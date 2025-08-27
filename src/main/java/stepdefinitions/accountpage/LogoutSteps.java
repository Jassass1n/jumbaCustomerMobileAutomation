package stepdefinitions.accountpage;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import pages.accountpage.AccountPage;

import io.cucumber.java.en.When;
import utils.TestContext;
import org.testng.Assert;


public class LogoutSteps {
    private final AccountPage accountPage;

    public LogoutSteps(TestContext context) {
        this.accountPage = new AccountPage(context.getDriver());
    }

    @Then("the user logs out and returns to login page")
    public void userClicksLogoutButton(){
        System.out.println("🟡 Attempting to click Logout button...");
        try {
            accountPage.clickLogoutButton();
            System.out.println("✅ Clicked Logout button.");
        } catch (Exception e) {
            System.out.println("❌ Error clicking logout button: " + e.getMessage());
            Assert.fail("Failed to click logout button: " + e.getMessage());
        }
    }

}
