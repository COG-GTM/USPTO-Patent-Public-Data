package gov.uspto.auth.storage;

import java.util.Map;

/**
 * Interface for authentication data storage.
 * 
 * This interface defines operations for storing and retrieving authentication-related
 * data such as credentials, sessions, and audit logs. The implementation can be
 * file-based, database-based, or use other storage mechanisms.
 * 
 * NIST 800-53 Controls:
 * - IA-5 (Authenticator Management)
 * - AU-9 (Protection of Audit Information)
 * 
 * Future implementation should include:
 * - Credential storage
 * - Session storage
 * - Audit log storage
 * - Secure data persistence
 * - Data encryption at rest
 */
public interface AuthenticationStorage {

    /**
     * Stores authentication data.
     * 
     * @param key the storage key
     * @param data the data to store
     * @return true if storage was successful, false otherwise
     */
    boolean store(String key, Map<String, String> data);

    /**
     * Retrieves authentication data.
     * 
     * @param key the storage key
     * @return the stored data, or null if not found
     */
    Map<String, String> retrieve(String key);

    /**
     * Updates existing authentication data.
     * 
     * @param key the storage key
     * @param data the updated data
     * @return true if update was successful, false otherwise
     */
    boolean update(String key, Map<String, String> data);

    /**
     * Deletes authentication data.
     * 
     * @param key the storage key
     * @return true if deletion was successful, false otherwise
     */
    boolean delete(String key);

    /**
     * Checks if data exists for a key.
     * 
     * @param key the storage key
     * @return true if data exists, false otherwise
     */
    boolean exists(String key);

    /**
     * Stores a single value.
     * 
     * @param key the storage key
     * @param value the value to store
     * @return true if storage was successful, false otherwise
     */
    boolean storeValue(String key, String value);

    /**
     * Retrieves a single value.
     * 
     * @param key the storage key
     * @return the stored value, or null if not found
     */
    String retrieveValue(String key);
}
