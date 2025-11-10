package gov.uspto.session;

import gov.uspto.session.lifecycle.ConcurrentSessionManager;
import gov.uspto.session.lifecycle.SessionCreationService;
import gov.uspto.session.lifecycle.SessionRenewalService;
import gov.uspto.session.lifecycle.SessionTerminationService;
import gov.uspto.session.management.SessionFactory;
import gov.uspto.session.model.Session;
import gov.uspto.session.model.SessionState;
import gov.uspto.session.security.SessionHijackingPrevention;
import gov.uspto.session.security.SessionIdGenerator;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for session lifecycle management
 */
public class SessionLifecycleTest {
    
    private InMemorySessionStore sessionStore;
    private SessionIdGenerator idGenerator;
    private SessionFactory sessionFactory;
    
    @Before
    public void setUp() {
        sessionStore = new InMemorySessionStore();
        idGenerator = new SessionIdGenerator();
        sessionFactory = new SessionFactory(idGenerator);
    }
    
    @Test
    public void testSessionCreation() throws Exception {
        SessionHijackingPrevention hijackingPrevention = new SessionHijackingPrevention(true, true, 5);
        SessionCreationService creationService = new SessionCreationService(
            sessionFactory, sessionStore, hijackingPrevention);
        
        Session session = creationService.createSession("user123");
        
        assertNotNull(session);
        assertEquals("user123", session.getUserId());
        assertTrue(sessionStore.exists(session.getSessionId()));
    }
    
    @Test
    public void testSessionCreationWithSecurityContext() throws Exception {
        SessionHijackingPrevention hijackingPrevention = new SessionHijackingPrevention(true, true, 5);
        SessionCreationService creationService = new SessionCreationService(
            sessionFactory, sessionStore, hijackingPrevention);
        
        Session session = creationService.createSession("user123", "192.168.1.100", "Mozilla/5.0");
        
        assertNotNull(session);
        assertEquals("user123", session.getUserId());
        assertEquals("192.168.1.100", session.getIpAddress());
        assertEquals("Mozilla/5.0", session.getUserAgent());
    }
    
    @Test
    public void testConcurrentSessionLimitEnforcement() throws Exception {
        SessionHijackingPrevention hijackingPrevention = new SessionHijackingPrevention(true, true, 2);
        SessionCreationService creationService = new SessionCreationService(
            sessionFactory, sessionStore, hijackingPrevention);
        
        creationService.createSession("user123");
        creationService.createSession("user123");
        
        try {
            creationService.createSession("user123");
            fail("Expected SessionCreationException due to concurrent session limit");
        } catch (SessionCreationService.SessionCreationException e) {
            assertTrue(e.getMessage().contains("Concurrent session limit exceeded"));
        }
    }
    
    @Test
    public void testSessionTermination() {
        Session session = sessionFactory.createSession("user123");
        sessionStore.save(session);
        
        SessionTerminationService terminationService = new SessionTerminationService(sessionStore);
        
        boolean terminated = terminationService.terminateSession(session.getSessionId());
        
        assertTrue(terminated);
        assertEquals(SessionState.TERMINATED, sessionStore.findById(session.getSessionId()).get().getState());
    }
    
    @Test
    public void testTerminateAllUserSessions() {
        Session session1 = sessionFactory.createSession("user123");
        Session session2 = sessionFactory.createSession("user123");
        Session session3 = sessionFactory.createSession("user123");
        sessionStore.save(session1);
        sessionStore.save(session2);
        sessionStore.save(session3);
        
        SessionTerminationService terminationService = new SessionTerminationService(sessionStore);
        
        int terminatedCount = terminationService.terminateAllUserSessions("user123");
        
        assertEquals(3, terminatedCount);
        
        Session[] sessions = sessionStore.findByUserId("user123");
        for (Session session : sessions) {
            assertEquals(SessionState.TERMINATED, session.getState());
        }
    }
    
    @Test
    public void testSessionDeletion() {
        Session session = sessionFactory.createSession("user123");
        sessionStore.save(session);
        
        SessionTerminationService terminationService = new SessionTerminationService(sessionStore);
        
        assertTrue(sessionStore.exists(session.getSessionId()));
        
        terminationService.deleteSession(session.getSessionId());
        
        assertFalse(sessionStore.exists(session.getSessionId()));
    }
    
    @Test
    public void testSessionRenewal() {
        Session session = sessionFactory.createSession("user123");
        sessionStore.save(session);
        
        SessionRenewalService renewalService = new SessionRenewalService(sessionStore, idGenerator);
        
        int initialAccessCount = session.getAccessCount();
        
        boolean renewed = renewalService.renewSession(session.getSessionId());
        
        assertTrue(renewed);
        assertTrue(sessionStore.findById(session.getSessionId()).get().getAccessCount() > initialAccessCount);
    }
    
    @Test
    public void testSessionIdRegeneration() {
        Session session = sessionFactory.createSession("user123");
        session.setAttribute("key1", "value1");
        session.setSecurityAttribute("role", "admin");
        sessionStore.save(session);
        
        SessionRenewalService renewalService = new SessionRenewalService(sessionStore, idGenerator);
        
        String oldSessionId = session.getSessionId();
        String newSessionId = renewalService.regenerateSessionId(oldSessionId);
        
        assertNotNull(newSessionId);
        assertNotEquals(oldSessionId, newSessionId);
        assertFalse(sessionStore.exists(oldSessionId));
        assertTrue(sessionStore.exists(newSessionId));
        
        Session newSession = sessionStore.findById(newSessionId).get();
        assertEquals("value1", newSession.getAttribute("key1"));
        assertEquals("admin", newSession.getSecurityAttribute("role"));
    }
    
    @Test
    public void testRefreshAfterReauth() {
        Session session = sessionFactory.createSession("user123");
        session.addReauthReason(gov.uspto.session.model.ReauthReason.PRIVILEGE_ESCALATION);
        sessionStore.save(session);
        
        SessionRenewalService renewalService = new SessionRenewalService(sessionStore, idGenerator);
        
        assertTrue(session.requiresReauthentication());
        
        boolean refreshed = renewalService.refreshAfterReauth(session.getSessionId());
        
        assertTrue(refreshed);
        assertFalse(sessionStore.findById(session.getSessionId()).get().requiresReauthentication());
    }
    
    @Test
    public void testConcurrentSessionManagement() {
        ConcurrentSessionManager concurrentManager = new ConcurrentSessionManager(sessionStore, 3);
        
        Session session1 = sessionFactory.createSession("user123");
        Session session2 = sessionFactory.createSession("user123");
        Session session3 = sessionFactory.createSession("user123");
        sessionStore.save(session1);
        sessionStore.save(session2);
        sessionStore.save(session3);
        
        Session[] activeSessions = concurrentManager.getActiveSessions("user123");
        assertEquals(3, activeSessions.length);
        
        int activeCount = concurrentManager.getActiveSessionCount("user123");
        assertEquals(3, activeCount);
        
        assertTrue(concurrentManager.hasReachedLimit("user123"));
    }
    
    @Test
    public void testTerminateOldestSession() {
        ConcurrentSessionManager concurrentManager = new ConcurrentSessionManager(sessionStore, 2);
        
        Session session1 = sessionFactory.createSession("user123");
        sessionStore.save(session1);
        
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        Session session2 = sessionFactory.createSession("user123");
        sessionStore.save(session2);
        
        boolean terminated = concurrentManager.terminateOldestIfLimitExceeded("user123");
        
        assertTrue(terminated);
        assertEquals(1, concurrentManager.getActiveSessionCount("user123"));
    }
    
    @Test
    public void testTerminateAllExcept() {
        ConcurrentSessionManager concurrentManager = new ConcurrentSessionManager(sessionStore, 5);
        
        Session session1 = sessionFactory.createSession("user123");
        Session session2 = sessionFactory.createSession("user123");
        Session session3 = sessionFactory.createSession("user123");
        sessionStore.save(session1);
        sessionStore.save(session2);
        sessionStore.save(session3);
        
        int terminatedCount = concurrentManager.terminateAllExcept("user123", session2.getSessionId());
        
        assertEquals(2, terminatedCount);
        assertEquals(1, concurrentManager.getActiveSessionCount("user123"));
        assertEquals(SessionState.ACTIVE, sessionStore.findById(session2.getSessionId()).get().getState());
    }
    
    @Test
    public void testGetSessionInfo() {
        ConcurrentSessionManager concurrentManager = new ConcurrentSessionManager(sessionStore, 5);
        
        Session session1 = sessionFactory.createSession("user123", "192.168.1.100", "Mozilla/5.0");
        Session session2 = sessionFactory.createSession("user123", "192.168.1.101", "Chrome/90.0");
        sessionStore.save(session1);
        sessionStore.save(session2);
        
        String[] info = concurrentManager.getSessionInfo("user123");
        
        assertEquals(2, info.length);
        assertTrue(info[0].contains("Session"));
        assertTrue(info[1].contains("Session"));
    }
}
