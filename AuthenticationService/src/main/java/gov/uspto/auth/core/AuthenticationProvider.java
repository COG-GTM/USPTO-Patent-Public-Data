package gov.uspto.auth.core;

/**
 * Interface for authentication providers.
 * 
 * An authentication provider is responsible for validating credentials and
 * producing an authentication result. Different implementations can support
 * various authentication mechanisms (password, token, certificate, etc.).
 * 
 * NIST 800-53 Controls:
 * - IA-2 (Identification and Authentication)
 * - IA-5 (Authenticator Management)
 */
public interface AuthenticationProvider {

    /**
     * Authenticates a credential and returns the result.
     * 
     * @param credential the credential to authenticate
     * @return the authentication result (success or failure)
     * @throws AuthenticationException if a system error occurs during authentication
     */
    AuthenticationResult authenticate(Credential credential) throws AuthenticationException;

    /**
     * Checks if this provider supports the given credential type.
     * 
     * @param credentialType the credential type to check
     * @return true if this provider supports the credential type, false otherwise
     */
    boolean supports(Credential.CredentialType credentialType);

    /**
     * Gets the name of this authentication provider.
     * 
     * @return the provider name
     */
    String getProviderName();
}
