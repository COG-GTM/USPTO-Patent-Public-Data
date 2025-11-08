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
    public void testPrincipalCreation() {
        Set<String> roles = new HashSet<>();
        roles.add("USER");
        roles.add("ADMIN");

        Principal principal = new Principal("testuser", roles, "PASSWORD");

        assertEquals("testuser", principal.getIdentifier());
        assertEquals(2, principal.getRoles().size());
        assertTrue(principal.getRoles().contains("USER"));
        assertTrue(principal.getRoles().contains("ADMIN"));
        assertEquals("PASSWORD", principal.getAuthenticationType());
        assertNotNull(principal.getAuthenticationTime());
    }

    @Test
    public void testPrincipalWithNullRoles() {
        Principal principal = new Principal("testuser", null, "PASSWORD");

        assertEquals("testuser", principal.getIdentifier());
        assertNotNull(principal.getRoles());
        assertEquals(0, principal.getRoles().size());
    }

    @Test
    public void testPrincipalWithNullAuthType() {
        Set<String> roles = new HashSet<>();
        roles.add("USER");

        Principal principal = new Principal("testuser", roles, null);

        assertEquals("UNKNOWN", principal.getAuthenticationType());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPrincipalWithNullIdentifier() {
        Set<String> roles = new HashSet<>();
        new Principal(null, roles, "PASSWORD");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPrincipalWithEmptyIdentifier() {
        Set<String> roles = new HashSet<>();
        new Principal("  ", roles, "PASSWORD");
    }

    @Test
    public void testHasRole() {
        Set<String> roles = new HashSet<>();
        roles.add("USER");
        roles.add("ADMIN");

        Principal principal = new Principal("testuser", roles, "PASSWORD");

        assertTrue(principal.hasRole("USER"));
        assertTrue(principal.hasRole("ADMIN"));
        assertFalse(principal.hasRole("AUDITOR"));
    }

    @Test
    public void testHasAnyRole() {
        Set<String> roles = new HashSet<>();
        roles.add("USER");

        Principal principal = new Principal("testuser", roles, "PASSWORD");

        Set<String> requiredRoles = new HashSet<>();
        requiredRoles.add("ADMIN");
        requiredRoles.add("USER");

        assertTrue(principal.hasAnyRole(requiredRoles));

        requiredRoles.clear();
        requiredRoles.add("ADMIN");
        requiredRoles.add("AUDITOR");

        assertFalse(principal.hasAnyRole(requiredRoles));
    }

    @Test
    public void testHasAllRoles() {
        Set<String> roles = new HashSet<>();
        roles.add("USER");
        roles.add("ADMIN");

        Principal principal = new Principal("testuser", roles, "PASSWORD");

        Set<String> requiredRoles = new HashSet<>();
        requiredRoles.add("USER");
        requiredRoles.add("ADMIN");

        assertTrue(principal.hasAllRoles(requiredRoles));

        requiredRoles.add("AUDITOR");
        assertFalse(principal.hasAllRoles(requiredRoles));
    }

    @Test
    public void testRolesAreImmutable() {
        Set<String> roles = new HashSet<>();
        roles.add("USER");

        Principal principal = new Principal("testuser", roles, "PASSWORD");

        try {
            principal.getRoles().add("ADMIN");
            assertFalse("Should have thrown UnsupportedOperationException", true);
        } catch (UnsupportedOperationException e) {
            assertTrue(true);
        }
    }
}
