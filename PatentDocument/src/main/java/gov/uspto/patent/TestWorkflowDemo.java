// Testing simplified workflow
package gov.uspto.patent;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Test file for SonarCloud auto-remediation workflow demo.
 * Contains intentional violations that should be fixed by Devin AI.
 */
public class TestWorkflowDemo {
    
    public String hashData(String data) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(data.getBytes());
        return bytesToHex(hash);
    }
    
    public String generateSessionToken() {
        SecureRandom random = new SecureRandom();
        StringBuilder token = new StringBuilder();
        for (int i = 0; i < 32; i++) {
            token.append(Integer.toHexString(random.nextInt(16)));
        }
        return token.toString();
    }
    
    public int readFirstByte(String filename) throws IOException {
        try (FileInputStream fis = new FileInputStream(filename)) {
            return fis.read();
        }
    }
    
    private String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }
}
