package gov.uspto.auth.storage;

import gov.uspto.auth.authenticator.Authenticator;
import gov.uspto.auth.authenticator.AuthenticatorType;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory implementation of authentication storage.
 * 
 * Stores authenticators, password history, failed attempts, and lockout information
 * in memory using thread-safe data structures.
 * 
 * NIST 800-53 Controls: IA-5 (Authenticator Management)
 */
public class InMemoryAuthenticationStorage implements AuthenticationStorage {

    private final Map<String, Map<AuthenticatorType, Authenticator>> authenticators;
    private final Map<String, List<String>> passwordHistory;
    private final Map<String, Integer> failedAttempts;
    private final Map<String, Instant> lockoutUntil;
    private final Map<String, List<Instant>> attemptTimestamps;

    /**
     * Creates a new in-memory authentication storage.
     */
    public InMemoryAuthenticationStorage() {
        this.authenticators = new ConcurrentHashMap<>();
        this.passwordHistory = new ConcurrentHashMap<>();
        this.failedAttempts = new ConcurrentHashMap<>();
        this.lockoutUntil = new ConcurrentHashMap<>();
        this.attemptTimestamps = new ConcurrentHashMap<>();
    }

    @Override
    public void store(String key, Map<String, Object> data) throws StorageException {
        if (key == null || key.trim().isEmpty()) {
            throw new StorageException("Key cannot be null or empty");
        }
        if (data == null) {
            throw new StorageException("Data cannot be null");
        }
    }

    @Override
    public Map<String, Object> retrieve(String key) throws StorageException {
        if (key == null || key.trim().isEmpty()) {
            throw new StorageException("Key cannot be null or empty");
        }
        return null;
    }

    @Override
    public void delete(String key) throws StorageException {
        if (key == null || key.trim().isEmpty()) {
            throw new StorageException("Key cannot be null or empty");
        }
    }

    @Override
    public boolean exists(String key) {
        return false;
    }

    /**
     * Stores an authenticator for the identifier.
     * 
     * @param identifier the user or service identifier
     * @param authenticator the authenticator to store
     * @throws StorageException if storage fails
     */
    public void storeAuthenticator(String identifier, Authenticator authenticator) throws StorageException {
        if (identifier == null || identifier.trim().isEmpty()) {
            throw new StorageException("Identifier cannot be null or empty");
        }
        if (authenticator == null) {
            throw new StorageException("Authenticator cannot be null");
        }

        authenticators.computeIfAbsent(identifier, k -> new ConcurrentHashMap<>())
                .put(authenticator.getType(), authenticator);
    }

    /**
     * Retrieves an authenticator for the identifier and type.
     * 
     * @param identifier the user or service identifier
     * @param type the authenticator type
     * @return the authenticator, or null if not found
     * @throws StorageException if retrieval fails
     */
    public Authenticator retrieveAuthenticator(String identifier, AuthenticatorType type) throws StorageException {
        if (identifier == null || identifier.trim().isEmpty()) {
            throw new StorageException("Identifier cannot be null or empty");
        }
        if (type == null) {
            throw new StorageException("Authenticator type cannot be null");
        }

        Map<AuthenticatorType, Authenticator> userAuthenticators = authenticators.get(identifier);
        if (userAuthenticators == null) {
            return null;
        }
        return userAuthenticators.get(type);
    }

    /**
     * Retrieves all authenticators for the identifier.
     * 
     * @param identifier the user or service identifier
     * @return list of authenticators
     * @throws StorageException if retrieval fails
     */
    public List<Authenticator> retrieveAllAuthenticators(String identifier) throws StorageException {
        if (identifier == null || identifier.trim().isEmpty()) {
            throw new StorageException("Identifier cannot be null or empty");
        }

        Map<AuthenticatorType, Authenticator> userAuthenticators = authenticators.get(identifier);
        if (userAuthenticators == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(userAuthenticators.values());
    }

    /**
     * Deletes an authenticator for the identifier and type.
     * 
     * @param identifier the user or service identifier
     * @param type the authenticator type
     * @throws StorageException if deletion fails
     */
    public void deleteAuthenticator(String identifier, AuthenticatorType type) throws StorageException {
        if (identifier == null || identifier.trim().isEmpty()) {
            throw new StorageException("Identifier cannot be null or empty");
        }
        if (type == null) {
            throw new StorageException("Authenticator type cannot be null");
        }

        Map<AuthenticatorType, Authenticator> userAuthenticators = authenticators.get(identifier);
        if (userAuthenticators != null) {
            userAuthenticators.remove(type);
            if (userAuthenticators.isEmpty()) {
                authenticators.remove(identifier);
            }
        }
    }

    /**
     * Checks if an authenticator exists for the identifier and type.
     * 
     * @param identifier the user or service identifier
     * @param type the authenticator type
     * @return true if exists, false otherwise
     * @throws StorageException if check fails
     */
    public boolean authenticatorExists(String identifier, AuthenticatorType type) throws StorageException {
        if (identifier == null || identifier.trim().isEmpty()) {
            throw new StorageException("Identifier cannot be null or empty");
        }
        if (type == null) {
            throw new StorageException("Authenticator type cannot be null");
        }

        Map<AuthenticatorType, Authenticator> userAuthenticators = authenticators.get(identifier);
        return userAuthenticators != null && userAuthenticators.containsKey(type);
    }

    /**
     * Stores a password hash in the password history for the identifier.
     * 
     * @param identifier the user or service identifier
     * @param passwordHash the password hash to store
     */
    public void storePasswordHistory(String identifier, String passwordHash) {
        passwordHistory.computeIfAbsent(identifier, k -> new ArrayList<>()).add(passwordHash);
    }

    /**
     * Gets the password history for the identifier.
     * 
     * @param identifier the user or service identifier
     * @return list of password hashes in history
     */
    public List<String> getPasswordHistory(String identifier) {
        return new ArrayList<>(passwordHistory.getOrDefault(identifier, new ArrayList<>()));
    }

    /**
     * Clears old password history entries, keeping only the most recent N entries.
     * 
     * @param identifier the user or service identifier
     * @param keepCount the number of recent entries to keep
     */
    public void trimPasswordHistory(String identifier, int keepCount) {
        List<String> history = passwordHistory.get(identifier);
        if (history != null && history.size() > keepCount) {
            List<String> trimmed = history.subList(Math.max(0, history.size() - keepCount), history.size());
            passwordHistory.put(identifier, new ArrayList<>(trimmed));
        }
    }

    /**
     * Records a failed authentication attempt for the identifier.
     * 
     * @param identifier the user or service identifier
     * @return the new failed attempt count
     */
    public int recordFailedAttempt(String identifier) {
        int count = failedAttempts.compute(identifier, (k, v) -> v == null ? 1 : v + 1);
        attemptTimestamps.computeIfAbsent(identifier, k -> new ArrayList<>()).add(Instant.now());
        return count;
    }

    /**
     * Gets the failed attempt count for the identifier.
     * 
     * @param identifier the user or service identifier
     * @return the failed attempt count
     */
    public int getFailedAttempts(String identifier) {
        return failedAttempts.getOrDefault(identifier, 0);
    }

    /**
     * Resets the failed attempt count for the identifier.
     * 
     * @param identifier the user or service identifier
     */
    public void resetFailedAttempts(String identifier) {
        failedAttempts.remove(identifier);
        attemptTimestamps.remove(identifier);
    }

    /**
     * Gets the failed attempt timestamps within a time window.
     * 
     * @param identifier the user or service identifier
     * @param windowStart the start of the time window
     * @return list of attempt timestamps within the window
     */
    public List<Instant> getFailedAttemptsInWindow(String identifier, Instant windowStart) {
        List<Instant> timestamps = attemptTimestamps.get(identifier);
        if (timestamps == null) {
            return new ArrayList<>();
        }
        return timestamps.stream()
                .filter(ts -> ts.isAfter(windowStart))
                .collect(Collectors.toList());
    }

    /**
     * Sets the lockout timestamp for the identifier.
     * 
     * @param identifier the user or service identifier
     * @param until the timestamp until which the account is locked
     */
    public void setLockoutUntil(String identifier, Instant until) {
        if (until == null) {
            lockoutUntil.remove(identifier);
        } else {
            lockoutUntil.put(identifier, until);
        }
    }

    /**
     * Gets the lockout timestamp for the identifier.
     * 
     * @param identifier the user or service identifier
     * @return the lockout timestamp, or null if not locked
     */
    public Instant getLockoutUntil(String identifier) {
        return lockoutUntil.get(identifier);
    }

    /**
     * Checks if the identifier is currently locked out.
     * 
     * @param identifier the user or service identifier
     * @return true if locked out, false otherwise
     */
    public boolean isLockedOut(String identifier) {
        Instant until = lockoutUntil.get(identifier);
        if (until == null) {
            return false;
        }
        if (Instant.now().isAfter(until)) {
            lockoutUntil.remove(identifier);
            return false;
        }
        return true;
    }

    /**
     * Clears all data for the identifier.
     * 
     * @param identifier the user or service identifier
     */
    public void clearAll(String identifier) {
        authenticators.remove(identifier);
        passwordHistory.remove(identifier);
        failedAttempts.remove(identifier);
        lockoutUntil.remove(identifier);
        attemptTimestamps.remove(identifier);
    }

    /**
     * Gets the total number of stored authenticators across all identifiers.
     * 
     * @return the total authenticator count
     */
    public int getTotalAuthenticatorCount() {
        return authenticators.values().stream()
                .mapToInt(Map::size)
                .sum();
    }
}
