package utils;

import drivers.DriverManager;
import io.appium.java_client.AppiumDriver;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.File;
import java.io.IOException;

public class TestListener implements ITestListener {

    @Override
    public void onTestSuccess(ITestResult result) {
        System.out.println("✅ Test passed: " + result.getName());
        captureScreenshot(result, "passed");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        System.out.println("❌ Test failed: " + result.getName());
        captureScreenshot(result, "failed");
    }

    private void captureScreenshot(ITestResult result, String status) {
        AppiumDriver driver = DriverManager.getDriver();
        if (driver == null) {
            System.out.println("⚠️ Driver is null. Cannot capture screenshot.");
            return;
        }

        // Additional check: make sure session is active
        try {
            if (driver.getSessionId() == null) {
                System.out.println("⚠️ Session is null. Driver might have crashed. Skipping screenshot.");
                return;
            }

            File srcFile = driver.getScreenshotAs(OutputType.FILE);
            String screenshotDir = System.getProperty("user.dir") + File.separator +
                    "test-results" + File.separator + "screenshots" + File.separator + status;
            File destDir = new File(screenshotDir);
            if (!destDir.exists() && !destDir.mkdirs()) {
                System.out.println("⚠️ Failed to create screenshot folder: " + destDir.getAbsolutePath());
                return;
            }

            String methodName = result.getName().replaceAll("[^a-zA-Z0-9-_]", "_");
            String fileName = methodName + ".png";

            File destFile = new File(destDir, fileName);
            FileUtils.copyFile(srcFile, destFile);

            System.out.println("📸 Screenshot saved: " + destFile.getAbsolutePath());

        } catch (Exception e) {
            System.err.println("❌ Screenshot capture failed: " + e.getMessage());
        }
    }
}