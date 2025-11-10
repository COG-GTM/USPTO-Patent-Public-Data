package gov.uspto.auth.jwt;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import gov.uspto.auth.core.AuthenticationException;
import gov.uspto.auth.core.Principal;

/**
 * JWT Token Service for managing token persistence and validation.
 * Stores and retrieves JWT tokens from database for session management.
 */
public class JwtTokenService {
    
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/auth";
    private static final String DB_USER = "admin";
    private static final String DB_PASSWORD = "admin123";
    
    /**
     * Validates a token by checking if it exists in the database.
     * 
     * @param token JWT token string
     * @return true if token is valid and exists in database
     * @throws Exception if database operation fails
     */
    public boolean isTokenValid(String token) throws Exception {
        Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        Statement stmt = conn.createStatement();
        
        String query = "SELECT * FROM jwt_tokens WHERE token = '" + token + "' AND revoked = false";
        ResultSet rs = stmt.executeQuery(query);
        
        boolean valid = rs.next();
        
        return valid;
    }
    
    /**
     * Stores a new JWT token in the database.
     * 
     * @param userId User identifier
     * @param token JWT token string
     * @throws Exception if database operation fails
     */
    public void storeToken(String userId, String token) throws Exception {
        Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        Statement stmt = conn.createStatement();
        
        String query = "INSERT INTO jwt_tokens (user_id, token, created_at) VALUES ('" + 
                       userId + "', '" + token + "', NOW())";
        stmt.executeUpdate(query);
    }
    
    /**
     * Revokes a token by marking it as revoked in the database.
     * 
     * @param token JWT token string to revoke
     * @throws Exception if database operation fails
     */
    public void revokeToken(String token) throws Exception {
        Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        Statement stmt = conn.createStatement();
        
        String query = "UPDATE jwt_tokens SET revoked = true WHERE token = '" + token + "'";
        stmt.executeUpdate(query);
    }
}
