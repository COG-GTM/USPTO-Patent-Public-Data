package gov.uspto.session.management;

import java.util.Collection;
import java.util.Optional;

import gov.uspto.session.model.Session;

public interface SessionStore {

    void save(Session session);

    Optional<Session> findById(String sessionId);

    void delete(String sessionId);

    boolean exists(String sessionId);

    Collection<Session> findByUserId(String userId);

    Collection<Session> findAll();

    void deleteByUserId(String userId);

    void deleteExpired();

    long count();

    long countByUserId(String userId);

    void clear();
}
