package gov.uspto.auth.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class AuthenticationExceptionTest {

    @Test
    public void testExceptionWithMessage() {
        AuthenticationException exception = new AuthenticationException(
                AuthenticationException.ERROR_INVALID_CREDENTIALS,
                "Invalid credentials");

        assertEquals(AuthenticationException.ERROR_INVALID_CREDENTIALS, exception.getErrorCode());
        assertEquals("Invalid credentials", exception.getMessage());
    }

    @Test
    public void testExceptionWithCause() {
        Exception cause = new RuntimeException("Database error");
        AuthenticationException exception = new AuthenticationException(
                AuthenticationException.ERROR_SYSTEM_ERROR,
                "System error occurred",
                cause);

        assertEquals(AuthenticationException.ERROR_SYSTEM_ERROR, exception.getErrorCode());
        assertEquals("System error occurred", exception.getMessage());
        assertNotNull(exception.getCause());
        assertEquals("Database error", exception.getCause().getMessage());
    }

    @Test
    public void testErrorCodes() {
        assertEquals("AUTH_001", AuthenticationException.ERROR_INVALID_CREDENTIALS);
        assertEquals("AUTH_002", AuthenticationException.ERROR_ACCOUNT_LOCKED);
        assertEquals("AUTH_003", AuthenticationException.ERROR_ACCOUNT_DISABLED);
        assertEquals("AUTH_004", AuthenticationException.ERROR_ACCOUNT_EXPIRED);
        assertEquals("AUTH_005", AuthenticationException.ERROR_CREDENTIALS_EXPIRED);
        assertEquals("AUTH_006", AuthenticationException.ERROR_INSUFFICIENT_PRIVILEGES);
        assertEquals("AUTH_007", AuthenticationException.ERROR_SYSTEM_ERROR);
        assertEquals("AUTH_008", AuthenticationException.ERROR_POLICY_VIOLATION);
        assertEquals("AUTH_009", AuthenticationException.ERROR_INVALID_TOKEN);
        assertEquals("AUTH_010", AuthenticationException.ERROR_TOKEN_EXPIRED);
    }
}
