package gov.uspto.session;

import gov.uspto.session.model.ReauthReason;
import gov.uspto.session.model.Session;
import gov.uspto.session.reauth.PrivilegeChangeDetector;
import gov.uspto.session.reauth.ReauthenticationPolicy;
import gov.uspto.session.reauth.ReauthenticationTrigger;
import gov.uspto.session.reauth.SessionTimeoutManager;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Tests for re-authentication functionality
 */
public class ReauthenticationTest {
    
    @Test
    public void testReauthenticationPolicyBuilder() {
        ReauthenticationPolicy policy = new ReauthenticationPolicy.Builder()
                .reauthTimeoutSeconds(1800)
                .requireReauthOnPrivilegeEscalation(true)
                .requireReauthOnRoleChange(true)
                .requireReauthOnSecurityAttributeChange(false)
                .addOrganizationPolicy("custom_rule", "value")
                .build();
        
        assertEquals(1800, policy.getReauthTimeoutSeconds());
        assertTrue(policy.requiresReauthOnPrivilegeEscalation());
        assertTrue(policy.requiresReauthOnRoleChange());
        assertFalse(policy.requiresReauthOnSecurityAttributeChange());
        assertEquals("value", policy.getOrganizationPolicy("custom_rule"));
    }
    
    @Test
    public void testReauthenticationTrigger() {
        ReauthenticationPolicy policy = new ReauthenticationPolicy.Builder()
                .requireReauthOnPrivilegeEscalation(true)
                .build();
        
        ReauthenticationTrigger trigger = new ReauthenticationTrigger(policy);
        Session session = new Session("session123", "user456");
        
        assertFalse(session.requiresReauthentication());
        
        trigger.triggerPrivilegeEscalation(session);
        
        assertTrue(session.requiresReauthentication());
        assertTrue(session.getPendingReauthReasons().contains(ReauthReason.PRIVILEGE_ESCALATION));
    }
    
    @Test
    public void testReauthenticationTriggerRoleChange() {
        ReauthenticationPolicy policy = new ReauthenticationPolicy.Builder()
                .requireReauthOnRoleChange(true)
                .build();
        
        ReauthenticationTrigger trigger = new ReauthenticationTrigger(policy);
        Session session = new Session("session123", "user456");
        
        trigger.triggerRoleChange(session);
        
        assertTrue(session.requiresReauthentication());
        assertTrue(session.getPendingReauthReasons().contains(ReauthReason.ROLE_CHANGE));
    }
    
    @Test
    public void testReauthenticationTriggerSecurityAttributeChange() {
        ReauthenticationPolicy policy = new ReauthenticationPolicy.Builder()
                .requireReauthOnSecurityAttributeChange(true)
                .build();
        
        ReauthenticationTrigger trigger = new ReauthenticationTrigger(policy);
        Session session = new Session("session123", "user456");
        
        trigger.triggerSecurityAttributeChange(session);
        
        assertTrue(session.requiresReauthentication());
        assertTrue(session.getPendingReauthReasons().contains(ReauthReason.SECURITY_ATTRIBUTE_CHANGE));
    }
    
    @Test
    public void testPrivilegeChangeDetection() {
        ReauthenticationPolicy policy = new ReauthenticationPolicy.Builder()
                .requireReauthOnPrivilegeEscalation(true)
                .build();
        
        ReauthenticationTrigger trigger = new ReauthenticationTrigger(policy);
        PrivilegeChangeDetector detector = new PrivilegeChangeDetector(trigger);
        
        Session session = new Session("session123", "user456");
        Set<String> privileges = new HashSet<>();
        privileges.add("read");
        session.setSecurityAttribute("privileges", privileges);
        
        boolean escalated = detector.detectPrivilegeEscalation(session, "write");
        
        assertTrue(escalated);
        assertTrue(session.requiresReauthentication());
    }
    
    @Test
    public void testRoleChangeDetection() {
        ReauthenticationPolicy policy = new ReauthenticationPolicy.Builder()
                .requireReauthOnRoleChange(true)
                .build();
        
        ReauthenticationTrigger trigger = new ReauthenticationTrigger(policy);
        PrivilegeChangeDetector detector = new PrivilegeChangeDetector(trigger);
        
        Session session = new Session("session123", "user456");
        session.setSecurityAttribute("role", "user");
        
        boolean changed = detector.detectRoleChange(session, "admin");
        
        assertTrue(changed);
        assertTrue(session.requiresReauthentication());
    }
    
    @Test
    public void testSecurityAttributeChangeDetection() {
        ReauthenticationPolicy policy = new ReauthenticationPolicy.Builder()
                .requireReauthOnSecurityAttributeChange(true)
                .build();
        
        ReauthenticationTrigger trigger = new ReauthenticationTrigger(policy);
        PrivilegeChangeDetector detector = new PrivilegeChangeDetector(trigger);
        
        Session session = new Session("session123", "user456");
        session.setSecurityAttribute("clearance", "secret");
        
        boolean changed = detector.detectSecurityAttributeChange(session, "clearance", "top-secret");
        
        assertTrue(changed);
        assertTrue(session.requiresReauthentication());
    }
    
    @Test
    public void testSessionTimeoutManager() {
        SessionTimeoutManager timeoutManager = new SessionTimeoutManager(3600, 1800, 900);
        
        Session session = new Session("session123", "user456");
        
        assertFalse(timeoutManager.isSessionTimedOut(session));
        assertFalse(timeoutManager.isSessionInactive(session));
        assertFalse(timeoutManager.requiresReauthDueToTimeout(session));
    }
    
    @Test
    public void testSessionTimeoutProcessing() {
        SessionTimeoutManager timeoutManager = new SessionTimeoutManager(3600, 1800, 900);
        
        Session session = new Session("session123", "user456");
        
        boolean stateChanged = timeoutManager.processTimeouts(session);
        
        assertFalse(stateChanged);
    }
    
    @Test
    public void testRemainingTimeCalculations() {
        SessionTimeoutManager timeoutManager = new SessionTimeoutManager(3600, 1800, 900);
        
        Session session = new Session("session123", "user456");
        
        long remainingSessionTime = timeoutManager.getRemainingSessionTime(session);
        long remainingInactivityTime = timeoutManager.getRemainingInactivityTime(session);
        long remainingReauthTime = timeoutManager.getRemainingReauthTime(session);
        
        assertTrue(remainingSessionTime > 0);
        assertTrue(remainingInactivityTime > 0);
        assertTrue(remainingReauthTime > 0);
    }
}
