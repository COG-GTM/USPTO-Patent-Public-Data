package gov.uspto.session;

import gov.uspto.session.management.SessionFactory;
import gov.uspto.session.management.SessionManager;
import gov.uspto.session.management.SessionValidator;
import gov.uspto.session.model.ReauthReason;
import gov.uspto.session.model.Session;
import gov.uspto.session.model.SessionState;
import gov.uspto.session.reauth.ReauthenticationPolicy;
import gov.uspto.session.security.SessionIdGenerator;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.*;

/**
 * Tests for SessionManager
 */
public class SessionManagerTest {
    
    private InMemorySessionStore sessionStore;
    private SessionManager sessionManager;
    
    @Before
    public void setUp() {
        sessionStore = new InMemorySessionStore();
        SessionIdGenerator idGenerator = new SessionIdGenerator();
        SessionFactory sessionFactory = new SessionFactory(idGenerator);
        SessionValidator sessionValidator = new SessionValidator(3600, 1800);
        ReauthenticationPolicy reauthPolicy = new ReauthenticationPolicy.Builder()
                .reauthTimeoutSeconds(3600)
                .build();
        
        sessionManager = new SessionManager(sessionStore, sessionFactory, sessionValidator, reauthPolicy);
    }
    
    @Test
    public void testCreateSession() {
        Session session = sessionManager.createSession("user123");
        
        assertNotNull(session);
        assertEquals("user123", session.getUserId());
        assertEquals(SessionState.ACTIVE, session.getState());
        assertTrue(sessionStore.exists(session.getSessionId()));
    }
    
    @Test
    public void testCreateSessionWithSecurityContext() {
        Session session = sessionManager.createSession("user123", "192.168.1.100", "Mozilla/5.0");
        
        assertNotNull(session);
        assertEquals("user123", session.getUserId());
        assertEquals("192.168.1.100", session.getIpAddress());
        assertEquals("Mozilla/5.0", session.getUserAgent());
        assertTrue(sessionStore.exists(session.getSessionId()));
    }
    
    @Test
    public void testGetSession() {
        Session created = sessionManager.createSession("user123");
        
        Optional<Session> retrieved = sessionManager.getSession(created.getSessionId());
        
        assertTrue(retrieved.isPresent());
        assertEquals(created.getSessionId(), retrieved.get().getSessionId());
        assertEquals(created.getUserId(), retrieved.get().getUserId());
    }
    
    @Test
    public void testGetNonExistentSession() {
        Optional<Session> retrieved = sessionManager.getSession("nonexistent");
        
        assertFalse(retrieved.isPresent());
    }
    
    @Test
    public void testValidateSession() {
        Session session = sessionManager.createSession("user123");
        
        assertTrue(sessionManager.validateSession(session.getSessionId()));
        assertFalse(sessionManager.validateSession("nonexistent"));
    }
    
    @Test
    public void testTouchSession() {
        Session session = sessionManager.createSession("user123");
        int initialAccessCount = session.getAccessCount();
        
        sessionManager.touchSession(session.getSessionId());
        
        Optional<Session> retrieved = sessionStore.findById(session.getSessionId());
        assertTrue(retrieved.isPresent());
        assertTrue(retrieved.get().getAccessCount() > initialAccessCount);
    }
    
    @Test
    public void testTriggerReauthentication() {
        Session session = sessionManager.createSession("user123");
        
        assertFalse(session.requiresReauthentication());
        
        sessionManager.triggerReauthentication(session.getSessionId(), ReauthReason.PRIVILEGE_ESCALATION);
        
        Optional<Session> retrieved = sessionStore.findById(session.getSessionId());
        assertTrue(retrieved.isPresent());
        assertTrue(retrieved.get().requiresReauthentication());
        assertTrue(retrieved.get().getPendingReauthReasons().contains(ReauthReason.PRIVILEGE_ESCALATION));
    }
    
    @Test
    public void testMarkReauthenticated() {
        Session session = sessionManager.createSession("user123");
        sessionManager.triggerReauthentication(session.getSessionId(), ReauthReason.ROLE_CHANGE);
        
        Optional<Session> beforeReauth = sessionStore.findById(session.getSessionId());
        assertTrue(beforeReauth.isPresent());
        assertTrue(beforeReauth.get().requiresReauthentication());
        
        sessionManager.markReauthenticated(session.getSessionId());
        
        Optional<Session> afterReauth = sessionStore.findById(session.getSessionId());
        assertTrue(afterReauth.isPresent());
        assertFalse(afterReauth.get().requiresReauthentication());
        assertEquals(0, afterReauth.get().getPendingReauthReasons().size());
    }
    
    @Test
    public void testTerminateSession() {
        Session session = sessionManager.createSession("user123");
        
        assertEquals(SessionState.ACTIVE, session.getState());
        
        sessionManager.terminateSession(session.getSessionId());
        
        Optional<Session> retrieved = sessionStore.findById(session.getSessionId());
        assertTrue(retrieved.isPresent());
        assertEquals(SessionState.TERMINATED, retrieved.get().getState());
    }
    
    @Test
    public void testTerminateAllUserSessions() {
        sessionManager.createSession("user123");
        sessionManager.createSession("user123");
        sessionManager.createSession("user123");
        
        assertEquals(3, sessionStore.findByUserId("user123").length);
        
        sessionManager.terminateAllUserSessions("user123");
        
        Session[] sessions = sessionStore.findByUserId("user123");
        assertEquals(3, sessions.length);
        for (Session session : sessions) {
            assertEquals(SessionState.TERMINATED, session.getState());
        }
    }
    
    @Test
    public void testGetActiveSessionCount() {
        sessionManager.createSession("user123");
        sessionManager.createSession("user123");
        Session session3 = sessionManager.createSession("user123");
        
        assertEquals(3, sessionManager.getActiveSessionCount("user123"));
        
        sessionManager.terminateSession(session3.getSessionId());
        
        assertEquals(2, sessionManager.getActiveSessionCount("user123"));
    }
}
