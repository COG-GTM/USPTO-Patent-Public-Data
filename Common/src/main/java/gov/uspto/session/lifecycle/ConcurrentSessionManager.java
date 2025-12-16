package gov.uspto.session.lifecycle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.uspto.session.management.SessionStore;
import gov.uspto.session.model.ReauthReason;
import gov.uspto.session.model.Session;
import gov.uspto.session.model.SessionState;
import gov.uspto.session.reauth.ReauthenticationPolicy;

public class ConcurrentSessionManager {

    private static final Logger LOG = LoggerFactory.getLogger(ConcurrentSessionManager.class);

    private final SessionStore sessionStore;
    private final ReauthenticationPolicy policy;
    private final List<ConcurrentSessionListener> listeners;
    private ConcurrentSessionStrategy strategy = ConcurrentSessionStrategy.TERMINATE_OLDEST;

    public ConcurrentSessionManager(SessionStore sessionStore, ReauthenticationPolicy policy) {
        this.sessionStore = sessionStore;
        this.policy = policy;
        this.listeners = new ArrayList<>();
    }

    public ConcurrentSessionCheckResult checkConcurrentSessions(String userId) {
        Collection<Session> userSessions = getActiveSessionsForUser(userId);
        int maxAllowed = policy.getMaxConcurrentSessions();

        boolean limitExceeded = userSessions.size() >= maxAllowed;
        int currentCount = userSessions.size();

        return new ConcurrentSessionCheckResult(limitExceeded, currentCount, maxAllowed);
    }

    public void enforceConcurrentSessionLimit(String userId, Session newSession) {
        Collection<Session> activeSessions = getActiveSessionsForUser(userId);
        int maxAllowed = policy.getMaxConcurrentSessions();

        if (activeSessions.size() < maxAllowed) {
            return;
        }

        int sessionsToTerminate = activeSessions.size() - maxAllowed + 1;

        List<Session> sessionsToRemove = selectSessionsToTerminate(activeSessions, sessionsToTerminate, newSession);

        for (Session session : sessionsToRemove) {
            session.setState(SessionState.INVALIDATED);
            session.addPendingReauthReason(ReauthReason.CONCURRENT_SESSION_DETECTED);
            sessionStore.save(session);

            LOG.info("Terminated session {} for user {} due to concurrent session limit",
                    session.getSessionId(), userId);

            notifyConcurrentSessionTerminated(session, newSession);
        }
    }

    private List<Session> selectSessionsToTerminate(Collection<Session> sessions, int count, Session newSession) {
        List<Session> sortedSessions;

        switch (strategy) {
            case TERMINATE_OLDEST:
                sortedSessions = sessions.stream()
                        .filter(s -> !s.getSessionId().equals(newSession.getSessionId()))
                        .sorted(Comparator.comparing(Session::getCreatedAt))
                        .collect(Collectors.toList());
                break;
            case TERMINATE_LEAST_RECENTLY_USED:
                sortedSessions = sessions.stream()
                        .filter(s -> !s.getSessionId().equals(newSession.getSessionId()))
                        .sorted(Comparator.comparing(Session::getLastAccessed))
                        .collect(Collectors.toList());
                break;
            case TERMINATE_NEWEST:
                sortedSessions = sessions.stream()
                        .filter(s -> !s.getSessionId().equals(newSession.getSessionId()))
                        .sorted(Comparator.comparing(Session::getCreatedAt).reversed())
                        .collect(Collectors.toList());
                break;
            case DENY_NEW:
            default:
                return new ArrayList<>();
        }

        return sortedSessions.stream()
                .limit(count)
                .collect(Collectors.toList());
    }

    public Collection<Session> getActiveSessionsForUser(String userId) {
        return sessionStore.findByUserId(userId).stream()
                .filter(s -> s.getState() == SessionState.ACTIVE || s.getState() == SessionState.PENDING_REAUTH)
                .collect(Collectors.toList());
    }

    public int getActiveSessionCount(String userId) {
        return getActiveSessionsForUser(userId).size();
    }

    public void terminateAllOtherSessions(String userId, String currentSessionId) {
        Collection<Session> userSessions = sessionStore.findByUserId(userId);
        for (Session session : userSessions) {
            if (!session.getSessionId().equals(currentSessionId)) {
                session.setState(SessionState.INVALIDATED);
                sessionStore.save(session);
                LOG.info("Terminated session {} for user {} (keeping current session {})",
                        session.getSessionId(), userId, currentSessionId);
            }
        }
    }

    public void setStrategy(ConcurrentSessionStrategy strategy) {
        this.strategy = strategy;
    }

    public ConcurrentSessionStrategy getStrategy() {
        return strategy;
    }

    public void addListener(ConcurrentSessionListener listener) {
        listeners.add(listener);
    }

    public void removeListener(ConcurrentSessionListener listener) {
        listeners.remove(listener);
    }

    private void notifyConcurrentSessionTerminated(Session terminatedSession, Session newSession) {
        for (ConcurrentSessionListener listener : listeners) {
            try {
                listener.onConcurrentSessionTerminated(terminatedSession, newSession);
            } catch (Exception e) {
                LOG.error("Error notifying concurrent session listener", e);
            }
        }
    }

    public SessionStore getSessionStore() {
        return sessionStore;
    }

    public ReauthenticationPolicy getPolicy() {
        return policy;
    }

    public enum ConcurrentSessionStrategy {
        TERMINATE_OLDEST,
        TERMINATE_NEWEST,
        TERMINATE_LEAST_RECENTLY_USED,
        DENY_NEW
    }

    public static class ConcurrentSessionCheckResult {
        private final boolean limitExceeded;
        private final int currentCount;
        private final int maxAllowed;

        public ConcurrentSessionCheckResult(boolean limitExceeded, int currentCount, int maxAllowed) {
            this.limitExceeded = limitExceeded;
            this.currentCount = currentCount;
            this.maxAllowed = maxAllowed;
        }

        public boolean isLimitExceeded() {
            return limitExceeded;
        }

        public int getCurrentCount() {
            return currentCount;
        }

        public int getMaxAllowed() {
            return maxAllowed;
        }

        public int getRemainingSlots() {
            return Math.max(0, maxAllowed - currentCount);
        }
    }

    public interface ConcurrentSessionListener {
        void onConcurrentSessionTerminated(Session terminatedSession, Session newSession);
    }
}
