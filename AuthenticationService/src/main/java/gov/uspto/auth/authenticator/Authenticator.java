package gov.uspto.auth.authenticator;

import java.time.Instant;

/**
 * Interface representing an authenticator (password, certificate, token, etc.).
 * 
 * An authenticator represents the stored authentication factor metadata and state
 * for a user or service identifier. This includes creation time, expiration,
 * status, and type information.
 * 
 * NIST 800-53 Controls: IA-5 (Authenticator Management)
 */
public interface Authenticator {

    /**
     * Gets the unique identifier for this authenticator.
     * 
     * @return the authenticator ID
     */
    String getId();

    /**
     * Gets the user or service identifier associated with this authenticator.
     * 
     * @return the identifier
     */
    String getIdentifier();

    /**
     * Gets the type of this authenticator.
     * 
     * @return the authenticator type
     */
    AuthenticatorType getType();

    /**
     * Gets the current status of this authenticator.
     * 
     * @return the authenticator status
     */
    AuthenticatorStatus getStatus();

    /**
     * Gets the creation timestamp of this authenticator.
     * 
     * @return the creation time
     */
    Instant getCreatedAt();

    /**
     * Gets the last update timestamp of this authenticator.
     * 
     * @return the last update time
     */
    Instant getUpdatedAt();

    /**
     * Gets the expiration timestamp of this authenticator.
     * 
     * @return the expiration time, or null if it does not expire
     */
    Instant getExpiresAt();

    /**
     * Checks if this authenticator has expired.
     * 
     * @return true if expired, false otherwise
     */
    boolean isExpired();

    /**
     * Checks if this authenticator is active (not expired, revoked, or locked).
     * 
     * @return true if active, false otherwise
     */
    boolean isActive();
}
