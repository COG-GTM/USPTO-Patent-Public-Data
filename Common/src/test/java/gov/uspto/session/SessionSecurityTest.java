package gov.uspto.session;

import gov.uspto.session.model.Session;
import gov.uspto.session.security.SessionEncryption;
import gov.uspto.session.security.SessionHijackingPrevention;
import gov.uspto.session.security.SessionIdGenerator;
import gov.uspto.session.security.SessionToken;
import org.junit.Test;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Security-specific tests for session management
 */
public class SessionSecurityTest {
    
    @Test
    public void testSessionIdGeneration() {
        SessionIdGenerator generator = new SessionIdGenerator();
        
        String id1 = generator.generateSessionId();
        String id2 = generator.generateSessionId();
        
        assertNotNull(id1);
        assertNotNull(id2);
        assertNotEquals(id1, id2);
        assertTrue(id1.length() > 20);
    }
    
    @Test
    public void testSessionIdUniqueness() {
        SessionIdGenerator generator = new SessionIdGenerator();
        Set<String> ids = new HashSet<>();
        
        for (int i = 0; i < 1000; i++) {
            String id = generator.generateSessionId();
            assertFalse("Duplicate session ID generated", ids.contains(id));
            ids.add(id);
        }
    }
    
    @Test
    public void testSessionTokenCreation() {
        SessionToken token = new SessionToken.Builder()
                .tokenId("token123")
                .sessionId("session456")
                .userId("user789")
                .expiresInSeconds(3600)
                .addClaim("role", "admin")
                .build();
        
        assertNotNull(token);
        assertEquals("token123", token.getTokenId());
        assertEquals("session456", token.getSessionId());
        assertEquals("user789", token.getUserId());
        assertNotNull(token.getIssuedAt());
        assertNotNull(token.getExpiresAt());
        assertFalse(token.isExpired());
        assertEquals("admin", token.getClaim("role"));
    }
    
    @Test
    public void testSessionTokenExpiration() {
        SessionToken token = new SessionToken.Builder()
                .tokenId("token123")
                .sessionId("session456")
                .userId("user789")
                .expiresAt(Instant.now().minusSeconds(10))
                .build();
        
        assertTrue(token.isExpired());
    }
    
    @Test
    public void testSessionEncryption() throws Exception {
        SessionEncryption encryption = SessionEncryption.withRandomKey();
        
        String plaintext = "sensitive session data";
        String encrypted = encryption.encrypt(plaintext);
        
        assertNotNull(encrypted);
        assertNotEquals(plaintext, encrypted);
        
        String decrypted = encryption.decrypt(encrypted);
        assertEquals(plaintext, decrypted);
    }
    
    @Test
    public void testSessionEncryptionWithKey() throws Exception {
        SessionEncryption encryption1 = SessionEncryption.withRandomKey();
        String key = encryption1.getKeyAsBase64();
        
        SessionEncryption encryption2 = SessionEncryption.fromBase64Key(key);
        
        String plaintext = "test data";
        String encrypted = encryption1.encrypt(plaintext);
        String decrypted = encryption2.decrypt(encrypted);
        
        assertEquals(plaintext, decrypted);
    }
    
    @Test
    public void testSessionBindingValidation() {
        SessionHijackingPrevention prevention = new SessionHijackingPrevention(true, true, 5);
        
        Session session = new Session("session123", "user456");
        session.setIpAddress("192.168.1.100");
        session.setUserAgent("Mozilla/5.0");
        
        assertTrue(prevention.validateSessionBinding(session, "192.168.1.100", "Mozilla/5.0"));
        assertFalse(prevention.validateSessionBinding(session, "192.168.1.200", "Mozilla/5.0"));
        assertFalse(prevention.validateSessionBinding(session, "192.168.1.100", "Chrome/90.0"));
    }
    
    @Test
    public void testSessionBindingWithoutEnforcement() {
        SessionHijackingPrevention prevention = new SessionHijackingPrevention(false, false, 5);
        
        Session session = new Session("session123", "user456");
        session.setIpAddress("192.168.1.100");
        session.setUserAgent("Mozilla/5.0");
        
        assertTrue(prevention.validateSessionBinding(session, "192.168.1.200", "Chrome/90.0"));
    }
    
    @Test
    public void testSuspiciousActivityDetection() {
        SessionHijackingPrevention prevention = new SessionHijackingPrevention(true, true, 5);
        
        Session session = new Session("session123", "user456");
        session.setIpAddress("192.168.1.100");
        
        assertFalse(prevention.detectSuspiciousActivity(session, "192.168.1.101"));
        assertTrue(prevention.detectSuspiciousActivity(session, "10.0.0.1"));
    }
    
    @Test
    public void testConcurrentSessionLimit() {
        SessionHijackingPrevention prevention = new SessionHijackingPrevention(true, true, 3);
        
        assertFalse(prevention.isConcurrentSessionLimitExceeded(2));
        assertTrue(prevention.isConcurrentSessionLimitExceeded(3));
        assertTrue(prevention.isConcurrentSessionLimitExceeded(5));
    }
    
    @Test
    public void testSessionIdRegeneration() {
        SessionHijackingPrevention prevention = new SessionHijackingPrevention(true, true, 5);
        SessionIdGenerator generator = new SessionIdGenerator();
        
        String oldId = "oldSession123";
        String newId = prevention.regenerateSessionId(oldId, generator);
        
        assertNotNull(newId);
        assertNotEquals(oldId, newId);
    }
    
    @Test
    public void testSessionFixationDetection() {
        SessionHijackingPrevention prevention = new SessionHijackingPrevention(true, true, 5);
        
        Session session = new Session("session123", "user456");
        
        assertFalse(prevention.detectSessionFixation(session));
    }
}
