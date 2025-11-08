package gov.uspto.auth.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class AuthenticationExceptionTest {

    @Test
    public void testExceptionWithMessage() {
        AuthenticationException exception = new AuthenticationException("Test error");

        assertEquals("Test error", exception.getMessage());
        assertEquals(AuthenticationException.ErrorCode.AUTHENTICATION_FAILED, exception.getErrorCode());
    }

    @Test
    public void testExceptionWithMessageAndErrorCode() {
        AuthenticationException exception = new AuthenticationException(
            "Account locked", 
            AuthenticationException.ErrorCode.ACCOUNT_LOCKED
        );

        assertEquals("Account locked", exception.getMessage());
        assertEquals(AuthenticationException.ErrorCode.ACCOUNT_LOCKED, exception.getErrorCode());
    }

    @Test
    public void testExceptionWithMessageAndCause() {
        Throwable cause = new RuntimeException("Root cause");
        AuthenticationException exception = new AuthenticationException("Test error", cause);

        assertEquals("Test error", exception.getMessage());
        assertEquals(cause, exception.getCause());
        assertEquals(AuthenticationException.ErrorCode.AUTHENTICATION_FAILED, exception.getErrorCode());
    }

    @Test
    public void testExceptionWithMessageErrorCodeAndCause() {
        Throwable cause = new RuntimeException("Root cause");
        AuthenticationException exception = new AuthenticationException(
            "System error", 
            AuthenticationException.ErrorCode.SYSTEM_ERROR,
            cause
        );

        assertEquals("System error", exception.getMessage());
        assertEquals(cause, exception.getCause());
        assertEquals(AuthenticationException.ErrorCode.SYSTEM_ERROR, exception.getErrorCode());
    }

    @Test
    public void testErrorCodeDescriptions() {
        assertEquals("Authentication failed", 
            AuthenticationException.ErrorCode.AUTHENTICATION_FAILED.getDescription());
        assertEquals("Invalid credentials provided", 
            AuthenticationException.ErrorCode.INVALID_CREDENTIALS.getDescription());
        assertEquals("Credentials have expired", 
            AuthenticationException.ErrorCode.EXPIRED_CREDENTIALS.getDescription());
        assertEquals("Account is locked", 
            AuthenticationException.ErrorCode.ACCOUNT_LOCKED.getDescription());
        assertEquals("Account is disabled", 
            AuthenticationException.ErrorCode.ACCOUNT_DISABLED.getDescription());
        assertEquals("Invalid or malformed token", 
            AuthenticationException.ErrorCode.INVALID_TOKEN.getDescription());
        assertEquals("Token has expired", 
            AuthenticationException.ErrorCode.EXPIRED_TOKEN.getDescription());
        assertEquals("Insufficient privileges for operation", 
            AuthenticationException.ErrorCode.INSUFFICIENT_PRIVILEGES.getDescription());
        assertEquals("System error during authentication", 
            AuthenticationException.ErrorCode.SYSTEM_ERROR.getDescription());
    }

    @Test
    public void testAllErrorCodes() {
        for (AuthenticationException.ErrorCode errorCode : AuthenticationException.ErrorCode.values()) {
            assertNotNull(errorCode.getDescription());
        }
    }
}
