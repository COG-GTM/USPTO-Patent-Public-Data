package gov.uspto.auth.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class CredentialTest {

    private static class TestCredential extends Credential {
        private boolean valid;
        private boolean cleared;

        public TestCredential(String identifier, CredentialType type, boolean valid) {
            super(identifier, type);
            this.valid = valid;
            this.cleared = false;
        }

        @Override
        public boolean isValid() {
            return valid && !cleared;
        }

        @Override
        public void clear() {
            this.cleared = true;
        }

        public boolean isCleared() {
            return cleared;
        }
    }

    @Test
    public void testCredentialCreation() {
        TestCredential credential = new TestCredential("testuser", Credential.CredentialType.PASSWORD, true);

        assertEquals("testuser", credential.getIdentifier());
        assertEquals(Credential.CredentialType.PASSWORD, credential.getType());
        assertTrue(credential.isValid());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCredentialWithNullIdentifier() {
        new TestCredential(null, Credential.CredentialType.PASSWORD, true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCredentialWithEmptyIdentifier() {
        new TestCredential("  ", Credential.CredentialType.PASSWORD, true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCredentialWithNullType() {
        new TestCredential("testuser", null, true);
    }

    @Test
    public void testCredentialValidation() {
        TestCredential validCredential = new TestCredential("testuser", Credential.CredentialType.PASSWORD, true);
        assertTrue(validCredential.isValid());

        TestCredential invalidCredential = new TestCredential("testuser", Credential.CredentialType.PASSWORD, false);
        assertFalse(invalidCredential.isValid());
    }

    @Test
    public void testCredentialClear() {
        TestCredential credential = new TestCredential("testuser", Credential.CredentialType.PASSWORD, true);
        assertTrue(credential.isValid());

        credential.clear();
        assertTrue(credential.isCleared());
        assertFalse(credential.isValid());
    }

    @Test
    public void testCredentialTypes() {
        assertEquals("Password-based authentication", 
            Credential.CredentialType.PASSWORD.getDescription());
        assertEquals("Token-based authentication", 
            Credential.CredentialType.TOKEN.getDescription());
        assertEquals("Certificate-based authentication", 
            Credential.CredentialType.CERTIFICATE.getDescription());
        assertEquals("API key authentication", 
            Credential.CredentialType.API_KEY.getDescription());
        assertEquals("Biometric authentication", 
            Credential.CredentialType.BIOMETRIC.getDescription());
    }

    @Test
    public void testAllCredentialTypes() {
        for (Credential.CredentialType type : Credential.CredentialType.values()) {
            assertNotNull(type.getDescription());
        }
    }

    @Test
    public void testToString() {
        TestCredential credential = new TestCredential("testuser", Credential.CredentialType.TOKEN, true);
        String toString = credential.toString();

        assertTrue(toString.contains("testuser"));
        assertTrue(toString.contains("TOKEN"));
    }
}
