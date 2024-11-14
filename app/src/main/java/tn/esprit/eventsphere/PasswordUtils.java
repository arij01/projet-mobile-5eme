package tn.esprit.eventsphere;

import android.util.Base64;
import java.security.MessageDigest;
import java.security.SecureRandom;

public class PasswordUtils {

    // Generate a salt for the password
    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16]; // Create a 16-byte salt
        random.nextBytes(salt);
        return Base64.encodeToString(salt, Base64.DEFAULT); // Use Android Base64
    }

    // Hash the password using SHA-256 and the salt
    public static String hashPassword(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt.getBytes()); // Add the salt to the digest
            byte[] hashedPassword = md.digest(password.getBytes()); // Hash the password
            return Base64.encodeToString(hashedPassword, Base64.DEFAULT); // Use Android Base64
        } catch (Exception e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    // Compare a raw password with the hashed password
    public static boolean verifyPassword(String rawPassword, String hashedPassword, String salt) {
        String hashedRawPassword = hashPassword(rawPassword, salt); // Hash the raw password with the same salt
        return hashedRawPassword.equals(hashedPassword); // Check if they match
    }
}
