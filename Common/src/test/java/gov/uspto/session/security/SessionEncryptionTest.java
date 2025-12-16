package gov.uspto.session.security;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class SessionEncryptionTest {

    private SessionEncryption encryption;

    @Before
    public void setUp() {
        encryption = new SessionEncryption();
    }

    @Test
    public void testEncryptDecrypt() {
        String plaintext = "sensitive session data";
        String encrypted = encryption.encrypt(plaintext);

        assertNotNull(encrypted);
        assertNotEquals(plaintext, encrypted);

        String decrypted = encryption.decrypt(encrypted);
        assertEquals(plaintext, decrypted);
    }

    @Test
    public void testEncryptDecryptWithSpecialCharacters() {
        String plaintext = "user@example.com:password123!@#$%^&*()";
        String encrypted = encryption.encrypt(plaintext);
        String decrypted = encryption.decrypt(encrypted);
        assertEquals(plaintext, decrypted);
    }

    @Test
    public void testEncryptDecryptWithUnicode() {
        String plaintext = "Unicode test: \u00e9\u00e8\u00ea \u4e2d\u6587 \u0420\u0443\u0441\u0441\u043a\u0438\u0439";
        String encrypted = encryption.encrypt(plaintext);
        String decrypted = encryption.decrypt(encrypted);
        assertEquals(plaintext, decrypted);
    }

    @Test
    public void testEncryptDecryptEmptyString() {
        String plaintext = "";
        String encrypted = encryption.encrypt(plaintext);
        String decrypted = encryption.decrypt(encrypted);
        assertEquals(plaintext, decrypted);
    }

    @Test
    public void testEncryptNull() {
        assertNull(encryption.encrypt(null));
    }

    @Test
    public void testDecryptNull() {
        assertNull(encryption.decrypt(null));
    }

    @Test
    public void testDifferentEncryptionsProduceDifferentResults() {
        String plaintext = "test data";
        String encrypted1 = encryption.encrypt(plaintext);
        String encrypted2 = encryption.encrypt(plaintext);

        assertNotEquals(encrypted1, encrypted2);

        assertEquals(plaintext, encryption.decrypt(encrypted1));
        assertEquals(plaintext, encryption.decrypt(encrypted2));
    }

    @Test
    public void testEncryptionWithProvidedKey() {
        String base64Key = encryption.getKeyBase64();
        SessionEncryption encryption2 = new SessionEncryption(base64Key);

        String plaintext = "test data";
        String encrypted = encryption.encrypt(plaintext);

        String decrypted = encryption2.decrypt(encrypted);
        assertEquals(plaintext, decrypted);
    }

    @Test
    public void testGetKeyBytes() {
        byte[] keyBytes = encryption.getKeyBytes();
        assertNotNull(keyBytes);
        assertTrue(keyBytes.length == 16 || keyBytes.length == 24 || keyBytes.length == 32);
    }

    @Test
    public void testGetKeyBase64() {
        String base64Key = encryption.getKeyBase64();
        assertNotNull(base64Key);
        assertFalse(base64Key.isEmpty());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidKeyLength() {
        new SessionEncryption(new byte[10]);
    }

    @Test(expected = SessionEncryption.SessionEncryptionException.class)
    public void testDecryptInvalidData() {
        encryption.decrypt("invalid-encrypted-data");
    }
}
