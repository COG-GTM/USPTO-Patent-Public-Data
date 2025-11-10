package gov.uspto.session;

import gov.uspto.session.model.ReauthReason;
import gov.uspto.session.model.Session;
import gov.uspto.session.model.SessionState;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for Session entity
 */
public class SessionTest {
    
    @Test
    public void testSessionCreation() {
        Session session = new Session("session123", "user456");
        
        assertNotNull(session);
        assertEquals("session123", session.getSessionId());
        assertEquals("user456", session.getUserId());
        assertEquals(SessionState.ACTIVE, session.getState());
        assertNotNull(session.getCreatedAt());
        assertNotNull(session.getLastAccessed());
        assertNotNull(session.getLastReauthentication());
    }
    
    @Test
    public void testSessionAttributes() {
        Session session = new Session("session123", "user456");
        
        session.setAttribute("key1", "value1");
        session.setAttribute("key2", 42);
        
        assertEquals("value1", session.getAttribute("key1"));
        assertEquals(42, session.getAttribute("key2"));
        assertEquals(2, session.getAttributes().size());
        
        session.removeAttribute("key1");
        assertNull(session.getAttribute("key1"));
        assertEquals(1, session.getAttributes().size());
    }
    
    @Test
    public void testSecurityAttributes() {
        Session session = new Session("session123", "user456");
        
        session.setSecurityAttribute("role", "admin");
        session.setSecurityAttribute("clearance", "secret");
        
        assertEquals("admin", session.getSecurityAttribute("role"));
        assertEquals("secret", session.getSecurityAttribute("clearance"));
        assertEquals(2, session.getSecurityAttributes().size());
    }
    
    @Test
    public void testReauthenticationTracking() {
        Session session = new Session("session123", "user456");
        
        assertFalse(session.requiresReauthentication());
        assertEquals(0, session.getPendingReauthReasons().size());
        
        session.addReauthReason(ReauthReason.PRIVILEGE_ESCALATION);
        assertTrue(session.requiresReauthentication());
        assertEquals(1, session.getPendingReauthReasons().size());
        assertTrue(session.getPendingReauthReasons().contains(ReauthReason.PRIVILEGE_ESCALATION));
        assertEquals(SessionState.REQUIRES_REAUTH, session.getState());
        
        session.addReauthReason(ReauthReason.ROLE_CHANGE);
        assertEquals(2, session.getPendingReauthReasons().size());
        
        session.markReauthenticated();
        assertFalse(session.requiresReauthentication());
        assertEquals(0, session.getPendingReauthReasons().size());
        assertEquals(SessionState.ACTIVE, session.getState());
    }
    
    @Test
    public void testSessionState() {
        Session session = new Session("session123", "user456");
        
        assertTrue(session.isActive());
        assertEquals(SessionState.ACTIVE, session.getState());
        
        session.setState(SessionState.EXPIRED);
        assertFalse(session.isActive());
        assertEquals(SessionState.EXPIRED, session.getState());
        
        session.setState(SessionState.TERMINATED);
        assertFalse(session.isActive());
        assertEquals(SessionState.TERMINATED, session.getState());
    }
    
    @Test
    public void testLastAccessedUpdate() {
        Session session = new Session("session123", "user456");
        
        assertEquals(0, session.getAccessCount());
        
        session.updateLastAccessed();
        assertEquals(1, session.getAccessCount());
        
        session.updateLastAccessed();
        assertEquals(2, session.getAccessCount());
    }
    
    @Test
    public void testSecurityContext() {
        Session session = new Session("session123", "user456");
        
        session.setIpAddress("192.168.1.100");
        session.setUserAgent("Mozilla/5.0");
        
        assertEquals("192.168.1.100", session.getIpAddress());
        assertEquals("Mozilla/5.0", session.getUserAgent());
    }
}
