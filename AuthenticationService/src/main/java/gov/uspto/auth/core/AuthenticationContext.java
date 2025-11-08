package gov.uspto.auth.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Thread-local authentication context for managing the current authenticated principal.
 * 
 * This class provides a thread-safe mechanism for storing and retrieving the currently
 * authenticated user or service principal within the execution context of a thread.
 * 
 * NIST 800-53 Controls: IA-2 (Identification and Authentication)
 * 
 * Usage:
 * <pre>
 * AuthenticationContext.setCurrentPrincipal(principal);
 * try {
 *     Principal current = AuthenticationContext.getCurrentPrincipal();
 *     // ... perform authenticated operations
 * } finally {
 *     AuthenticationContext.clear();
 * }
 * </pre>
 */
public class AuthenticationContext {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationContext.class);

    private static final ThreadLocal<Principal> CURRENT_PRINCIPAL = new ThreadLocal<>();

    /**
     * Gets the currently authenticated principal for this thread.
     * 
     * @return the current principal, or null if no principal is authenticated
     */
    public static Principal getCurrentPrincipal() {
        return CURRENT_PRINCIPAL.get();
    }

    /**
     * Sets the currently authenticated principal for this thread.
     * 
     * @param principal the principal to set as current
     * @throws IllegalArgumentException if principal is null
     */
    public static void setCurrentPrincipal(Principal principal) {
        if (principal == null) {
            throw new IllegalArgumentException("Principal cannot be null");
        }
        LOGGER.debug("Setting current principal: {}", principal.getIdentifier());
        CURRENT_PRINCIPAL.set(principal);
    }

    /**
     * Clears the current authentication context for this thread.
     * 
     * This method should be called in a finally block to ensure proper cleanup
     * of thread-local storage, especially in thread pool scenarios where threads
     * are reused for bulk processing operations.
     */
    public static void clear() {
        Principal principal = CURRENT_PRINCIPAL.get();
        if (principal != null) {
            LOGGER.debug("Clearing authentication context for principal: {}", principal.getIdentifier());
        }
        CURRENT_PRINCIPAL.remove();
    }

    /**
     * Checks if there is a currently authenticated principal.
     * 
     * @return true if a principal is authenticated, false otherwise
     */
    public static boolean isAuthenticated() {
        return CURRENT_PRINCIPAL.get() != null;
    }
}
