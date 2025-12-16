package gov.uspto.session.security;

import java.security.SecureRandom;
import java.util.Base64;

public class SessionIdGenerator {

    private static final int DEFAULT_ID_LENGTH_BYTES = 32;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final int idLengthBytes;

    public SessionIdGenerator() {
        this(DEFAULT_ID_LENGTH_BYTES);
    }

    public SessionIdGenerator(int idLengthBytes) {
        if (idLengthBytes < 16) {
            throw new IllegalArgumentException("Session ID length must be at least 16 bytes for security");
        }
        this.idLengthBytes = idLengthBytes;
    }

    public String generateSessionId() {
        byte[] randomBytes = new byte[idLengthBytes];
        SECURE_RANDOM.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    public String generateSessionId(String prefix) {
        return prefix + "_" + generateSessionId();
    }

    public static boolean isValidSessionIdFormat(String sessionId) {
        if (sessionId == null || sessionId.isEmpty()) {
            return false;
        }
        String idPart = sessionId;
        int firstUnderscoreIndex = sessionId.indexOf('_');
        if (firstUnderscoreIndex > 0 && firstUnderscoreIndex < sessionId.length() - 1) {
            idPart = sessionId.substring(firstUnderscoreIndex + 1);
        }
        try {
            byte[] decoded = Base64.getUrlDecoder().decode(idPart);
            return decoded.length >= 16;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public int getIdLengthBytes() {
        return idLengthBytes;
    }
}
