package gov.uspto.auth.authenticator;

/**
 * Enumeration of authenticator lifecycle states.
 * 
 * NIST 800-53 Controls: IA-5 (Authenticator Management)
 */
public enum AuthenticatorStatus {
    
    /**
     * Authenticator is active and can be used for authentication.
     */
    ACTIVE("active"),
    
    /**
     * Authenticator has expired and cannot be used until renewed.
     */
    EXPIRED("expired"),
    
    /**
     * Authenticator has been revoked and cannot be used.
     */
    REVOKED("revoked"),
    
    /**
     * Authenticator is locked due to policy violations (e.g., too many failed attempts).
     */
    LOCKED("locked");

    private final String value;

    AuthenticatorStatus(String value) {
        this.value = value;
    }

    /**
     * Gets the string value of this status.
     * 
     * @return the string value
     */
    public String getValue() {
        return value;
    }

    /**
     * Gets an AuthenticatorStatus from its string value.
     * 
     * @param value the string value
     * @return the corresponding AuthenticatorStatus
     * @throws IllegalArgumentException if the value is not recognized
     */
    public static AuthenticatorStatus fromValue(String value) {
        for (AuthenticatorStatus status : values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown authenticator status: " + value);
    }

    @Override
    public String toString() {
        return value;
    }
}
