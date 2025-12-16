package gov.uspto.session.management;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import gov.uspto.session.model.ReauthReason;
import gov.uspto.session.model.Session;
import gov.uspto.session.model.SessionState;

public class SessionManagerTest {

    private SessionManager sessionManager;

    @Before
    public void setUp() {
        sessionManager = new SessionManager();
    }

    @Test
    public void testCreateSession() {
        Session session = sessionManager.createSession("user123");

        assertNotNull(session);
        assertNotNull(session.getSessionId());
        assertEquals("user123", session.getUserId());
        assertEquals(SessionState.ACTIVE, session.getState());
    }

    @Test
    public void testCreateSessionWithIpAndUserAgent() {
        Session session = sessionManager.createSession("user123", "192.168.1.1", "Mozilla/5.0");

        assertNotNull(session);
        assertEquals("192.168.1.1", session.getIpAddress());
        assertEquals("Mozilla/5.0", session.getUserAgent());
    }

    @Test
    public void testGetSession() {
        Session created = sessionManager.createSession("user123");
        Optional<Session> retrieved = sessionManager.getSession(created.getSessionId());

        assertTrue(retrieved.isPresent());
        assertEquals(created.getSessionId(), retrieved.get().getSessionId());
    }

    @Test
    public void testGetSessionNotFound() {
        Optional<Session> session = sessionManager.getSession("nonexistent");
        assertFalse(session.isPresent());
    }

    @Test
    public void testValidateSession() {
        Session session = sessionManager.createSession("user123");

        sessionManager.validateSession(session.getSessionId());
    }

    @Test(expected = SessionManager.SessionValidationException.class)
    public void testValidateSessionNotFound() {
        sessionManager.validateSession("nonexistent");
    }

    @Test
    public void testTriggerReauthentication() {
        Session session = sessionManager.createSession("user123");
        sessionManager.triggerReauthentication(session.getSessionId(), ReauthReason.PRIVILEGE_ESCALATION);

        Session updated = sessionManager.getSession(session.getSessionId()).get();
        assertEquals(SessionState.PENDING_REAUTH, updated.getState());
        assertTrue(updated.getPendingReauthReasons().contains(ReauthReason.PRIVILEGE_ESCALATION));
    }

    @Test
    public void testIsReauthenticationRequired() {
        Session session = sessionManager.createSession("user123");
        assertFalse(sessionManager.isReauthenticationRequired(session));

        session.addPendingReauthReason(ReauthReason.SESSION_TIMEOUT);
        assertTrue(sessionManager.isReauthenticationRequired(session));
    }

    @Test
    public void testCompleteReauthentication() {
        Session session = sessionManager.createSession("user123");
        sessionManager.triggerReauthentication(session.getSessionId(), ReauthReason.SESSION_TIMEOUT);

        sessionManager.completeReauthentication(session.getSessionId());

        Session updated = sessionManager.getSession(session.getSessionId()).get();
        assertEquals(SessionState.ACTIVE, updated.getState());
        assertFalse(updated.hasReauthPending());
        assertEquals(1, updated.getReauthenticationCount());
    }

    @Test
    public void testTouchSession() throws InterruptedException {
        Session session = sessionManager.createSession("user123");
        long initialAccess = session.getLastAccessed().toEpochMilli();

        Thread.sleep(10);
        sessionManager.touchSession(session.getSessionId());

        Session updated = sessionManager.getSession(session.getSessionId()).get();
        assertTrue(updated.getLastAccessed().toEpochMilli() > initialAccess);
    }

    @Test
    public void testInvalidateSession() {
        Session session = sessionManager.createSession("user123");
        sessionManager.invalidateSession(session.getSessionId());

        Session updated = sessionManager.getSession(session.getSessionId()).get();
        assertEquals(SessionState.INVALIDATED, updated.getState());
    }

    @Test
    public void testExpireSession() {
        Session session = sessionManager.createSession("user123");
        sessionManager.expireSession(session.getSessionId());

        Session updated = sessionManager.getSession(session.getSessionId()).get();
        assertEquals(SessionState.EXPIRED, updated.getState());
    }

    @Test
    public void testDeleteSession() {
        Session session = sessionManager.createSession("user123");
        sessionManager.deleteSession(session.getSessionId());

        assertFalse(sessionManager.getSession(session.getSessionId()).isPresent());
    }

    @Test
    public void testGetSessionsByUserId() {
        sessionManager.createSession("user123");
        sessionManager.createSession("user123");
        sessionManager.createSession("user456");

        Collection<Session> user123Sessions = sessionManager.getSessionsByUserId("user123");
        assertEquals(2, user123Sessions.size());

        Collection<Session> user456Sessions = sessionManager.getSessionsByUserId("user456");
        assertEquals(1, user456Sessions.size());
    }

    @Test
    public void testInvalidateAllUserSessions() {
        sessionManager.createSession("user123");
        sessionManager.createSession("user123");

        sessionManager.invalidateAllUserSessions("user123");

        Collection<Session> sessions = sessionManager.getSessionsByUserId("user123");
        for (Session session : sessions) {
            assertEquals(SessionState.INVALIDATED, session.getState());
        }
    }

    @Test
    public void testDeleteAllUserSessions() {
        sessionManager.createSession("user123");
        sessionManager.createSession("user123");

        sessionManager.deleteAllUserSessions("user123");

        Collection<Session> sessions = sessionManager.getSessionsByUserId("user123");
        assertTrue(sessions.isEmpty());
    }

    @Test
    public void testGetActiveSessionCount() {
        sessionManager.createSession("user1");
        sessionManager.createSession("user2");
        Session session3 = sessionManager.createSession("user3");
        sessionManager.invalidateSession(session3.getSessionId());

        assertEquals(2, sessionManager.getActiveSessionCount());
    }

    @Test
    public void testGetTotalSessionCount() {
        sessionManager.createSession("user1");
        sessionManager.createSession("user2");
        sessionManager.createSession("user3");

        assertEquals(3, sessionManager.getTotalSessionCount());
    }

    @Test
    public void testValidateAndGetResult() {
        Session session = sessionManager.createSession("user123");
        SessionValidator.ValidationResult result = sessionManager.validateAndGetResult(session.getSessionId());

        assertTrue(result.isValid());
        assertTrue(result.getErrors().isEmpty());
    }

    @Test
    public void testValidateAndGetResultNotFound() {
        SessionValidator.ValidationResult result = sessionManager.validateAndGetResult("nonexistent");

        assertFalse(result.isValid());
        assertTrue(result.getErrors().contains("Session not found"));
    }
}
