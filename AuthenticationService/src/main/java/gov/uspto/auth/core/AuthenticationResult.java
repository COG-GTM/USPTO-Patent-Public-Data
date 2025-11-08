package gov.uspto.auth.core;

import java.time.Instant;

/**
 * Result of an authentication attempt.
 * 
 * This class encapsulates the outcome of an authentication operation, including
 * success/failure status, the authenticated principal (if successful), and any
 * error information (if failed).
 * 
 * NIST 800-53 Controls:
 * - IA-2 (Identification and Authentication)
 * - AU-2 (Audit Events) - supports audit trail requirements
 */
public class AuthenticationResult {

    private final boolean success;
    private final Principal principal;
    private final String errorMessage;
    private final AuthenticationException.ErrorCode errorCode;
    private final Instant timestamp;

    private AuthenticationResult(boolean success, Principal principal, 
                                 String errorMessage, AuthenticationException.ErrorCode errorCode) {
        this.success = success;
        this.principal = principal;
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
        this.timestamp = Instant.now();
    }

    /**
     * Creates a successful authentication result.
     * 
     * @param principal the authenticated principal
     * @return a successful authentication result
     */
    public static AuthenticationResult success(Principal principal) {
        if (principal == null) {
            throw new IllegalArgumentException("Principal cannot be null for successful authentication");
        }
        return new AuthenticationResult(true, principal, null, null);
    }

    /**
     * Creates a failed authentication result.
     * 
     * @param errorMessage the error message describing the failure
     * @return a failed authentication result
     */
    public static AuthenticationResult failure(String errorMessage) {
        return new AuthenticationResult(false, null, errorMessage, 
                                       AuthenticationException.ErrorCode.AUTHENTICATION_FAILED);
    }

    /**
     * Creates a failed authentication result with a specific error code.
     * 
     * @param errorMessage the error message describing the failure
     * @param errorCode the specific error code
     * @return a failed authentication result
     */
    public static AuthenticationResult failure(String errorMessage, 
                                               AuthenticationException.ErrorCode errorCode) {
        return new AuthenticationResult(false, null, errorMessage, errorCode);
    }

    public boolean isSuccess() {
        return success;
    }

    public Principal getPrincipal() {
        return principal;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public AuthenticationException.ErrorCode getErrorCode() {
        return errorCode;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    /**
     * Converts this result to an exception if it represents a failure.
     * 
     * @return an AuthenticationException if this is a failure result
     * @throws IllegalStateException if this is a success result
     */
    public AuthenticationException toException() {
        if (success) {
            throw new IllegalStateException("Cannot convert successful result to exception");
        }
        return new AuthenticationException(errorMessage, errorCode);
    }

    @Override
    public String toString() {
        if (success) {
            return "AuthenticationResult [success=true, principal=" + principal + 
                   ", timestamp=" + timestamp + "]";
        } else {
            return "AuthenticationResult [success=false, errorMessage=" + errorMessage + 
                   ", errorCode=" + errorCode + ", timestamp=" + timestamp + "]";
        }
    }
}
