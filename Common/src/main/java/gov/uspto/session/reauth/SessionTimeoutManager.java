package gov.uspto.session.reauth;

import gov.uspto.session.model.ReauthReason;
import gov.uspto.session.model.Session;
import gov.uspto.session.model.SessionState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages timeout-based re-authentication and session expiration.
 * Enforces NIST 800-53 IA-11 time-based re-authentication requirements.
 */
public class SessionTimeoutManager {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SessionTimeoutManager.class);
    
    private final long sessionTimeoutSeconds;
    private final long inactivityTimeoutSeconds;
    private final long reauthTimeoutSeconds;
    
    public SessionTimeoutManager(long sessionTimeoutSeconds, 
                                long inactivityTimeoutSeconds,
                                long reauthTimeoutSeconds) {
        this.sessionTimeoutSeconds = sessionTimeoutSeconds;
        this.inactivityTimeoutSeconds = inactivityTimeoutSeconds;
        this.reauthTimeoutSeconds = reauthTimeoutSeconds;
    }
    
    /**
     * Check if session has exceeded maximum lifetime
     * @param session the session to check
     * @return true if session has timed out
     */
    public boolean isSessionTimedOut(Session session) {
        long sessionAge = Math.abs(session.getSessionDurationSeconds());
        return sessionAge > sessionTimeoutSeconds;
    }
    
    /**
     * Check if session has been inactive too long
     * @param session the session to check
     * @return true if session is inactive
     */
    public boolean isSessionInactive(Session session) {
        long inactivityTime = session.getTimeSinceLastAccessSeconds();
        return inactivityTime > inactivityTimeoutSeconds;
    }
    
    /**
     * Check if session requires re-authentication due to timeout
     * @param session the session to check
     * @return true if re-authentication timeout exceeded
     */
    public boolean requiresReauthDueToTimeout(Session session) {
        long timeSinceReauth = session.getTimeSinceLastReauthSeconds();
        return timeSinceReauth > reauthTimeoutSeconds;
    }
    
    /**
     * Process session timeouts and update state
     * @param session the session to process
     * @return true if session state was changed
     */
    public boolean processTimeouts(Session session) {
        boolean stateChanged = false;
        
        if (isSessionTimedOut(session)) {
            LOGGER.info("Session {} has exceeded maximum lifetime", session.getSessionId());
            session.setState(SessionState.EXPIRED);
            stateChanged = true;
        } else if (isSessionInactive(session)) {
            LOGGER.info("Session {} has been inactive too long", session.getSessionId());
            session.setState(SessionState.EXPIRED);
            stateChanged = true;
        } else if (requiresReauthDueToTimeout(session)) {
            LOGGER.info("Session {} requires re-authentication due to timeout", session.getSessionId());
            session.addReauthReason(ReauthReason.SESSION_TIMEOUT);
            stateChanged = true;
        }
        
        return stateChanged;
    }
    
    /**
     * Get remaining time before session timeout
     * @param session the session
     * @return seconds remaining before timeout
     */
    public long getRemainingSessionTime(Session session) {
        long sessionAge = Math.abs(session.getSessionDurationSeconds());
        return Math.max(0, sessionTimeoutSeconds - sessionAge);
    }
    
    /**
     * Get remaining time before inactivity timeout
     * @param session the session
     * @return seconds remaining before inactivity timeout
     */
    public long getRemainingInactivityTime(Session session) {
        long inactivityTime = session.getTimeSinceLastAccessSeconds();
        return Math.max(0, inactivityTimeoutSeconds - inactivityTime);
    }
    
    /**
     * Get remaining time before re-authentication required
     * @param session the session
     * @return seconds remaining before re-authentication required
     */
    public long getRemainingReauthTime(Session session) {
        long timeSinceReauth = session.getTimeSinceLastReauthSeconds();
        return Math.max(0, reauthTimeoutSeconds - timeSinceReauth);
    }
}
