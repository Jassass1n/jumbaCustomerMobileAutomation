package utils;

import helpers.ElementHelper;

import java.time.LocalTime;

public class SessionManager {

    private static final ThreadLocal<Boolean> loggedIn = ThreadLocal.withInitial(() -> false);
    private static final ThreadLocal<String> accessToken = new ThreadLocal<>();
    private static final ThreadLocal<String> userId = new ThreadLocal<>();
    private static final ThreadLocal<String> userRole = new ThreadLocal<>();
    private static final ThreadLocal<String> cartId = new ThreadLocal<>();
    private static final ThreadLocal<String> orderRef = new ThreadLocal<>();
    private static final ThreadLocal<String> balanceAmount = new ThreadLocal<>();

    public static boolean isLoggedIn() {
        return loggedIn.get() || isHomePageVisible();
    }

    public static void setLoggedIn(boolean value) {
        loggedIn.set(value);
    }

    private static boolean isHomePageVisible() {
        try {
            // Replace "home.title" with the key for your actual home page element
            return ElementHelper.isElementDisplayed("selfCollectBtn.text");
        } catch (Exception e) {
            return false;
        }
    }

    public static void setAccessToken(String token) {
        accessToken.set(token);
    }

    public static String getAccessToken() {
        return accessToken.get();
    }

    public static void setUserId(String id) {
        userId.set(id);
    }

    public static String getUserId() {
        return userId.get();
    }

    public static void setUserRole(String role) {
        userRole.set(role);
    }

    public static String getUserRole() {
        return userRole.get();
    }

    public static void setCartId(String id) {
        cartId.set(id);
    }

    public static String getCartId() {
        return cartId.get();
    }

    public static void setOrderRef(String ref) {
        orderRef.set(ref);
    }

    public static String getOrderRef() {
        return orderRef.get();
    }

    public static void setBalanceAmount(String amount) {
        balanceAmount.set(amount);
    }

    public static String getBalanceAmount() {
        return balanceAmount.get();
    }

    public static void resetSession() {
        loggedIn.set(false);
        accessToken.remove();
        userId.remove();
        userRole.remove();
        cartId.remove();
        orderRef.remove();
        balanceAmount.remove();
        log("🔄 Session reset (loggedIn=false, other data cleared)");
    }

    public static void printSessionState() {
        log("🧾 Session State:");
        log("  - loggedIn: " + isLoggedIn());
        log("  - accessToken: " + getAccessToken());
        log("  - userId: " + getUserId());
        log("  - userRole: " + getUserRole());
        log("  - cartId: " + getCartId());
        log("  - orderRef: " + getOrderRef());
        log("  - balanceAmount: " + getBalanceAmount());
    }

    private static void log(String message) {
        System.out.println("[" + LocalTime.now() + "] " + message);
    }
}