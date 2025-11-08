package gov.uspto.auth.core;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SecurityConstantsTest {

    @Test
    public void testNistControls() {
        assertEquals("IA-1", SecurityConstants.NIST_CONTROLS.IA_1);
        assertEquals("IA-2", SecurityConstants.NIST_CONTROLS.IA_2);
        assertEquals("IA-4", SecurityConstants.NIST_CONTROLS.IA_4);
        assertEquals("IA-5", SecurityConstants.NIST_CONTROLS.IA_5);
        assertEquals("IA-9", SecurityConstants.NIST_CONTROLS.IA_9);
        assertEquals("IA-11", SecurityConstants.NIST_CONTROLS.IA_11);
        assertEquals("IA-12", SecurityConstants.NIST_CONTROLS.IA_12);
    }

    @Test
    public void testTimeouts() {
        assertEquals(30, SecurityConstants.TIMEOUTS.DEFAULT_SESSION_TIMEOUT);
        assertEquals(60, SecurityConstants.TIMEOUTS.DEFAULT_TOKEN_EXPIRATION);
        assertEquals(15, SecurityConstants.TIMEOUTS.PRIVILEGED_SESSION_TIMEOUT);
    }

    @Test
    public void testPasswordPolicy() {
        assertEquals(12, SecurityConstants.PASSWORD_POLICY.MIN_LENGTH);
        assertEquals(128, SecurityConstants.PASSWORD_POLICY.MAX_LENGTH);
        assertEquals(90, SecurityConstants.PASSWORD_POLICY.EXPIRATION_DAYS);
        assertEquals(3, SecurityConstants.PASSWORD_POLICY.MAX_LOGIN_ATTEMPTS);
        assertEquals(15, SecurityConstants.PASSWORD_POLICY.LOCKOUT_DURATION_MINUTES);
    }

    @Test
    public void testCrypto() {
        assertEquals("bcrypt", SecurityConstants.CRYPTO.HASH_ALGORITHM);
        assertEquals(12, SecurityConstants.CRYPTO.BCRYPT_ROUNDS);
        assertEquals("SHA-256", SecurityConstants.CRYPTO.TOKEN_ALGORITHM);
        assertEquals(32, SecurityConstants.CRYPTO.TOKEN_LENGTH_BYTES);
    }

    @Test
    public void testAuthTypes() {
        assertEquals("password", SecurityConstants.AUTH_TYPES.PASSWORD);
        assertEquals("token", SecurityConstants.AUTH_TYPES.TOKEN);
        assertEquals("certificate", SecurityConstants.AUTH_TYPES.CERTIFICATE);
        assertEquals("service_account", SecurityConstants.AUTH_TYPES.SERVICE_ACCOUNT);
    }

    @Test
    public void testRoles() {
        assertEquals("ADMIN", SecurityConstants.ROLES.ADMIN);
        assertEquals("USER", SecurityConstants.ROLES.USER);
        assertEquals("SERVICE", SecurityConstants.ROLES.SERVICE);
        assertEquals("AUDITOR", SecurityConstants.ROLES.AUDITOR);
    }

    @Test
    public void testAuditEvents() {
        assertEquals("AUTH_LOGIN_SUCCESS", SecurityConstants.AUDIT_EVENTS.LOGIN_SUCCESS);
        assertEquals("AUTH_LOGIN_FAILURE", SecurityConstants.AUDIT_EVENTS.LOGIN_FAILURE);
        assertEquals("AUTH_LOGOUT", SecurityConstants.AUDIT_EVENTS.LOGOUT);
        assertEquals("AUTH_PASSWORD_CHANGE", SecurityConstants.AUDIT_EVENTS.PASSWORD_CHANGE);
        assertEquals("AUTH_ACCOUNT_LOCKED", SecurityConstants.AUDIT_EVENTS.ACCOUNT_LOCKED);
    }
}
