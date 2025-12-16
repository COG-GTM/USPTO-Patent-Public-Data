package gov.uspto.session.model;

import static org.junit.Assert.*;

import java.time.Instant;

import org.junit.Before;
import org.junit.Test;

public class SessionTest {

    private Session session;
    private static final String SESSION_ID = "test-session-123";
    private static final String USER_ID = "user-456";

    @Before
    public void setUp() {
        session = new Session(SESSION_ID, USER_ID);
    }

    @Test
    public void testSessionCreation() {
        assertNotNull(session);
        assertEquals(SESSION_ID, session.getSessionId());
        assertEquals(USER_ID, session.getUserId());
        assertEquals(SessionState.ACTIVE, session.getState());
        assertNotNull(session.getCreatedAt());
        assertNotNull(session.getLastAccessed());
        assertNotNull(session.getLastReauthentication());
    }

    @Test
    public void testSessionWithCustomCreationTime() {
        Instant customTime = Instant.now().minusSeconds(3600);
        Session customSession = new Session(SESSION_ID, USER_ID, customTime);
        assertEquals(customTime, customSession.getCreatedAt());
    }

    @Test(expected = NullPointerException.class)
    public void testSessionCreationWithNullId() {
        new Session(null, USER_ID);
    }

    @Test
    public void testSessionState() {
        assertTrue(session.isActive());
        assertFalse(session.isPendingReauth());
        assertFalse(session.isExpired());
        assertFalse(session.isInvalidated());

        session.setState(SessionState.PENDING_REAUTH);
        assertTrue(session.isPendingReauth());
        assertFalse(session.isActive());

        session.setState(SessionState.EXPIRED);
        assertTrue(session.isExpired());

        session.setState(SessionState.INVALIDATED);
        assertTrue(session.isInvalidated());
    }

    @Test
    public void testTouch() throws InterruptedException {
        Instant beforeTouch = session.getLastAccessed();
        Thread.sleep(10);
        session.touch();
        assertTrue(session.getLastAccessed().isAfter(beforeTouch));
    }

    @Test
    public void testAttributes() {
        session.setAttribute("key1", "value1");
        session.setAttribute("key2", 123);

        assertEquals("value1", session.getAttribute("key1"));
        assertEquals(123, session.getAttribute("key2"));
        assertNull(session.getAttribute("nonexistent"));

        session.removeAttribute("key1");
        assertNull(session.getAttribute("key1"));
    }

    @Test
    public void testSecurityAttributes() {
        session.setSecurityAttribute("role", "admin");
        session.setSecurityAttribute("level", 5);

        assertEquals("admin", session.getSecurityAttribute("role"));
        assertEquals(5, session.getSecurityAttribute("level"));

        session.removeSecurityAttribute("role");
        assertNull(session.getSecurityAttribute("role"));
    }

    @Test
    public void testReauthReasons() {
        assertFalse(session.hasReauthPending());
        assertTrue(session.getPendingReauthReasons().isEmpty());

        session.addPendingReauthReason(ReauthReason.SESSION_TIMEOUT);
        assertTrue(session.hasReauthPending());
        assertTrue(session.getPendingReauthReasons().contains(ReauthReason.SESSION_TIMEOUT));
        assertEquals(SessionState.PENDING_REAUTH, session.getState());

        session.addPendingReauthReason(ReauthReason.PRIVILEGE_ESCALATION);
        assertEquals(2, session.getPendingReauthReasons().size());

        session.clearPendingReauthReasons();
        assertFalse(session.hasReauthPending());
    }

    @Test
    public void testRecordReauthentication() {
        session.addPendingReauthReason(ReauthReason.SESSION_TIMEOUT);
        assertEquals(SessionState.PENDING_REAUTH, session.getState());
        assertEquals(0, session.getReauthenticationCount());

        session.recordReauthentication();

        assertEquals(SessionState.ACTIVE, session.getState());
        assertFalse(session.hasReauthPending());
        assertEquals(1, session.getReauthenticationCount());
    }

    @Test
    public void testIpAndUserAgent() {
        assertNull(session.getIpAddress());
        assertNull(session.getUserAgent());

        session.setIpAddress("192.168.1.1");
        session.setUserAgent("Mozilla/5.0");

        assertEquals("192.168.1.1", session.getIpAddress());
        assertEquals("Mozilla/5.0", session.getUserAgent());
    }

    @Test
    public void testSessionDuration() {
        assertTrue(session.getSessionDurationSeconds() >= 0);
        assertTrue(session.getIdleTimeSeconds() >= 0);
        assertTrue(session.getTimeSinceLastReauthSeconds() >= 0);
    }

    @Test
    public void testEqualsAndHashCode() {
        Session sameSession = new Session(SESSION_ID, "different-user");
        Session differentSession = new Session("different-id", USER_ID);

        assertEquals(session, sameSession);
        assertEquals(session.hashCode(), sameSession.hashCode());
        assertNotEquals(session, differentSession);
    }

    @Test
    public void testToString() {
        String str = session.toString();
        assertTrue(str.contains(SESSION_ID));
        assertTrue(str.contains(USER_ID));
    }
}
