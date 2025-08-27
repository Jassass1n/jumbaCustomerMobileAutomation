package utils;

import java.util.Random;

public class RandomGenerator {

    public static String generateRandomReference() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder ref = new StringBuilder();
        Random random = new Random();

        // Read prefix and length from config
        String prefix = ConfigReader.getProperty("payment.refPrefix");
        int length;

        try {
            length = Integer.parseInt(ConfigReader.getProperty("payment.refLength"));
        } catch (NumberFormatException e) {
            length = 8; // fallback
            System.out.println("⚠️ Invalid 'payment.refLength'. Using default value: 8");
        }

        ref.append(prefix != null ? prefix : "");

        for (int i = 0; i < length; i++) {
            ref.append(characters.charAt(random.nextInt(characters.length())));
        }

        return ref.toString();
    }
}