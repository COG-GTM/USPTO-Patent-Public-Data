package gov.uspto.session.reauth;

import gov.uspto.session.model.ReauthReason;
import gov.uspto.session.model.Session;

import java.util.HashMap;
import java.util.Map;

/**
 * NIST 800-53 IA-11 compliant re-authentication policy.
 * Defines when re-authentication is required based on configurable rules.
 */
public class ReauthenticationPolicy {
    
    private final long reauthTimeoutSeconds;
    private final boolean requireReauthOnPrivilegeEscalation;
    private final boolean requireReauthOnRoleChange;
    private final boolean requireReauthOnSecurityAttributeChange;
    private final Map<String, Object> organizationDefinedPolicies;
    
    private ReauthenticationPolicy(Builder builder) {
        this.reauthTimeoutSeconds = builder.reauthTimeoutSeconds;
        this.requireReauthOnPrivilegeEscalation = builder.requireReauthOnPrivilegeEscalation;
        this.requireReauthOnRoleChange = builder.requireReauthOnRoleChange;
        this.requireReauthOnSecurityAttributeChange = builder.requireReauthOnSecurityAttributeChange;
        this.organizationDefinedPolicies = builder.organizationDefinedPolicies;
    }
    
    /**
     * Check if session requires re-authentication based on policy
     * @param session the session to check
     * @return true if re-authentication is required
     */
    public boolean requiresReauthentication(Session session) {
        if (session.requiresReauthentication()) {
            return true;
        }
        
        long timeSinceReauth = session.getTimeSinceLastReauthSeconds();
        if (timeSinceReauth > reauthTimeoutSeconds) {
            return true;
        }
        
        return false;
    }
    
    /**
     * Check if privilege escalation requires re-authentication
     * @return true if policy requires re-auth on privilege escalation
     */
    public boolean requiresReauthOnPrivilegeEscalation() {
        return requireReauthOnPrivilegeEscalation;
    }
    
    /**
     * Check if role change requires re-authentication
     * @return true if policy requires re-auth on role change
     */
    public boolean requiresReauthOnRoleChange() {
        return requireReauthOnRoleChange;
    }
    
    /**
     * Check if security attribute change requires re-authentication
     * @return true if policy requires re-auth on security attribute change
     */
    public boolean requiresReauthOnSecurityAttributeChange() {
        return requireReauthOnSecurityAttributeChange;
    }
    
    /**
     * Get re-authentication timeout in seconds
     * @return timeout in seconds
     */
    public long getReauthTimeoutSeconds() {
        return reauthTimeoutSeconds;
    }
    
    /**
     * Get organization-defined policy value
     * @param key policy key
     * @return policy value
     */
    public Object getOrganizationPolicy(String key) {
        return organizationDefinedPolicies.get(key);
    }
    
    /**
     * Builder for ReauthenticationPolicy
     */
    public static class Builder {
        private long reauthTimeoutSeconds = 3600;
        private boolean requireReauthOnPrivilegeEscalation = true;
        private boolean requireReauthOnRoleChange = true;
        private boolean requireReauthOnSecurityAttributeChange = true;
        private Map<String, Object> organizationDefinedPolicies = new HashMap<>();
        
        public Builder reauthTimeoutSeconds(long seconds) {
            this.reauthTimeoutSeconds = seconds;
            return this;
        }
        
        public Builder requireReauthOnPrivilegeEscalation(boolean require) {
            this.requireReauthOnPrivilegeEscalation = require;
            return this;
        }
        
        public Builder requireReauthOnRoleChange(boolean require) {
            this.requireReauthOnRoleChange = require;
            return this;
        }
        
        public Builder requireReauthOnSecurityAttributeChange(boolean require) {
            this.requireReauthOnSecurityAttributeChange = require;
            return this;
        }
        
        public Builder addOrganizationPolicy(String key, Object value) {
            this.organizationDefinedPolicies.put(key, value);
            return this;
        }
        
        public ReauthenticationPolicy build() {
            return new ReauthenticationPolicy(this);
        }
    }
}
