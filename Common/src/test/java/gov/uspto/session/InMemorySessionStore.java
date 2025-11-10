package gov.uspto.session;

import gov.uspto.session.management.SessionStore;
import gov.uspto.session.model.Session;
import gov.uspto.session.model.SessionState;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory implementation of SessionStore for testing.
 */
public class InMemorySessionStore implements SessionStore {
    
    private final Map<String, Session> sessions = new ConcurrentHashMap<>();
    
    @Override
    public void save(Session session) {
        sessions.put(session.getSessionId(), session);
    }
    
    @Override
    public Optional<Session> findById(String sessionId) {
        return Optional.ofNullable(sessions.get(sessionId));
    }
    
    @Override
    public Session[] findByUserId(String userId) {
        List<Session> userSessions = new ArrayList<>();
        for (Session session : sessions.values()) {
            if (session.getUserId().equals(userId)) {
                userSessions.add(session);
            }
        }
        return userSessions.toArray(new Session[0]);
    }
    
    @Override
    public void delete(String sessionId) {
        sessions.remove(sessionId);
    }
    
    @Override
    public void deleteByUserId(String userId) {
        sessions.values().removeIf(session -> session.getUserId().equals(userId));
    }
    
    @Override
    public boolean exists(String sessionId) {
        return sessions.containsKey(sessionId);
    }
    
    @Override
    public int countActiveSessionsForUser(String userId) {
        int count = 0;
        for (Session session : sessions.values()) {
            if (session.getUserId().equals(userId) && 
                (session.getState() == SessionState.ACTIVE || 
                 session.getState() == SessionState.REQUIRES_REAUTH)) {
                count++;
            }
        }
        return count;
    }
    
    public void clear() {
        sessions.clear();
    }
}
