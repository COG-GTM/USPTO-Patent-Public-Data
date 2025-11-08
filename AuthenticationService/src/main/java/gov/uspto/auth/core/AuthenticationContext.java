package gov.uspto.auth.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Thread-local authentication context for managing the current authenticated principal.
 * 
 * This class provides a thread-safe mechanism for storing and retrieving the currently
 * authenticated principal within the context of a single thread. This is particularly
 * important for bulk processing scenarios where multiple authentication contexts may
 * exist concurrently.
 * 
 * NIST 800-53 Control: IA-2 (Identification and Authentication)
 * 
 * Usage:
 * <pre>
 * try {
 *     AuthenticationContext.setCurrentPrincipal(principal);
 *     // Perform authenticated operations
 * } finally {
 *     AuthenticationContext.clear();
 * }
 * </pre>
 * 
 * WARNING: Always call clear() in a finally block to prevent thread-local memory leaks,
 * especially in bulk processing scenarios where threads may be reused.
 */
public class AuthenticationContext {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationContext.class);
    
    private static final ThreadLocal<Principal> CURRENT_PRINCIPAL = new ThreadLocal<>();

    private AuthenticationContext() {
    }

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
     * Clears the current principal from the thread-local context.
     * 
     * This method MUST be called when authentication context is no longer needed
     * to prevent thread-local memory leaks. Always call this in a finally block.
     */
    public static void clear() {
        Principal principal = CURRENT_PRINCIPAL.get();
        if (principal != null) {
            LOGGER.debug("Clearing current principal: {}", principal.getIdentifier());
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
