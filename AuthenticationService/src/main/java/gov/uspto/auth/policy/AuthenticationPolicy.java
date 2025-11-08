package gov.uspto.auth.policy;

import gov.uspto.auth.core.Principal;

/**
 * Interface for authentication policy enforcement.
 * 
 * This interface defines operations for enforcing authentication policies
 * such as password complexity, account lockout, and session management.
 * 
 * NIST 800-53 Controls:
 * - IA-5 (Authenticator Management)
 * - AC-7 (Unsuccessful Logon Attempts)
 * 
 * Future implementation should include:
 * - Password complexity validation
 * - Account lockout enforcement
 * - Session timeout enforcement
 * - Multi-factor authentication requirements
 * - Risk-based authentication
 */
public interface AuthenticationPolicy {

    /**
     * Validates that a password meets policy requirements.
     * 
     * @param password the password to validate
     * @return the validation result
     */
    PolicyValidationResult validatePassword(String password);

    /**
     * Records a failed authentication attempt.
     * 
     * @param identifier the identifier that failed authentication
     * @return true if the account should be locked, false otherwise
     */
    boolean recordFailedAttempt(String identifier);

    /**
     * Records a successful authentication.
     * 
     * @param identifier the identifier that authenticated successfully
     */
    void recordSuccessfulAuthentication(String identifier);

    /**
     * Checks if an account is locked due to failed attempts.
     * 
     * @param identifier the identifier to check
     * @return true if the account is locked, false otherwise
     */
    boolean isAccountLocked(String identifier);

    /**
     * Unlocks an account.
     * 
     * @param identifier the identifier to unlock
     * @return true if unlock was successful, false otherwise
     */
    boolean unlockAccount(String identifier);

    /**
     * Checks if re-authentication is required for a principal.
     * 
     * @param principal the principal to check
     * @return true if re-authentication is required, false otherwise
     */
    boolean requiresReAuthentication(Principal principal);

    /**
     * Checks if multi-factor authentication is required.
     * 
     * @param identifier the identifier to check
     * @return true if MFA is required, false otherwise
     */
    boolean requiresMultiFactorAuth(String identifier);

    /**
     * Result of policy validation.
     */
    class PolicyValidationResult {
        private final boolean valid;
        private final String message;

        public PolicyValidationResult(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }

        public boolean isValid() {
            return valid;
        }

        public String getMessage() {
            return message;
        }
    }
}
