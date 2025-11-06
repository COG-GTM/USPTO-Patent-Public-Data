//testing sonarqube scan
package gov.uspto.patent;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.Random;

/**
 * Test file with intentional code violations for SonarCloud workflow testing.
 * This file contains security issues, bugs, and code smells.
 */
public class TestWorkflowViolations {
    
    public String weakHash(String input) throws Exception {
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
    
    public String insecureRandomToken() {
        Random random = new Random();
        StringBuilder token = new StringBuilder();
        for (int i = 0; i < 32; i++) {
            token.append(Integer.toHexString(random.nextInt(16)));
        }
        return token.toString();
    }
    
    public String resourceLeak(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line = reader.readLine();
        return line;
    }
    
    public int complexMethod(int a, int b, int c, int d, int e, int f) {
        int result = 0;
        if (a > 0) {
            if (b > 0) {
                if (c > 0) {
                    if (d > 0) {
                        if (e > 0) {
                            if (f > 0) {
                                result = a + b + c + d + e + f;
                            } else {
                                result = a + b + c + d + e;
                            }
                        } else {
                            result = a + b + c + d;
                        }
                    } else {
                        result = a + b + c;
                    }
                } else {
                    result = a + b;
                }
            } else {
                result = a;
            }
        }
        return result;
    }
    
    private void neverCalled() {
        System.out.println("This method is never used");
    }
    
    public void emptyCatch() {
        try {
            int x = 1 / 0;
        } catch (Exception e) {
        }
    }
}
