package gov.uspto.session.model;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Core session entity with NIST 800-53 IA-11 compliance fields.
 * Represents an authenticated user session with re-authentication tracking.
 */
public class Session {
    
    private final String sessionId;
    private final String userId;
    private final Instant createdAt;
    private Instant lastAccessed;
    private Instant lastReauthentication;
    private SessionState state;
    
    private final Map<String, Object> attributes;
    private final Map<String, Object> securityAttributes;
    private final Set<ReauthReason> pendingReauthReasons;
    
    private String ipAddress;
    private String userAgent;
    private int accessCount;
    
    public Session(String sessionId, String userId) {
        this.sessionId = sessionId;
        this.userId = userId;
        this.createdAt = Instant.now();
        this.lastAccessed = Instant.now();
        this.lastReauthentication = Instant.now();
        this.state = SessionState.ACTIVE;
        this.attributes = new HashMap<>();
        this.securityAttributes = new HashMap<>();
        this.pendingReauthReasons = new HashSet<>();
        this.accessCount = 0;
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
    
    public void updateLastAccessed() {
        this.lastAccessed = Instant.now();
        this.accessCount++;
    }
    
    public Instant getLastReauthentication() {
        return lastReauthentication;
    }
    
    public void markReauthenticated() {
        this.lastReauthentication = Instant.now();
        this.pendingReauthReasons.clear();
        if (this.state == SessionState.REQUIRES_REAUTH) {
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
    
    public boolean requiresReauthentication() {
        return state == SessionState.REQUIRES_REAUTH || !pendingReauthReasons.isEmpty();
    }
    
    public Set<ReauthReason> getPendingReauthReasons() {
        return Collections.unmodifiableSet(pendingReauthReasons);
    }
    
    public void addReauthReason(ReauthReason reason) {
        this.pendingReauthReasons.add(reason);
        if (this.state == SessionState.ACTIVE) {
            this.state = SessionState.REQUIRES_REAUTH;
        }
    }
    
    public void clearReauthReasons() {
        this.pendingReauthReasons.clear();
    }
    
    public Map<String, Object> getAttributes() {
        return Collections.unmodifiableMap(attributes);
    }
    
    public Object getAttribute(String key) {
        return attributes.get(key);
    }
    
    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }
    
    public void removeAttribute(String key) {
        attributes.remove(key);
    }
    
    public Map<String, Object> getSecurityAttributes() {
        return Collections.unmodifiableMap(securityAttributes);
    }
    
    public Object getSecurityAttribute(String key) {
        return securityAttributes.get(key);
    }
    
    public void setSecurityAttribute(String key, Object value) {
        securityAttributes.put(key, value);
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
    
    public int getAccessCount() {
        return accessCount;
    }
    
    public long getSessionDurationSeconds() {
        return createdAt.getEpochSecond() - Instant.now().getEpochSecond();
    }
    
    public long getTimeSinceLastAccessSeconds() {
        return Instant.now().getEpochSecond() - lastAccessed.getEpochSecond();
    }
    
    public long getTimeSinceLastReauthSeconds() {
        return Instant.now().getEpochSecond() - lastReauthentication.getEpochSecond();
    }
    
    @Override
    public String toString() {
        return "Session{" +
                "sessionId='" + sessionId + '\'' +
                ", userId='" + userId + '\'' +
                ", state=" + state +
                ", createdAt=" + createdAt +
                ", lastAccessed=" + lastAccessed +
                ", requiresReauth=" + requiresReauthentication() +
                '}';
    }
}
