//TEST SQ SCAN_2

package gov.uspto.common;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

/**
 * Simple test file for triggering SonarCloud workflow.
 * Edit the TEST_ME string in GitHub UI to retrigger the workflow.
 */
public class SonarUiTrigger {
    public static final String EDIT_ME = "change-me";
    private final SecureRandom random = new SecureRandom();

    public String insecureMd5(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256"); // Fixed: use SHA-256 instead of MD5
        byte[] digest = md.digest(input.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) sb.append('0');
            sb.append(hex);
        }
        return sb.toString();
    }

    public String insecureToken() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 32; i++) {
            sb.append(Integer.toHexString(random.nextInt(16)));
        }
        return sb.toString();
    }

    public Cipher badCipher() throws NoSuchPaddingException, NoSuchAlgorithmException {
        return Cipher.getInstance("AES/GCM/NoPadding");
    }

    public void emptyCatch() {
        try {
            Integer.parseInt("NaN");
        } catch (Exception e) { /* Sonar: empty catch */ }
    }
}
