package gov.uspto.auth.authenticator;

import gov.uspto.auth.core.Credential;

/**
 * Interface for managing authenticators (passwords, tokens, certificates, etc.).
 * 
 * This interface provides methods for creating, validating, updating, and revoking
 * authenticators in compliance with NIST 800-53 IA-5 (Authenticator Management).
 * 
 * NIST 800-53 Controls: IA-5 (Authenticator Management), IA-5(1) (Password-based Authentication)
 */
public interface AuthenticatorManager {

    /**
     * Creates a new authenticator for the specified identifier.
     * 
     * @param identifier the user or service identifier
     * @param credential the credential to create
     * @throws AuthenticatorException if authenticator creation fails
     */
    void createAuthenticator(String identifier, Credential credential) throws AuthenticatorException;

    /**
     * Validates an authenticator.
     * 
     * @param identifier the user or service identifier
     * @param credential the credential to validate
     * @return true if the authenticator is valid, false otherwise
     * @throws AuthenticatorException if validation fails
     */
    boolean validateAuthenticator(String identifier, Credential credential) throws AuthenticatorException;

    /**
     * Updates an existing authenticator.
     * 
     * @param identifier the user or service identifier
     * @param oldCredential the old credential
     * @param newCredential the new credential
     * @throws AuthenticatorException if update fails
     */
    void updateAuthenticator(String identifier, Credential oldCredential, Credential newCredential) 
            throws AuthenticatorException;

    /**
     * Revokes an authenticator.
     * 
     * @param identifier the user or service identifier
     * @throws AuthenticatorException if revocation fails
     */
    void revokeAuthenticator(String identifier) throws AuthenticatorException;

    /**
     * Checks if an authenticator has expired.
     * 
     * @param identifier the user or service identifier
     * @return true if the authenticator has expired, false otherwise
     * @throws AuthenticatorException if check fails
     */
    boolean isAuthenticatorExpired(String identifier) throws AuthenticatorException;
}
