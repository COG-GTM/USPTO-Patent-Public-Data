package gov.uspto.session.management;

import gov.uspto.session.model.Session;
import gov.uspto.session.model.SessionState;

/**
 * Validates session state and integrity.
 * Checks session validity, expiration, and security constraints.
 */
public class SessionValidator {
    
    private final long maxSessionAgeSeconds;
    private final long maxInactivitySeconds;
    
    public SessionValidator(long maxSessionAgeSeconds, long maxInactivitySeconds) {
        this.maxSessionAgeSeconds = maxSessionAgeSeconds;
        this.maxInactivitySeconds = maxInactivitySeconds;
    }
    
    /**
     * Validate if session is still valid
     * @param session the session to validate
     * @return true if session is valid
     */
    public boolean isValid(Session session) {
        if (session == null) {
            return false;
        }
        
        if (session.getState() == SessionState.TERMINATED || 
            session.getState() == SessionState.EXPIRED) {
            return false;
        }
        
        if (isExpired(session)) {
            return false;
        }
        
        if (isInactive(session)) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Check if session has exceeded maximum age
     * @param session the session to check
     * @return true if session is expired
     */
    public boolean isExpired(Session session) {
        long sessionAge = session.getSessionDurationSeconds();
        return Math.abs(sessionAge) > maxSessionAgeSeconds;
    }
    
    /**
     * Check if session has been inactive too long
     * @param session the session to check
     * @return true if session is inactive
     */
    public boolean isInactive(Session session) {
        long inactivityTime = session.getTimeSinceLastAccessSeconds();
        return inactivityTime > maxInactivitySeconds;
    }
    
    /**
     * Validate session security context
     * @param session the session
     * @param currentIpAddress current request IP
     * @param currentUserAgent current request user agent
     * @return true if security context matches
     */
    public boolean validateSecurityContext(Session session, String currentIpAddress, String currentUserAgent) {
        if (session.getIpAddress() != null && !session.getIpAddress().equals(currentIpAddress)) {
            return false;
        }
        
        if (session.getUserAgent() != null && !session.getUserAgent().equals(currentUserAgent)) {
            return false;
        }
        
        return true;
    }
}
