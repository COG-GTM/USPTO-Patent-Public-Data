package gov.uspto.auth.session;

/**
 * Exception thrown when session management operations fail.
 * 
 * NIST 800-53 Controls: IA-11 (Re-authentication), AC-12 (Session Termination)
 */
public class SessionException extends Exception {

    private static final long serialVersionUID = 1L;

    public SessionException(String message) {
        super(message);
    }

    public SessionException(String message, Throwable cause) {
        super(message, cause);
    }
}
