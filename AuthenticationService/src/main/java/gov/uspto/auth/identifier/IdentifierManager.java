package gov.uspto.auth.identifier;

/**
 * Interface for managing user and service identifiers.
 * 
 * This interface provides methods for creating, validating, and managing unique
 * identifiers for users and services in compliance with NIST 800-53 IA-4.
 * 
 * NIST 800-53 Controls: IA-4 (Identifier Management)
 */
public interface IdentifierManager {

    /**
     * Creates a new identifier for a user or service.
     * 
     * @param identifierType the type of identifier (user, service, etc.)
     * @param attributes additional attributes for identifier creation
     * @return the created identifier
     * @throws IdentifierException if identifier creation fails
     */
    String createIdentifier(String identifierType, java.util.Map<String, String> attributes) 
            throws IdentifierException;

    /**
     * Validates an identifier format and uniqueness.
     * 
     * @param identifier the identifier to validate
     * @return true if the identifier is valid, false otherwise
     */
    boolean validateIdentifier(String identifier);

    /**
     * Checks if an identifier exists.
     * 
     * @param identifier the identifier to check
     * @return true if the identifier exists, false otherwise
     */
    boolean identifierExists(String identifier);

    /**
     * Disables an identifier.
     * 
     * @param identifier the identifier to disable
     * @throws IdentifierException if disabling fails
     */
    void disableIdentifier(String identifier) throws IdentifierException;

    /**
     * Enables a previously disabled identifier.
     * 
     * @param identifier the identifier to enable
     * @throws IdentifierException if enabling fails
     */
    void enableIdentifier(String identifier) throws IdentifierException;
}
