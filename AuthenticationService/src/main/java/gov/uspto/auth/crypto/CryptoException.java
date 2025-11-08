package gov.uspto.auth.crypto;

/**
 * Exception thrown when cryptographic operations fail.
 * 
 * NIST 800-53 Controls: SC-13 (Cryptographic Protection)
 */
public class CryptoException extends Exception {

    private static final long serialVersionUID = 1L;

    public CryptoException(String message) {
        super(message);
    }

    public CryptoException(String message, Throwable cause) {
        super(message, cause);
    }
}
