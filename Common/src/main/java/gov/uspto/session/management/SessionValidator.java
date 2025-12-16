package gov.uspto.session.management;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.uspto.session.model.ReauthReason;
import gov.uspto.session.model.Session;
import gov.uspto.session.model.SessionState;

public class SessionValidator {

    private static final Logger LOG = LoggerFactory.getLogger(SessionValidator.class);

    private final SessionValidationConfig config;

    public SessionValidator() {
        this(new SessionValidationConfig());
    }

    public SessionValidator(SessionValidationConfig config) {
        this.config = config;
    }

    public ValidationResult validate(Session session) {
        List<String> errors = new ArrayList<>();
        List<ReauthReason> reauthReasons = new ArrayList<>();

        if (session == null) {
            return new ValidationResult(false, Collections.singletonList("Session is null"), Collections.emptyList());
        }

        if (session.getState() == SessionState.INVALIDATED) {
            errors.add("Session has been invalidated");
        }

        if (session.getState() == SessionState.EXPIRED) {
            errors.add("Session has expired");
        }

        if (session.getState() == SessionState.LOCKED) {
            errors.add("Session is locked");
        }

        if (isAbsoluteTimeoutExceeded(session)) {
            errors.add("Session absolute timeout exceeded");
            reauthReasons.add(ReauthReason.SESSION_TIMEOUT);
        }

        if (isIdleTimeoutExceeded(session)) {
            errors.add("Session idle timeout exceeded");
            reauthReasons.add(ReauthReason.IDLE_TIMEOUT);
        }

        if (isReauthTimeoutExceeded(session)) {
            reauthReasons.add(ReauthReason.SESSION_TIMEOUT);
        }

        if (session.hasReauthPending()) {
            reauthReasons.addAll(session.getPendingReauthReasons());
        }

        boolean valid = errors.isEmpty();
        return new ValidationResult(valid, errors, reauthReasons);
    }

    public boolean isValid(Session session) {
        return validate(session).isValid();
    }

    public boolean isAbsoluteTimeoutExceeded(Session session) {
        if (config.getAbsoluteTimeoutSeconds() <= 0) {
            return false;
        }
        Duration sessionDuration = Duration.between(session.getCreatedAt(), Instant.now());
        return sessionDuration.getSeconds() > config.getAbsoluteTimeoutSeconds();
    }

    public boolean isIdleTimeoutExceeded(Session session) {
        if (config.getIdleTimeoutSeconds() <= 0) {
            return false;
        }
        Duration idleDuration = Duration.between(session.getLastAccessed(), Instant.now());
        return idleDuration.getSeconds() > config.getIdleTimeoutSeconds();
    }

    public boolean isReauthTimeoutExceeded(Session session) {
        if (config.getReauthTimeoutSeconds() <= 0) {
            return false;
        }
        Duration reauthDuration = Duration.between(session.getLastReauthentication(), Instant.now());
        return reauthDuration.getSeconds() > config.getReauthTimeoutSeconds();
    }

    public SessionValidationConfig getConfig() {
        return config;
    }

    public static class SessionValidationConfig {
        private long absoluteTimeoutSeconds = 28800;
        private long idleTimeoutSeconds = 1800;
        private long reauthTimeoutSeconds = 3600;

        public long getAbsoluteTimeoutSeconds() {
            return absoluteTimeoutSeconds;
        }

        public void setAbsoluteTimeoutSeconds(long absoluteTimeoutSeconds) {
            this.absoluteTimeoutSeconds = absoluteTimeoutSeconds;
        }

        public long getIdleTimeoutSeconds() {
            return idleTimeoutSeconds;
        }

        public void setIdleTimeoutSeconds(long idleTimeoutSeconds) {
            this.idleTimeoutSeconds = idleTimeoutSeconds;
        }

        public long getReauthTimeoutSeconds() {
            return reauthTimeoutSeconds;
        }

        public void setReauthTimeoutSeconds(long reauthTimeoutSeconds) {
            this.reauthTimeoutSeconds = reauthTimeoutSeconds;
        }
    }

    public static class ValidationResult {
        private final boolean valid;
        private final List<String> errors;
        private final List<ReauthReason> reauthReasons;

        public ValidationResult(boolean valid, List<String> errors, List<ReauthReason> reauthReasons) {
            this.valid = valid;
            this.errors = errors;
            this.reauthReasons = reauthReasons;
        }

        public boolean isValid() {
            return valid;
        }

        public List<String> getErrors() {
            return errors;
        }

        public List<ReauthReason> getReauthReasons() {
            return reauthReasons;
        }

        public boolean requiresReauthentication() {
            return !reauthReasons.isEmpty();
        }
    }
}
