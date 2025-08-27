package stepdefinitions.accountpage;

import helpers.LoggerHelper;
import io.cucumber.java.en.And;
import io.qameta.allure.Allure;
import org.slf4j.Logger;
import helpers.NavigationHelper;
import pages.home.HomePage;
import pages.accountpage.AccountPage;
import pages.accountpage.ProfilePage;
import stepdefinitions.HomeSteps;
import utils.TestContext;

public class ProfileSteps {

    private final NavigationHelper navigationHelper;
    private final ProfilePage profilePage;
    private static final Logger logger = LoggerHelper.getLogger(ProfileSteps.class);

    public ProfileSteps(TestContext context) {

        HomeSteps homeSteps = new HomeSteps(context);
        HomePage homePage = new HomePage(context.getDriver());
        AccountPage accountPage = new AccountPage(context.getDriver());
        AccountPageSteps accountPageSteps = new AccountPageSteps(context);
        ProfilePage profilePage = new ProfilePage(context.getDriver());

        this.navigationHelper = new NavigationHelper.Builder()
                .withHomeSteps(homeSteps)
                .withHomePage(homePage)
                .withAccountPage(accountPage)
                .withAccountPageSteps(accountPageSteps)
                .withProfilePage(profilePage)
                .build();
        this.profilePage = profilePage;
    }

    @And("the user is on profile page")
    public void theUserIsOnProfilePage() {
        logger.info("🔍 Navigating to Profile page via Account...");
        Allure.step("Navigating to Profile page via Account");

        navigationHelper.goToProfilePageViaAccount();

        logger.info("✅ User successfully landed on the Profile page.");
        Allure.step("✅ Profile page loaded successfully");
    }

}