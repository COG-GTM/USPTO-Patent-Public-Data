package gov.uspto.session.reauth;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.uspto.session.management.SessionManager;
import gov.uspto.session.model.ReauthReason;
import gov.uspto.session.model.Session;
import gov.uspto.session.model.SessionState;

public class SessionTimeoutManager {

    private static final Logger LOG = LoggerFactory.getLogger(SessionTimeoutManager.class);

    private final SessionManager sessionManager;
    private final ReauthenticationPolicy policy;
    private final ScheduledExecutorService scheduler;
    private ScheduledFuture<?> cleanupTask;
    private long cleanupIntervalSeconds = 60;
    private boolean running = false;

    public SessionTimeoutManager(SessionManager sessionManager, ReauthenticationPolicy policy) {
        this.sessionManager = sessionManager;
        this.policy = policy;
        this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "session-timeout-manager");
            t.setDaemon(true);
            return t;
        });
    }

    public void start() {
        if (running) {
            LOG.warn("SessionTimeoutManager is already running");
            return;
        }

        cleanupTask = scheduler.scheduleAtFixedRate(
                this::checkTimeouts,
                cleanupIntervalSeconds,
                cleanupIntervalSeconds,
                TimeUnit.SECONDS
        );
        running = true;
        LOG.info("SessionTimeoutManager started with cleanup interval of {} seconds", cleanupIntervalSeconds);
    }

    public void stop() {
        if (!running) {
            return;
        }

        if (cleanupTask != null) {
            cleanupTask.cancel(false);
        }
        running = false;
        LOG.info("SessionTimeoutManager stopped");
    }

    public void shutdown() {
        stop();
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(10, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
        LOG.info("SessionTimeoutManager shutdown complete");
    }

    public void checkTimeouts() {
        try {
            Collection<Session> sessions = sessionManager.getSessionStore().findAll();
            Instant now = Instant.now();

            for (Session session : sessions) {
                if (session.getState() == SessionState.INVALIDATED ||
                    session.getState() == SessionState.EXPIRED) {
                    continue;
                }

                checkSessionTimeout(session, now);
                checkIdleTimeout(session, now);
                checkReauthInterval(session, now);
            }

            sessionManager.cleanupExpiredSessions();
        } catch (Exception e) {
            LOG.error("Error during timeout check", e);
        }
    }

    private void checkSessionTimeout(Session session, Instant now) {
        Duration sessionDuration = Duration.between(session.getCreatedAt(), now);
        if (sessionDuration.getSeconds() > policy.getSessionTimeoutSeconds()) {
            LOG.info("Session {} exceeded absolute timeout", session.getSessionId());
            sessionManager.expireSession(session.getSessionId());
        }
    }

    private void checkIdleTimeout(Session session, Instant now) {
        Duration idleDuration = Duration.between(session.getLastAccessed(), now);
        if (idleDuration.getSeconds() > policy.getIdleTimeoutSeconds()) {
            LOG.info("Session {} exceeded idle timeout", session.getSessionId());
            sessionManager.triggerReauthentication(session.getSessionId(), ReauthReason.IDLE_TIMEOUT);
        }
    }

    private void checkReauthInterval(Session session, Instant now) {
        Duration reauthDuration = Duration.between(session.getLastReauthentication(), now);
        if (reauthDuration.getSeconds() > policy.getReauthIntervalSeconds()) {
            if (!session.hasReauthPending()) {
                LOG.info("Session {} exceeded re-authentication interval", session.getSessionId());
                sessionManager.triggerReauthentication(session.getSessionId(), ReauthReason.SESSION_TIMEOUT);
            }
        }
    }

    public long getRemainingSessionTime(Session session) {
        long elapsed = session.getSessionDurationSeconds();
        long remaining = policy.getSessionTimeoutSeconds() - elapsed;
        return Math.max(0, remaining);
    }

    public long getRemainingIdleTime(Session session) {
        long elapsed = session.getIdleTimeSeconds();
        long remaining = policy.getIdleTimeoutSeconds() - elapsed;
        return Math.max(0, remaining);
    }

    public long getRemainingReauthTime(Session session) {
        long elapsed = session.getTimeSinceLastReauthSeconds();
        long remaining = policy.getReauthIntervalSeconds() - elapsed;
        return Math.max(0, remaining);
    }

    public TimeoutStatus getTimeoutStatus(Session session) {
        return new TimeoutStatus(
                getRemainingSessionTime(session),
                getRemainingIdleTime(session),
                getRemainingReauthTime(session)
        );
    }

    public void setCleanupIntervalSeconds(long cleanupIntervalSeconds) {
        this.cleanupIntervalSeconds = cleanupIntervalSeconds;
    }

    public long getCleanupIntervalSeconds() {
        return cleanupIntervalSeconds;
    }

    public boolean isRunning() {
        return running;
    }

    public ReauthenticationPolicy getPolicy() {
        return policy;
    }

    public static class TimeoutStatus {
        private final long remainingSessionSeconds;
        private final long remainingIdleSeconds;
        private final long remainingReauthSeconds;

        public TimeoutStatus(long remainingSessionSeconds, long remainingIdleSeconds, long remainingReauthSeconds) {
            this.remainingSessionSeconds = remainingSessionSeconds;
            this.remainingIdleSeconds = remainingIdleSeconds;
            this.remainingReauthSeconds = remainingReauthSeconds;
        }

        public long getRemainingSessionSeconds() {
            return remainingSessionSeconds;
        }

        public long getRemainingIdleSeconds() {
            return remainingIdleSeconds;
        }

        public long getRemainingReauthSeconds() {
            return remainingReauthSeconds;
        }

        public boolean isSessionExpiringSoon(long thresholdSeconds) {
            return remainingSessionSeconds > 0 && remainingSessionSeconds <= thresholdSeconds;
        }

        public boolean isIdleExpiringSoon(long thresholdSeconds) {
            return remainingIdleSeconds > 0 && remainingIdleSeconds <= thresholdSeconds;
        }

        public boolean isReauthRequired() {
            return remainingReauthSeconds <= 0;
        }

        public long getMinRemainingSeconds() {
            return Math.min(remainingSessionSeconds, Math.min(remainingIdleSeconds, remainingReauthSeconds));
        }
    }
}
