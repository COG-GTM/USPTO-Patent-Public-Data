package gov.uspto.session.reauth;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

import gov.uspto.session.model.ReauthReason;

public class ReauthenticationPolicy {

    private final String policyId;
    private final String policyName;
    private long sessionTimeoutSeconds;
    private long idleTimeoutSeconds;
    private long reauthIntervalSeconds;
    private boolean requireReauthOnPrivilegeEscalation;
    private boolean requireReauthOnRoleChange;
    private boolean requireReauthOnSecurityAttributeChange;
    private boolean requireReauthOnIpChange;
    private boolean requireReauthOnUserAgentChange;
    private Set<String> sensitiveOperations;
    private int maxConcurrentSessions;
    private boolean enforceStrictMode;

    public ReauthenticationPolicy(String policyId, String policyName) {
        this.policyId = Objects.requireNonNull(policyId, "policyId cannot be null");
        this.policyName = Objects.requireNonNull(policyName, "policyName cannot be null");
        this.sessionTimeoutSeconds = 28800;
        this.idleTimeoutSeconds = 1800;
        this.reauthIntervalSeconds = 3600;
        this.requireReauthOnPrivilegeEscalation = true;
        this.requireReauthOnRoleChange = true;
        this.requireReauthOnSecurityAttributeChange = true;
        this.requireReauthOnIpChange = false;
        this.requireReauthOnUserAgentChange = false;
        this.sensitiveOperations = new java.util.HashSet<>();
        this.maxConcurrentSessions = 5;
        this.enforceStrictMode = false;
    }

    public String getPolicyId() {
        return policyId;
    }

    public String getPolicyName() {
        return policyName;
    }

    public long getSessionTimeoutSeconds() {
        return sessionTimeoutSeconds;
    }

    public void setSessionTimeoutSeconds(long sessionTimeoutSeconds) {
        this.sessionTimeoutSeconds = sessionTimeoutSeconds;
    }

    public long getIdleTimeoutSeconds() {
        return idleTimeoutSeconds;
    }

    public void setIdleTimeoutSeconds(long idleTimeoutSeconds) {
        this.idleTimeoutSeconds = idleTimeoutSeconds;
    }

    public long getReauthIntervalSeconds() {
        return reauthIntervalSeconds;
    }

    public void setReauthIntervalSeconds(long reauthIntervalSeconds) {
        this.reauthIntervalSeconds = reauthIntervalSeconds;
    }

    public boolean isRequireReauthOnPrivilegeEscalation() {
        return requireReauthOnPrivilegeEscalation;
    }

    public void setRequireReauthOnPrivilegeEscalation(boolean requireReauthOnPrivilegeEscalation) {
        this.requireReauthOnPrivilegeEscalation = requireReauthOnPrivilegeEscalation;
    }

    public boolean isRequireReauthOnRoleChange() {
        return requireReauthOnRoleChange;
    }

    public void setRequireReauthOnRoleChange(boolean requireReauthOnRoleChange) {
        this.requireReauthOnRoleChange = requireReauthOnRoleChange;
    }

    public boolean isRequireReauthOnSecurityAttributeChange() {
        return requireReauthOnSecurityAttributeChange;
    }

    public void setRequireReauthOnSecurityAttributeChange(boolean requireReauthOnSecurityAttributeChange) {
        this.requireReauthOnSecurityAttributeChange = requireReauthOnSecurityAttributeChange;
    }

    public boolean isRequireReauthOnIpChange() {
        return requireReauthOnIpChange;
    }

    public void setRequireReauthOnIpChange(boolean requireReauthOnIpChange) {
        this.requireReauthOnIpChange = requireReauthOnIpChange;
    }

    public boolean isRequireReauthOnUserAgentChange() {
        return requireReauthOnUserAgentChange;
    }

    public void setRequireReauthOnUserAgentChange(boolean requireReauthOnUserAgentChange) {
        this.requireReauthOnUserAgentChange = requireReauthOnUserAgentChange;
    }

    public Set<String> getSensitiveOperations() {
        return sensitiveOperations;
    }

    public void setSensitiveOperations(Set<String> sensitiveOperations) {
        this.sensitiveOperations = sensitiveOperations;
    }

    public void addSensitiveOperation(String operation) {
        this.sensitiveOperations.add(operation);
    }

    public boolean isSensitiveOperation(String operation) {
        return sensitiveOperations.contains(operation);
    }

    public int getMaxConcurrentSessions() {
        return maxConcurrentSessions;
    }

    public void setMaxConcurrentSessions(int maxConcurrentSessions) {
        this.maxConcurrentSessions = maxConcurrentSessions;
    }

    public boolean isEnforceStrictMode() {
        return enforceStrictMode;
    }

    public void setEnforceStrictMode(boolean enforceStrictMode) {
        this.enforceStrictMode = enforceStrictMode;
    }

    public Set<ReauthReason> getEnabledReauthReasons() {
        Set<ReauthReason> reasons = EnumSet.noneOf(ReauthReason.class);
        reasons.add(ReauthReason.SESSION_TIMEOUT);
        reasons.add(ReauthReason.IDLE_TIMEOUT);
        if (requireReauthOnPrivilegeEscalation) {
            reasons.add(ReauthReason.PRIVILEGE_ESCALATION);
        }
        if (requireReauthOnRoleChange) {
            reasons.add(ReauthReason.ROLE_CHANGE);
        }
        if (requireReauthOnSecurityAttributeChange) {
            reasons.add(ReauthReason.SECURITY_ATTRIBUTE_CHANGE);
        }
        if (requireReauthOnIpChange) {
            reasons.add(ReauthReason.IP_ADDRESS_CHANGE);
        }
        if (requireReauthOnUserAgentChange) {
            reasons.add(ReauthReason.USER_AGENT_CHANGE);
        }
        if (!sensitiveOperations.isEmpty()) {
            reasons.add(ReauthReason.SENSITIVE_OPERATION);
        }
        return reasons;
    }

    public boolean isReauthReasonEnabled(ReauthReason reason) {
        return getEnabledReauthReasons().contains(reason);
    }

    public static ReauthenticationPolicy createDefaultPolicy() {
        return new ReauthenticationPolicy("default", "Default Policy");
    }

    public static ReauthenticationPolicy createStrictPolicy() {
        ReauthenticationPolicy policy = new ReauthenticationPolicy("strict", "Strict Security Policy");
        policy.setSessionTimeoutSeconds(14400);
        policy.setIdleTimeoutSeconds(900);
        policy.setReauthIntervalSeconds(1800);
        policy.setRequireReauthOnIpChange(true);
        policy.setRequireReauthOnUserAgentChange(true);
        policy.setMaxConcurrentSessions(1);
        policy.setEnforceStrictMode(true);
        return policy;
    }

    public static ReauthenticationPolicy createRelaxedPolicy() {
        ReauthenticationPolicy policy = new ReauthenticationPolicy("relaxed", "Relaxed Policy");
        policy.setSessionTimeoutSeconds(86400);
        policy.setIdleTimeoutSeconds(7200);
        policy.setReauthIntervalSeconds(14400);
        policy.setRequireReauthOnPrivilegeEscalation(true);
        policy.setRequireReauthOnRoleChange(false);
        policy.setRequireReauthOnSecurityAttributeChange(false);
        policy.setMaxConcurrentSessions(10);
        return policy;
    }

    @Override
    public String toString() {
        return "ReauthenticationPolicy{" +
                "policyId='" + policyId + '\'' +
                ", policyName='" + policyName + '\'' +
                ", sessionTimeoutSeconds=" + sessionTimeoutSeconds +
                ", idleTimeoutSeconds=" + idleTimeoutSeconds +
                '}';
    }
}
