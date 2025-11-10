package gov.uspto.session.lifecycle;

import gov.uspto.session.management.SessionStore;
import gov.uspto.session.model.Session;
import gov.uspto.session.model.SessionState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Service for terminating sessions and cleanup.
 * Handles graceful session termination and resource cleanup.
 */
public class SessionTerminationService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SessionTerminationService.class);
    
    private final SessionStore sessionStore;
    
    public SessionTerminationService(SessionStore sessionStore) {
        this.sessionStore = sessionStore;
    }
    
    /**
     * Terminate a session
     * @param sessionId the session ID
     * @return true if session was terminated
     */
    public boolean terminateSession(String sessionId) {
        Optional<Session> sessionOpt = sessionStore.findById(sessionId);
        
        if (!sessionOpt.isPresent()) {
            LOGGER.warn("Cannot terminate session {}: not found", sessionId);
            return false;
        }
        
        Session session = sessionOpt.get();
        session.setState(SessionState.TERMINATED);
        sessionStore.save(session);
        
        LOGGER.info("Terminated session {} for user {}", sessionId, session.getUserId());
        return true;
    }
    
    /**
     * Terminate all sessions for a user
     * @param userId the user ID
     * @return number of sessions terminated
     */
    public int terminateAllUserSessions(String userId) {
        Session[] sessions = sessionStore.findByUserId(userId);
        int terminatedCount = 0;
        
        for (Session session : sessions) {
            if (session.getState() != SessionState.TERMINATED) {
                session.setState(SessionState.TERMINATED);
                sessionStore.save(session);
                terminatedCount++;
            }
        }
        
        LOGGER.info("Terminated {} sessions for user {}", terminatedCount, userId);
        return terminatedCount;
    }
    
    /**
     * Delete a session from storage
     * @param sessionId the session ID
     */
    public void deleteSession(String sessionId) {
        sessionStore.delete(sessionId);
        LOGGER.info("Deleted session {}", sessionId);
    }
    
    /**
     * Delete all sessions for a user
     * @param userId the user ID
     */
    public void deleteAllUserSessions(String userId) {
        sessionStore.deleteByUserId(userId);
        LOGGER.info("Deleted all sessions for user {}", userId);
    }
    
    /**
     * Expire a session
     * @param sessionId the session ID
     * @return true if session was expired
     */
    public boolean expireSession(String sessionId) {
        Optional<Session> sessionOpt = sessionStore.findById(sessionId);
        
        if (!sessionOpt.isPresent()) {
            LOGGER.warn("Cannot expire session {}: not found", sessionId);
            return false;
        }
        
        Session session = sessionOpt.get();
        session.setState(SessionState.EXPIRED);
        sessionStore.save(session);
        
        LOGGER.info("Expired session {} for user {}", sessionId, session.getUserId());
        return true;
    }
}
