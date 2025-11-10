package gov.uspto.session.management;

import gov.uspto.session.model.ReauthReason;
import gov.uspto.session.model.Session;
import gov.uspto.session.model.SessionState;
import gov.uspto.session.reauth.ReauthenticationPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Main session orchestrator.
 * Coordinates session lifecycle, validation, and re-authentication.
 * NIST 800-53 IA-11 compliant session management.
 */
public class SessionManager {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SessionManager.class);
    
    private final SessionStore sessionStore;
    private final SessionFactory sessionFactory;
    private final SessionValidator sessionValidator;
    private final ReauthenticationPolicy reauthPolicy;
    
    public SessionManager(SessionStore sessionStore, 
                         SessionFactory sessionFactory,
                         SessionValidator sessionValidator,
                         ReauthenticationPolicy reauthPolicy) {
        this.sessionStore = sessionStore;
        this.sessionFactory = sessionFactory;
        this.sessionValidator = sessionValidator;
        this.reauthPolicy = reauthPolicy;
    }
    
    /**
     * Create a new session for a user
     * @param userId the user ID (placeholder for Part 1.2 integration)
     * @return the created session
     */
    public Session createSession(String userId) {
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
     */
    public Session createSession(String userId, String ipAddress, String userAgent) {
        Session session = sessionFactory.createSession(userId, ipAddress, userAgent);
        sessionStore.save(session);
        LOGGER.info("Created session {} for user {} from IP {}", 
                   session.getSessionId(), userId, ipAddress);
        return session;
    }
    
    /**
     * Retrieve a session by ID
     * @param sessionId the session ID
     * @return Optional containing the session if found and valid
     */
    public Optional<Session> getSession(String sessionId) {
        Optional<Session> sessionOpt = sessionStore.findById(sessionId);
        
        if (!sessionOpt.isPresent()) {
            LOGGER.debug("Session {} not found", sessionId);
            return Optional.empty();
        }
        
        Session session = sessionOpt.get();
        
        if (!sessionValidator.isValid(session)) {
            LOGGER.info("Session {} is invalid or expired", sessionId);
            session.setState(SessionState.EXPIRED);
            sessionStore.save(session);
            return Optional.empty();
        }
        
        return Optional.of(session);
    }
    
    /**
     * Validate a session
     * @param sessionId the session ID
     * @return true if session is valid
     */
    public boolean validateSession(String sessionId) {
        Optional<Session> sessionOpt = getSession(sessionId);
        return sessionOpt.isPresent();
    }
    
    /**
     * Update session access time
     * @param sessionId the session ID
     */
    public void touchSession(String sessionId) {
        Optional<Session> sessionOpt = sessionStore.findById(sessionId);
        if (sessionOpt.isPresent()) {
            Session session = sessionOpt.get();
            session.updateLastAccessed();
            sessionStore.save(session);
        }
    }
    
    /**
     * Trigger re-authentication for a session
     * @param sessionId the session ID
     * @param reason the re-authentication reason
     */
    public void triggerReauthentication(String sessionId, ReauthReason reason) {
        Optional<Session> sessionOpt = sessionStore.findById(sessionId);
        if (sessionOpt.isPresent()) {
            Session session = sessionOpt.get();
            session.addReauthReason(reason);
            sessionStore.save(session);
            LOGGER.info("Triggered re-authentication for session {} due to {}", 
                       sessionId, reason);
        }
    }
    
    /**
     * Check if session requires re-authentication
     * @param session the session
     * @return true if re-authentication is required
     */
    public boolean isReauthenticationRequired(Session session) {
        if (session.requiresReauthentication()) {
            return true;
        }
        
        return reauthPolicy.requiresReauthentication(session);
    }
    
    /**
     * Mark session as re-authenticated
     * @param sessionId the session ID
     */
    public void markReauthenticated(String sessionId) {
        Optional<Session> sessionOpt = sessionStore.findById(sessionId);
        if (sessionOpt.isPresent()) {
            Session session = sessionOpt.get();
            session.markReauthenticated();
            sessionStore.save(session);
            LOGGER.info("Session {} re-authenticated", sessionId);
        }
    }
    
    /**
     * Terminate a session
     * @param sessionId the session ID
     */
    public void terminateSession(String sessionId) {
        Optional<Session> sessionOpt = sessionStore.findById(sessionId);
        if (sessionOpt.isPresent()) {
            Session session = sessionOpt.get();
            session.setState(SessionState.TERMINATED);
            sessionStore.save(session);
            LOGGER.info("Terminated session {}", sessionId);
        }
    }
    
    /**
     * Terminate all sessions for a user
     * @param userId the user ID
     */
    public void terminateAllUserSessions(String userId) {
        Session[] sessions = sessionStore.findByUserId(userId);
        for (Session session : sessions) {
            session.setState(SessionState.TERMINATED);
            sessionStore.save(session);
        }
        LOGGER.info("Terminated all sessions for user {}", userId);
    }
    
    /**
     * Get count of active sessions for a user
     * @param userId the user ID
     * @return number of active sessions
     */
    public int getActiveSessionCount(String userId) {
        return sessionStore.countActiveSessionsForUser(userId);
    }
}
