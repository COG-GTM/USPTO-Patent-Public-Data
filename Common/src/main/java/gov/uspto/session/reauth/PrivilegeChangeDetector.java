package gov.uspto.session.reauth;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.uspto.session.model.ReauthReason;
import gov.uspto.session.model.Session;

public class PrivilegeChangeDetector {

    private static final Logger LOG = LoggerFactory.getLogger(PrivilegeChangeDetector.class);

    private static final String ROLES_ATTRIBUTE = "user.roles";
    private static final String PRIVILEGES_ATTRIBUTE = "user.privileges";
    private static final String SECURITY_LEVEL_ATTRIBUTE = "user.securityLevel";

    private final ReauthenticationTrigger reauthTrigger;

    public PrivilegeChangeDetector() {
        this(null);
    }

    public PrivilegeChangeDetector(ReauthenticationTrigger reauthTrigger) {
        this.reauthTrigger = reauthTrigger;
    }

    @SuppressWarnings("unchecked")
    public ChangeDetectionResult detectRoleChange(Session session, Set<String> newRoles) {
        Set<String> currentRoles = (Set<String>) session.getSecurityAttribute(ROLES_ATTRIBUTE);
        if (currentRoles == null) {
            currentRoles = new HashSet<>();
        }

        Set<String> addedRoles = new HashSet<>(newRoles);
        addedRoles.removeAll(currentRoles);

        Set<String> removedRoles = new HashSet<>(currentRoles);
        removedRoles.removeAll(newRoles);

        boolean changed = !addedRoles.isEmpty() || !removedRoles.isEmpty();
        boolean escalation = !addedRoles.isEmpty();

        if (changed) {
            LOG.info("Role change detected for session {}: added={}, removed={}",
                    session.getSessionId(), addedRoles, removedRoles);

            if (reauthTrigger != null) {
                if (escalation) {
                    reauthTrigger.triggerForPrivilegeEscalation(session);
                } else {
                    reauthTrigger.triggerForRoleChange(session);
                }
            }
        }

        return new ChangeDetectionResult(changed, escalation,
                escalation ? ReauthReason.PRIVILEGE_ESCALATION : ReauthReason.ROLE_CHANGE,
                "Roles changed: added=" + addedRoles + ", removed=" + removedRoles);
    }

    @SuppressWarnings("unchecked")
    public ChangeDetectionResult detectPrivilegeChange(Session session, Set<String> newPrivileges) {
        Set<String> currentPrivileges = (Set<String>) session.getSecurityAttribute(PRIVILEGES_ATTRIBUTE);
        if (currentPrivileges == null) {
            currentPrivileges = new HashSet<>();
        }

        Set<String> addedPrivileges = new HashSet<>(newPrivileges);
        addedPrivileges.removeAll(currentPrivileges);

        Set<String> removedPrivileges = new HashSet<>(currentPrivileges);
        removedPrivileges.removeAll(newPrivileges);

        boolean changed = !addedPrivileges.isEmpty() || !removedPrivileges.isEmpty();
        boolean escalation = !addedPrivileges.isEmpty();

        if (changed) {
            LOG.info("Privilege change detected for session {}: added={}, removed={}",
                    session.getSessionId(), addedPrivileges, removedPrivileges);

            if (reauthTrigger != null && escalation) {
                reauthTrigger.triggerForPrivilegeEscalation(session);
            }
        }

        return new ChangeDetectionResult(changed, escalation,
                ReauthReason.PRIVILEGE_ESCALATION,
                "Privileges changed: added=" + addedPrivileges + ", removed=" + removedPrivileges);
    }

    public ChangeDetectionResult detectSecurityLevelChange(Session session, int newSecurityLevel) {
        Integer currentLevel = (Integer) session.getSecurityAttribute(SECURITY_LEVEL_ATTRIBUTE);
        if (currentLevel == null) {
            currentLevel = 0;
        }

        boolean changed = !currentLevel.equals(newSecurityLevel);
        boolean escalation = newSecurityLevel > currentLevel;

        if (changed) {
            LOG.info("Security level change detected for session {}: {} -> {}",
                    session.getSessionId(), currentLevel, newSecurityLevel);

            if (reauthTrigger != null && escalation) {
                reauthTrigger.triggerForPrivilegeEscalation(session);
            }
        }

        return new ChangeDetectionResult(changed, escalation,
                ReauthReason.PRIVILEGE_ESCALATION,
                "Security level changed: " + currentLevel + " -> " + newSecurityLevel);
    }

    public ChangeDetectionResult detectSecurityAttributeChange(Session session, String attributeName, Object newValue) {
        Object currentValue = session.getSecurityAttribute(attributeName);

        boolean changed = !Objects.equals(currentValue, newValue);

        if (changed) {
            LOG.info("Security attribute change detected for session {}: {}",
                    session.getSessionId(), attributeName);

            if (reauthTrigger != null) {
                reauthTrigger.triggerForSecurityAttributeChange(session);
            }
        }

        return new ChangeDetectionResult(changed, false,
                ReauthReason.SECURITY_ATTRIBUTE_CHANGE,
                "Security attribute '" + attributeName + "' changed");
    }

    public void updateRoles(Session session, Set<String> roles) {
        session.setSecurityAttribute(ROLES_ATTRIBUTE, new HashSet<>(roles));
    }

    public void updatePrivileges(Session session, Set<String> privileges) {
        session.setSecurityAttribute(PRIVILEGES_ATTRIBUTE, new HashSet<>(privileges));
    }

    public void updateSecurityLevel(Session session, int securityLevel) {
        session.setSecurityAttribute(SECURITY_LEVEL_ATTRIBUTE, securityLevel);
    }

    @SuppressWarnings("unchecked")
    public Set<String> getCurrentRoles(Session session) {
        Set<String> roles = (Set<String>) session.getSecurityAttribute(ROLES_ATTRIBUTE);
        return roles != null ? new HashSet<>(roles) : new HashSet<>();
    }

    @SuppressWarnings("unchecked")
    public Set<String> getCurrentPrivileges(Session session) {
        Set<String> privileges = (Set<String>) session.getSecurityAttribute(PRIVILEGES_ATTRIBUTE);
        return privileges != null ? new HashSet<>(privileges) : new HashSet<>();
    }

    public int getCurrentSecurityLevel(Session session) {
        Integer level = (Integer) session.getSecurityAttribute(SECURITY_LEVEL_ATTRIBUTE);
        return level != null ? level : 0;
    }

    public static class ChangeDetectionResult {
        private final boolean changed;
        private final boolean escalation;
        private final ReauthReason reauthReason;
        private final String description;

        public ChangeDetectionResult(boolean changed, boolean escalation, ReauthReason reauthReason, String description) {
            this.changed = changed;
            this.escalation = escalation;
            this.reauthReason = reauthReason;
            this.description = description;
        }

        public boolean isChanged() {
            return changed;
        }

        public boolean isEscalation() {
            return escalation;
        }

        public ReauthReason getReauthReason() {
            return reauthReason;
        }

        public String getDescription() {
            return description;
        }

        public boolean requiresReauthentication() {
            return changed && escalation;
        }
    }
}
