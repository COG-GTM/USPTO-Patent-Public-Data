package gov.uspto.session.lifecycle;

import gov.uspto.session.management.SessionStore;
import gov.uspto.session.model.Session;
import gov.uspto.session.model.SessionState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Manages concurrent sessions for users.
 * Enforces concurrent session limits and handles session conflicts.
 */
public class ConcurrentSessionManager {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ConcurrentSessionManager.class);
    
    private final SessionStore sessionStore;
    private final int maxConcurrentSessions;
    
    public ConcurrentSessionManager(SessionStore sessionStore, int maxConcurrentSessions) {
        this.sessionStore = sessionStore;
        this.maxConcurrentSessions = maxConcurrentSessions;
    }
    
    /**
     * Get all active sessions for a user
     * @param userId the user ID
     * @return array of active sessions
     */
    public Session[] getActiveSessions(String userId) {
        Session[] allSessions = sessionStore.findByUserId(userId);
        List<Session> activeSessions = new ArrayList<>();
        
        for (Session session : allSessions) {
            if (session.getState() == SessionState.ACTIVE || 
                session.getState() == SessionState.REQUIRES_REAUTH) {
                activeSessions.add(session);
            }
        }
        
        return activeSessions.toArray(new Session[0]);
    }
    
    /**
     * Get count of active sessions for a user
     * @param userId the user ID
     * @return number of active sessions
     */
    public int getActiveSessionCount(String userId) {
        return sessionStore.countActiveSessionsForUser(userId);
    }
    
    /**
     * Check if user has reached concurrent session limit
     * @param userId the user ID
     * @return true if limit reached
     */
    public boolean hasReachedLimit(String userId) {
        int activeCount = getActiveSessionCount(userId);
        return activeCount >= maxConcurrentSessions;
    }
    
    /**
     * Terminate oldest session if limit is exceeded
     * @param userId the user ID
     * @return true if a session was terminated
     */
    public boolean terminateOldestIfLimitExceeded(String userId) {
        Session[] activeSessions = getActiveSessions(userId);
        
        if (activeSessions.length >= maxConcurrentSessions) {
            Session oldestSession = findOldestSession(activeSessions);
            if (oldestSession != null) {
                oldestSession.setState(SessionState.TERMINATED);
                sessionStore.save(oldestSession);
                LOGGER.info("Terminated oldest session {} for user {} due to concurrent session limit", 
                           oldestSession.getSessionId(), userId);
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Terminate all sessions except the specified one
     * @param userId the user ID
     * @param keepSessionId the session ID to keep
     * @return number of sessions terminated
     */
    public int terminateAllExcept(String userId, String keepSessionId) {
        Session[] allSessions = sessionStore.findByUserId(userId);
        int terminatedCount = 0;
        
        for (Session session : allSessions) {
            if (!session.getSessionId().equals(keepSessionId) && 
                session.getState() != SessionState.TERMINATED) {
                session.setState(SessionState.TERMINATED);
                sessionStore.save(session);
                terminatedCount++;
            }
        }
        
        LOGGER.info("Terminated {} sessions for user {}, keeping session {}", 
                   terminatedCount, userId, keepSessionId);
        return terminatedCount;
    }
    
    /**
     * Get session information for all active sessions
     * @param userId the user ID
     * @return array of session info strings
     */
    public String[] getSessionInfo(String userId) {
        Session[] activeSessions = getActiveSessions(userId);
        String[] info = new String[activeSessions.length];
        
        for (int i = 0; i < activeSessions.length; i++) {
            Session session = activeSessions[i];
            info[i] = String.format("Session %s: created=%s, lastAccessed=%s, IP=%s", 
                                   session.getSessionId(),
                                   session.getCreatedAt(),
                                   session.getLastAccessed(),
                                   session.getIpAddress());
        }
        
        return info;
    }
    
    /**
     * Find the oldest session by creation time
     * @param sessions array of sessions
     * @return oldest session
     */
    private Session findOldestSession(Session[] sessions) {
        if (sessions == null || sessions.length == 0) {
            return null;
        }
        
        return Arrays.stream(sessions)
                .min(Comparator.comparing(Session::getCreatedAt))
                .orElse(null);
    }
    
    /**
     * Find the least recently accessed session
     * @param sessions array of sessions
     * @return least recently accessed session
     */
    private Session findLeastRecentlyAccessedSession(Session[] sessions) {
        if (sessions == null || sessions.length == 0) {
            return null;
        }
        
        return Arrays.stream(sessions)
                .min(Comparator.comparing(Session::getLastAccessed))
                .orElse(null);
    }
}
