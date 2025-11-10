package gov.uspto.session.model;

/**
 * NIST 800-53 IA-11 compliant re-authentication reasons.
 * Defines circumstances requiring user re-authentication.
 */
public enum ReauthReason {
    
    /**
     * Session timeout - time-based expiration
     */
    SESSION_TIMEOUT,
    
    /**
     * Privilege escalation - user attempting to access higher privilege resources
     */
    PRIVILEGE_ESCALATION,
    
    /**
     * Role change - user's role or permissions have changed
     */
    ROLE_CHANGE,
    
    /**
     * Security attribute change - security-relevant attributes modified
     */
    SECURITY_ATTRIBUTE_CHANGE,
    
    /**
     * Organization-defined circumstance - configurable policy trigger
     */
    ORGANIZATION_DEFINED,
    
    /**
     * Suspicious activity detected
     */
    SUSPICIOUS_ACTIVITY,
    
    /**
     * Manual re-authentication request
     */
    MANUAL_REQUEST,
    
    /**
     * Session renewal required
     */
    SESSION_RENEWAL
}
