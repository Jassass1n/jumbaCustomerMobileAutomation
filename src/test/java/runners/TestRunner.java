package runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

@CucumberOptions(
        features = "src/test/resources/features",
        glue = {
                "stepdefinitions",       // ✅ Step defs
                "hooks",                 // Optional: only if you use @Before/@After hooks
                "utils",                 // Optional: avoid if no step methods here
                "pages.components"       // ⚠️ Should be avoided unless they contain step methods
        },
        plugin = {
                "pretty",
                "html:target/cucumber-reports/cucumber.html",
                "json:target/cucumber-reports/cucumber.json",
                "junit:target/cucumber-reports/cucumber.xml",
                "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"
        },
        tags = "@notification or @guestSession or @otpSession or @cart or @logout or @orderlisting or @selfcollect or @reorder or @paynow or @bankTransferViaOrderListing or @bankTransfer or @productSearch or @orderTracking",  // @notification or @guestSession or @otpSession or @cart or @switchDelivery or @logout or @orderlisting or @order or @selfcollect or @delivery or @reorder or @paynow or @bankTransferViaOrderListing or @bankTransfer or @productSearch or @orderTracking
        monochrome = true
)
public class TestRunner extends AbstractTestNGCucumberTests {

        @Override
        @DataProvider(parallel = false)
        public Object[][] scenarios() {
                return super.scenarios();
        }
}