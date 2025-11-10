package gov.uspto.session.lifecycle;

import gov.uspto.session.management.SessionFactory;
import gov.uspto.session.management.SessionStore;
import gov.uspto.session.model.Session;
import gov.uspto.session.security.SessionHijackingPrevention;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service for creating new sessions with security controls.
 * Enforces concurrent session limits and security policies.
 */
public class SessionCreationService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SessionCreationService.class);
    
    private final SessionFactory sessionFactory;
    private final SessionStore sessionStore;
    private final SessionHijackingPrevention hijackingPrevention;
    
    public SessionCreationService(SessionFactory sessionFactory,
                                 SessionStore sessionStore,
                                 SessionHijackingPrevention hijackingPrevention) {
        this.sessionFactory = sessionFactory;
        this.sessionStore = sessionStore;
        this.hijackingPrevention = hijackingPrevention;
    }
    
    /**
     * Create a new session for a user
     * @param userId the user ID
     * @return the created session
     * @throws SessionCreationException if session cannot be created
     */
    public Session createSession(String userId) throws SessionCreationException {
        validateConcurrentSessionLimit(userId);
        
        Session session = sessionFactory.createSession(userId);
        sessionStore.save(session);
        
        LOGGER.info("Created session {} for user {}", session.getSessionId(), userId);
        return session;
    }
    
    /**
     * Create a new session with security context
     * @param userId the user ID
     * @param ipAddress client IP address
     * @param userAgent client user agent
     * @return the created session
     * @throws SessionCreationException if session cannot be created
     */
    public Session createSession(String userId, String ipAddress, String userAgent) 
            throws SessionCreationException {
        validateConcurrentSessionLimit(userId);
        
        Session session = sessionFactory.createSession(userId, ipAddress, userAgent);
        sessionStore.save(session);
        
        LOGGER.info("Created session {} for user {} from IP {}", 
                   session.getSessionId(), userId, ipAddress);
        return session;
    }
    
    /**
     * Validate concurrent session limit
     * @param userId the user ID
     * @throws SessionCreationException if limit exceeded
     */
    private void validateConcurrentSessionLimit(String userId) throws SessionCreationException {
        int activeSessionCount = sessionStore.countActiveSessionsForUser(userId);
        
        if (hijackingPrevention.isConcurrentSessionLimitExceeded(activeSessionCount)) {
            LOGGER.warn("Concurrent session limit exceeded for user {}: {} active sessions", 
                       userId, activeSessionCount);
            throw new SessionCreationException(
                "Concurrent session limit exceeded for user: " + userId);
        }
    }
    
    /**
     * Exception thrown when session creation fails
     */
    public static class SessionCreationException extends Exception {
        public SessionCreationException(String message) {
            super(message);
        }
        
        public SessionCreationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
