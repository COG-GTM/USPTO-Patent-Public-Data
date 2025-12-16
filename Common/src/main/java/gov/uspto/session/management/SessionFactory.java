package gov.uspto.session.management;

import java.time.Instant;

import gov.uspto.session.model.Session;
import gov.uspto.session.security.SessionIdGenerator;

public class SessionFactory {

    private final SessionIdGenerator idGenerator;
    private String sessionIdPrefix;

    public SessionFactory() {
        this(new SessionIdGenerator());
    }

    public SessionFactory(SessionIdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    public Session createSession(String userId) {
        String sessionId = generateSessionId();
        return new Session(sessionId, userId);
    }

    public Session createSession(String userId, Instant createdAt) {
        String sessionId = generateSessionId();
        return new Session(sessionId, userId, createdAt);
    }

    public Session createSession(String userId, String ipAddress, String userAgent) {
        Session session = createSession(userId);
        session.setIpAddress(ipAddress);
        session.setUserAgent(userAgent);
        return session;
    }

    private String generateSessionId() {
        if (sessionIdPrefix != null && !sessionIdPrefix.isEmpty()) {
            return idGenerator.generateSessionId(sessionIdPrefix);
        }
        return idGenerator.generateSessionId();
    }

    public void setSessionIdPrefix(String prefix) {
        this.sessionIdPrefix = prefix;
    }

    public String getSessionIdPrefix() {
        return sessionIdPrefix;
    }

    public SessionIdGenerator getIdGenerator() {
        return idGenerator;
    }
}
