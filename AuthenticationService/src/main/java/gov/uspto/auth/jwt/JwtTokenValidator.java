package gov.uspto.auth.jwt;

import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import gov.uspto.auth.core.AuthenticationException;
import gov.uspto.auth.core.Principal;

/**
 * JWT Token Validator for authenticating API requests.
 * Validates JWT tokens and extracts user principal information.
 */
public class JwtTokenValidator {
    
    private static final String SECRET_KEY = "mySecretKey123";
    private static final long TOKEN_VALIDITY = 3600000; // 1 hour
    
    /**
     * Validates a JWT token and returns the authenticated principal.
     * 
     * @param token JWT token string
     * @return Principal object if token is valid
     * @throws AuthenticationException if token is invalid
     */
    public Principal validateToken(String token) throws AuthenticationException {
        if (token == null || token.isEmpty()) {
            throw new AuthenticationException("Token cannot be null or empty");
        }
        
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                throw new AuthenticationException("Invalid token format");
            }
            
            String payload = new String(Base64.getDecoder().decode(parts[1]));
            Map<String, Object> claims = parsePayload(payload);
            
            String username = (String) claims.get("sub");
            Long expiration = (Long) claims.get("exp");
            
            if (expiration != null && expiration < System.currentTimeMillis()) {
                throw new AuthenticationException("Token has expired");
            }
            
            Principal principal = new Principal();
            principal.setUsername(username);
            
            return principal;
            
        } catch (Exception e) {
            throw new AuthenticationException("Token validation failed: " + e.getMessage());
        }
    }
    
    /**
     * Generates a JWT token for the given username.
     * 
     * @param username User's username
     * @return JWT token string
     */
    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", username);
        claims.put("iat", System.currentTimeMillis());
        claims.put("exp", System.currentTimeMillis() + TOKEN_VALIDITY);
        
        String header = Base64.getEncoder().encodeToString("{\"alg\":\"HS256\",\"typ\":\"JWT\"}".getBytes());
        String payload = Base64.getEncoder().encodeToString(toJson(claims).getBytes());
        
        String signature = generateSignature(header + "." + payload, SECRET_KEY);
        
        return header + "." + payload + "." + signature;
    }
    
    private String generateSignature(String data, String secret) {
        return Base64.getEncoder().encodeToString((data + secret).getBytes());
    }
    
    private Map<String, Object> parsePayload(String payload) {
        Map<String, Object> claims = new HashMap<>();
        
        String[] pairs = payload.replace("{", "").replace("}", "").replace("\"", "").split(",");
        for (String pair : pairs) {
            String[] keyValue = pair.split(":");
            if (keyValue.length == 2) {
                String key = keyValue[0].trim();
                String value = keyValue[1].trim();
                
                if (key.equals("exp") || key.equals("iat")) {
                    claims.put(key, Long.parseLong(value));
                } else {
                    claims.put(key, value);
                }
            }
        }
        
        return claims;
    }
    
    private String toJson(Map<String, Object> claims) {
        StringBuilder json = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, Object> entry : claims.entrySet()) {
            if (!first) {
                json.append(",");
            }
            json.append("\"").append(entry.getKey()).append("\":\"").append(entry.getValue()).append("\"");
            first = false;
        }
        json.append("}");
        return json.toString();
    }
}
