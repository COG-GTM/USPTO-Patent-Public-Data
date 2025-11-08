package gov.uspto.auth.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

public class AuthenticationResultTest {

    @Test
    public void testSuccessResult() {
        Set<String> roles = new HashSet<>();
        roles.add("USER");
        Principal principal = new Principal("testuser", roles, "PASSWORD");

        AuthenticationResult result = AuthenticationResult.success(principal);

        assertTrue(result.isSuccess());
        assertNotNull(result.getPrincipal());
        assertEquals("testuser", result.getPrincipal().getIdentifier());
        assertNull(result.getErrorMessage());
        assertNull(result.getErrorCode());
        assertNotNull(result.getTimestamp());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSuccessResultWithNullPrincipal() {
        AuthenticationResult.success(null);
    }

    @Test
    public void testFailureResult() {
        AuthenticationResult result = AuthenticationResult.failure("Invalid credentials");

        assertFalse(result.isSuccess());
        assertNull(result.getPrincipal());
        assertEquals("Invalid credentials", result.getErrorMessage());
        assertEquals(AuthenticationException.ErrorCode.AUTHENTICATION_FAILED, result.getErrorCode());
        assertNotNull(result.getTimestamp());
    }

    @Test
    public void testFailureResultWithErrorCode() {
        AuthenticationResult result = AuthenticationResult.failure(
            "Account is locked", 
            AuthenticationException.ErrorCode.ACCOUNT_LOCKED
        );

        assertFalse(result.isSuccess());
        assertNull(result.getPrincipal());
        assertEquals("Account is locked", result.getErrorMessage());
        assertEquals(AuthenticationException.ErrorCode.ACCOUNT_LOCKED, result.getErrorCode());
        assertNotNull(result.getTimestamp());
    }

    @Test
    public void testToException() {
        AuthenticationResult result = AuthenticationResult.failure(
            "Invalid token", 
            AuthenticationException.ErrorCode.INVALID_TOKEN
        );

        AuthenticationException exception = result.toException();

        assertNotNull(exception);
        assertEquals("Invalid token", exception.getMessage());
        assertEquals(AuthenticationException.ErrorCode.INVALID_TOKEN, exception.getErrorCode());
    }

    @Test(expected = IllegalStateException.class)
    public void testToExceptionOnSuccessResult() {
        Set<String> roles = new HashSet<>();
        roles.add("USER");
        Principal principal = new Principal("testuser", roles, "PASSWORD");

        AuthenticationResult result = AuthenticationResult.success(principal);
        result.toException();
    }

    @Test
    public void testToString() {
        Set<String> roles = new HashSet<>();
        roles.add("USER");
        Principal principal = new Principal("testuser", roles, "PASSWORD");

        AuthenticationResult successResult = AuthenticationResult.success(principal);
        String successString = successResult.toString();
        assertTrue(successString.contains("success=true"));
        assertTrue(successString.contains("principal="));

        AuthenticationResult failureResult = AuthenticationResult.failure("Test error");
        String failureString = failureResult.toString();
        assertTrue(failureString.contains("success=false"));
        assertTrue(failureString.contains("errorMessage=Test error"));
    }
}
