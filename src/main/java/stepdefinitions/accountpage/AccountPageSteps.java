package stepdefinitions.accountpage;

import helpers.ElementHelper;
import helpers.LoggerHelper;
import helpers.NavigationHelper;
import io.appium.java_client.MobileBy;
import io.cucumber.java.en.*;
import io.qameta.allure.Allure;
import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.testng.Assert;
import pages.home.HomePage;
import pages.accountpage.AccountPage;
import pages.accountpage.ProfilePage;
import utils.SessionManager;
import utils.TestContext;

public class AccountPageSteps {

    private static final Logger logger = LoggerHelper.getLogger(AccountPageSteps.class);
    private final AccountPage accountPage;
    private final HomePage homePage;
    private final ProfilePage profilePage;
    private final NavigationHelper navigationHelper;

    public AccountPageSteps(TestContext context) {
        this.accountPage = new AccountPage(context.getDriver());
        this.homePage = new HomePage(context.getDriver());
        this.profilePage = new ProfilePage(context.getDriver());
        this.navigationHelper = context.getNavigationHelper();
    }

    @Given("user is on Profile page")
    public void userIsOnProfilePage() {
        logger.info("🔍 Navigating to Profile page via Account...");
        Allure.step("Navigating to Profile page via Account");
        try {
            navigationHelper.goToProfilePageViaAccount();
            logger.info("✅ User successfully landed on the Profile page.");
            Allure.step("✅ Profile page loaded successfully");
        } catch (Exception e) {
            logger.error("❌ Error navigating to Profile page: {}", e.getMessage(), e);
            Allure.addAttachment("Profile Page Error", e.toString());
            Assert.fail("Failed to navigate to Profile page: " + e.getMessage());
        }
    }

    @When("user clicks account button")
    public void userClicksAccountButton() {
        logger.info("🟢 Clicking account button");
        try {
            homePage.switchToAccountPage();
            logger.info("✅ Clicked account button.");
            Allure.step("Clicked account button successfully.");
        } catch (Exception e) {
            logger.error("❌ Error clicking account button: {}", e.getMessage(), e);
            Allure.addAttachment("Click Account Button Error", e.toString());
            Assert.fail("Failed to click account button: " + e.getMessage());
        }
    }

    @When("user clicks account button as guest")
    public void userClicksAccountButtonAsGuest() {
        logger.info("🟡 Attempting to click Login button...");
        try {
            SessionManager.setLoggedIn(false);
            By loginBtn = MobileBy.AndroidUIAutomator("new UiSelector().text(\"Log in\")");

            if (ElementHelper.waitForElementToBeClickable(loginBtn, 10)) {
                ElementHelper.clickElement(loginBtn);
                logger.info("✅ Clicked Login button.");
                Allure.step("Clicked Login button.");
            } else {
                throw new RuntimeException("Login button not clickable");
            }
        } catch (Exception e) {
            logger.error("❌ Failed to click Login button: {}", e.getMessage(), e);
            Allure.addAttachment("Click Login Button Error", e.toString());
            Assert.fail("Failed to click login button as guest: " + e.getMessage());
        }
    }

    @And("account profile should be displayed")
    public void accountProfileShouldBeDisplayed() {
        logger.info("🔍 Waiting for Account profile to load...");
        try {
            boolean isLoaded = accountPage.waitForPageLoad(10);
            boolean isProfileVisible = accountPage.isAccountProfileDisplayed();
            boolean isPhoneCorrect = accountPage.isLoggedInPhoneNumberCorrect();

            logger.info("✅ Account profile loaded: {}, profile visible: {}, phone correct: {}", isLoaded, isProfileVisible, isPhoneCorrect);
            Allure.step("Verified account profile elements.");

            Assert.assertTrue(isLoaded, "Profile page did not load within timeout.");
            Assert.assertTrue(isProfileVisible, "Profile page is not displayed.");
            Assert.assertTrue(isPhoneCorrect, "Logged in phone number does not match expected.");
        } catch (Exception e) {
            logger.error("❌ Error checking Account profile: {}", e.getMessage(), e);
            Allure.addAttachment("Account Profile Error", e.toString());
            Assert.fail("Error checking Account profile: " + e.getMessage());
        }
    }

    @Then("account page should be displayed")
    public void accountPageShouldBeDisplayed() {
        logger.info("🔍 Waiting for Account page to load...");
        try {
            boolean isLoaded = accountPage.waitForPageLoad(10);
            boolean isDisplayed = accountPage.isAccountPageDisplayed();
            logger.info("✅ Account page loaded: {}, displayed: {}", isLoaded, isDisplayed);
            Allure.step("Account page verified.");
        } catch (Exception e) {
            logger.error("❌ Error checking Account page: {}", e.getMessage(), e);
            Allure.addAttachment("Account Page Check Error", e.toString());
        }
    }

    @When("user clicks profile button")
    public void userClicksProfileButton() {
        logger.info("🟢 Clicking profile button");
        try {
            profilePage.clickProfileButton();
            logger.info("✅ Clicked profile button.");
            Allure.step("Clicked profile button.");
        } catch (Exception e) {
            logger.error("❌ Error clicking profile button: {}", e.getMessage(), e);
            Allure.addAttachment("Profile Button Error", e.toString());
            Assert.fail("Failed to click profile button: " + e.getMessage());
        }
    }

    @And("profile page should be displayed")
    public void profilePageShouldBeDisplayed() {
        logger.info("🔍 Waiting for Profile page to load...");
        try {
            boolean isLoaded = accountPage.waitForPageLoad(10);
            boolean isDisplayed = accountPage.isProfilePageDisplayed();
            logger.info("✅ Profile page loaded: {}, displayed: {}", isLoaded, isDisplayed);
            Allure.step("Profile page check completed.");
        } catch (Exception e) {
            logger.error("❌ Error checking Profile page: {}", e.getMessage(), e);
            Allure.addAttachment("Profile Page Error", e.toString());
        }
    }

    @When("user clicks notification preferences button")
    public void userClicksNotificationPreferencesButton() {
        logger.info("🟢 Clicking notification preferences button");
        try {
            accountPage.clickNotificationPreferencesButton();
            logger.info("✅ Clicked notification preferences button.");
            Allure.step("Clicked notification preferences button.");
        } catch (Exception e) {
            logger.error("❌ Error clicking notification preferences button: {}", e.getMessage(), e);
            Allure.addAttachment("Notification Preferences Button Error", e.toString());
            Assert.fail("Failed to click notification preferences button: " + e.getMessage());
        }
    }

    @And("notification preferences page should be displayed")
    public void notificationPreferencesPageShouldBeDisplayed() {
        logger.info("🔍 Waiting for Notification Preferences page to load...");
        try {
            boolean isLoaded = accountPage.waitForNotificationsPageLoad(10);
            boolean isDisplayed = accountPage.isNotificationPreferencesPageDisplayed();
            logger.info("✅ Notification Preferences page loaded: {}, displayed: {}", isLoaded, isDisplayed);
            Allure.step("Notification Preferences page check completed.");
        } catch (Exception e) {
            logger.error("❌ Error checking Notification Preferences page: {}", e.getMessage(), e);
            Allure.addAttachment("Notification Preferences Page Error", e.toString());
        }
    }

    @When("user toggles all notification preferences")
    public void userToggleAllNotificationPreferences() {
        logger.info("🟢 Clicking Account Verified button");
        try {
            accountPage.toggleAllNotificationToggles();
            logger.info("✅ Clicked Account Verified button.");
            Allure.step("Toggled all notification preferences.");
        } catch (Exception e) {
            logger.error("❌ Error clicking Account Verified button: {}", e.getMessage(), e);
            Allure.addAttachment("Toggle Notification Error", e.toString());
            Assert.fail("Failed to click Account Verified button: " + e.getMessage());
        }
    }

    @Then("user clicks update preferences button")
    public void userClicksUpdatePreferencesButton() {
        logger.info("🟢 Clicking update preferences button");
        try {
            boolean toastDisplayed = accountPage.clickUpdatePreferencesButtonAndVerifyToast();

            if (toastDisplayed) {
                logger.info("✅ Toast displayed after clicking update preferences.");
                Allure.step("Toast displayed after updating preferences.");
            } else {
                logger.warn("❌ Toast was not displayed.");
                Allure.addAttachment("Toast Missing", "Toast message not shown.");
                Assert.fail("Toast message was not displayed after clicking update preferences button.");
            }
        } catch (Exception e) {
            logger.error("❌ Error clicking update preferences button: {}", e.getMessage(), e);
            Allure.addAttachment("Update Preferences Error", e.toString());
            Assert.fail("Failed to click update preferences button: " + e.getMessage());
        }
    }
}