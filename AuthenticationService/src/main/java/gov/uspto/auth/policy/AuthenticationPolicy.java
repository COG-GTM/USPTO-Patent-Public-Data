package gov.uspto.auth.policy;

/**
 * Interface for authentication policy enforcement.
 * 
 * This interface provides methods for defining and enforcing authentication
 * policies such as password complexity, account lockout, and session timeouts.
 * 
 * NIST 800-53 Controls: IA-5 (Authenticator Management), AC-7 (Unsuccessful Logon Attempts)
 */
public interface AuthenticationPolicy {

    /**
     * Validates a password against the password policy.
     * 
     * @param password the password to validate
     * @return the policy validation result
     */
    PolicyValidationResult validatePassword(String password);

    /**
     * Checks if an account should be locked due to failed login attempts.
     * 
     * @param identifier the user identifier
     * @param failedAttempts the number of failed login attempts
     * @return true if the account should be locked, false otherwise
     */
    boolean shouldLockAccount(String identifier, int failedAttempts);

    /**
     * Gets the session timeout for a user or service.
     * 
     * @param identifier the user or service identifier
     * @param isPrivileged whether this is a privileged session
     * @return the session timeout in minutes
     */
    int getSessionTimeout(String identifier, boolean isPrivileged);

    /**
     * Checks if re-authentication is required.
     * 
     * @param identifier the user identifier
     * @param lastAuthenticationTime the last authentication timestamp in milliseconds
     * @return true if re-authentication is required, false otherwise
     */
    boolean requiresReauthentication(String identifier, long lastAuthenticationTime);
}
