package gov.uspto.session.management;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.uspto.session.model.ReauthReason;
import gov.uspto.session.model.Session;
import gov.uspto.session.model.SessionState;
import gov.uspto.session.security.SessionHijackingPrevention;

public class SessionManager {

    private static final Logger LOG = LoggerFactory.getLogger(SessionManager.class);

    private final SessionStore sessionStore;
    private final SessionFactory sessionFactory;
    private final SessionValidator sessionValidator;
    private final SessionHijackingPrevention hijackingPrevention;

    public SessionManager() {
        this(new InMemorySessionStore(), new SessionFactory(), new SessionValidator(), new SessionHijackingPrevention());
    }

    public SessionManager(SessionStore sessionStore) {
        this(sessionStore, new SessionFactory(), new SessionValidator(), new SessionHijackingPrevention());
    }

    public SessionManager(SessionStore sessionStore, SessionFactory sessionFactory,
                          SessionValidator sessionValidator, SessionHijackingPrevention hijackingPrevention) {
        this.sessionStore = sessionStore;
        this.sessionFactory = sessionFactory;
        this.sessionValidator = sessionValidator;
        this.hijackingPrevention = hijackingPrevention;
    }

    public Session createSession(String userId) {
        Session session = sessionFactory.createSession(userId);
        sessionStore.save(session);
        LOG.info("Created session {} for user {}", session.getSessionId(), userId);
        return session;
    }

    public Session createSession(String userId, String ipAddress, String userAgent) {
        Session session = sessionFactory.createSession(userId, ipAddress, userAgent);
        hijackingPrevention.bindSession(session, ipAddress, userAgent);
        sessionStore.save(session);
        LOG.info("Created session {} for user {} from IP {}", session.getSessionId(), userId, ipAddress);
        return session;
    }

    public Optional<Session> getSession(String sessionId) {
        return sessionStore.findById(sessionId);
    }

    public void validateSession(String sessionId) throws SessionValidationException {
        Session session = sessionStore.findById(sessionId)
                .orElseThrow(() -> new SessionValidationException("Session not found: " + sessionId));

        SessionValidator.ValidationResult result = sessionValidator.validate(session);
        if (!result.isValid()) {
            throw new SessionValidationException("Session validation failed: " + String.join(", ", result.getErrors()));
        }
    }

    public SessionValidator.ValidationResult validateAndGetResult(String sessionId) {
        Optional<Session> sessionOpt = sessionStore.findById(sessionId);
        if (!sessionOpt.isPresent()) {
            return new SessionValidator.ValidationResult(false,
                    Collections.singletonList("Session not found"), Collections.emptyList());
        }
        return sessionValidator.validate(sessionOpt.get());
    }

    public void triggerReauthentication(String sessionId, ReauthReason reason) {
        sessionStore.findById(sessionId).ifPresent(session -> {
            session.addPendingReauthReason(reason);
            session.setState(SessionState.PENDING_REAUTH);
            sessionStore.save(session);
            LOG.info("Triggered re-authentication for session {} due to {}", sessionId, reason);
        });
    }

    public boolean isReauthenticationRequired(Session session) {
        if (session == null) {
            return true;
        }
        if (session.hasReauthPending()) {
            return true;
        }
        SessionValidator.ValidationResult result = sessionValidator.validate(session);
        return result.requiresReauthentication();
    }

    public void completeReauthentication(String sessionId) {
        sessionStore.findById(sessionId).ifPresent(session -> {
            session.recordReauthentication();
            sessionStore.save(session);
            LOG.info("Completed re-authentication for session {}", sessionId);
        });
    }

    public void touchSession(String sessionId) {
        sessionStore.findById(sessionId).ifPresent(session -> {
            session.touch();
            sessionStore.save(session);
        });
    }

    public void invalidateSession(String sessionId) {
        sessionStore.findById(sessionId).ifPresent(session -> {
            session.setState(SessionState.INVALIDATED);
            sessionStore.save(session);
            LOG.info("Invalidated session {}", sessionId);
        });
    }

    public void expireSession(String sessionId) {
        sessionStore.findById(sessionId).ifPresent(session -> {
            session.setState(SessionState.EXPIRED);
            sessionStore.save(session);
            LOG.info("Expired session {}", sessionId);
        });
    }

    public void deleteSession(String sessionId) {
        sessionStore.delete(sessionId);
        LOG.info("Deleted session {}", sessionId);
    }

    public Collection<Session> getSessionsByUserId(String userId) {
        return sessionStore.findByUserId(userId);
    }

    public void invalidateAllUserSessions(String userId) {
        sessionStore.findByUserId(userId).forEach(session -> {
            session.setState(SessionState.INVALIDATED);
            sessionStore.save(session);
        });
        LOG.info("Invalidated all sessions for user {}", userId);
    }

    public void deleteAllUserSessions(String userId) {
        sessionStore.deleteByUserId(userId);
        LOG.info("Deleted all sessions for user {}", userId);
    }

    public void cleanupExpiredSessions() {
        sessionStore.deleteExpired();
        LOG.debug("Cleaned up expired sessions");
    }

    public SessionHijackingPrevention.ValidationResult validateRequest(String sessionId, String ipAddress, String userAgent) {
        Optional<Session> sessionOpt = sessionStore.findById(sessionId);
        if (!sessionOpt.isPresent()) {
            SessionHijackingPrevention.ValidationResult result = new SessionHijackingPrevention.ValidationResult();
            result.addViolation(null, "Session not found");
            return result;
        }
        return hijackingPrevention.validateRequest(sessionOpt.get(), ipAddress, userAgent);
    }

    public long getActiveSessionCount() {
        return sessionStore.findAll().stream()
                .filter(s -> s.getState() == SessionState.ACTIVE)
                .count();
    }

    public long getTotalSessionCount() {
        return sessionStore.count();
    }

    public SessionStore getSessionStore() {
        return sessionStore;
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public SessionValidator getSessionValidator() {
        return sessionValidator;
    }

    public SessionHijackingPrevention getHijackingPrevention() {
        return hijackingPrevention;
    }

    public static class SessionValidationException extends RuntimeException {
        public SessionValidationException(String message) {
            super(message);
        }
    }
}
