package gov.uspto.auth.password;

import gov.uspto.auth.authenticator.Authenticator;
import gov.uspto.auth.authenticator.AuthenticatorStatus;
import gov.uspto.auth.authenticator.AuthenticatorType;

import java.time.Instant;
import java.util.UUID;

/**
 * Password-based authenticator implementation.
 * 
 * Stores password hash (not plaintext) and metadata including:
 * - Creation and update timestamps
 * - Expiration timestamp
 * - Status (active, expired, revoked, locked)
 * - Failed attempt tracking
 * 
 * NIST 800-53 Controls: IA-5(1) (Password-based Authentication)
 */
public class PasswordAuthenticator implements Authenticator {

    private final String id;
    private final String identifier;
    private final String passwordHash;
    private final Instant createdAt;
    private final Instant updatedAt;
    private final Instant expiresAt;
    private final AuthenticatorStatus status;
    private final int failedAttempts;
    private final Instant lockedUntil;

    private PasswordAuthenticator(Builder builder) {
        this.id = builder.id;
        this.identifier = builder.identifier;
        this.passwordHash = builder.passwordHash;
        this.createdAt = builder.createdAt;
        this.updatedAt = builder.updatedAt;
        this.expiresAt = builder.expiresAt;
        this.status = builder.status;
        this.failedAttempts = builder.failedAttempts;
        this.lockedUntil = builder.lockedUntil;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public AuthenticatorType getType() {
        return AuthenticatorType.PASSWORD;
    }

    @Override
    public AuthenticatorStatus getStatus() {
        return status;
    }

    @Override
    public Instant getCreatedAt() {
        return createdAt;
    }

    @Override
    public Instant getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public Instant getExpiresAt() {
        return expiresAt;
    }

    @Override
    public boolean isExpired() {
        if (expiresAt == null) {
            return false;
        }
        return Instant.now().isAfter(expiresAt);
    }

    @Override
    public boolean isActive() {
        if (status != AuthenticatorStatus.ACTIVE) {
            return false;
        }
        if (isExpired()) {
            return false;
        }
        if (lockedUntil != null && Instant.now().isBefore(lockedUntil)) {
            return false;
        }
        return true;
    }

    /**
     * Gets the password hash.
     * 
     * @return the BCrypt password hash
     */
    public String getPasswordHash() {
        return passwordHash;
    }

    /**
     * Gets the number of failed authentication attempts.
     * 
     * @return the failed attempt count
     */
    public int getFailedAttempts() {
        return failedAttempts;
    }

    /**
     * Gets the timestamp until which this authenticator is locked.
     * 
     * @return the locked until timestamp, or null if not locked
     */
    public Instant getLockedUntil() {
        return lockedUntil;
    }

    /**
     * Checks if this authenticator is currently locked.
     * 
     * @return true if locked, false otherwise
     */
    public boolean isLocked() {
        if (lockedUntil == null) {
            return false;
        }
        return Instant.now().isBefore(lockedUntil);
    }

    /**
     * Creates a builder for a new password authenticator.
     * 
     * @param identifier the user or service identifier
     * @param passwordHash the BCrypt password hash
     * @return a new builder
     */
    public static Builder builder(String identifier, String passwordHash) {
        return new Builder(identifier, passwordHash);
    }

    /**
     * Creates a builder from an existing authenticator (for updates).
     * 
     * @param authenticator the existing authenticator
     * @return a new builder with values from the existing authenticator
     */
    public static Builder from(PasswordAuthenticator authenticator) {
        return new Builder(authenticator.identifier, authenticator.passwordHash)
                .id(authenticator.id)
                .createdAt(authenticator.createdAt)
                .updatedAt(authenticator.updatedAt)
                .expiresAt(authenticator.expiresAt)
                .status(authenticator.status)
                .failedAttempts(authenticator.failedAttempts)
                .lockedUntil(authenticator.lockedUntil);
    }

    /**
     * Builder for creating PasswordAuthenticator instances.
     */
    public static class Builder {
        private String id;
        private final String identifier;
        private final String passwordHash;
        private Instant createdAt;
        private Instant updatedAt;
        private Instant expiresAt;
        private AuthenticatorStatus status;
        private int failedAttempts;
        private Instant lockedUntil;

        private Builder(String identifier, String passwordHash) {
            if (identifier == null || identifier.trim().isEmpty()) {
                throw new IllegalArgumentException("Identifier cannot be null or empty");
            }
            if (passwordHash == null || passwordHash.trim().isEmpty()) {
                throw new IllegalArgumentException("Password hash cannot be null or empty");
            }
            this.identifier = identifier;
            this.passwordHash = passwordHash;
            this.id = UUID.randomUUID().toString();
            this.createdAt = Instant.now();
            this.updatedAt = Instant.now();
            this.status = AuthenticatorStatus.ACTIVE;
            this.failedAttempts = 0;
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder updatedAt(Instant updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Builder expiresAt(Instant expiresAt) {
            this.expiresAt = expiresAt;
            return this;
        }

        public Builder status(AuthenticatorStatus status) {
            this.status = status;
            return this;
        }

        public Builder failedAttempts(int failedAttempts) {
            this.failedAttempts = failedAttempts;
            return this;
        }

        public Builder lockedUntil(Instant lockedUntil) {
            this.lockedUntil = lockedUntil;
            return this;
        }

        public PasswordAuthenticator build() {
            return new PasswordAuthenticator(this);
        }
    }

    @Override
    public String toString() {
        return "PasswordAuthenticator{" +
                "id='" + id + '\'' +
                ", identifier='" + identifier + '\'' +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", expiresAt=" + expiresAt +
                ", failedAttempts=" + failedAttempts +
                ", lockedUntil=" + lockedUntil +
                '}';
    }
}
