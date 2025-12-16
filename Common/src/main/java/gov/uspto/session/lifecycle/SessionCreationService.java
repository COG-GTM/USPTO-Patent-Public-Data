package gov.uspto.session.lifecycle;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.uspto.session.management.SessionFactory;
import gov.uspto.session.management.SessionStore;
import gov.uspto.session.model.Session;
import gov.uspto.session.security.SessionHijackingPrevention;

public class SessionCreationService {

    private static final Logger LOG = LoggerFactory.getLogger(SessionCreationService.class);

    private final SessionStore sessionStore;
    private final SessionFactory sessionFactory;
    private final SessionHijackingPrevention hijackingPrevention;
    private final List<SessionCreationListener> listeners;

    public SessionCreationService(SessionStore sessionStore, SessionFactory sessionFactory) {
        this(sessionStore, sessionFactory, new SessionHijackingPrevention());
    }

    public SessionCreationService(SessionStore sessionStore, SessionFactory sessionFactory,
                                   SessionHijackingPrevention hijackingPrevention) {
        this.sessionStore = sessionStore;
        this.sessionFactory = sessionFactory;
        this.hijackingPrevention = hijackingPrevention;
        this.listeners = new ArrayList<>();
    }

    public Session createSession(String userId) {
        return createSession(userId, null, null);
    }

    public Session createSession(String userId, String ipAddress, String userAgent) {
        Session session = sessionFactory.createSession(userId);

        if (ipAddress != null || userAgent != null) {
            hijackingPrevention.bindSession(session, ipAddress, userAgent);
        }

        sessionStore.save(session);

        LOG.info("Created session {} for user {} from IP {}",
                session.getSessionId(), userId, ipAddress);

        notifySessionCreated(session);

        return session;
    }

    public Session createSessionWithAttributes(String userId, String ipAddress, String userAgent,
                                                java.util.Map<String, Object> attributes) {
        Session session = createSession(userId, ipAddress, userAgent);

        if (attributes != null) {
            for (java.util.Map.Entry<String, Object> entry : attributes.entrySet()) {
                session.setAttribute(entry.getKey(), entry.getValue());
            }
            sessionStore.save(session);
        }

        return session;
    }

    public void addListener(SessionCreationListener listener) {
        listeners.add(listener);
    }

    public void removeListener(SessionCreationListener listener) {
        listeners.remove(listener);
    }

    private void notifySessionCreated(Session session) {
        for (SessionCreationListener listener : listeners) {
            try {
                listener.onSessionCreated(session);
            } catch (Exception e) {
                LOG.error("Error notifying session creation listener", e);
            }
        }
    }

    public SessionStore getSessionStore() {
        return sessionStore;
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public interface SessionCreationListener {
        void onSessionCreated(Session session);
    }
}
