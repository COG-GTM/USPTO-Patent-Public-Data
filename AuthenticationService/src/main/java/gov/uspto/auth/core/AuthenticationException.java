package gov.uspto.auth.core;

/**
 * Base exception class for authentication-related errors.
 * 
 * This exception is thrown when authentication fails due to invalid credentials,
 * system errors, or policy violations. It includes error codes for different
 * failure types to support proper error handling and audit logging.
 * 
 * NIST 800-53 Controls: IA-2 (Identification and Authentication), AU-2 (Audit Events)
 */
public class AuthenticationException extends Exception {

    private static final long serialVersionUID = 1L;

    private final String errorCode;

    /**
     * Error code constants for different authentication failure types.
     */
    public static final String ERROR_INVALID_CREDENTIALS = "AUTH_001";
    public static final String ERROR_ACCOUNT_LOCKED = "AUTH_002";
    public static final String ERROR_ACCOUNT_DISABLED = "AUTH_003";
    public static final String ERROR_ACCOUNT_EXPIRED = "AUTH_004";
    public static final String ERROR_CREDENTIALS_EXPIRED = "AUTH_005";
    public static final String ERROR_INSUFFICIENT_PRIVILEGES = "AUTH_006";
    public static final String ERROR_SYSTEM_ERROR = "AUTH_007";
    public static final String ERROR_POLICY_VIOLATION = "AUTH_008";
    public static final String ERROR_INVALID_TOKEN = "AUTH_009";
    public static final String ERROR_TOKEN_EXPIRED = "AUTH_010";

    /**
     * Creates an authentication exception with the specified error code and message.
     * 
     * @param errorCode the error code
     * @param message the error message
     */
    public AuthenticationException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * Creates an authentication exception with the specified error code, message, and cause.
     * 
     * @param errorCode the error code
     * @param message the error message
     * @param cause the underlying cause
     */
    public AuthenticationException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    /**
     * Gets the error code for this exception.
     * 
     * @return the error code
     */
    public String getErrorCode() {
        return errorCode;
    }

    @Override
    public String toString() {
        return "AuthenticationException{" +
                "errorCode='" + errorCode + '\'' +
                ", message='" + getMessage() + '\'' +
                '}';
    }
}
