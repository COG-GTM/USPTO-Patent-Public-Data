package gov.uspto.auth.password;

import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * Password hashing utility using BCrypt algorithm.
 * 
 * BCrypt is a secure password hashing function that includes:
 * - Automatic salt generation
 * - Configurable work factor (cost)
 * - Resistance to rainbow table attacks
 * 
 * NIST 800-53 Controls: IA-5(1) (Password-based Authentication)
 */
public class PasswordHasher {

    private static final Logger LOGGER = LoggerFactory.getLogger(PasswordHasher.class);
    
    private static final int DEFAULT_COST = 12;
    private final int cost;

    /**
     * Creates a password hasher with default cost factor.
     */
    public PasswordHasher() {
        this(DEFAULT_COST);
    }

    /**
     * Creates a password hasher with specified cost factor.
     * 
     * @param cost the BCrypt cost factor (4-31, higher is more secure but slower)
     */
    public PasswordHasher(int cost) {
        if (cost < 4 || cost > 31) {
            throw new IllegalArgumentException("BCrypt cost must be between 4 and 31");
        }
        this.cost = cost;
    }

    /**
     * Hashes a password using BCrypt.
     * 
     * @param password the password to hash
     * @return the BCrypt hash
     */
    public String hashPassword(char[] password) {
        if (password == null || password.length == 0) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }

        String passwordStr = new String(password);
        try {
            String hash = BCrypt.hashpw(passwordStr, BCrypt.gensalt(cost));
            LOGGER.debug("Password hashed successfully with cost factor {}", cost);
            return hash;
        } finally {
            clearString(passwordStr);
        }
    }

    /**
     * Hashes a password from a String.
     * 
     * @param password the password to hash
     * @return the BCrypt hash
     */
    public String hashPassword(String password) {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        return hashPassword(password.toCharArray());
    }

    /**
     * Verifies a password against a BCrypt hash.
     * 
     * @param password the password to verify
     * @param hash the BCrypt hash to verify against
     * @return true if the password matches the hash, false otherwise
     */
    public boolean verifyPassword(char[] password, String hash) {
        if (password == null || password.length == 0) {
            LOGGER.warn("Attempted to verify null or empty password");
            return false;
        }
        if (hash == null || hash.isEmpty()) {
            LOGGER.warn("Attempted to verify against null or empty hash");
            return false;
        }

        String passwordStr = new String(password);
        try {
            boolean matches = BCrypt.checkpw(passwordStr, hash);
            if (matches) {
                LOGGER.debug("Password verification successful");
            } else {
                LOGGER.debug("Password verification failed");
            }
            return matches;
        } catch (IllegalArgumentException e) {
            LOGGER.error("Invalid BCrypt hash format", e);
            return false;
        } finally {
            clearString(passwordStr);
        }
    }

    /**
     * Verifies a password from a String.
     * 
     * @param password the password to verify
     * @param hash the BCrypt hash to verify against
     * @return true if the password matches the hash, false otherwise
     */
    public boolean verifyPassword(String password, String hash) {
        if (password == null || password.isEmpty()) {
            return false;
        }
        return verifyPassword(password.toCharArray(), hash);
    }

    /**
     * Checks if a hash needs to be rehashed (e.g., cost factor changed).
     * 
     * @param hash the BCrypt hash to check
     * @return true if the hash should be rehashed, false otherwise
     */
    public boolean needsRehash(String hash) {
        if (hash == null || hash.isEmpty()) {
            return true;
        }
        try {
            String[] parts = hash.split("\\$");
            if (parts.length < 4) {
                return true;
            }
            int hashCost = Integer.parseInt(parts[2]);
            return hashCost != cost;
        } catch (Exception e) {
            LOGGER.warn("Failed to parse BCrypt hash cost", e);
            return true;
        }
    }

    /**
     * Gets the current cost factor.
     * 
     * @return the cost factor
     */
    public int getCost() {
        return cost;
    }

    /**
     * Attempts to clear a String from memory (best effort).
     * 
     * @param str the string to clear
     */
    private void clearString(String str) {
        if (str != null) {
            try {
                char[] chars = str.toCharArray();
                Arrays.fill(chars, '\0');
            } catch (Exception e) {
                LOGGER.warn("Failed to clear string from memory", e);
            }
        }
    }
}
