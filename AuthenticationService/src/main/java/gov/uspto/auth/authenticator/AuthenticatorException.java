package gov.uspto.auth.authenticator;

/**
 * Exception thrown when authenticator management operations fail.
 * 
 * NIST 800-53 Controls: IA-5 (Authenticator Management)
 */
public class AuthenticatorException extends Exception {

    private static final long serialVersionUID = 1L;

    public AuthenticatorException(String message) {
        super(message);
    }

    public AuthenticatorException(String message, Throwable cause) {
        super(message, cause);
    }
}
