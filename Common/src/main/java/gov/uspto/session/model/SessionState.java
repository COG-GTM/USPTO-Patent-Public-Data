package gov.uspto.session.model;

public enum SessionState {
    ACTIVE,
    EXPIRED,
    INVALIDATED,
    PENDING_REAUTH,
    LOCKED
}
