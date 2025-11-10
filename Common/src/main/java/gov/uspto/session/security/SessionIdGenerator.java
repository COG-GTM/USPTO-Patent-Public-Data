package gov.uspto.session.security;

import java.security.SecureRandom;
import java.util.Base64;

/**
 * Generates cryptographically secure session IDs.
 * Uses SecureRandom for high-entropy random number generation.
 */
public class SessionIdGenerator {
    
    private static final int DEFAULT_SESSION_ID_LENGTH = 32;
    private final SecureRandom secureRandom;
    private final int sessionIdLength;
    
    public SessionIdGenerator() {
        this(DEFAULT_SESSION_ID_LENGTH);
    }
    
    public SessionIdGenerator(int sessionIdLength) {
        this.secureRandom = new SecureRandom();
        this.sessionIdLength = sessionIdLength;
    }
    
    /**
     * Generate a cryptographically secure session ID
     * @return base64-encoded session ID
     */
    public String generateSessionId() {
        byte[] randomBytes = new byte[sessionIdLength];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }
    
    /**
     * Generate a session ID with custom length
     * @param length the length in bytes
     * @return base64-encoded session ID
     */
    public String generateSessionId(int length) {
        byte[] randomBytes = new byte[length];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }
}
