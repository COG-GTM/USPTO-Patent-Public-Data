package gov.uspto.session.security;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import gov.uspto.session.model.ReauthReason;
import gov.uspto.session.model.Session;

public class SessionHijackingPreventionTest {

    private SessionHijackingPrevention prevention;
    private Session session;

    @Before
    public void setUp() {
        prevention = new SessionHijackingPrevention();
        session = new Session("test-session", "test-user");
        session.setIpAddress("192.168.1.100");
        session.setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/91.0.4472.124");
    }

    @Test
    public void testValidateRequestWithMatchingIpAndUserAgent() {
        SessionHijackingPrevention.ValidationResult result = prevention.validateRequest(
                session, "192.168.1.100", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/91.0.4472.124");

        assertTrue(result.isValid());
        assertNull(result.getReauthReason());
    }

    @Test
    public void testValidateRequestWithDifferentIp() {
        SessionHijackingPrevention.SessionHijackingConfig config = new SessionHijackingPrevention.SessionHijackingConfig();
        config.setStrictIpBinding(true);
        prevention = new SessionHijackingPrevention(config);

        SessionHijackingPrevention.ValidationResult result = prevention.validateRequest(
                session, "10.0.0.1", session.getUserAgent());

        assertFalse(result.isValid());
        assertEquals(ReauthReason.IP_ADDRESS_CHANGE, result.getReauthReason());
    }

    @Test
    public void testValidateRequestWithSameSubnet() {
        SessionHijackingPrevention.ValidationResult result = prevention.validateRequest(
                session, "192.168.1.200", session.getUserAgent());

        assertTrue(result.isValid());
    }

    @Test
    public void testValidateRequestWithDifferentUserAgent() {
        SessionHijackingPrevention.SessionHijackingConfig config = new SessionHijackingPrevention.SessionHijackingConfig();
        config.setStrictUserAgentBinding(true);
        prevention = new SessionHijackingPrevention(config);

        SessionHijackingPrevention.ValidationResult result = prevention.validateRequest(
                session, session.getIpAddress(), "Mozilla/5.0 Firefox/89.0");

        assertFalse(result.isValid());
        assertEquals(ReauthReason.USER_AGENT_CHANGE, result.getReauthReason());
    }

    @Test
    public void testValidateRequestWithSameBrowserFamily() {
        SessionHijackingPrevention.ValidationResult result = prevention.validateRequest(
                session, session.getIpAddress(), "Mozilla/5.0 (Macintosh; Intel Mac OS X) Chrome/92.0.4515.107");

        assertTrue(result.isValid());
    }

    @Test
    public void testBindSession() {
        Session newSession = new Session("new-session", "user");
        prevention.bindSession(newSession, "10.0.0.1", "Mozilla/5.0 Firefox/89.0");

        assertEquals("10.0.0.1", newSession.getIpAddress());
        assertEquals("Mozilla/5.0 Firefox/89.0", newSession.getUserAgent());
    }

    @Test
    public void testGenerateSessionFingerprint() {
        String fingerprint = prevention.generateSessionFingerprint("192.168.1.1", "Mozilla/5.0 Chrome/91.0");
        assertNotNull(fingerprint);
        assertTrue(fingerprint.contains("192.168.1.1"));
        assertTrue(fingerprint.contains("chrome"));
    }

    @Test
    public void testDisabledIpBinding() {
        SessionHijackingPrevention.SessionHijackingConfig config = new SessionHijackingPrevention.SessionHijackingConfig();
        config.setIpBindingEnabled(false);
        prevention = new SessionHijackingPrevention(config);

        SessionHijackingPrevention.ValidationResult result = prevention.validateRequest(
                session, "completely.different.ip", session.getUserAgent());

        assertTrue(result.isValid());
    }

    @Test
    public void testDisabledUserAgentBinding() {
        SessionHijackingPrevention.SessionHijackingConfig config = new SessionHijackingPrevention.SessionHijackingConfig();
        config.setUserAgentBindingEnabled(false);
        prevention = new SessionHijackingPrevention(config);

        SessionHijackingPrevention.ValidationResult result = prevention.validateRequest(
                session, session.getIpAddress(), "Completely Different User Agent");

        assertTrue(result.isValid());
    }

    @Test
    public void testConfigGettersAndSetters() {
        SessionHijackingPrevention.SessionHijackingConfig config = prevention.getConfig();
        assertNotNull(config);

        config.setSubnetMaskBits(16);
        assertEquals(16, config.getSubnetMaskBits());
    }
}
