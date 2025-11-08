package gov.uspto.auth.identifier;

/**
 * Interface for managing user and service identifiers.
 * 
 * This interface defines operations for creating, validating, and managing
 * unique identifiers for users and services in compliance with NIST 800-53.
 * 
 * NIST 800-53 Control: IA-4 (Identifier Management)
 * 
 * Future implementation should include:
 * - Unique identifier generation
 * - Identifier format validation
 * - Identifier lifecycle management
 * - Identifier reuse policies
 * - Identifier revocation
 */
public interface IdentifierManager {

    /**
     * Creates a new identifier for a user or service.
     * 
     * @param identifierType the type of identifier (USER, SERVICE, etc.)
     * @param attributes additional attributes for identifier creation
     * @return the created identifier, or null if creation failed
     */
    String createIdentifier(IdentifierType identifierType, String... attributes);

    /**
     * Validates an identifier format and existence.
     * 
     * @param identifier the identifier to validate
     * @return true if the identifier is valid, false otherwise
     */
    boolean validateIdentifier(String identifier);

    /**
     * Checks if an identifier is already in use.
     * 
     * @param identifier the identifier to check
     * @return true if the identifier exists, false otherwise
     */
    boolean identifierExists(String identifier);

    /**
     * Revokes an identifier.
     * 
     * @param identifier the identifier to revoke
     * @return true if revocation was successful, false otherwise
     */
    boolean revokeIdentifier(String identifier);

    /**
     * Checks if an identifier has been revoked.
     * 
     * @param identifier the identifier to check
     * @return true if the identifier is revoked, false otherwise
     */
    boolean isIdentifierRevoked(String identifier);

    /**
     * Types of identifiers that can be managed.
     */
    enum IdentifierType {
        USER,
        SERVICE,
        DEVICE,
        APPLICATION
    }
}
