package gov.uspto.session.lifecycle;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.uspto.session.management.SessionFactory;
import gov.uspto.session.management.SessionStore;
import gov.uspto.session.model.Session;
import gov.uspto.session.model.SessionState;

public class SessionRenewalService {

    private static final Logger LOG = LoggerFactory.getLogger(SessionRenewalService.class);

    private final SessionStore sessionStore;
    private final SessionFactory sessionFactory;
    private final List<SessionRenewalListener> listeners;
    private boolean preserveAttributes = true;
    private boolean preserveSecurityAttributes = true;

    public SessionRenewalService(SessionStore sessionStore, SessionFactory sessionFactory) {
        this.sessionStore = sessionStore;
        this.sessionFactory = sessionFactory;
        this.listeners = new ArrayList<>();
    }

    public void touchSession(String sessionId) {
        sessionStore.findById(sessionId).ifPresent(session -> {
            session.touch();
            sessionStore.save(session);
        });
    }

    public Optional<Session> renewSession(String sessionId) {
        Optional<Session> existingSession = sessionStore.findById(sessionId);
        if (!existingSession.isPresent()) {
            LOG.warn("Cannot renew session {}: session not found", sessionId);
            return Optional.empty();
        }

        Session oldSession = existingSession.get();
        if (oldSession.getState() == SessionState.INVALIDATED) {
            LOG.warn("Cannot renew session {}: session is invalidated", sessionId);
            return Optional.empty();
        }

        Session newSession = sessionFactory.createSession(oldSession.getUserId());

        if (preserveAttributes) {
            for (java.util.Map.Entry<String, Object> entry : oldSession.getAttributes().entrySet()) {
                newSession.setAttribute(entry.getKey(), entry.getValue());
            }
        }

        if (preserveSecurityAttributes) {
            for (java.util.Map.Entry<String, Object> entry : oldSession.getSecurityAttributes().entrySet()) {
                newSession.setSecurityAttribute(entry.getKey(), entry.getValue());
            }
        }

        newSession.setIpAddress(oldSession.getIpAddress());
        newSession.setUserAgent(oldSession.getUserAgent());
        newSession.setLastReauthentication(Instant.now());

        oldSession.setState(SessionState.INVALIDATED);
        sessionStore.save(oldSession);

        sessionStore.save(newSession);

        LOG.info("Renewed session {} -> {} for user {}",
                oldSession.getSessionId(), newSession.getSessionId(), newSession.getUserId());

        notifySessionRenewed(oldSession, newSession);

        return Optional.of(newSession);
    }

    public void extendSession(String sessionId) {
        sessionStore.findById(sessionId).ifPresent(session -> {
            session.touch();
            session.setLastReauthentication(Instant.now());
            sessionStore.save(session);
            LOG.info("Extended session {}", sessionId);
        });
    }

    public void recordReauthentication(String sessionId) {
        sessionStore.findById(sessionId).ifPresent(session -> {
            session.recordReauthentication();
            sessionStore.save(session);
            LOG.info("Recorded re-authentication for session {}", sessionId);
        });
    }

    public void setPreserveAttributes(boolean preserveAttributes) {
        this.preserveAttributes = preserveAttributes;
    }

    public boolean isPreserveAttributes() {
        return preserveAttributes;
    }

    public void setPreserveSecurityAttributes(boolean preserveSecurityAttributes) {
        this.preserveSecurityAttributes = preserveSecurityAttributes;
    }

    public boolean isPreserveSecurityAttributes() {
        return preserveSecurityAttributes;
    }

    public void addListener(SessionRenewalListener listener) {
        listeners.add(listener);
    }

    public void removeListener(SessionRenewalListener listener) {
        listeners.remove(listener);
    }

    private void notifySessionRenewed(Session oldSession, Session newSession) {
        for (SessionRenewalListener listener : listeners) {
            try {
                listener.onSessionRenewed(oldSession, newSession);
            } catch (Exception e) {
                LOG.error("Error notifying session renewal listener", e);
            }
        }
    }

    public SessionStore getSessionStore() {
        return sessionStore;
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public interface SessionRenewalListener {
        void onSessionRenewed(Session oldSession, Session newSession);
    }
}
