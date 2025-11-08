package gov.uspto.auth.core;

/**
 * Abstract base class for authentication credentials.
 * 
 * Credentials represent the information provided by a user or service to prove
 * their identity. Different credential types include passwords, tokens, certificates,
 * biometric data, etc.
 * 
 * NIST 800-53 Controls: IA-5 (Authenticator Management)
 */
public abstract class Credential {

    private final String identifier;

    /**
     * Creates a credential with the specified identifier.
     * 
     * @param identifier the user or service identifier associated with this credential
     */
    protected Credential(String identifier) {
        if (identifier == null || identifier.trim().isEmpty()) {
            throw new IllegalArgumentException("Credential identifier cannot be null or empty");
        }
        this.identifier = identifier;
    }

    /**
     * Gets the identifier associated with this credential.
     * 
     * @return the identifier
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Validates the credential format and content.
     * 
     * @return true if the credential is valid, false otherwise
     */
    public abstract boolean isValid();

    /**
     * Gets the type of this credential (e.g., "password", "token", "certificate").
     * 
     * @return the credential type
     */
    public abstract String getCredentialType();

    /**
     * Clears sensitive credential data from memory.
     * 
     * This method should be called after authentication to ensure sensitive
     * information like passwords are not retained in memory longer than necessary.
     */
    public abstract void clear();

    @Override
    public String toString() {
        return "Credential{" +
                "identifier='" + identifier + '\'' +
                ", type='" + getCredentialType() + '\'' +
                '}';
    }
}
