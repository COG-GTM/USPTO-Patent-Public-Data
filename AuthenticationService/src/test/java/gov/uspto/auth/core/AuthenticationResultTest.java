package gov.uspto.auth.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class AuthenticationResultTest {

    @Test
    public void testSuccessResult() {
        Principal principal = new Principal.Builder()
                .identifier("user123")
                .name("Test User")
                .addRole("USER")
                .authenticationType("password")
                .build();

        AuthenticationResult result = AuthenticationResult.success(principal);

        assertTrue(result.isSuccess());
        assertNotNull(result.getPrincipal());
        assertEquals("user123", result.getPrincipal().getIdentifier());
        assertNull(result.getErrorCode());
        assertNull(result.getErrorMessage());
        assertNotNull(result.getTimestamp());
    }

    @Test
    public void testSuccessResultWithSourceAddress() {
        Principal principal = new Principal.Builder()
                .identifier("user123")
                .name("Test User")
                .addRole("USER")
                .authenticationType("password")
                .build();

        AuthenticationResult result = AuthenticationResult.success(principal, "192.168.1.1");

        assertTrue(result.isSuccess());
        assertNotNull(result.getPrincipal());
        assertEquals("192.168.1.1", result.getSourceAddress());
    }

    @Test
    public void testFailureResult() {
        AuthenticationResult result = AuthenticationResult.failure(
                AuthenticationException.ERROR_INVALID_CREDENTIALS,
                "Invalid username or password");

        assertFalse(result.isSuccess());
        assertNull(result.getPrincipal());
        assertEquals(AuthenticationException.ERROR_INVALID_CREDENTIALS, result.getErrorCode());
        assertEquals("Invalid username or password", result.getErrorMessage());
        assertNotNull(result.getTimestamp());
    }

    @Test
    public void testFailureResultWithSourceAddress() {
        AuthenticationResult result = AuthenticationResult.failure(
                AuthenticationException.ERROR_ACCOUNT_LOCKED,
                "Account is locked",
                "192.168.1.1");

        assertFalse(result.isSuccess());
        assertEquals("192.168.1.1", result.getSourceAddress());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSuccessWithNullPrincipal() {
        AuthenticationResult.success(null);
    }
}
