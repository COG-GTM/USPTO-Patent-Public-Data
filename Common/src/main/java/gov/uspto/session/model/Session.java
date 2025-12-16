package gov.uspto.session.model;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class Session {

    private final String sessionId;
    private final String userId;
    private final Instant createdAt;
    private Instant lastAccessed;
    private Instant lastReauthentication;
    private SessionState state;
    private final Map<String, Object> attributes;
    private final Set<ReauthReason> pendingReauthReasons;
    private final Map<String, Object> securityAttributes;
    private String ipAddress;
    private String userAgent;
    private int reauthenticationCount;

    public Session(String sessionId, String userId) {
        this(sessionId, userId, Instant.now());
    }

    public Session(String sessionId, String userId, Instant createdAt) {
        this.sessionId = Objects.requireNonNull(sessionId, "sessionId cannot be null");
        this.userId = userId;
        this.createdAt = createdAt;
        this.lastAccessed = createdAt;
        this.lastReauthentication = createdAt;
        this.state = SessionState.ACTIVE;
        this.attributes = new HashMap<>();
        this.pendingReauthReasons = new HashSet<>();
        this.securityAttributes = new HashMap<>();
        this.reauthenticationCount = 0;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getUserId() {
        return userId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getLastAccessed() {
        return lastAccessed;
    }

    public void setLastAccessed(Instant lastAccessed) {
        this.lastAccessed = lastAccessed;
    }

    public void touch() {
        this.lastAccessed = Instant.now();
    }

    public Instant getLastReauthentication() {
        return lastReauthentication;
    }

    public void setLastReauthentication(Instant lastReauthentication) {
        this.lastReauthentication = lastReauthentication;
    }

    public void recordReauthentication() {
        this.lastReauthentication = Instant.now();
        this.reauthenticationCount++;
        this.pendingReauthReasons.clear();
        if (this.state == SessionState.PENDING_REAUTH) {
            this.state = SessionState.ACTIVE;
        }
    }

    public SessionState getState() {
        return state;
    }

    public void setState(SessionState state) {
        this.state = state;
    }

    public boolean isActive() {
        return state == SessionState.ACTIVE;
    }

    public boolean isPendingReauth() {
        return state == SessionState.PENDING_REAUTH;
    }

    public boolean isExpired() {
        return state == SessionState.EXPIRED;
    }

    public boolean isInvalidated() {
        return state == SessionState.INVALIDATED;
    }

    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    public void setAttribute(String name, Object value) {
        attributes.put(name, value);
    }

    public void removeAttribute(String name) {
        attributes.remove(name);
    }

    public Map<String, Object> getAttributes() {
        return Collections.unmodifiableMap(attributes);
    }

    public Set<ReauthReason> getPendingReauthReasons() {
        return Collections.unmodifiableSet(pendingReauthReasons);
    }

    public void addPendingReauthReason(ReauthReason reason) {
        this.pendingReauthReasons.add(reason);
        if (this.state == SessionState.ACTIVE) {
            this.state = SessionState.PENDING_REAUTH;
        }
    }

    public void clearPendingReauthReasons() {
        this.pendingReauthReasons.clear();
    }

    public boolean hasReauthPending() {
        return !pendingReauthReasons.isEmpty();
    }

    public Object getSecurityAttribute(String name) {
        return securityAttributes.get(name);
    }

    public void setSecurityAttribute(String name, Object value) {
        securityAttributes.put(name, value);
    }

    public void removeSecurityAttribute(String name) {
        securityAttributes.remove(name);
    }

    public Map<String, Object> getSecurityAttributes() {
        return Collections.unmodifiableMap(securityAttributes);
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public int getReauthenticationCount() {
        return reauthenticationCount;
    }

    public long getSessionDurationSeconds() {
        return java.time.Duration.between(createdAt, Instant.now()).getSeconds();
    }

    public long getIdleTimeSeconds() {
        return java.time.Duration.between(lastAccessed, Instant.now()).getSeconds();
    }

    public long getTimeSinceLastReauthSeconds() {
        return java.time.Duration.between(lastReauthentication, Instant.now()).getSeconds();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Session session = (Session) o;
        return Objects.equals(sessionId, session.sessionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sessionId);
    }

    @Override
    public String toString() {
        return "Session{" +
                "sessionId='" + sessionId + '\'' +
                ", userId='" + userId + '\'' +
                ", state=" + state +
                ", createdAt=" + createdAt +
                ", lastAccessed=" + lastAccessed +
                '}';
    }
}
