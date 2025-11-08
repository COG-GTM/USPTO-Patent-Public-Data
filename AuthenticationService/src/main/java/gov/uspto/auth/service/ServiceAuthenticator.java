package gov.uspto.auth.service;

import gov.uspto.auth.core.AuthenticationResult;
import gov.uspto.auth.core.Credential;

/**
 * Interface for service-to-service authentication.
 * 
 * This interface defines operations for authenticating services and applications
 * in a microservices architecture, in compliance with NIST 800-53.
 * 
 * NIST 800-53 Control: IA-9 (Service Identification and Authentication)
 * 
 * Future implementation should include:
 * - API key authentication
 * - Certificate-based authentication
 * - Service identity verification
 * - Service authorization
 * - Service credential rotation
 */
public interface ServiceAuthenticator {

    /**
     * Authenticates a service using provided credentials.
     * 
     * @param serviceId the service identifier
     * @param credential the service credential
     * @return the authentication result
     */
    AuthenticationResult authenticateService(String serviceId, Credential credential);

    /**
     * Registers a new service.
     * 
     * @param serviceId the service identifier
     * @param serviceName the service name
     * @param credential the service credential
     * @return true if registration was successful, false otherwise
     */
    boolean registerService(String serviceId, String serviceName, Credential credential);

    /**
     * Revokes service credentials.
     * 
     * @param serviceId the service identifier
     * @return true if revocation was successful, false otherwise
     */
    boolean revokeServiceCredentials(String serviceId);

    /**
     * Rotates service credentials.
     * 
     * @param serviceId the service identifier
     * @param newCredential the new credential
     * @return true if rotation was successful, false otherwise
     */
    boolean rotateServiceCredentials(String serviceId, Credential newCredential);

    /**
     * Validates a service credential.
     * 
     * @param serviceId the service identifier
     * @param credential the credential to validate
     * @return true if the credential is valid, false otherwise
     */
    boolean validateServiceCredential(String serviceId, Credential credential);

    /**
     * Checks if a service is registered.
     * 
     * @param serviceId the service identifier
     * @return true if the service is registered, false otherwise
     */
    boolean isServiceRegistered(String serviceId);
}
