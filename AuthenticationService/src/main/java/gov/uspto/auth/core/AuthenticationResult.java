package gov.uspto.auth.core;

import java.time.Instant;

/**
 * Result of an authentication attempt.
 * 
 * This class encapsulates the outcome of an authentication operation, including
 * success/failure status, the authenticated principal (if successful), error details,
 * and audit trail information for NIST 800-53 compliance.
 * 
 * NIST 800-53 Controls: IA-2 (Identification and Authentication), AU-2 (Audit Events)
 */
public class AuthenticationResult {

    private final boolean success;
    private final Principal principal;
    private final String errorCode;
    private final String errorMessage;
    private final Instant timestamp;
    private final String sourceAddress;

    private AuthenticationResult(boolean success, Principal principal, String errorCode, 
                                 String errorMessage, String sourceAddress) {
        this.success = success;
        this.principal = principal;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.timestamp = Instant.now();
        this.sourceAddress = sourceAddress;
    }

    /**
     * Creates a successful authentication result.
     * 
     * @param principal the authenticated principal
     * @return a successful authentication result
     */
    public static AuthenticationResult success(Principal principal) {
        return success(principal, null);
    }

    /**
     * Creates a successful authentication result with source address.
     * 
     * @param principal the authenticated principal
     * @param sourceAddress the source address of the authentication request
     * @return a successful authentication result
     */
    public static AuthenticationResult success(Principal principal, String sourceAddress) {
        if (principal == null) {
            throw new IllegalArgumentException("Principal cannot be null for successful authentication");
        }
        return new AuthenticationResult(true, principal, null, null, sourceAddress);
    }

    /**
     * Creates a failed authentication result.
     * 
     * @param errorCode the error code
     * @param errorMessage the error message
     * @return a failed authentication result
     */
    public static AuthenticationResult failure(String errorCode, String errorMessage) {
        return failure(errorCode, errorMessage, null);
    }

    /**
     * Creates a failed authentication result with source address.
     * 
     * @param errorCode the error code
     * @param errorMessage the error message
     * @param sourceAddress the source address of the authentication request
     * @return a failed authentication result
     */
    public static AuthenticationResult failure(String errorCode, String errorMessage, String sourceAddress) {
        return new AuthenticationResult(false, null, errorCode, errorMessage, sourceAddress);
    }

    /**
     * Checks if the authentication was successful.
     * 
     * @return true if authentication succeeded, false otherwise
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Gets the authenticated principal.
     * 
     * @return the principal if authentication succeeded, null otherwise
     */
    public Principal getPrincipal() {
        return principal;
    }

    /**
     * Gets the error code if authentication failed.
     * 
     * @return the error code, or null if authentication succeeded
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * Gets the error message if authentication failed.
     * 
     * @return the error message, or null if authentication succeeded
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Gets the timestamp of the authentication attempt.
     * 
     * @return the timestamp
     */
    public Instant getTimestamp() {
        return timestamp;
    }

    /**
     * Gets the source address of the authentication request.
     * 
     * @return the source address, or null if not provided
     */
    public String getSourceAddress() {
        return sourceAddress;
    }

    @Override
    public String toString() {
        return "AuthenticationResult{" +
                "success=" + success +
                ", principal=" + (principal != null ? principal.getIdentifier() : "null") +
                ", errorCode='" + errorCode + '\'' +
                ", timestamp=" + timestamp +
                ", sourceAddress='" + sourceAddress + '\'' +
                '}';
    }
}
