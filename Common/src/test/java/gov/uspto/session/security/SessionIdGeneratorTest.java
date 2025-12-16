package gov.uspto.session.security;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

public class SessionIdGeneratorTest {

    private SessionIdGenerator generator;

    @Before
    public void setUp() {
        generator = new SessionIdGenerator();
    }

    @Test
    public void testGenerateSessionId() {
        String sessionId = generator.generateSessionId();
        assertNotNull(sessionId);
        assertFalse(sessionId.isEmpty());
    }

    @Test
    public void testGenerateSessionIdWithPrefix() {
        String sessionId = generator.generateSessionId("USPTO");
        assertNotNull(sessionId);
        assertTrue(sessionId.startsWith("USPTO_"));
    }

    @Test
    public void testSessionIdUniqueness() {
        Set<String> sessionIds = new HashSet<>();
        int count = 1000;

        for (int i = 0; i < count; i++) {
            sessionIds.add(generator.generateSessionId());
        }

        assertEquals(count, sessionIds.size());
    }

    @Test
    public void testSessionIdLength() {
        SessionIdGenerator customGenerator = new SessionIdGenerator(32);
        String sessionId = customGenerator.generateSessionId();
        assertTrue(SessionIdGenerator.isValidSessionIdFormat(sessionId));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMinimumIdLength() {
        new SessionIdGenerator(8);
    }

    @Test
    public void testIsValidSessionIdFormat() {
        String validId = generator.generateSessionId();
        assertTrue(SessionIdGenerator.isValidSessionIdFormat(validId));

        assertFalse(SessionIdGenerator.isValidSessionIdFormat(null));
        assertFalse(SessionIdGenerator.isValidSessionIdFormat(""));
        assertFalse(SessionIdGenerator.isValidSessionIdFormat("!!!invalid!!!"));
    }

    @Test
    public void testIsValidSessionIdFormatWithPrefix() {
        String validIdWithPrefix = generator.generateSessionId("test");
        assertTrue(validIdWithPrefix.startsWith("test_"));
        assertTrue(SessionIdGenerator.isValidSessionIdFormat(validIdWithPrefix));
    }

    @Test
    public void testGetIdLengthBytes() {
        assertEquals(32, generator.getIdLengthBytes());

        SessionIdGenerator customGenerator = new SessionIdGenerator(64);
        assertEquals(64, customGenerator.getIdLengthBytes());
    }
}
