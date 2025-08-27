package helpers;

public class EmailReportTest {
    public static void main(String[] args) {
        String sourceDir = "allure-report";
        String zipPath = "allure-report.zip";

        try {
            ReportZipper.zipFolder(sourceDir, zipPath);
            System.out.println("✅ Report zipped successfully.");
            EmailReportSender.sendReportEmail(zipPath);
        } catch (Exception e) {
            System.err.println("❌ Failed to zip/send report: " + e.getMessage());
            e.printStackTrace();
        }
    }
}