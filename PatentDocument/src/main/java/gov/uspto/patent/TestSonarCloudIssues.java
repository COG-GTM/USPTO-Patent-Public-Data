package gov.uspto.patent;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

/**
 * Test file with intentional SonarCloud violations for demo purposes.
 * This file demonstrates the auto-remediation workflow.
 */
public class TestSonarCloudIssues {
    
    public void resourceLeakExample(String filename) throws IOException {
        FileInputStream fis = new FileInputStream(filename);
        int data = fis.read();
        System.out.println("Read data: " + data);
    }
    
    public int generateRandomNumber() {
        Random random = new Random();
        return random.nextInt(1000);
    }
    
    public String complexMethod(int a, int b, int c, int d) {
        if (a > 0) {
            if (b > 0) {
                if (c > 0) {
                    if (d > 0) {
                        if (a > b) {
                            if (b > c) {
                                if (c > d) {
                                    return "case1";
                                } else {
                                    return "case2";
                                }
                            } else {
                                return "case3";
                            }
                        } else {
                            return "case4";
                        }
                    } else {
                        return "case5";
                    }
                } else {
                    return "case6";
                }
            } else {
                return "case7";
            }
        } else {
            return "case8";
        }
    }
    
    private void unusedMethod() {
        System.out.println("This method is never called");
    }
    
    public void multipleResourceLeaks(String input, String output) throws IOException {
        FileInputStream in = new FileInputStream(input);
        FileOutputStream out = new FileOutputStream(output);
        
        byte[] buffer = new byte[1024];
        int bytesRead = in.read(buffer);
        out.write(buffer, 0, bytesRead);
        
    }
    
    public void emptyCatchBlock() {
        try {
            int result = 10 / 0;
        } catch (Exception e) {
        }
    }
    
    public void unusedVariable() {
        int unusedVar = 42;
        System.out.println("Method executed");
    }
}
