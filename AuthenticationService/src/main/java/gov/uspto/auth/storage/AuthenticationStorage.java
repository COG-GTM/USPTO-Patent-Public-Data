package gov.uspto.auth.storage;

import java.util.Map;

/**
 * Interface for file-based authentication data storage.
 * 
 * This interface provides methods for storing and retrieving authentication-related
 * data such as user credentials, sessions, and audit logs.
 * 
 * Note: The purpose of this storage package is not fully clear from the requirements.
 * This is a basic interface that can be enhanced based on team clarification.
 * 
 * NIST 800-53 Controls: AU-9 (Protection of Audit Information)
 */
public interface AuthenticationStorage {

    /**
     * Stores authentication data.
     * 
     * @param key the storage key
     * @param data the data to store
     * @throws StorageException if storage fails
     */
    void store(String key, Map<String, Object> data) throws StorageException;

    /**
     * Retrieves authentication data.
     * 
     * @param key the storage key
     * @return the stored data, or null if not found
     * @throws StorageException if retrieval fails
     */
    Map<String, Object> retrieve(String key) throws StorageException;

    /**
     * Deletes authentication data.
     * 
     * @param key the storage key
     * @throws StorageException if deletion fails
     */
    void delete(String key) throws StorageException;

    /**
     * Checks if data exists for a key.
     * 
     * @param key the storage key
     * @return true if data exists, false otherwise
     */
    boolean exists(String key);
}
