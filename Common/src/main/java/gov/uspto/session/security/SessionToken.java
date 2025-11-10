package gov.uspto.session.security;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Secure session token representation.
 * Designed for both server-side sessions and potential JWT integration.
 */
public class SessionToken {
    
    private final String tokenId;
    private final String sessionId;
    private final String userId;
    private final Instant issuedAt;
    private final Instant expiresAt;
    private final Map<String, Object> claims;
    
    private SessionToken(Builder builder) {
        this.tokenId = builder.tokenId;
        this.sessionId = builder.sessionId;
        this.userId = builder.userId;
        this.issuedAt = builder.issuedAt;
        this.expiresAt = builder.expiresAt;
        this.claims = builder.claims;
    }
    
    public String getTokenId() {
        return tokenId;
    }
    
    public String getSessionId() {
        return sessionId;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public Instant getIssuedAt() {
        return issuedAt;
    }
    
    public Instant getExpiresAt() {
        return expiresAt;
    }
    
    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }
    
    public Map<String, Object> getClaims() {
        return new HashMap<>(claims);
    }
    
    public Object getClaim(String key) {
        return claims.get(key);
    }
    
    @Override
    public String toString() {
        return "SessionToken{" +
                "tokenId='" + tokenId + '\'' +
                ", sessionId='" + sessionId + '\'' +
                ", userId='" + userId + '\'' +
                ", issuedAt=" + issuedAt +
                ", expiresAt=" + expiresAt +
                ", expired=" + isExpired() +
                '}';
    }
    
    /**
     * Builder for SessionToken
     */
    public static class Builder {
        private String tokenId;
        private String sessionId;
        private String userId;
        private Instant issuedAt = Instant.now();
        private Instant expiresAt;
        private Map<String, Object> claims = new HashMap<>();
        
        public Builder tokenId(String tokenId) {
            this.tokenId = tokenId;
            return this;
        }
        
        public Builder sessionId(String sessionId) {
            this.sessionId = sessionId;
            return this;
        }
        
        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }
        
        public Builder issuedAt(Instant issuedAt) {
            this.issuedAt = issuedAt;
            return this;
        }
        
        public Builder expiresAt(Instant expiresAt) {
            this.expiresAt = expiresAt;
            return this;
        }
        
        public Builder expiresInSeconds(long seconds) {
            this.expiresAt = Instant.now().plusSeconds(seconds);
            return this;
        }
        
        public Builder addClaim(String key, Object value) {
            this.claims.put(key, value);
            return this;
        }
        
        public SessionToken build() {
            if (tokenId == null || sessionId == null || userId == null) {
                throw new IllegalStateException("tokenId, sessionId, and userId are required");
            }
            if (expiresAt == null) {
                expiresAt = Instant.now().plusSeconds(3600);
            }
            return new SessionToken(this);
        }
    }
}
