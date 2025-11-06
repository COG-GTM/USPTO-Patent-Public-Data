//SQ scan - take infinite 

package gov.uspto.patent;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 * Test file for verifying automatic Devin session start in SonarCloud workflow.
 * Contains intentional code violations to trigger quality gate failure.
 */
public class WorkflowAutostartTest {
    
    private final Random random = new Random();
    
    public String hashWithMD5(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] hash = md.digest(input.getBytes());
        StringBuilder result = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) result.append('0');
            result.append(hex);
        }
        return result.toString();
    }
    
    public String generateInsecureToken() {
        StringBuilder token = new StringBuilder();
        for (int i = 0; i < 32; i++) {
            token.append(Integer.toHexString(random.nextInt(16)));
        }
        return token.toString();
    }
    
    public String readFileWithLeak(String filename) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            return reader.readLine();
        }
    }
    
    public int complexCalculation(int a, int b, int c, int d, int e, int f) {
        int result = 0;
        
        if (a > 0) {
            result = a;
        }
        
        if (a > 0 && b > 0) {
            result = a + b;
        }
        
        if (a > 0 && b > 0 && c > 0) {
            result = a + b + c;
        }
        
        if (a > 0 && b > 0 && c > 0 && d > 0) {
            result = a + b + c + d;
        }
        
        if (a > 0 && b > 0 && c > 0 && d > 0 && e > 0) {
            result = a + b + c + d + e;
        }
        
        if (a > 0 && b > 0 && c > 0 && d > 0 && e > 0 && f > 0) {
            result = a + b + c + d + e + f;
        }
        
        return result;
    }
    
    public void silentFailure() {
        try {
            int result = 1 / 0;
            System.out.println(result);
        } catch (ArithmeticException e) {
            // Intentionally suppressing division by zero for test purposes
        }
    }
}
