package gov.uspto.auth.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Test;

public class AuthenticationContextTest {

    @After
    public void cleanup() {
        AuthenticationContext.clear();
    }

    @Test
    public void testSetAndGetCurrentPrincipal() {
        Principal principal = new Principal.Builder()
                .identifier("user123")
                .name("Test User")
                .addRole("USER")
                .authenticationType("password")
                .build();

        AuthenticationContext.setCurrentPrincipal(principal);
        Principal retrieved = AuthenticationContext.getCurrentPrincipal();

        assertNotNull(retrieved);
        assertEquals("user123", retrieved.getIdentifier());
        assertEquals("Test User", retrieved.getName());
    }

    @Test
    public void testIsAuthenticated() {
        assertFalse(AuthenticationContext.isAuthenticated());

        Principal principal = new Principal.Builder()
                .identifier("user123")
                .name("Test User")
                .addRole("USER")
                .authenticationType("password")
                .build();

        AuthenticationContext.setCurrentPrincipal(principal);
        assertTrue(AuthenticationContext.isAuthenticated());
    }

    @Test
    public void testClear() {
        Principal principal = new Principal.Builder()
                .identifier("user123")
                .name("Test User")
                .addRole("USER")
                .authenticationType("password")
                .build();

        AuthenticationContext.setCurrentPrincipal(principal);
        assertTrue(AuthenticationContext.isAuthenticated());

        AuthenticationContext.clear();
        assertFalse(AuthenticationContext.isAuthenticated());
        assertNull(AuthenticationContext.getCurrentPrincipal());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetNullPrincipal() {
        AuthenticationContext.setCurrentPrincipal(null);
    }
}
