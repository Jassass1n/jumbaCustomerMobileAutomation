package utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;

public class PaymentContextManager {
    private static String paymentMethod;
    private static String amount;
    private static String referenceNumber;
    private static String timestamp;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy,hh:mm:ss a");

    public static void setPaymentMethod(String methodName) {
        paymentMethod = methodName;
        System.out.println("📌 Payment method set to: " + paymentMethod);
    }

    public static void capturePaymentDetails(AppiumDriver driver, String amountFieldLocator, String refFieldLocator) {
        try {
            timestamp = LocalDateTime.now().format(formatter);

            WebElement amountField = driver.findElement(By.id(amountFieldLocator));
            amount = amountField.getText();

            WebElement refField = driver.findElement(By.id(refFieldLocator));
            referenceNumber = refField.getText();

            System.out.println("📦 Captured Payment Details:");
            System.out.println("⏰ Time: " + timestamp);
            System.out.println("💰 Amount: " + amount);
            System.out.println("🔖 Ref #: " + referenceNumber);
        } catch (Exception e) {
            System.out.println("❌ Failed to capture payment details: " + e.getMessage());
            throw e;
        }
    }

    public static String getPaymentMethod() {
        return paymentMethod;
    }

    public static String getAmount() {
        return amount;
    }

    public static String getReferenceNumber() {
        return referenceNumber;
    }

    public static String getTimestamp() {
        return timestamp;
    }

    public static void printContext() {
        System.out.println("🔎 Stored Payment Context:");
        System.out.println("Method: " + paymentMethod);
        System.out.println("Amount: " + amount);
        System.out.println("Ref #: " + referenceNumber);
        System.out.println("Time: " + timestamp);
    }
}