package gov.uspto.auth.password;

import gov.uspto.auth.core.Credential;

import java.util.Arrays;

/**
 * Password-based credential implementation.
 * 
 * Stores password as char[] for security (can be cleared from memory).
 * 
 * NIST 800-53 Controls: IA-5(1) (Password-based Authentication)
 */
public class PasswordCredential extends Credential {

    private char[] password;

    /**
     * Creates a password credential.
     * 
     * @param identifier the user or service identifier
     * @param password the password as char array
     */
    public PasswordCredential(String identifier, char[] password) {
        super(identifier);
        if (password == null || password.length == 0) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        this.password = Arrays.copyOf(password, password.length);
    }

    /**
     * Creates a password credential from a String.
     * 
     * @param identifier the user or service identifier
     * @param password the password as String
     */
    public PasswordCredential(String identifier, String password) {
        this(identifier, password != null ? password.toCharArray() : null);
    }

    /**
     * Gets the password.
     * 
     * @return a copy of the password char array
     */
    public char[] getPassword() {
        return password != null ? Arrays.copyOf(password, password.length) : null;
    }

    @Override
    public boolean isValid() {
        return password != null && password.length > 0;
    }

    @Override
    public String getCredentialType() {
        return "password";
    }

    @Override
    public void clear() {
        if (password != null) {
            Arrays.fill(password, '\0');
            password = null;
        }
    }

    @Override
    public String toString() {
        return "PasswordCredential{" +
                "identifier='" + getIdentifier() + '\'' +
                ", passwordLength=" + (password != null ? password.length : 0) +
                '}';
    }
}
