package gov.uspto.auth.authenticator;

import gov.uspto.auth.core.Credential;

/**
 * Interface for managing authenticators (passwords, tokens, certificates).
 * 
 * This interface defines operations for creating, updating, validating, and
 * revoking authenticators in compliance with NIST 800-53 requirements.
 * 
 * NIST 800-53 Control: IA-5 (Authenticator Management)
 * 
 * Future implementation should include:
 * - Password strength validation
 * - Password history tracking
 * - Token generation and validation
 * - Certificate validation
 * - Authenticator lifecycle management
 */
public interface AuthenticatorManager {

    /**
     * Creates a new authenticator for the specified identifier.
     * 
     * @param identifier the user or service identifier
     * @param credential the credential to create
     * @return true if creation was successful, false otherwise
     */
    boolean createAuthenticator(String identifier, Credential credential);

    /**
     * Updates an existing authenticator.
     * 
     * @param identifier the user or service identifier
     * @param oldCredential the current credential
     * @param newCredential the new credential
     * @return true if update was successful, false otherwise
     */
    boolean updateAuthenticator(String identifier, Credential oldCredential, Credential newCredential);

    /**
     * Validates an authenticator.
     * 
     * @param identifier the user or service identifier
     * @param credential the credential to validate
     * @return true if the authenticator is valid, false otherwise
     */
    boolean validateAuthenticator(String identifier, Credential credential);

    /**
     * Revokes an authenticator.
     * 
     * @param identifier the user or service identifier
     * @return true if revocation was successful, false otherwise
     */
    boolean revokeAuthenticator(String identifier);

    /**
     * Checks if an authenticator has expired.
     * 
     * @param identifier the user or service identifier
     * @return true if the authenticator has expired, false otherwise
     */
    boolean isAuthenticatorExpired(String identifier);
}
