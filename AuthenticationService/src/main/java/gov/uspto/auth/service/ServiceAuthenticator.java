package gov.uspto.auth.service;

import gov.uspto.auth.core.AuthenticationResult;
import gov.uspto.auth.core.Credential;

/**
 * Interface for service-to-service authentication.
 * 
 * This interface provides methods for authenticating services and managing
 * service credentials in compliance with NIST 800-53 IA-9.
 * 
 * NIST 800-53 Controls: IA-9 (Service Identification and Authentication)
 */
public interface ServiceAuthenticator {

    /**
     * Authenticates a service using its credentials.
     * 
     * @param serviceCredential the service credential
     * @return the authentication result
     * @throws ServiceAuthenticationException if authentication fails
     */
    AuthenticationResult authenticateService(Credential serviceCredential) 
            throws ServiceAuthenticationException;

    /**
     * Registers a new service.
     * 
     * @param serviceId the service identifier
     * @param serviceCredential the service credential
     * @throws ServiceAuthenticationException if registration fails
     */
    void registerService(String serviceId, Credential serviceCredential) 
            throws ServiceAuthenticationException;

    /**
     * Revokes service credentials.
     * 
     * @param serviceId the service identifier
     * @throws ServiceAuthenticationException if revocation fails
     */
    void revokeServiceCredentials(String serviceId) throws ServiceAuthenticationException;

    /**
     * Validates a service token.
     * 
     * @param serviceToken the service token
     * @return true if the token is valid, false otherwise
     */
    boolean validateServiceToken(String serviceToken);
}
