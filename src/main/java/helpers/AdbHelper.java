package helpers;

import org.slf4j.Logger;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class AdbHelper {

    private static final Logger logger = LoggerHelper.getLogger(AdbHelper.class);

    public static void pushAndScanFile(String udid, String localPath, String remotePath) {
        try {
            pushFileToDevice(udid, localPath, remotePath);
            scanMediaFile(udid, remotePath);
            verifyFilePresence(udid, remotePath);

            TimeUnit.SECONDS.sleep(5);
        } catch (Exception e) {
            logger.error("❌ pushAndScanFile failed: {}", e.getMessage(), e);
        }
    }

    public static void pushFileToDevice(String udid, String localPath, String remotePath) throws IOException, InterruptedException {
        String adbCommand = String.format("adb -s %s push %s %s", udid, localPath, remotePath);
        executeShellCommand(adbCommand);
        logger.info("📤 File pushed to device: {}", remotePath);
    }

    public static void scanMediaFile(String udid, String remoteFilePath) throws IOException, InterruptedException {
        String adbCommand = String.format(
                "adb -s %s shell am broadcast -a android.intent.action.MEDIA_SCANNER_SCAN_FILE -d file://%s",
                udid, remoteFilePath
        );
        int result = executeShellCommand(adbCommand);

        if (result == 0) {
            logger.info("✅ Media scan broadcast successful for: {}", remoteFilePath);
        } else {
            logger.error("❌ Media scan broadcast failed for: {}", remoteFilePath);
        }
    }

    public static void verifyFilePresence(String udid, String remotePath) throws IOException, InterruptedException {
        String adbCommand = String.format("adb -s %s shell ls %s", udid, remotePath);
        int result = executeShellCommand(adbCommand);

        if (result == 0) {
            logger.info("📁 File confirmed at: {}", remotePath);
        } else {
            logger.error("❌ File not found at: {}", remotePath);
        }
    }

    private static int executeShellCommand(String command) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder("bash", "-c", command);
        Process process = pb.start();
        return process.waitFor();
    }
}