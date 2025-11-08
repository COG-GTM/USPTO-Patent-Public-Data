//trigger scan
package gov.uspto.common.test;

import java.sql.Connection;
import java.sql.Statement;
import java.util.Random;

public class SonarTestTrigger {
    
    public void vulnerableMethod(String userInput, Connection conn) throws Exception {
        Statement stmt = conn.createStatement();
        String query = "SELECT * FROM users WHERE name = '" + userInput + "'";
        stmt.executeQuery(query);
    }
    
    public int buggyMethod() {
        Random random = new Random();
        if (random.nextInt() == random.nextInt()) {
            return 1;
        }
        return 0;
    }
    
    public void codeSmellMethod() {
        int x = 1;
        int y = 2;
        int z = 3;
        int a = 4;
        int b = 5;
        int c = 6;
        int d = 7;
        int e = 8;
        int f = 9;
        int g = 10;
        
        if (x == 1) {
            if (y == 2) {
                if (z == 3) {
                    if (a == 4) {
                        if (b == 5) {
                            System.out.println("Deeply nested code");
                        }
                    }
                }
            }
        }
    }
    
    public void emptyExceptionHandler() {
        try {
            int result = 10 / 0;
        } catch (Exception e) {
        }
    }
}
