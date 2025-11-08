package gov.uspto.auth.session;

import gov.uspto.auth.core.Principal;
import java.time.Instant;

/**
 * Interface for managing authentication sessions.
 * 
 * This interface defines operations for creating, validating, and terminating
 * authentication sessions in compliance with NIST 800-53.
 * 
 * NIST 800-53 Control: IA-11 (Re-authentication)
 * 
 * Future implementation should include:
 * - Session creation and validation
 * - Session timeout management
 * - Session re-authentication
 * - Concurrent session limits
 * - Session termination
 */
public interface SessionManager {

    /**
     * Creates a new authentication session.
     * 
     * @param principal the authenticated principal
     * @return the session identifier
     */
    String createSession(Principal principal);

    /**
     * Validates an existing session.
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
     * Refreshes a session, extending its timeout.
     * 
     * @param sessionId the session identifier
     * @return true if refresh was successful, false otherwise
     */
    boolean refreshSession(String sessionId);

    /**
     * Terminates a session.
     * 
     * @param sessionId the session identifier
     * @return true if termination was successful, false otherwise
     */
    boolean terminateSession(String sessionId);

    /**
     * Terminates all sessions for a principal.
     * 
     * @param identifier the principal identifier
     * @return the number of sessions terminated
     */
    int terminateAllSessions(String identifier);

    /**
     * Checks if a session has expired.
     * 
     * @param sessionId the session identifier
     * @return true if the session has expired, false otherwise
     */
    boolean isSessionExpired(String sessionId);

    /**
     * Gets the expiration time for a session.
     * 
     * @param sessionId the session identifier
     * @return the expiration time, or null if session doesn't exist
     */
    Instant getSessionExpiration(String sessionId);

    /**
     * Gets the number of active sessions for a principal.
     * 
     * @param identifier the principal identifier
     * @return the number of active sessions
     */
    int getActiveSessionCount(String identifier);
}
