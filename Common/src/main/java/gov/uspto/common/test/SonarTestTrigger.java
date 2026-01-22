//trigger scan
package gov.uspto.common.test;

import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

public class SonarTestTrigger {
    
    private static final Logger LOGGER = Logger.getLogger(SonarTestTrigger.class.getName());
    private final SecureRandom secureRandom = new SecureRandom();
    
    public void vulnerableMethod(String userInput, Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            String query = "SELECT * FROM users WHERE name = '" + userInput + "'";
            stmt.executeQuery(query);
        }
    }
    
    public int buggyMethod() {
        if (secureRandom.nextInt() == secureRandom.nextInt()) {
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
        
        if (x == 1 && y == 2 && z == 3 && a == 4 && b == 5) {
            LOGGER.info("Deeply nested code");
        }
    }
    
    public void emptyExceptionHandler() {
        try {
            int unused = 10 / 0;
        } catch (ArithmeticException e) {
            LOGGER.warning("Division by zero occurred: " + e.getMessage());
        }
    }
}
