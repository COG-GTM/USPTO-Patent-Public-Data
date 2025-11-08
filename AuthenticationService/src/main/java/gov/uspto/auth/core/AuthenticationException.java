package gov.uspto.auth.core;

/**
 * Base exception class for authentication-related errors.
 * 
 * This exception is thrown when authentication operations fail for various reasons
 * including invalid credentials, expired tokens, locked accounts, or system errors.
 * 
 * NIST 800-53 Control: IA-2 (Identification and Authentication)
 */
public class AuthenticationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final ErrorCode errorCode;

    public AuthenticationException(String message) {
        this(message, ErrorCode.AUTHENTICATION_FAILED);
    }

    public AuthenticationException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public AuthenticationException(String message, Throwable cause) {
        this(message, ErrorCode.AUTHENTICATION_FAILED, cause);
    }

    public AuthenticationException(String message, ErrorCode errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    /**
     * Error codes for different authentication failure types.
     */
    public enum ErrorCode {
        AUTHENTICATION_FAILED("Authentication failed"),
        INVALID_CREDENTIALS("Invalid credentials provided"),
        EXPIRED_CREDENTIALS("Credentials have expired"),
        ACCOUNT_LOCKED("Account is locked"),
        ACCOUNT_DISABLED("Account is disabled"),
        INVALID_TOKEN("Invalid or malformed token"),
        EXPIRED_TOKEN("Token has expired"),
        INSUFFICIENT_PRIVILEGES("Insufficient privileges for operation"),
        SYSTEM_ERROR("System error during authentication");

        private final String description;

        ErrorCode(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}
