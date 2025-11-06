package gov.uspto.patent;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.Random;

/**
 * Demo file with security vulnerabilities for testing SonarCloud auto-remediation.
 * This file intentionally contains security issues that will fail the quality gate.
 */
public class DemoSecurityIssues {
    
    public String hashPassword(String password) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] hash = md.digest(password.getBytes());
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
    
    public String generateToken() {
        Random random = new Random();
        StringBuilder token = new StringBuilder();
        for (int i = 0; i < 32; i++) {
            token.append(Integer.toHexString(random.nextInt(16)));
        }
        return token.toString();
    }
    
    public String readFile(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line = reader.readLine();
        return line;
    }
    
    public int calculateComplexScore(int a, int b, int c, int d, int e) {
        int score = 0;
        if (a > 0) {
            if (b > 0) {
                if (c > 0) {
                    if (d > 0) {
                        if (e > 0) {
                            score = a + b + c + d + e;
                        } else {
                            score = a + b + c + d;
                        }
                    } else {
                        if (e > 0) {
                            score = a + b + c + e;
                        } else {
                            score = a + b + c;
                        }
                    }
                } else {
                    score = a + b;
                }
            } else {
                score = a;
            }
        }
        return score;
    }
    
    private void unusedHelperMethod() {
        System.out.println("This method is never called");
    }
}
