package gov.uspto.session.lifecycle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.uspto.session.management.SessionStore;
import gov.uspto.session.model.Session;
import gov.uspto.session.model.SessionState;

public class SessionTerminationService {

    private static final Logger LOG = LoggerFactory.getLogger(SessionTerminationService.class);

    private final SessionStore sessionStore;
    private final List<SessionTerminationListener> listeners;

    public SessionTerminationService(SessionStore sessionStore) {
        this.sessionStore = sessionStore;
        this.listeners = new ArrayList<>();
    }

    public void terminateSession(String sessionId, TerminationReason reason) {
        sessionStore.findById(sessionId).ifPresent(session -> {
            terminateSession(session, reason);
        });
    }

    public void terminateSession(Session session, TerminationReason reason) {
        SessionState previousState = session.getState();

        switch (reason) {
            case USER_LOGOUT:
            case ADMIN_TERMINATION:
            case SECURITY_VIOLATION:
                session.setState(SessionState.INVALIDATED);
                break;
            case SESSION_TIMEOUT:
            case IDLE_TIMEOUT:
                session.setState(SessionState.EXPIRED);
                break;
            default:
                session.setState(SessionState.INVALIDATED);
        }

        sessionStore.save(session);

        LOG.info("Terminated session {} due to {}, state changed from {} to {}",
                session.getSessionId(), reason, previousState, session.getState());

        notifySessionTerminated(session, reason);
    }

    public void terminateAllUserSessions(String userId, TerminationReason reason) {
        Collection<Session> userSessions = sessionStore.findByUserId(userId);
        for (Session session : userSessions) {
            terminateSession(session, reason);
        }
        LOG.info("Terminated {} sessions for user {} due to {}",
                userSessions.size(), userId, reason);
    }

    public void terminateAndDeleteSession(String sessionId, TerminationReason reason) {
        sessionStore.findById(sessionId).ifPresent(session -> {
            notifySessionTerminated(session, reason);
            sessionStore.delete(sessionId);
            LOG.info("Terminated and deleted session {} due to {}", sessionId, reason);
        });
    }

    public void deleteSession(String sessionId) {
        sessionStore.delete(sessionId);
        LOG.info("Deleted session {}", sessionId);
    }

    public void deleteAllUserSessions(String userId) {
        sessionStore.deleteByUserId(userId);
        LOG.info("Deleted all sessions for user {}", userId);
    }

    public void cleanupExpiredSessions() {
        Collection<Session> allSessions = sessionStore.findAll();
        int count = 0;
        for (Session session : allSessions) {
            if (session.getState() == SessionState.EXPIRED ||
                session.getState() == SessionState.INVALIDATED) {
                sessionStore.delete(session.getSessionId());
                count++;
            }
        }
        if (count > 0) {
            LOG.info("Cleaned up {} expired/invalidated sessions", count);
        }
    }

    public void addListener(SessionTerminationListener listener) {
        listeners.add(listener);
    }

    public void removeListener(SessionTerminationListener listener) {
        listeners.remove(listener);
    }

    private void notifySessionTerminated(Session session, TerminationReason reason) {
        for (SessionTerminationListener listener : listeners) {
            try {
                listener.onSessionTerminated(session, reason);
            } catch (Exception e) {
                LOG.error("Error notifying session termination listener", e);
            }
        }
    }

    public SessionStore getSessionStore() {
        return sessionStore;
    }

    public enum TerminationReason {
        USER_LOGOUT,
        SESSION_TIMEOUT,
        IDLE_TIMEOUT,
        ADMIN_TERMINATION,
        SECURITY_VIOLATION,
        CONCURRENT_SESSION_LIMIT,
        PASSWORD_CHANGE,
        ACCOUNT_DISABLED,
        SYSTEM_SHUTDOWN
    }

    public interface SessionTerminationListener {
        void onSessionTerminated(Session session, TerminationReason reason);
    }
}
