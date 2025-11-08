package gov.uspto.auth.core;

/**
 * Abstract base class for authentication credentials.
 * 
 * Credentials represent the information provided by a user or service to prove
 * their identity. Different credential types (password, token, certificate) should
 * extend this class.
 * 
 * NIST 800-53 Control: IA-5 (Authenticator Management)
 */
public abstract class Credential {

    private final String identifier;
    private final CredentialType type;

    protected Credential(String identifier, CredentialType type) {
        if (identifier == null || identifier.trim().isEmpty()) {
            throw new IllegalArgumentException("Identifier cannot be null or empty");
        }
        if (type == null) {
            throw new IllegalArgumentException("Credential type cannot be null");
        }
        this.identifier = identifier;
        this.type = type;
    }

    public String getIdentifier() {
        return identifier;
    }

    public CredentialType getType() {
        return type;
    }

    /**
     * Validates the credential format and content.
     * 
     * @return true if the credential is valid, false otherwise
     */
    public abstract boolean isValid();

    /**
     * Clears sensitive credential data from memory.
     * 
     * This method should be called when the credential is no longer needed
     * to prevent sensitive data from remaining in memory.
     */
    public abstract void clear();

    /**
     * Types of credentials supported by the authentication framework.
     */
    public enum CredentialType {
        PASSWORD("Password-based authentication"),
        TOKEN("Token-based authentication"),
        CERTIFICATE("Certificate-based authentication"),
        API_KEY("API key authentication"),
        BIOMETRIC("Biometric authentication");

        private final String description;

        CredentialType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    @Override
    public String toString() {
        return "Credential [identifier=" + identifier + ", type=" + type + "]";
    }
}
