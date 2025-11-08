package gov.uspto.auth.identifier;

/**
 * Exception thrown when identifier management operations fail.
 * 
 * NIST 800-53 Controls: IA-4 (Identifier Management)
 */
public class IdentifierException extends Exception {

    private static final long serialVersionUID = 1L;

    public IdentifierException(String message) {
        super(message);
    }

    public IdentifierException(String message, Throwable cause) {
        super(message, cause);
    }
}
