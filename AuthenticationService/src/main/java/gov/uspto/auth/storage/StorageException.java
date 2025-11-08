package gov.uspto.auth.storage;

/**
 * Exception thrown when storage operations fail.
 * 
 * NIST 800-53 Controls: AU-9 (Protection of Audit Information)
 */
public class StorageException extends Exception {

    private static final long serialVersionUID = 1L;

    public StorageException(String message) {
        super(message);
    }

    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
