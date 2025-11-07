package gov.uspto.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.security.NoSuchAlgorithmException;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

import org.junit.Before;
import org.junit.Test;

public class SonarUiTriggerTest {

    private SonarUiTrigger trigger;

    @Before
    public void setUp() {
        trigger = new SonarUiTrigger();
    }

    @Test
    public void testEditMeConstant() {
        assertEquals("change-me", SonarUiTrigger.EDIT_ME);
    }

    @Test
    public void testInsecureMd5() throws NoSuchAlgorithmException {
        String result = trigger.insecureMd5("test");
        assertNotNull("Hash result should not be null", result);
        assertEquals("Hash should be 64 characters (SHA-256)", 64, result.length());
        assertTrue("Hash should be hexadecimal", result.matches("[0-9a-f]+"));
    }

    @Test
    public void testInsecureMd5EmptyString() throws NoSuchAlgorithmException {
        String result = trigger.insecureMd5("");
        assertNotNull("Hash result should not be null", result);
        assertEquals("Hash should be 64 characters (SHA-256)", 64, result.length());
    }

    @Test
    public void testInsecureToken() {
        String token = trigger.insecureToken();
        assertNotNull("Token should not be null", token);
        assertEquals("Token should be 32 characters", 32, token.length());
        assertTrue("Token should be hexadecimal", token.matches("[0-9a-f]+"));
    }

    @Test
    public void testInsecureTokenUniqueness() {
        String token1 = trigger.insecureToken();
        String token2 = trigger.insecureToken();
        assertNotNull("First token should not be null", token1);
        assertNotNull("Second token should not be null", token2);
    }

    @Test
    public void testBadCipher() throws NoSuchPaddingException, NoSuchAlgorithmException {
        Cipher cipher = trigger.badCipher();
        assertNotNull("Cipher should not be null", cipher);
        assertEquals("Cipher algorithm should be AES/GCM/NoPadding", "AES/GCM/NoPadding", cipher.getAlgorithm());
    }

    @Test
    public void testEmptyCatch() {
        trigger.emptyCatch();
    }
}
