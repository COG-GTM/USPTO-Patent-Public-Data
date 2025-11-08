package gov.uspto.auth.authenticator;

import gov.uspto.auth.core.Credential;
import gov.uspto.auth.password.PasswordAuthenticator;
import gov.uspto.auth.password.PasswordCredential;
import gov.uspto.auth.password.PasswordHasher;
import gov.uspto.auth.password.PasswordPolicy;
import gov.uspto.auth.password.PasswordValidator;
import gov.uspto.auth.policy.PolicyValidationResult;
import gov.uspto.auth.storage.InMemoryAuthenticationStorage;
import gov.uspto.auth.storage.StorageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * In-memory implementation of AuthenticatorManager.
 * 
 * Manages authenticator lifecycle including:
 * - Creation with policy validation
 * - Validation against stored authenticators
 * - Updates with password history checking
 * - Revocation and expiration
 * - Renewal of expired authenticators
 * 
 * NIST 800-53 Controls: IA-5 (Authenticator Management)
 */
public class InMemoryAuthenticatorManager implements AuthenticatorManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(InMemoryAuthenticatorManager.class);

    private final InMemoryAuthenticationStorage storage;
    private final PasswordHasher passwordHasher;
    private final PasswordPolicy passwordPolicy;
    private final PasswordValidator passwordValidator;

    /**
     * Creates an in-memory authenticator manager.
     * 
     * @param storage the authentication storage
     * @param passwordHasher the password hasher
     * @param passwordPolicy the password policy
     */
    public InMemoryAuthenticatorManager(
            InMemoryAuthenticationStorage storage,
            PasswordHasher passwordHasher,
            PasswordPolicy passwordPolicy) {
        if (storage == null) {
            throw new IllegalArgumentException("Storage cannot be null");
        }
        if (passwordHasher == null) {
            throw new IllegalArgumentException("Password hasher cannot be null");
        }
        if (passwordPolicy == null) {
            throw new IllegalArgumentException("Password policy cannot be null");
        }
        this.storage = storage;
        this.passwordHasher = passwordHasher;
        this.passwordPolicy = passwordPolicy;
        this.passwordValidator = new PasswordValidator(passwordPolicy);
    }

    @Override
    public Authenticator createAuthenticator(String identifier, Credential credential) 
            throws AuthenticatorException {
        if (identifier == null || identifier.trim().isEmpty()) {
            throw new AuthenticatorException("Identifier cannot be null or empty");
        }
        if (credential == null) {
            throw new AuthenticatorException("Credential cannot be null");
        }

        try {
            if (credential instanceof PasswordCredential) {
                return createPasswordAuthenticator(identifier, (PasswordCredential) credential);
            } else {
                throw new AuthenticatorException("Unsupported credential type: " + 
                        credential.getClass().getSimpleName());
            }
        } catch (StorageException e) {
            throw new AuthenticatorException("Failed to create authenticator", e);
        }
    }

    private Authenticator createPasswordAuthenticator(String identifier, PasswordCredential credential) 
            throws AuthenticatorException, StorageException {
        
        PolicyValidationResult validationResult = passwordValidator.validate(credential.getPassword());
        if (!validationResult.isValid()) {
            String violations = String.join("; ", validationResult.getViolations());
            LOGGER.warn("Password validation failed for identifier {}: {}", identifier, violations);
            throw new AuthenticatorException("Password validation failed: " + violations);
        }

        if (storage.authenticatorExists(identifier, AuthenticatorType.PASSWORD)) {
            throw new AuthenticatorException("Password authenticator already exists for identifier: " + identifier);
        }

        String passwordHash = passwordHasher.hashPassword(credential.getPassword());

        Instant expiresAt = passwordPolicy.getPasswordExpiryDays() > 0 
                ? Instant.now().plus(passwordPolicy.getPasswordExpiryDays(), ChronoUnit.DAYS)
                : null;

        PasswordAuthenticator authenticator = PasswordAuthenticator.builder(identifier, passwordHash)
                .expiresAt(expiresAt)
                .build();

        storage.storeAuthenticator(identifier, authenticator);
        storage.storePasswordHistory(identifier, passwordHash);

        LOGGER.info("Created password authenticator for identifier: {}", identifier);
        return authenticator;
    }

    @Override
    public boolean validateAuthenticator(String identifier, Credential credential) 
            throws AuthenticatorException {
        if (identifier == null || identifier.trim().isEmpty()) {
            throw new AuthenticatorException("Identifier cannot be null or empty");
        }
        if (credential == null) {
            throw new AuthenticatorException("Credential cannot be null");
        }

        try {
            if (credential instanceof PasswordCredential) {
                return validatePasswordAuthenticator(identifier, (PasswordCredential) credential);
            } else {
                throw new AuthenticatorException("Unsupported credential type: " + 
                        credential.getClass().getSimpleName());
            }
        } catch (StorageException e) {
            throw new AuthenticatorException("Failed to validate authenticator", e);
        }
    }

    private boolean validatePasswordAuthenticator(String identifier, PasswordCredential credential) 
            throws AuthenticatorException, StorageException {
        
        PasswordAuthenticator authenticator = (PasswordAuthenticator) storage.retrieveAuthenticator(
                identifier, AuthenticatorType.PASSWORD);

        if (authenticator == null) {
            LOGGER.warn("No password authenticator found for identifier: {}", identifier);
            return false;
        }

        if (!authenticator.isActive()) {
            LOGGER.warn("Password authenticator is not active for identifier: {}", identifier);
            return false;
        }

        boolean matches = passwordHasher.verifyPassword(credential.getPassword(), authenticator.getPasswordHash());

        if (matches) {
            storage.resetFailedAttempts(identifier);
            LOGGER.debug("Password validation successful for identifier: {}", identifier);
        } else {
            handleFailedAttempt(identifier, authenticator);
            LOGGER.warn("Password validation failed for identifier: {}", identifier);
        }

        return matches;
    }

    private void handleFailedAttempt(String identifier, PasswordAuthenticator authenticator) 
            throws StorageException, AuthenticatorException {
        
        int failedAttempts = storage.recordFailedAttempt(identifier);
        
        Instant windowStart = Instant.now().minus(passwordPolicy.getLockoutWindowMinutes(), ChronoUnit.MINUTES);
        List<Instant> recentAttempts = storage.getFailedAttemptsInWindow(identifier, windowStart);

        if (recentAttempts.size() >= passwordPolicy.getMaxFailedAttempts()) {
            Instant lockoutUntil = Instant.now().plus(passwordPolicy.getLockoutDurationMinutes(), ChronoUnit.MINUTES);
            storage.setLockoutUntil(identifier, lockoutUntil);

            PasswordAuthenticator lockedAuthenticator = PasswordAuthenticator.from(authenticator)
                    .status(AuthenticatorStatus.LOCKED)
                    .lockedUntil(lockoutUntil)
                    .failedAttempts(failedAttempts)
                    .updatedAt(Instant.now())
                    .build();

            storage.storeAuthenticator(identifier, lockedAuthenticator);
            LOGGER.warn("Account locked for identifier {} due to {} failed attempts", identifier, recentAttempts.size());
        }
    }

    @Override
    public Authenticator updateAuthenticator(String identifier, Credential oldCredential, Credential newCredential) 
            throws AuthenticatorException {
        if (identifier == null || identifier.trim().isEmpty()) {
            throw new AuthenticatorException("Identifier cannot be null or empty");
        }
        if (oldCredential == null || newCredential == null) {
            throw new AuthenticatorException("Credentials cannot be null");
        }

        try {
            if (!validateAuthenticator(identifier, oldCredential)) {
                throw new AuthenticatorException("Old credential validation failed");
            }

            if (oldCredential instanceof PasswordCredential && newCredential instanceof PasswordCredential) {
                return updatePasswordAuthenticator(identifier, (PasswordCredential) newCredential);
            } else {
                throw new AuthenticatorException("Unsupported credential type");
            }
        } catch (StorageException e) {
            throw new AuthenticatorException("Failed to update authenticator", e);
        }
    }

    private Authenticator updatePasswordAuthenticator(String identifier, PasswordCredential newCredential) 
            throws AuthenticatorException, StorageException {
        
        PolicyValidationResult validationResult = passwordValidator.validate(newCredential.getPassword());
        if (!validationResult.isValid()) {
            String violations = String.join("; ", validationResult.getViolations());
            throw new AuthenticatorException("Password validation failed: " + violations);
        }

        String newPasswordHash = passwordHasher.hashPassword(newCredential.getPassword());

        List<String> history = storage.getPasswordHistory(identifier);
        for (String oldHash : history) {
            if (passwordHasher.verifyPassword(newCredential.getPassword(), oldHash)) {
                throw new AuthenticatorException("Password has been used recently and cannot be reused");
            }
        }

        PasswordAuthenticator oldAuthenticator = (PasswordAuthenticator) storage.retrieveAuthenticator(
                identifier, AuthenticatorType.PASSWORD);

        Instant expiresAt = passwordPolicy.getPasswordExpiryDays() > 0 
                ? Instant.now().plus(passwordPolicy.getPasswordExpiryDays(), ChronoUnit.DAYS)
                : null;

        PasswordAuthenticator newAuthenticator = PasswordAuthenticator.from(oldAuthenticator)
                .updatedAt(Instant.now())
                .expiresAt(expiresAt)
                .status(AuthenticatorStatus.ACTIVE)
                .failedAttempts(0)
                .lockedUntil(null)
                .build();

        PasswordAuthenticator updatedAuthenticator = PasswordAuthenticator.builder(identifier, newPasswordHash)
                .id(newAuthenticator.getId())
                .createdAt(newAuthenticator.getCreatedAt())
                .updatedAt(Instant.now())
                .expiresAt(expiresAt)
                .status(AuthenticatorStatus.ACTIVE)
                .build();

        storage.storeAuthenticator(identifier, updatedAuthenticator);
        storage.storePasswordHistory(identifier, newPasswordHash);
        storage.trimPasswordHistory(identifier, passwordPolicy.getPasswordHistorySize());
        storage.resetFailedAttempts(identifier);
        storage.setLockoutUntil(identifier, null);

        LOGGER.info("Updated password authenticator for identifier: {}", identifier);
        return updatedAuthenticator;
    }

    @Override
    public void revokeAuthenticator(String identifier, AuthenticatorType type) throws AuthenticatorException {
        if (identifier == null || identifier.trim().isEmpty()) {
            throw new AuthenticatorException("Identifier cannot be null or empty");
        }
        if (type == null) {
            throw new AuthenticatorException("Authenticator type cannot be null");
        }

        try {
            Authenticator authenticator = storage.retrieveAuthenticator(identifier, type);
            if (authenticator == null) {
                throw new AuthenticatorException("Authenticator not found");
            }

            if (authenticator instanceof PasswordAuthenticator) {
                PasswordAuthenticator revokedAuthenticator = PasswordAuthenticator.from((PasswordAuthenticator) authenticator)
                        .status(AuthenticatorStatus.REVOKED)
                        .updatedAt(Instant.now())
                        .build();
                storage.storeAuthenticator(identifier, revokedAuthenticator);
            }

            LOGGER.info("Revoked {} authenticator for identifier: {}", type, identifier);
        } catch (StorageException e) {
            throw new AuthenticatorException("Failed to revoke authenticator", e);
        }
    }

    @Override
    public void expireAuthenticator(String identifier, AuthenticatorType type) throws AuthenticatorException {
        if (identifier == null || identifier.trim().isEmpty()) {
            throw new AuthenticatorException("Identifier cannot be null or empty");
        }
        if (type == null) {
            throw new AuthenticatorException("Authenticator type cannot be null");
        }

        try {
            Authenticator authenticator = storage.retrieveAuthenticator(identifier, type);
            if (authenticator == null) {
                throw new AuthenticatorException("Authenticator not found");
            }

            if (authenticator instanceof PasswordAuthenticator) {
                PasswordAuthenticator expiredAuthenticator = PasswordAuthenticator.from((PasswordAuthenticator) authenticator)
                        .status(AuthenticatorStatus.EXPIRED)
                        .expiresAt(Instant.now())
                        .updatedAt(Instant.now())
                        .build();
                storage.storeAuthenticator(identifier, expiredAuthenticator);
            }

            LOGGER.info("Expired {} authenticator for identifier: {}", type, identifier);
        } catch (StorageException e) {
            throw new AuthenticatorException("Failed to expire authenticator", e);
        }
    }

    @Override
    public Authenticator renewAuthenticator(String identifier, AuthenticatorType type, Credential credential) 
            throws AuthenticatorException {
        if (identifier == null || identifier.trim().isEmpty()) {
            throw new AuthenticatorException("Identifier cannot be null or empty");
        }
        if (type == null) {
            throw new AuthenticatorException("Authenticator type cannot be null");
        }
        if (credential == null) {
            throw new AuthenticatorException("Credential cannot be null");
        }

        try {
            Authenticator authenticator = storage.retrieveAuthenticator(identifier, type);
            if (authenticator == null) {
                throw new AuthenticatorException("Authenticator not found");
            }

            if (authenticator.getStatus() != AuthenticatorStatus.EXPIRED) {
                throw new AuthenticatorException("Only expired authenticators can be renewed");
            }

            if (credential instanceof PasswordCredential) {
                return updatePasswordAuthenticator(identifier, (PasswordCredential) credential);
            } else {
                throw new AuthenticatorException("Unsupported credential type");
            }
        } catch (StorageException e) {
            throw new AuthenticatorException("Failed to renew authenticator", e);
        }
    }

    @Override
    public List<Authenticator> listUserAuthenticators(String identifier) throws AuthenticatorException {
        if (identifier == null || identifier.trim().isEmpty()) {
            throw new AuthenticatorException("Identifier cannot be null or empty");
        }

        try {
            return storage.retrieveAllAuthenticators(identifier);
        } catch (StorageException e) {
            throw new AuthenticatorException("Failed to list authenticators", e);
        }
    }

    @Override
    public Authenticator getAuthenticator(String identifier, AuthenticatorType type) throws AuthenticatorException {
        if (identifier == null || identifier.trim().isEmpty()) {
            throw new AuthenticatorException("Identifier cannot be null or empty");
        }
        if (type == null) {
            throw new AuthenticatorException("Authenticator type cannot be null");
        }

        try {
            return storage.retrieveAuthenticator(identifier, type);
        } catch (StorageException e) {
            throw new AuthenticatorException("Failed to get authenticator", e);
        }
    }

    @Override
    public boolean isAuthenticatorExpired(String identifier, AuthenticatorType type) throws AuthenticatorException {
        Authenticator authenticator = getAuthenticator(identifier, type);
        if (authenticator == null) {
            throw new AuthenticatorException("Authenticator not found");
        }
        return authenticator.isExpired();
    }

    /**
     * Gets the password policy used by this manager.
     * 
     * @return the password policy
     */
    public PasswordPolicy getPasswordPolicy() {
        return passwordPolicy;
    }

    /**
     * Gets the storage used by this manager.
     * 
     * @return the authentication storage
     */
    public InMemoryAuthenticationStorage getStorage() {
        return storage;
    }
}
