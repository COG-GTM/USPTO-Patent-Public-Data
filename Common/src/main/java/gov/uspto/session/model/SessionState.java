package gov.uspto.session.model;

/**
 * Session lifecycle states
 */
public enum SessionState {
    
    /**
     * Session is active and valid
     */
    ACTIVE,
    
    /**
     * Session requires re-authentication
     */
    REQUIRES_REAUTH,
    
    /**
     * Session has expired
     */
    EXPIRED,
    
    /**
     * Session has been terminated
     */
    TERMINATED,
    
    /**
     * Session is suspended (temporarily inactive)
     */
    SUSPENDED
}
