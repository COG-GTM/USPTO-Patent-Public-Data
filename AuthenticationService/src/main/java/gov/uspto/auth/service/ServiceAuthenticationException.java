package gov.uspto.auth.service;

/**
 * Exception thrown when service authentication operations fail.
 * 
 * NIST 800-53 Controls: IA-9 (Service Identification and Authentication)
 */
public class ServiceAuthenticationException extends Exception {

    private static final long serialVersionUID = 1L;

    public ServiceAuthenticationException(String message) {
        super(message);
    }

    public ServiceAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
