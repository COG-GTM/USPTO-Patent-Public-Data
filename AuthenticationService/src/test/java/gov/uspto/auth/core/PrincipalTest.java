package gov.uspto.auth.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

public class PrincipalTest {

    @Test
    public void testPrincipalBuilder() {
        Principal principal = new Principal.Builder()
                .identifier("user123")
                .name("Test User")
                .addRole("USER")
                .addRole("ADMIN")
                .authenticationType("password")
                .serviceAccount(false)
                .build();

        assertNotNull(principal);
        assertEquals("user123", principal.getIdentifier());
        assertEquals("Test User", principal.getName());
        assertEquals(2, principal.getRoles().size());
        assertTrue(principal.hasRole("USER"));
        assertTrue(principal.hasRole("ADMIN"));
        assertEquals("password", principal.getAuthenticationType());
        assertFalse(principal.isServiceAccount());
    }

    @Test
    public void testHasAnyRole() {
        Principal principal = new Principal.Builder()
                .identifier("user123")
                .name("Test User")
                .addRole("USER")
                .authenticationType("password")
                .build();

        assertTrue(principal.hasAnyRole("USER", "ADMIN"));
        assertTrue(principal.hasAnyRole("ADMIN", "USER"));
        assertFalse(principal.hasAnyRole("ADMIN", "AUDITOR"));
    }

    @Test
    public void testServiceAccount() {
        Principal principal = new Principal.Builder()
                .identifier("service123")
                .name("Test Service")
                .addRole("SERVICE")
                .authenticationType("token")
                .serviceAccount(true)
                .build();

        assertTrue(principal.isServiceAccount());
    }

    @Test
    public void testRolesSet() {
        Set<String> roles = new HashSet<>();
        roles.add("USER");
        roles.add("ADMIN");

        Principal principal = new Principal.Builder()
                .identifier("user123")
                .name("Test User")
                .roles(roles)
                .authenticationType("password")
                .build();

        assertEquals(2, principal.getRoles().size());
        assertTrue(principal.hasRole("USER"));
        assertTrue(principal.hasRole("ADMIN"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMissingIdentifier() {
        new Principal.Builder()
                .name("Test User")
                .addRole("USER")
                .authenticationType("password")
                .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMissingName() {
        new Principal.Builder()
                .identifier("user123")
                .addRole("USER")
                .authenticationType("password")
                .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyIdentifier() {
        new Principal.Builder()
                .identifier("")
                .name("Test User")
                .addRole("USER")
                .authenticationType("password")
                .build();
    }
}
