//testing SQ scan
package gov.uspto.common;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Demo file to trigger SonarCloud workflow for customer demonstration.
 * Fixed security vulnerabilities and code quality issues.
 */
public class DemoSecurityIssue {
    
    private static final String DB_URL = "jdbc:mysql://localhost:3306/patents";
    
    /**
     * Secure method with proper resource management and SQL injection prevention.
     * Uses PreparedStatement to prevent SQL injection.
     * Uses try-with-resources to ensure proper resource cleanup.
     */
    public String getUserData(String userId) throws SQLException {
        String dbUser = System.getenv("DB_USER");
        String dbPassword = System.getenv("DB_PASSWORD");
        
        if (dbUser == null || dbPassword == null) {
            throw new SQLException("Database credentials not configured");
        }
        
        String query = "SELECT * FROM users WHERE id = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, dbUser, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                StringBuilder result = new StringBuilder();
                while (rs.next()) {
                    result.append(rs.getString("name"));
                }
                return result.toString();
            }
        }
    }
}
