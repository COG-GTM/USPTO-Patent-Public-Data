package gov.uspto.auth.session;

import gov.uspto.auth.core.Principal;

/**
 * Interface for managing authentication sessions.
 * 
 * This interface provides methods for creating, validating, and terminating
 * authentication sessions in compliance with NIST 800-53 IA-11.
 * 
 * NIST 800-53 Controls: IA-11 (Re-authentication), AC-12 (Session Termination)
 */
public interface SessionManager {

    /**
     * Creates a new session for the authenticated principal.
     * 
     * @param principal the authenticated principal
     * @return the session identifier
     * @throws SessionException if session creation fails
     */
    String createSession(Principal principal) throws SessionException;

    /**
     * Validates a session.
     * 
     * @param sessionId the session identifier
     * @return true if the session is valid, false otherwise
     */
    boolean validateSession(String sessionId);

    /**
     * Gets the principal associated with a session.
     * 
     * @param sessionId the session identifier
     * @return the principal, or null if session is invalid
     */
    Principal getSessionPrincipal(String sessionId);

    /**
     * Terminates a session.
     * 
     * @param sessionId the session identifier
     * @throws SessionException if termination fails
     */
    void terminateSession(String sessionId) throws SessionException;

    /**
     * Checks if re-authentication is required for a session.
     * 
     * @param sessionId the session identifier
     * @return true if re-authentication is required, false otherwise
     */
    boolean requiresReauthentication(String sessionId);

    /**
     * Updates the last activity time for a session.
     * 
     * @param sessionId the session identifier
     * @throws SessionException if update fails
     */
    void updateSessionActivity(String sessionId) throws SessionException;
}
