package gov.uspto.session.security;

import gov.uspto.session.model.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements anti-hijacking measures for session security.
 * Provides session binding, fixation protection, and anomaly detection.
 */
public class SessionHijackingPrevention {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SessionHijackingPrevention.class);
    
    private final boolean enforceIpBinding;
    private final boolean enforceUserAgentBinding;
    private final int maxConcurrentSessions;
    
    public SessionHijackingPrevention(boolean enforceIpBinding, 
                                     boolean enforceUserAgentBinding,
                                     int maxConcurrentSessions) {
        this.enforceIpBinding = enforceIpBinding;
        this.enforceUserAgentBinding = enforceUserAgentBinding;
        this.maxConcurrentSessions = maxConcurrentSessions;
    }
    
    /**
     * Validate session binding to prevent hijacking
     * @param session the session
     * @param currentIpAddress current request IP
     * @param currentUserAgent current request user agent
     * @return true if session binding is valid
     */
    public boolean validateSessionBinding(Session session, String currentIpAddress, String currentUserAgent) {
        if (enforceIpBinding && session.getIpAddress() != null) {
            if (!session.getIpAddress().equals(currentIpAddress)) {
                LOGGER.warn("Session {} IP mismatch: expected {}, got {}", 
                           session.getSessionId(), session.getIpAddress(), currentIpAddress);
                return false;
            }
        }
        
        if (enforceUserAgentBinding && session.getUserAgent() != null) {
            if (!session.getUserAgent().equals(currentUserAgent)) {
                LOGGER.warn("Session {} User-Agent mismatch: expected {}, got {}", 
                           session.getSessionId(), session.getUserAgent(), currentUserAgent);
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Detect session fixation attack
     * @param session the session
     * @return true if fixation attack detected
     */
    public boolean detectSessionFixation(Session session) {
        if (session.getAccessCount() == 0 && session.getTimeSinceLastAccessSeconds() > 300) {
            LOGGER.warn("Potential session fixation detected for session {}", session.getSessionId());
            return true;
        }
        return false;
    }
    
    /**
     * Detect suspicious session activity
     * @param session the session
     * @param currentIpAddress current request IP
     * @return true if suspicious activity detected
     */
    public boolean detectSuspiciousActivity(Session session, String currentIpAddress) {
        if (session.getIpAddress() != null && !session.getIpAddress().equals(currentIpAddress)) {
            String previousIp = session.getIpAddress();
            if (!isSameSubnet(previousIp, currentIpAddress)) {
                LOGGER.warn("Suspicious activity: Session {} accessed from different subnet: {} -> {}", 
                           session.getSessionId(), previousIp, currentIpAddress);
                return true;
            }
        }
        
        if (session.getAccessCount() > 1000) {
            LOGGER.warn("Suspicious activity: Session {} has excessive access count: {}", 
                       session.getSessionId(), session.getAccessCount());
            return true;
        }
        
        return false;
    }
    
    /**
     * Check if concurrent session limit is exceeded
     * @param activeSessionCount number of active sessions for user
     * @return true if limit exceeded
     */
    public boolean isConcurrentSessionLimitExceeded(int activeSessionCount) {
        return activeSessionCount >= maxConcurrentSessions;
    }
    
    /**
     * Regenerate session ID to prevent fixation
     * @param oldSessionId the old session ID
     * @param generator session ID generator
     * @return new session ID
     */
    public String regenerateSessionId(String oldSessionId, SessionIdGenerator generator) {
        String newSessionId = generator.generateSessionId();
        LOGGER.info("Regenerated session ID: {} -> {}", oldSessionId, newSessionId);
        return newSessionId;
    }
    
    /**
     * Check if two IPs are in the same subnet (simple /24 check)
     * @param ip1 first IP address
     * @param ip2 second IP address
     * @return true if same subnet
     */
    private boolean isSameSubnet(String ip1, String ip2) {
        if (ip1 == null || ip2 == null) {
            return false;
        }
        
        String[] parts1 = ip1.split("\\.");
        String[] parts2 = ip2.split("\\.");
        
        if (parts1.length != 4 || parts2.length != 4) {
            return false;
        }
        
        return parts1[0].equals(parts2[0]) && 
               parts1[1].equals(parts2[1]) && 
               parts1[2].equals(parts2[2]);
    }
}
