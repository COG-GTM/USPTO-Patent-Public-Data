package gov.uspto.session.reauth;

import static org.junit.Assert.*;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import gov.uspto.session.model.ReauthReason;

public class ReauthenticationPolicyTest {

    private ReauthenticationPolicy policy;

    @Before
    public void setUp() {
        policy = new ReauthenticationPolicy("test-policy", "Test Policy");
    }

    @Test
    public void testPolicyCreation() {
        assertEquals("test-policy", policy.getPolicyId());
        assertEquals("Test Policy", policy.getPolicyName());
    }

    @Test
    public void testDefaultValues() {
        assertEquals(28800, policy.getSessionTimeoutSeconds());
        assertEquals(1800, policy.getIdleTimeoutSeconds());
        assertEquals(3600, policy.getReauthIntervalSeconds());
        assertTrue(policy.isRequireReauthOnPrivilegeEscalation());
        assertTrue(policy.isRequireReauthOnRoleChange());
        assertTrue(policy.isRequireReauthOnSecurityAttributeChange());
        assertFalse(policy.isRequireReauthOnIpChange());
        assertFalse(policy.isRequireReauthOnUserAgentChange());
        assertEquals(5, policy.getMaxConcurrentSessions());
        assertFalse(policy.isEnforceStrictMode());
    }

    @Test
    public void testSettersAndGetters() {
        policy.setSessionTimeoutSeconds(14400);
        assertEquals(14400, policy.getSessionTimeoutSeconds());

        policy.setIdleTimeoutSeconds(900);
        assertEquals(900, policy.getIdleTimeoutSeconds());

        policy.setReauthIntervalSeconds(1800);
        assertEquals(1800, policy.getReauthIntervalSeconds());

        policy.setRequireReauthOnIpChange(true);
        assertTrue(policy.isRequireReauthOnIpChange());

        policy.setMaxConcurrentSessions(3);
        assertEquals(3, policy.getMaxConcurrentSessions());
    }

    @Test
    public void testSensitiveOperations() {
        assertTrue(policy.getSensitiveOperations().isEmpty());

        policy.addSensitiveOperation("DELETE_USER");
        policy.addSensitiveOperation("CHANGE_PASSWORD");

        assertTrue(policy.isSensitiveOperation("DELETE_USER"));
        assertTrue(policy.isSensitiveOperation("CHANGE_PASSWORD"));
        assertFalse(policy.isSensitiveOperation("VIEW_PROFILE"));
    }

    @Test
    public void testGetEnabledReauthReasons() {
        Set<ReauthReason> reasons = policy.getEnabledReauthReasons();

        assertTrue(reasons.contains(ReauthReason.SESSION_TIMEOUT));
        assertTrue(reasons.contains(ReauthReason.IDLE_TIMEOUT));
        assertTrue(reasons.contains(ReauthReason.PRIVILEGE_ESCALATION));
        assertTrue(reasons.contains(ReauthReason.ROLE_CHANGE));
        assertTrue(reasons.contains(ReauthReason.SECURITY_ATTRIBUTE_CHANGE));
        assertFalse(reasons.contains(ReauthReason.IP_ADDRESS_CHANGE));
    }

    @Test
    public void testIsReauthReasonEnabled() {
        assertTrue(policy.isReauthReasonEnabled(ReauthReason.SESSION_TIMEOUT));
        assertTrue(policy.isReauthReasonEnabled(ReauthReason.PRIVILEGE_ESCALATION));
        assertFalse(policy.isReauthReasonEnabled(ReauthReason.IP_ADDRESS_CHANGE));

        policy.setRequireReauthOnIpChange(true);
        assertTrue(policy.isReauthReasonEnabled(ReauthReason.IP_ADDRESS_CHANGE));
    }

    @Test
    public void testCreateDefaultPolicy() {
        ReauthenticationPolicy defaultPolicy = ReauthenticationPolicy.createDefaultPolicy();

        assertEquals("default", defaultPolicy.getPolicyId());
        assertEquals("Default Policy", defaultPolicy.getPolicyName());
    }

    @Test
    public void testCreateStrictPolicy() {
        ReauthenticationPolicy strictPolicy = ReauthenticationPolicy.createStrictPolicy();

        assertEquals("strict", strictPolicy.getPolicyId());
        assertEquals(14400, strictPolicy.getSessionTimeoutSeconds());
        assertEquals(900, strictPolicy.getIdleTimeoutSeconds());
        assertEquals(1800, strictPolicy.getReauthIntervalSeconds());
        assertTrue(strictPolicy.isRequireReauthOnIpChange());
        assertTrue(strictPolicy.isRequireReauthOnUserAgentChange());
        assertEquals(1, strictPolicy.getMaxConcurrentSessions());
        assertTrue(strictPolicy.isEnforceStrictMode());
    }

    @Test
    public void testCreateRelaxedPolicy() {
        ReauthenticationPolicy relaxedPolicy = ReauthenticationPolicy.createRelaxedPolicy();

        assertEquals("relaxed", relaxedPolicy.getPolicyId());
        assertEquals(86400, relaxedPolicy.getSessionTimeoutSeconds());
        assertEquals(7200, relaxedPolicy.getIdleTimeoutSeconds());
        assertEquals(14400, relaxedPolicy.getReauthIntervalSeconds());
        assertFalse(relaxedPolicy.isRequireReauthOnRoleChange());
        assertEquals(10, relaxedPolicy.getMaxConcurrentSessions());
    }

    @Test
    public void testToString() {
        String str = policy.toString();
        assertTrue(str.contains("test-policy"));
        assertTrue(str.contains("Test Policy"));
    }
}
