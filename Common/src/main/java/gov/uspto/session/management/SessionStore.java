package gov.uspto.session.management;

import gov.uspto.session.model.Session;

import java.util.Optional;

/**
 * Interface for session persistence.
 * Implementations can support Redis, database, in-memory, or other storage backends.
 * Designed for future integration with Part 1.1 infrastructure.
 */
public interface SessionStore {
    
    /**
     * Store a session
     * @param session the session to store
     */
    void save(Session session);
    
    /**
     * Retrieve a session by ID
     * @param sessionId the session ID
     * @return Optional containing the session if found
     */
    Optional<Session> findById(String sessionId);
    
    /**
     * Retrieve all sessions for a user
     * @param userId the user ID
     * @return array of sessions for the user
     */
    Session[] findByUserId(String userId);
    
    /**
     * Delete a session
     * @param sessionId the session ID to delete
     */
    void delete(String sessionId);
    
    /**
     * Delete all sessions for a user
     * @param userId the user ID
     */
    void deleteByUserId(String userId);
    
    /**
     * Check if a session exists
     * @param sessionId the session ID
     * @return true if session exists
     */
    boolean exists(String sessionId);
    
    /**
     * Get count of active sessions for a user
     * @param userId the user ID
     * @return number of active sessions
     */
    int countActiveSessionsForUser(String userId);
}
