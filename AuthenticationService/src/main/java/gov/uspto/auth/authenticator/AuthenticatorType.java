package gov.uspto.auth.authenticator;

/**
 * Enumeration of authenticator types supported by the system.
 * 
 * NIST 800-53 Controls:
 * - IA-5(1): Password-based Authentication
 * - IA-5(2): PKI-based Authentication
 * - IA-5(11): Hardware Token-based Authentication
 */
public enum AuthenticatorType {
    
    /**
     * Password-based authenticator (NIST 800-53 IA-5(1)).
     */
    PASSWORD("password"),
    
    /**
     * PKI certificate-based authenticator (NIST 800-53 IA-5(2)).
     */
    PKI_CERT("pki_cert"),
    
    /**
     * Hardware token-based authenticator (NIST 800-53 IA-5(11)).
     */
    HARDWARE_TOKEN("hardware_token"),
    
    /**
     * API key-based authenticator.
     */
    API_KEY("api_key");

    private final String value;

    AuthenticatorType(String value) {
        this.value = value;
    }

    /**
     * Gets the string value of this authenticator type.
     * 
     * @return the string value
     */
    public String getValue() {
        return value;
    }

    /**
     * Gets an AuthenticatorType from its string value.
     * 
     * @param value the string value
     * @return the corresponding AuthenticatorType
     * @throws IllegalArgumentException if the value is not recognized
     */
    public static AuthenticatorType fromValue(String value) {
        for (AuthenticatorType type : values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown authenticator type: " + value);
    }

    @Override
    public String toString() {
        return value;
    }
}
