package gov.uspto.auth;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class AuthenticationConfigTest {

    @Test
    public void testDefaultConfiguration() {
        AuthenticationConfig config = new AuthenticationConfig();

        assertEquals(30, config.getSessionTimeoutMinutes());
        assertEquals(3, config.getMaxLoginAttempts());
        assertEquals(15, config.getAccountLockoutMinutes());
        assertEquals(12, config.getPasswordMinLength());
        assertEquals(60, config.getTokenExpiryMinutes());
        assertTrue(config.isRequireStrongPasswords());
        assertFalse(config.isEnableMfa());
    }

    @Test
    public void testGetProperty() {
        AuthenticationConfig config = new AuthenticationConfig();

        assertNotNull(config.getProperty("auth.session.timeout.minutes"));
        assertEquals("30", config.getProperty("auth.session.timeout.minutes"));
    }

    @Test
    public void testGetPropertyWithDefault() {
        AuthenticationConfig config = new AuthenticationConfig();

        assertEquals("default", config.getProperty("nonexistent.property", "default"));
    }

    @Test
    public void testToString() {
        AuthenticationConfig config = new AuthenticationConfig();
        String toString = config.toString();

        assertNotNull(toString);
        assertTrue(toString.contains("sessionTimeoutMinutes=30"));
        assertTrue(toString.contains("maxLoginAttempts=3"));
        assertTrue(toString.contains("accountLockoutMinutes=15"));
        assertTrue(toString.contains("passwordMinLength=12"));
        assertTrue(toString.contains("tokenExpiryMinutes=60"));
        assertTrue(toString.contains("requireStrongPasswords=true"));
        assertTrue(toString.contains("enableMfa=false"));
    }
}
