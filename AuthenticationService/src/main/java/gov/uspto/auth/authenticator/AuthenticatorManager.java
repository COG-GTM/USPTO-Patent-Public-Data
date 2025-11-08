package gov.uspto.auth.authenticator;

import gov.uspto.auth.core.Credential;

import java.util.List;

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
     * @return the created authenticator
     * @throws AuthenticatorException if authenticator creation fails
     */
    Authenticator createAuthenticator(String identifier, Credential credential) throws AuthenticatorException;

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
     * @return the updated authenticator
     * @throws AuthenticatorException if update fails
     */
    Authenticator updateAuthenticator(String identifier, Credential oldCredential, Credential newCredential) 
            throws AuthenticatorException;

    /**
     * Revokes an authenticator for the specified identifier and type.
     * 
     * @param identifier the user or service identifier
     * @param type the authenticator type to revoke
     * @throws AuthenticatorException if revocation fails
     */
    void revokeAuthenticator(String identifier, AuthenticatorType type) throws AuthenticatorException;

    /**
     * Expires an authenticator for the specified identifier and type.
     * 
     * @param identifier the user or service identifier
     * @param type the authenticator type to expire
     * @throws AuthenticatorException if expiration fails
     */
    void expireAuthenticator(String identifier, AuthenticatorType type) throws AuthenticatorException;

    /**
     * Renews an expired authenticator.
     * 
     * @param identifier the user or service identifier
     * @param type the authenticator type to renew
     * @param credential the new credential
     * @return the renewed authenticator
     * @throws AuthenticatorException if renewal fails
     */
    Authenticator renewAuthenticator(String identifier, AuthenticatorType type, Credential credential) 
            throws AuthenticatorException;

    /**
     * Lists all authenticators for the specified identifier.
     * 
     * @param identifier the user or service identifier
     * @return list of authenticators for the identifier
     * @throws AuthenticatorException if listing fails
     */
    List<Authenticator> listUserAuthenticators(String identifier) throws AuthenticatorException;

    /**
     * Gets a specific authenticator by identifier and type.
     * 
     * @param identifier the user or service identifier
     * @param type the authenticator type
     * @return the authenticator, or null if not found
     * @throws AuthenticatorException if retrieval fails
     */
    Authenticator getAuthenticator(String identifier, AuthenticatorType type) throws AuthenticatorException;

    /**
     * Checks if an authenticator has expired.
     * 
     * @param identifier the user or service identifier
     * @param type the authenticator type
     * @return true if the authenticator has expired, false otherwise
     * @throws AuthenticatorException if check fails
     */
    boolean isAuthenticatorExpired(String identifier, AuthenticatorType type) throws AuthenticatorException;
}
