package gov.uspto.session.reauth;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.uspto.session.model.ReauthReason;
import gov.uspto.session.model.Session;

public class ReauthenticationTrigger {

    private static final Logger LOG = LoggerFactory.getLogger(ReauthenticationTrigger.class);

    private final ReauthenticationPolicy policy;
    private final List<ReauthenticationListener> listeners;

    public ReauthenticationTrigger(ReauthenticationPolicy policy) {
        this.policy = Objects.requireNonNull(policy, "policy cannot be null");
        this.listeners = new ArrayList<>();
    }

    public TriggerResult evaluateSession(Session session) {
        List<ReauthReason> triggeredReasons = new ArrayList<>();

        if (isSessionTimeoutTriggered(session)) {
            triggeredReasons.add(ReauthReason.SESSION_TIMEOUT);
        }

        if (isIdleTimeoutTriggered(session)) {
            triggeredReasons.add(ReauthReason.IDLE_TIMEOUT);
        }

        if (isReauthIntervalTriggered(session)) {
            triggeredReasons.add(ReauthReason.SESSION_TIMEOUT);
        }

        triggeredReasons.addAll(session.getPendingReauthReasons());

        boolean triggered = !triggeredReasons.isEmpty();
        return new TriggerResult(triggered, triggeredReasons);
    }

    public boolean isSessionTimeoutTriggered(Session session) {
        long sessionDuration = session.getSessionDurationSeconds();
        return sessionDuration > policy.getSessionTimeoutSeconds();
    }

    public boolean isIdleTimeoutTriggered(Session session) {
        long idleTime = session.getIdleTimeSeconds();
        return idleTime > policy.getIdleTimeoutSeconds();
    }

    public boolean isReauthIntervalTriggered(Session session) {
        long timeSinceReauth = session.getTimeSinceLastReauthSeconds();
        return timeSinceReauth > policy.getReauthIntervalSeconds();
    }

    public void triggerForPrivilegeEscalation(Session session) {
        if (policy.isRequireReauthOnPrivilegeEscalation()) {
            trigger(session, ReauthReason.PRIVILEGE_ESCALATION);
        }
    }

    public void triggerForRoleChange(Session session) {
        if (policy.isRequireReauthOnRoleChange()) {
            trigger(session, ReauthReason.ROLE_CHANGE);
        }
    }

    public void triggerForSecurityAttributeChange(Session session) {
        if (policy.isRequireReauthOnSecurityAttributeChange()) {
            trigger(session, ReauthReason.SECURITY_ATTRIBUTE_CHANGE);
        }
    }

    public void triggerForSensitiveOperation(Session session, String operation) {
        if (policy.isSensitiveOperation(operation)) {
            trigger(session, ReauthReason.SENSITIVE_OPERATION);
        }
    }

    public void triggerForIpChange(Session session) {
        if (policy.isRequireReauthOnIpChange()) {
            trigger(session, ReauthReason.IP_ADDRESS_CHANGE);
        }
    }

    public void triggerForUserAgentChange(Session session) {
        if (policy.isRequireReauthOnUserAgentChange()) {
            trigger(session, ReauthReason.USER_AGENT_CHANGE);
        }
    }

    public void triggerForConcurrentSession(Session session) {
        trigger(session, ReauthReason.CONCURRENT_SESSION_DETECTED);
    }

    public void triggerForOrganizationPolicy(Session session) {
        trigger(session, ReauthReason.ORGANIZATION_POLICY);
    }

    public void triggerManually(Session session) {
        trigger(session, ReauthReason.MANUAL_REQUEST);
    }

    public void triggerForSecurityEvent(Session session) {
        trigger(session, ReauthReason.SECURITY_EVENT);
    }

    private void trigger(Session session, ReauthReason reason) {
        session.addPendingReauthReason(reason);
        LOG.info("Re-authentication triggered for session {} due to {}", session.getSessionId(), reason);
        notifyListeners(session, reason);
    }

    public void addListener(ReauthenticationListener listener) {
        listeners.add(listener);
    }

    public void removeListener(ReauthenticationListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners(Session session, ReauthReason reason) {
        for (ReauthenticationListener listener : listeners) {
            try {
                listener.onReauthenticationTriggered(session, reason);
            } catch (Exception e) {
                LOG.error("Error notifying re-authentication listener", e);
            }
        }
    }

    public ReauthenticationPolicy getPolicy() {
        return policy;
    }

    public interface ReauthenticationListener {
        void onReauthenticationTriggered(Session session, ReauthReason reason);
    }

    public static class TriggerResult {
        private final boolean triggered;
        private final List<ReauthReason> reasons;

        public TriggerResult(boolean triggered, List<ReauthReason> reasons) {
            this.triggered = triggered;
            this.reasons = reasons;
        }

        public boolean isTriggered() {
            return triggered;
        }

        public List<ReauthReason> getReasons() {
            return reasons;
        }

        public boolean hasReason(ReauthReason reason) {
            return reasons.contains(reason);
        }
    }
}
