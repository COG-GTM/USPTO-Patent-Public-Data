package gov.uspto.session.management;

import gov.uspto.session.model.Session;
import gov.uspto.session.security.SessionIdGenerator;

/**
 * Factory for creating new Session instances.
 * Handles session ID generation and initial session setup.
 */
public class SessionFactory {
    
    private final SessionIdGenerator idGenerator;
    
    public SessionFactory(SessionIdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }
    
    /**
     * Create a new session for a user
     * @param userId the user ID
     * @return new Session instance
     */
    public Session createSession(String userId) {
        String sessionId = idGenerator.generateSessionId();
        Session session = new Session(sessionId, userId);
        return session;
    }
    
    /**
     * Create a new session with security context
     * @param userId the user ID
     * @param ipAddress client IP address
     * @param userAgent client user agent
     * @return new Session instance with security context
     */
    public Session createSession(String userId, String ipAddress, String userAgent) {
        Session session = createSession(userId);
        session.setIpAddress(ipAddress);
        session.setUserAgent(userAgent);
        return session;
    }
}
