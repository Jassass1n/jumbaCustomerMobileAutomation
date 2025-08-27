package helpers;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pages.BasePage;
import utils.AndroidUtils;

import java.time.Duration;
import java.util.List;
import java.util.Properties;
import java.util.HashSet;


public class OrderCardHelper extends BasePage {
    private static final Logger logger = LoggerFactory.getLogger(OrderCardHelper.class);
    private final Properties configProperties;
    private final AndroidDriver driver;
    private final String[] priorityStatuses;
    private final String orderType;
    private static final int WAIT_TIMEOUT_SECONDS = 10;
    private static final int MAX_SCROLL_ATTEMPTS = 20;

    public OrderCardHelper(AppiumDriver driver) {
        super(driver);
        this.driver = (AndroidDriver) driver;
        this.configProperties = PropertiesLoader.loadProperties("src/main/resources/config.properties");
        this.priorityStatuses = new String[]{
                configProperties.getProperty("orders.status.completed"),
                configProperties.getProperty("orders.status.approved"),
                configProperties.getProperty("orders.status.submitted")
        };
        this.orderType = configProperties.getProperty("orders.order.type");
    }

    public void openFirstMatchingOrderCard(String orderType, String status) {
        logger.info("🔍 Searching for first '{}' order card{}", orderType, status != null ? " with status: " + status : "");

        // Set 50 records per page
        setRecordsPerPage();

        int pageCount = 1;
        HashSet<String> seenCardTexts = new HashSet<>();

        try {
            while (true) {
                int scrollAttempts = 0;
                boolean newCardsFound;

                do {
                    newCardsFound = false;
                    // Find all orderType TextViews on the current screen
                    List<WebElement> orderTypeElements = driver.findElements(
                            MobileBy.xpath("//android.widget.TextView[contains(@text, '" + orderType + "')]")
                    );
                    if (orderTypeElements.isEmpty()) {
                        logger.debug("🔍 No '{}' orders visible on page {}, scroll attempt {}", orderType, pageCount, scrollAttempts + 1);
                        if (scrollAttempts < MAX_SCROLL_ATTEMPTS && scrollPage()) {
                            scrollAttempts++;
                            newCardsFound = true;
                            continue;
                        }
                        break; // No cards found, try next page
                    }

                    // Iterate through each orderType element
                    for (int i = 0; i < orderTypeElements.size(); i++) {
                        WebElement orderTypeElement = orderTypeElements.get(i);
                        logger.info("📦 Checking '{}' order TextView [{}] on page {}", orderType, i + 1, pageCount);

                        // Find the parent order card
                        WebElement card = null;
                        String cardResourceId = configProperties.getProperty("orders.card.resourceId", "");
                        if (!cardResourceId.isEmpty()) {
                            try {
                                List<WebElement> cards = driver.findElements(
                                        MobileBy.AndroidUIAutomator("new UiSelector().resourceId(\"" + cardResourceId + "\")")
                                );
                                for (WebElement vg : cards) {
                                    if (vg.findElements(By.xpath(".//android.widget.TextView[contains(@text, '" + orderType + "')]")).size() > 0) {
                                        card = vg;
                                        logger.debug("🔹 Found matching card with resourceId: {}", cardResourceId);
                                        break;
                                    }
                                }
                            } catch (Exception e) {
                                logger.warn("⚠️ Failed to find card with resourceId '{}': {}", cardResourceId, e.getMessage());
                            }
                        }

                        if (card == null) {
                            try {
                                for (int j = 1; j <= 3; j++) {
                                    try {
                                        card = orderTypeElement.findElement(By.xpath("./ancestor::android.view.ViewGroup[" + j + "]"));
                                        card.findElement(By.xpath(".//android.widget.TextView[contains(@text, '" + orderType + "')]"));
                                        logger.debug("🔹 Found parent ViewGroup at level {}", j);
                                        break;
                                    } catch (Exception e) {
                                        logger.debug("🔍 No ViewGroup at ancestor level {}", j);
                                    }
                                }
                                if (card == null) {
                                    throw new NoSuchElementException("No valid parent ViewGroup found for '" + orderType + "' TextView");
                                }
                            } catch (Exception e) {
                                logger.warn("⚠️ Ancestor lookup failed: {}. Trying fallback selector.", e.getMessage());
                                List<WebElement> viewGroups = driver.findElements(MobileBy.className("android.view.ViewGroup"));
                                for (WebElement vg : viewGroups) {
                                    try {
                                        vg.findElement(By.xpath(".//android.widget.TextView[contains(@text, '" + orderType + "')]"));
                                        card = vg;
                                        logger.debug("🔹 Found matching ViewGroup via fallback");
                                        break;
                                    } catch (Exception ignored) {
                                    }
                                }
                                if (card == null) {
                                    logger.warn("⚠️ No parent ViewGroup found for '{}' TextView [{}] on page {}", orderType, i + 1, pageCount);
                                    continue; // Skip to next orderType element
                                }
                            }
                        }

                        // Debug: Log card text
                        StringBuilder cardText = new StringBuilder();
                        try {
                            List<WebElement> textViews = card.findElements(By.xpath(".//android.widget.TextView"));
                            for (WebElement tv : textViews) {
                                cardText.append(tv.getText()).append(" | ");
                            }
                            logger.debug("🔍 Card text: {}", cardText);
                        } catch (Exception e) {
                            logger.debug("🔍 Card text not readable: {}", e.getMessage());
                        }

                        // Check for duplicate cards to avoid infinite loop
                        String cardTextStr = cardText.toString();
                        if (seenCardTexts.contains(cardTextStr)) {
                            logger.warn("⚠️ Detected duplicate card ('{}') on page {}, skipping", cardTextStr, pageCount);
                            continue;
                        }
                        seenCardTexts.add(cardTextStr);
                        newCardsFound = true;

                        // Mandatory status check before clicking card
                        if (status != null) {
                            try {
                                card.findElement(By.xpath(".//android.widget.TextView[contains(translate(@text, '✔ ', ''), '" + status + "')]"));
                                logger.info("📄 Confirmed status in card: {}", status);
                            } catch (Exception e) {
                                logger.warn("⚠️ Status '{}' not found in '{}' card: {}, trying next card", status, orderType, cardText);
                                continue; // Try next orderType element
                            }
                        }

                        // Click the card to expand
                        new WebDriverWait(driver, Duration.ofSeconds(5))
                                .until(ExpectedConditions.elementToBeClickable(card)).click();
                        logger.info("🖱️ Clicked '{}' card on page {}", orderType, pageCount);
                        try {
                            Thread.sleep(3000); // Longer pause to ensure expansion
                            logger.debug("🔹 Paused to ensure card expansion");
                        } catch (InterruptedException e) {
                            logger.debug("🔍 Pause interrupted: {}", e.getMessage());
                        }

                        // Scroll to make "View Details" visible and click
                        String viewDetailsText = configProperties.getProperty("orders.viewDetailsBtn.text", "View Details");
                        String viewDetailsResourceId = configProperties.getProperty("orders.viewDetailsBtn.resourceId", "");
                        By viewDetailsLocator = !viewDetailsResourceId.isEmpty()
                                ? MobileBy.AndroidUIAutomator("new UiSelector().resourceId(\"" + viewDetailsResourceId + "\")")
                                : MobileBy.AndroidUIAutomator("new UiSelector().textContains(\"" + viewDetailsText + "\")");

                        // Try scrolling within the card
                        try {
                            if (cardScroll(card, viewDetailsLocator)) {
                                WebElement viewDetailsBtn = new WebDriverWait(driver, Duration.ofSeconds(5))
                                        .until(ExpectedConditions.elementToBeClickable(viewDetailsLocator));
                                logger.info("✅ Clicked 'View Details' on '{}' card after card scroll", orderType);
                                viewDetailsBtn.click();
                                return; // Exit after success
                            }
                        } catch (Exception e) {
                            logger.debug("🔍 Card scroll failed: {}", e.getMessage());
                        }

                        // Fall back to page scroll
                        try {
                            WebElement viewDetailsBtn = new WebDriverWait(driver, Duration.ofSeconds(5))
                                    .until(ExpectedConditions.elementToBeClickable(viewDetailsLocator));
                            logger.info("✅ 'View Details' button is clickable without scrolling");
                            viewDetailsBtn.click();
                        } catch (Exception e) {
                            logger.debug("🔍 'View Details' not clickable, attempting page scroll");
                            int viewDetailsScrollAttempts = 0;
                            do {
                                if (scrollPage()) {
                                    try {
                                        WebElement viewDetailsBtn = new WebDriverWait(driver, Duration.ofSeconds(5))
                                                .until(ExpectedConditions.elementToBeClickable(viewDetailsLocator));
                                        viewDetailsBtn.click();
                                        logger.info("✅ Clicked 'View Details' on '{}' card after page scroll", orderType);
                                        return; // Exit after success
                                    } catch (Exception ex) {
                                        logger.debug("🔍 'View Details' still not clickable after page scroll attempt {}", viewDetailsScrollAttempts + 1);
                                    }
                                }
                                viewDetailsScrollAttempts++;
                            } while (viewDetailsScrollAttempts < MAX_SCROLL_ATTEMPTS && scrollPage());
                            logger.warn("⚠️ Failed to make 'View Details' visible for '{}' order on page {} after page scroll", orderType, pageCount);
                            continue; // Try next card
                        }
                        return; // Exit after success
                    }
                } while (newCardsFound && scrollAttempts < MAX_SCROLL_ATTEMPTS && scrollPage());

                // Move to next page
                if (!goToNextPage(pageCount)) {
                    logger.error("❌ No matching '{}' order found after scrolling and {} pages", orderType, pageCount);
                    throw new NoSuchElementException("No order card found with type '" + orderType + "'" + (status != null ? " and status '" + status + "'" : ""));
                }
                pageCount++;
                seenCardTexts.clear(); // Reset seen cards for new page
            }
        } catch (Exception e) {
            logger.error("❌ Failed to open '{}' order card: {}", orderType, e.getMessage());
            throw new RuntimeException("Failed to open order card for type '" + orderType + "'" + (status != null ? " with status '" + status + "'" : ""), e);
        }
    }

    // Overloaded method for no status filter
    public void openFirstMatchingOrderCard(String orderType) {
        openFirstMatchingOrderCard(orderType, null);
    }

    private void setRecordsPerPage() {
        String recordsPerPageText = configProperties.getProperty("orders.recordsPerPage.text", "Show 50");
        String recordsPerPageResourceId = configProperties.getProperty("orders.recordsPerPage.resourceId", "");
        By recordsPerPageLocator = !recordsPerPageResourceId.isEmpty()
                ? MobileBy.AndroidUIAutomator("new UiSelector().resourceId(\"" + recordsPerPageResourceId + "\")")
                : MobileBy.AndroidUIAutomator("new UiSelector().textContains(\"" + recordsPerPageText + "\")");

        try {
            WebElement recordsPerPageElement = new WebDriverWait(driver, Duration.ofSeconds(5))
                    .until(ExpectedConditions.elementToBeClickable(recordsPerPageLocator));
            recordsPerPageElement.click();
            logger.info("✅ Clicked '{}' to set 50 records per page", recordsPerPageText);
            Thread.sleep(2000); // Wait for page to reload
            logger.debug("🔹 Paused to ensure page reload after setting records per page");
        } catch (Exception e) {
            logger.warn("⚠️ Failed to set 50 records per page: {}", e.getMessage());
            // Continue with default records per page
        }
    }

    private boolean scrollPage() {
        String scrollableResourceId = configProperties.getProperty("orders.scrollable.resourceId", "");
        String scrollableSelector = !scrollableResourceId.isEmpty()
                ? "new UiSelector().resourceId(\"" + scrollableResourceId + "\")"
                : "new UiSelector().scrollable(true)";
        try {
            driver.findElement(MobileBy.AndroidUIAutomator(
                    "new UiScrollable(" + scrollableSelector + ").setMaxSearchSwipes(20).scrollForward()"
            ));
            logger.debug("🔍 Scrolled page forward");
            return true;
        } catch (Exception e) {
            logger.debug("🔍 Page scroll failed: {}", e.getMessage());
            return false;
        }
    }

    private boolean cardScroll(WebElement card, By viewDetailsLocator) {
        String scrollableResourceId = configProperties.getProperty("orders.cardScrollable.resourceId", "");
        if (!scrollableResourceId.isEmpty()) {
            try {
                card.findElement(MobileBy.AndroidUIAutomator(
                        "new UiScrollable(new UiSelector().resourceId(\"" + scrollableResourceId + "\")).setMaxSearchSwipes(20).scrollIntoView(" +
                                viewDetailsLocator.toString().replace("By.AndroidUIAutomator: ", "") + ")"
                ));
                logger.debug("🔍 Scrolled within card to find 'View Details'");
                return true;
            } catch (Exception e) {
                logger.debug("🔍 Card scroll with resourceId failed: {}", e.getMessage());
            }
        }
        try {
            card.findElement(MobileBy.AndroidUIAutomator(
                    "new UiScrollable(new UiSelector().scrollable(true)).setMaxSearchSwipes(20).scrollIntoView(" +
                            viewDetailsLocator.toString().replace("By.AndroidUIAutomator: ", "") + ")"
            ));
            logger.debug("🔍 Scrolled within card to find 'View Details' (fallback)");
            return true;
        } catch (Exception e) {
            logger.debug("🔍 Card scroll failed: {}", e.getMessage());
            return false;
        }
    }

    private boolean goToNextPage(int pageCount) {
        try {
            WebElement nextButton = driver.findElement(
                    MobileBy.AndroidUIAutomator("new UiSelector().text(\"" + configProperties.getProperty("orders.pagination.next.text") + "\")")
            );
            if (!nextButton.isEnabled()) {
                logger.debug("🔍 'Next' button disabled, no more pages available");
                return false;
            }
            new WebDriverWait(driver, Duration.ofSeconds(5))
                    .until(ExpectedConditions.elementToBeClickable(nextButton)).click();
            logger.info("📄 Clicked 'Next' to load page {}", pageCount + 1);
            return true;
        } catch (Exception e) {
            logger.debug("🔍 No more pages available: {}", e.getMessage());
            return false;
        }
    }
}