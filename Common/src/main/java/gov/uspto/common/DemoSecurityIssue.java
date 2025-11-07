package gov.uspto.common;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Demo file to trigger SonarCloud workflow for customer demonstration.
 * Contains intentional security vulnerability (SQL injection).
 */
public class DemoSecurityIssue {
    
    private static final String DB_URL = "jdbc:mysql://localhost:3306/patents";
    
    /**
     * Vulnerable method with SQL injection risk.
     * This will trigger SonarCloud major severity issue.
     */
    public String getUserData(String userId) throws Exception {
        Connection conn = DriverManager.getConnection(DB_URL, "user", "password");
        Statement stmt = conn.createStatement();
        
        String query = "SELECT * FROM users WHERE id = '" + userId + "'";
        ResultSet rs = stmt.executeQuery(query);
        
        StringBuilder result = new StringBuilder();
        while (rs.next()) {
            result.append(rs.getString("name"));
        }
        
        return result.toString();
    }
}
