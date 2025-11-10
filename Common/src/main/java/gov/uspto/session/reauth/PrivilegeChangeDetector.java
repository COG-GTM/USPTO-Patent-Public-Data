package gov.uspto.session.reauth;

import gov.uspto.session.model.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * Detects privilege and role changes that require re-authentication.
 * Placeholder for Part 1.2 (Authenticator Management) integration.
 */
public class PrivilegeChangeDetector {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(PrivilegeChangeDetector.class);
    
    private final ReauthenticationTrigger reauthTrigger;
    
    public PrivilegeChangeDetector(ReauthenticationTrigger reauthTrigger) {
        this.reauthTrigger = reauthTrigger;
    }
    
    /**
     * Check if user is attempting privilege escalation
     * @param session the session
     * @param requestedPrivilege the privilege being requested
     * @return true if privilege escalation detected
     */
    public boolean detectPrivilegeEscalation(Session session, String requestedPrivilege) {
        Set<String> currentPrivileges = getCurrentPrivileges(session);
        
        if (!currentPrivileges.contains(requestedPrivilege)) {
            LOGGER.info("Privilege escalation detected for session {}: requesting {}", 
                       session.getSessionId(), requestedPrivilege);
            reauthTrigger.triggerPrivilegeEscalation(session);
            return true;
        }
        
        return false;
    }
    
    /**
     * Check if user's role has changed
     * @param session the session
     * @param newRole the new role
     * @return true if role change detected
     */
    public boolean detectRoleChange(Session session, String newRole) {
        String currentRole = getCurrentRole(session);
        
        if (currentRole != null && !currentRole.equals(newRole)) {
            LOGGER.info("Role change detected for session {}: {} -> {}", 
                       session.getSessionId(), currentRole, newRole);
            reauthTrigger.triggerRoleChange(session);
            return true;
        }
        
        return false;
    }
    
    /**
     * Check if security attributes have changed
     * @param session the session
     * @param attributeKey the security attribute key
     * @param newValue the new value
     * @return true if security attribute change detected
     */
    public boolean detectSecurityAttributeChange(Session session, String attributeKey, Object newValue) {
        Object currentValue = session.getSecurityAttribute(attributeKey);
        
        if (currentValue != null && !currentValue.equals(newValue)) {
            LOGGER.info("Security attribute change detected for session {}: {} changed", 
                       session.getSessionId(), attributeKey);
            reauthTrigger.triggerSecurityAttributeChange(session);
            return true;
        }
        
        return false;
    }
    
    /**
     * Get current privileges for session
     * Placeholder for Part 1.2 integration
     * @param session the session
     * @return set of current privileges
     */
    private Set<String> getCurrentPrivileges(Session session) {
        Object privileges = session.getSecurityAttribute("privileges");
        if (privileges instanceof Set) {
            return (Set<String>) privileges;
        }
        return new HashSet<>();
    }
    
    /**
     * Get current role for session
     * Placeholder for Part 1.2 integration
     * @param session the session
     * @return current role
     */
    private String getCurrentRole(Session session) {
        Object role = session.getSecurityAttribute("role");
        return role != null ? role.toString() : null;
    }
}
