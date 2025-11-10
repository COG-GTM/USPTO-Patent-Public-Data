package gov.uspto.session.lifecycle;

import gov.uspto.session.management.SessionStore;
import gov.uspto.session.model.Session;
import gov.uspto.session.model.SessionState;
import gov.uspto.session.security.SessionIdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Service for renewing and refreshing sessions.
 * Handles session extension and ID regeneration for security.
 */
public class SessionRenewalService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SessionRenewalService.class);
    
    private final SessionStore sessionStore;
    private final SessionIdGenerator idGenerator;
    
    public SessionRenewalService(SessionStore sessionStore, SessionIdGenerator idGenerator) {
        this.sessionStore = sessionStore;
        this.idGenerator = idGenerator;
    }
    
    /**
     * Renew a session by updating its last accessed time
     * @param sessionId the session ID
     * @return true if session was renewed
     */
    public boolean renewSession(String sessionId) {
        Optional<Session> sessionOpt = sessionStore.findById(sessionId);
        
        if (!sessionOpt.isPresent()) {
            LOGGER.warn("Cannot renew session {}: not found", sessionId);
            return false;
        }
        
        Session session = sessionOpt.get();
        
        if (session.getState() != SessionState.ACTIVE && 
            session.getState() != SessionState.REQUIRES_REAUTH) {
            LOGGER.warn("Cannot renew session {}: invalid state {}", sessionId, session.getState());
            return false;
        }
        
        session.updateLastAccessed();
        sessionStore.save(session);
        
        LOGGER.debug("Renewed session {}", sessionId);
        return true;
    }
    
    /**
     * Regenerate session ID for security (prevents fixation attacks)
     * @param oldSessionId the old session ID
     * @return the new session ID, or null if regeneration failed
     */
    public String regenerateSessionId(String oldSessionId) {
        Optional<Session> sessionOpt = sessionStore.findById(oldSessionId);
        
        if (!sessionOpt.isPresent()) {
            LOGGER.warn("Cannot regenerate session ID for {}: not found", oldSessionId);
            return null;
        }
        
        Session oldSession = sessionOpt.get();
        String newSessionId = idGenerator.generateSessionId();
        
        Session newSession = new Session(newSessionId, oldSession.getUserId());
        newSession.setState(oldSession.getState());
        newSession.setIpAddress(oldSession.getIpAddress());
        newSession.setUserAgent(oldSession.getUserAgent());
        
        for (String key : oldSession.getAttributes().keySet()) {
            newSession.setAttribute(key, oldSession.getAttribute(key));
        }
        
        for (String key : oldSession.getSecurityAttributes().keySet()) {
            newSession.setSecurityAttribute(key, oldSession.getSecurityAttribute(key));
        }
        
        sessionStore.save(newSession);
        sessionStore.delete(oldSessionId);
        
        LOGGER.info("Regenerated session ID: {} -> {}", oldSessionId, newSessionId);
        return newSessionId;
    }
    
    /**
     * Refresh session after re-authentication
     * @param sessionId the session ID
     * @return true if session was refreshed
     */
    public boolean refreshAfterReauth(String sessionId) {
        Optional<Session> sessionOpt = sessionStore.findById(sessionId);
        
        if (!sessionOpt.isPresent()) {
            LOGGER.warn("Cannot refresh session {}: not found", sessionId);
            return false;
        }
        
        Session session = sessionOpt.get();
        session.markReauthenticated();
        session.updateLastAccessed();
        sessionStore.save(session);
        
        LOGGER.info("Refreshed session {} after re-authentication", sessionId);
        return true;
    }
    
    /**
     * Extend session lifetime
     * @param sessionId the session ID
     * @return true if session was extended
     */
    public boolean extendSession(String sessionId) {
        Optional<Session> sessionOpt = sessionStore.findById(sessionId);
        
        if (!sessionOpt.isPresent()) {
            LOGGER.warn("Cannot extend session {}: not found", sessionId);
            return false;
        }
        
        Session session = sessionOpt.get();
        
        if (session.getState() == SessionState.EXPIRED || 
            session.getState() == SessionState.TERMINATED) {
            LOGGER.warn("Cannot extend session {}: invalid state {}", sessionId, session.getState());
            return false;
        }
        
        session.updateLastAccessed();
        sessionStore.save(session);
        
        LOGGER.debug("Extended session {}", sessionId);
        return true;
    }
}
