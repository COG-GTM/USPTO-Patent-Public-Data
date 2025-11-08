package gov.uspto.auth.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class AuthenticationConfigTest {

    @Test
    public void testDefaultConfiguration() {
        AuthenticationConfig config = new AuthenticationConfig();

        assertEquals(30, config.getSessionTimeoutMinutes());
        assertEquals(3, config.getMaxLoginAttempts());
        assertEquals(15, config.getAccountLockoutDurationMinutes());
        assertEquals(12, config.getPasswordMinLength());
        assertEquals(90, config.getPasswordExpirationDays());
        assertTrue(config.isRequirePasswordComplexity());
        assertEquals(60, config.getTokenExpirationMinutes());
        assertTrue(config.isEnableAuditLogging());
    }

    @Test
    public void testSetters() {
        AuthenticationConfig config = new AuthenticationConfig();

        config.setSessionTimeoutMinutes(60);
        config.setMaxLoginAttempts(5);
        config.setAccountLockoutDurationMinutes(30);
        config.setPasswordMinLength(16);
        config.setPasswordExpirationDays(180);
        config.setRequirePasswordComplexity(false);
        config.setTokenExpirationMinutes(120);
        config.setEnableAuditLogging(false);

        assertEquals(60, config.getSessionTimeoutMinutes());
        assertEquals(5, config.getMaxLoginAttempts());
        assertEquals(30, config.getAccountLockoutDurationMinutes());
        assertEquals(16, config.getPasswordMinLength());
        assertEquals(180, config.getPasswordExpirationDays());
        assertEquals(false, config.isRequirePasswordComplexity());
        assertEquals(120, config.getTokenExpirationMinutes());
        assertEquals(false, config.isEnableAuditLogging());
    }
}
