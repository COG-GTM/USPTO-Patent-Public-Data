package gov.uspto.session.security;

import java.time.Instant;
import java.util.Objects;

public class SessionToken {

    private final String tokenValue;
    private final String sessionId;
    private final Instant issuedAt;
    private final Instant expiresAt;
    private final TokenType tokenType;
    private boolean revoked;

    public enum TokenType {
        SESSION,
        REFRESH,
        ACCESS,
        REAUTH
    }

    public SessionToken(String tokenValue, String sessionId, Instant expiresAt) {
        this(tokenValue, sessionId, Instant.now(), expiresAt, TokenType.SESSION);
    }

    public SessionToken(String tokenValue, String sessionId, Instant issuedAt, Instant expiresAt, TokenType tokenType) {
        this.tokenValue = Objects.requireNonNull(tokenValue, "tokenValue cannot be null");
        this.sessionId = Objects.requireNonNull(sessionId, "sessionId cannot be null");
        this.issuedAt = issuedAt;
        this.expiresAt = expiresAt;
        this.tokenType = tokenType;
        this.revoked = false;
    }

    public String getTokenValue() {
        return tokenValue;
    }

    public String getSessionId() {
        return sessionId;
    }

    public Instant getIssuedAt() {
        return issuedAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public TokenType getTokenType() {
        return tokenType;
    }

    public boolean isRevoked() {
        return revoked;
    }

    public void revoke() {
        this.revoked = true;
    }

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    public boolean isValid() {
        return !revoked && !isExpired();
    }

    public long getRemainingValiditySeconds() {
        if (isExpired()) {
            return 0;
        }
        return java.time.Duration.between(Instant.now(), expiresAt).getSeconds();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SessionToken that = (SessionToken) o;
        return Objects.equals(tokenValue, that.tokenValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tokenValue);
    }

    @Override
    public String toString() {
        return "SessionToken{" +
                "sessionId='" + sessionId + '\'' +
                ", tokenType=" + tokenType +
                ", issuedAt=" + issuedAt +
                ", expiresAt=" + expiresAt +
                ", revoked=" + revoked +
                '}';
    }

    public static class Builder {
        private String tokenValue;
        private String sessionId;
        private Instant issuedAt = Instant.now();
        private Instant expiresAt;
        private TokenType tokenType = TokenType.SESSION;

        public Builder tokenValue(String tokenValue) {
            this.tokenValue = tokenValue;
            return this;
        }

        public Builder sessionId(String sessionId) {
            this.sessionId = sessionId;
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

        public Builder tokenType(TokenType tokenType) {
            this.tokenType = tokenType;
            return this;
        }

        public Builder validForSeconds(long seconds) {
            this.expiresAt = this.issuedAt.plusSeconds(seconds);
            return this;
        }

        public SessionToken build() {
            if (expiresAt == null) {
                expiresAt = issuedAt.plusSeconds(3600);
            }
            return new SessionToken(tokenValue, sessionId, issuedAt, expiresAt, tokenType);
        }
    }
}
