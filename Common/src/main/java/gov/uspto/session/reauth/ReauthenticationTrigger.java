package gov.uspto.session.reauth;

import gov.uspto.session.model.ReauthReason;
import gov.uspto.session.model.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Triggers re-authentication events based on various conditions.
 * Monitors session activity and enforces NIST 800-53 IA-11 requirements.
 */
public class ReauthenticationTrigger {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ReauthenticationTrigger.class);
    
    private final ReauthenticationPolicy policy;
    
    public ReauthenticationTrigger(ReauthenticationPolicy policy) {
        this.policy = policy;
    }
    
    /**
     * Check if session requires re-authentication and trigger if needed
     * @param session the session to check
     * @return true if re-authentication was triggered
     */
    public boolean checkAndTrigger(Session session) {
        if (session.requiresReauthentication()) {
            return true;
        }
        
        long timeSinceReauth = session.getTimeSinceLastReauthSeconds();
        if (timeSinceReauth > policy.getReauthTimeoutSeconds()) {
            triggerReauth(session, ReauthReason.SESSION_TIMEOUT);
            return true;
        }
        
        return false;
    }
    
    /**
     * Trigger re-authentication for privilege escalation
     * @param session the session
     */
    public void triggerPrivilegeEscalation(Session session) {
        if (policy.requiresReauthOnPrivilegeEscalation()) {
            triggerReauth(session, ReauthReason.PRIVILEGE_ESCALATION);
        }
    }
    
    /**
     * Trigger re-authentication for role change
     * @param session the session
     */
    public void triggerRoleChange(Session session) {
        if (policy.requiresReauthOnRoleChange()) {
            triggerReauth(session, ReauthReason.ROLE_CHANGE);
        }
    }
    
    /**
     * Trigger re-authentication for security attribute change
     * @param session the session
     */
    public void triggerSecurityAttributeChange(Session session) {
        if (policy.requiresReauthOnSecurityAttributeChange()) {
            triggerReauth(session, ReauthReason.SECURITY_ATTRIBUTE_CHANGE);
        }
    }
    
    /**
     * Trigger re-authentication for suspicious activity
     * @param session the session
     */
    public void triggerSuspiciousActivity(Session session) {
        triggerReauth(session, ReauthReason.SUSPICIOUS_ACTIVITY);
    }
    
    /**
     * Trigger re-authentication for organization-defined reason
     * @param session the session
     */
    public void triggerOrganizationDefined(Session session) {
        triggerReauth(session, ReauthReason.ORGANIZATION_DEFINED);
    }
    
    /**
     * Internal method to trigger re-authentication
     * @param session the session
     * @param reason the reason for re-authentication
     */
    private void triggerReauth(Session session, ReauthReason reason) {
        session.addReauthReason(reason);
        LOGGER.info("Triggered re-authentication for session {} due to {}", 
                   session.getSessionId(), reason);
    }
}
