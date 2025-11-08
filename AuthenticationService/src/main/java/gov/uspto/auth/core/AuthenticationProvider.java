package gov.uspto.auth.core;

/**
 * Interface for authentication providers that can authenticate credentials.
 * 
 * Implementations of this interface support different authentication mechanisms
 * such as password-based authentication, token-based authentication, certificate-based
 * authentication, or other custom authentication schemes.
 * 
 * NIST 800-53 Controls: IA-2 (Identification and Authentication), IA-5 (Authenticator Management)
 * 
 * @see Credential
 * @see AuthenticationResult
 */
public interface AuthenticationProvider {

    /**
     * Authenticates the provided credential and returns the result.
     * 
     * @param credential the credential to authenticate
     * @return the authentication result containing success/failure status and principal if successful
     * @throws AuthenticationException if authentication fails due to invalid credentials or system errors
     * @throws IllegalArgumentException if credential is null or invalid
     */
    AuthenticationResult authenticate(Credential credential) throws AuthenticationException;

    /**
     * Checks if this provider supports the given credential type.
     * 
     * @param credentialClass the credential class to check
     * @return true if this provider can authenticate the credential type, false otherwise
     */
    boolean supports(Class<? extends Credential> credentialClass);

    /**
     * Gets the name of this authentication provider.
     * 
     * @return the provider name
     */
    String getProviderName();
}
