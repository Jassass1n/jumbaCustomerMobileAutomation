package stepdefinitions.payments;

import config.ConfigurationManager;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.Payments;
import pages.components.BankTransferComponent;
import helpers.AdbHelper;
import drivers.DriverManager;
import utils.RandomGenerator;
import utils.ScreenshotUtil;
import utils.TestContext;

import java.time.Duration;

public class BankTransferSteps {

    private final Payments payments;
    private final AppiumDriver driver;
    private final TestContext testContext;
    private final BankTransferComponent bankTransferComponent;

    public BankTransferSteps(TestContext context) {
        this.testContext = context;
        this.driver = context.getDriver();
        this.payments = new Payments(testContext);
        this.bankTransferComponent = new BankTransferComponent(testContext);
    }
    private String generatedReferenceNumber;



    // Added Scenario parameter here
    @When("user selects a bank from the dropdown")
    public void user_selects_a_bank_from_the_dropdown() {
        try {
            System.out.println("➡️ Starting to expand and select a bank...");

            // Ensure Bank Transfer section is expanded, toast is dismissed, and Confirm Payment is scrolled to
            bankTransferComponent.expandBankTransferSectionIfNeeded();

            bankTransferComponent.selectRandomBankFromDropdown();
            System.out.println("✅ Bank selected successfully.");
        } catch (Exception e) {
            System.out.println("❌ Failed to select bank: " + e.getMessage());
            Assert.fail("❌ Failed to select bank: " + e.getMessage());
        }
    }


    @And("user enters a unique reference number for bank transfer")
    public void user_enters_unique_reference_number_for_bank_transfer() {
        try {
            // NOW we log and proceed with entering the reference number
            System.out.println("➡️ Starting to enter unique reference number for Bank Transfer...");

            generatedReferenceNumber = RandomGenerator.generateRandomReference();
            bankTransferComponent.enterReferenceNumber(generatedReferenceNumber);

            ScreenshotUtil.captureAndAttachScreenshot(driver, testContext.getScenario(), "Entered_Ref_" + generatedReferenceNumber, false);

            System.out.println("✅ Successfully entered unique reference number: " + generatedReferenceNumber);
        } catch (Exception e) {
            System.out.println("❌ Exception while entering reference number: " + e.getMessage());
            Assert.fail("❌ Failed to enter unique reference number: " + e.getMessage());
        }
    }



    // Added Scenario parameter here too
    @When("user uploads proof of payment image")
    public void user_uploads_proof_of_payment_image() {
        System.out.println("➡️ Starting to upload proof of payment image...");

        try {
            String fileName = ConfigurationManager.get("payment.proofOfPaymentFile");
            String localPath = System.getProperty("user.dir") + "tests/src/test/test_files/" + fileName;
            String remotePath = "/sdcard/Download/" + fileName;  // Fixed folder: Download

            // Use AdbHelper to push file and scan media
            String udid = ConfigurationManager.get("udid");
            AdbHelper.pushAndScanFile(udid, localPath, remotePath);
            AdbHelper.scanMediaFile(udid, remotePath);

            int filePickerWaitInSeconds = 15;
            bankTransferComponent.uploadProofOfPayment("Downloads", filePickerWaitInSeconds);  // Pass folder name "Downloads"

            ScreenshotUtil.captureAndAttachScreenshot(driver, testContext.getScenario(), "BankTransfer", false);

        } catch (Exception e) {
            ScreenshotUtil.captureAndAttachScreenshot(driver, testContext.getScenario(), "BankTransfer", true);
            System.out.println("❌ Error uploading proof of payment: " + e.getMessage());
            Assert.fail("❌ Failed to upload proof of payment: " + e.getMessage());
        }
    }

    @Then("confirm payment button should be enabled")
    public void confirm_payment_button_should_be_enabled() {
        System.out.println("➡️ Verifying Confirm Payment button is enabled...");

        try {
            bankTransferComponent.confirmPaymentIfEnabled();
            System.out.println("✅ Confirm Payment button is enabled.");
        } catch (Exception e) {
            System.out.println("❌ Confirm Payment button is not enabled: " + e.getMessage());
            Assert.fail("❌ Confirm Payment button is not enabled: " + e.getMessage());
        }
    }

    @When("user clicks confirm payment button")
    public void user_clicks_confirm_payment_button() {
        System.out.println("➡️ Clicking Confirm Payment button...");

        try {
            payments.clickConfirmPayment();
            System.out.println("✅ Clicked Confirm Payment button.");
        } catch (Exception e) {
            System.out.println("❌ Error clicking Confirm Payment: " + e.getMessage());
            Assert.fail("Failed to click Confirm Payment: " + e.getMessage());
        }
    }

    @And("user clicks complete order button")
    public void user_clicks_complete_order_button() {
        System.out.println("➡️ Clicking Complete Order button...");

        try {
            payments.clickCompleteOrder();
            System.out.println("✅ Clicked Complete Order button.");
        } catch (Exception e) {
            System.out.println("❌ Error clicking Complete Order: " + e.getMessage());
            Assert.fail("Failed to click Complete Order: " + e.getMessage());
        }
    }

    @When("user clicks view order")
    public void user_clicks_view_order() {
        System.out.println("➡️ Clicking View Order button...");

        try {
            payments.clickViewOrder();
            System.out.println("✅ Clicked View Order button.");
        } catch (Exception e) {
            System.out.println("❌ Error clicking View Order: " + e.getMessage());
            Assert.fail("Failed to click View Order: " + e.getMessage());
        }
    }

    @Then("the order details page should be displayed with correct payment info")
    public void the_order_details_page_should_be_displayed_with_correct_payment_info() {
        System.out.println("➡️ Verifying Order Details page...");

        try {
            payments.isOrderDetailsPageVisible();
            System.out.println("✅ Verified Order Details page.");
        } catch (Exception e) {
            System.out.println("❌ Error verifying Order Details: " + e.getMessage());
            Assert.fail("Failed to verify Order Details: " + e.getMessage());
        }
    }

    @When("user clicks pay balance later button")
    public void user_clicks_pay_balance_later_button() {
        System.out.println("➡️ Clicking Pay Balance Later button...");

        try {
            payments.clickPayBalanceLater();
            System.out.println("✅ Clicked Pay Balance Later button.");
        } catch (Exception e) {
            System.out.println("❌ Error clicking Pay Balance Later: " + e.getMessage());
            Assert.fail("Failed to click Pay Balance Later: " + e.getMessage());
        }
    }

    @Then("user clicks back to home button and home page should be displayed")
    public void user_clicks_back_to_home_button_and_home_page_should_be_displayed() {
        System.out.println("➡️ Clicking Back to Home button...");

        try {
            payments.clickBackToHome();
            System.out.println("✅ Clicked Back to Home button.");
        } catch (Exception e) {
            System.out.println("❌ Error clicking Back to Home: " + e.getMessage());
            Assert.fail("Failed to click Back to Home: " + e.getMessage());
        }
    }
}