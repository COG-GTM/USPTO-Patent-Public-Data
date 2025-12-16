package gov.uspto.session.management;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import gov.uspto.session.model.Session;
import gov.uspto.session.model.SessionState;

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
    public void delete(String sessionId) {
        sessions.remove(sessionId);
    }

    @Override
    public boolean exists(String sessionId) {
        return sessions.containsKey(sessionId);
    }

    @Override
    public Collection<Session> findByUserId(String userId) {
        return sessions.values().stream()
                .filter(s -> userId.equals(s.getUserId()))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<Session> findAll() {
        return sessions.values();
    }

    @Override
    public void deleteByUserId(String userId) {
        sessions.entrySet().removeIf(entry -> userId.equals(entry.getValue().getUserId()));
    }

    @Override
    public void deleteExpired() {
        sessions.entrySet().removeIf(entry -> entry.getValue().getState() == SessionState.EXPIRED);
    }

    @Override
    public long count() {
        return sessions.size();
    }

    @Override
    public long countByUserId(String userId) {
        return sessions.values().stream()
                .filter(s -> userId.equals(s.getUserId()))
                .count();
    }

    @Override
    public void clear() {
        sessions.clear();
    }
}
