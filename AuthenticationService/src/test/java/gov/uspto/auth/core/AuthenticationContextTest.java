package gov.uspto.auth.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Test;

public class AuthenticationContextTest {

    @After
    public void cleanup() {
        AuthenticationContext.clear();
    }

    @Test
    public void testSetAndGetCurrentPrincipal() {
        Set<String> roles = new HashSet<>();
        roles.add("USER");
        Principal principal = new Principal("testuser", roles, "PASSWORD");

        AuthenticationContext.setCurrentPrincipal(principal);
        Principal retrieved = AuthenticationContext.getCurrentPrincipal();

        assertNotNull(retrieved);
        assertEquals("testuser", retrieved.getIdentifier());
    }

    @Test
    public void testClearPrincipal() {
        Set<String> roles = new HashSet<>();
        roles.add("USER");
        Principal principal = new Principal("testuser", roles, "PASSWORD");

        AuthenticationContext.setCurrentPrincipal(principal);
        assertTrue(AuthenticationContext.isAuthenticated());

        AuthenticationContext.clear();
        assertFalse(AuthenticationContext.isAuthenticated());
        assertNull(AuthenticationContext.getCurrentPrincipal());
    }

    @Test
    public void testIsAuthenticated() {
        assertFalse(AuthenticationContext.isAuthenticated());

        Set<String> roles = new HashSet<>();
        roles.add("USER");
        Principal principal = new Principal("testuser", roles, "PASSWORD");
        AuthenticationContext.setCurrentPrincipal(principal);

        assertTrue(AuthenticationContext.isAuthenticated());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetNullPrincipal() {
        AuthenticationContext.setCurrentPrincipal(null);
    }

    @Test
    public void testThreadLocalIsolation() throws InterruptedException {
        Set<String> roles = new HashSet<>();
        roles.add("USER");
        Principal principal1 = new Principal("user1", roles, "PASSWORD");
        AuthenticationContext.setCurrentPrincipal(principal1);

        Thread thread = new Thread(() -> {
            assertNull(AuthenticationContext.getCurrentPrincipal());
            Principal principal2 = new Principal("user2", roles, "PASSWORD");
            AuthenticationContext.setCurrentPrincipal(principal2);
            assertEquals("user2", AuthenticationContext.getCurrentPrincipal().getIdentifier());
            AuthenticationContext.clear();
        });

        thread.start();
        thread.join();

        assertEquals("user1", AuthenticationContext.getCurrentPrincipal().getIdentifier());
    }
}
