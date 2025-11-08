package gov.uspto.auth.identity;

/**
 * Exception thrown when identity proofing operations fail.
 * 
 * NIST 800-53 Controls: IA-12 (Identity Proofing)
 */
public class IdentityProofingException extends Exception {

    private static final long serialVersionUID = 1L;

    public IdentityProofingException(String message) {
        super(message);
    }

    public IdentityProofingException(String message, Throwable cause) {
        super(message, cause);
    }
}
