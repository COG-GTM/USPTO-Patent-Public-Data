package gov.uspto.auth.core;

import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents an authenticated user or service principal.
 * 
 * A principal is an entity that has been successfully authenticated and contains
 * information about the user's identity, roles, and authentication metadata.
 * 
 * NIST 800-53 Controls:
 * - IA-2 (Identification and Authentication)
 * - IA-4 (Identifier Management)
 */
public class Principal {

    private final String identifier;
    private final Set<String> roles;
    private final Instant authenticationTime;
    private final String authenticationType;

    public Principal(String identifier, Set<String> roles, String authenticationType) {
        if (identifier == null || identifier.trim().isEmpty()) {
            throw new IllegalArgumentException("Identifier cannot be null or empty");
        }
        this.identifier = identifier;
        this.roles = roles != null ? new HashSet<>(roles) : new HashSet<>();
        this.authenticationTime = Instant.now();
        this.authenticationType = authenticationType != null ? authenticationType : "UNKNOWN";
    }

    public String getIdentifier() {
        return identifier;
    }

    public Set<String> getRoles() {
        return Collections.unmodifiableSet(roles);
    }

    public Instant getAuthenticationTime() {
        return authenticationTime;
    }

    public String getAuthenticationType() {
        return authenticationType;
    }

    /**
     * Checks if the principal has a specific role.
     * 
     * @param role the role to check
     * @return true if the principal has the role, false otherwise
     */
    public boolean hasRole(String role) {
        return roles.contains(role);
    }

    /**
     * Checks if the principal has any of the specified roles.
     * 
     * @param requiredRoles the roles to check
     * @return true if the principal has at least one of the roles, false otherwise
     */
    public boolean hasAnyRole(Set<String> requiredRoles) {
        if (requiredRoles == null || requiredRoles.isEmpty()) {
            return false;
        }
        for (String role : requiredRoles) {
            if (roles.contains(role)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the principal has all of the specified roles.
     * 
     * @param requiredRoles the roles to check
     * @return true if the principal has all of the roles, false otherwise
     */
    public boolean hasAllRoles(Set<String> requiredRoles) {
        if (requiredRoles == null || requiredRoles.isEmpty()) {
            return true;
        }
        return roles.containsAll(requiredRoles);
    }

    @Override
    public String toString() {
        return "Principal [identifier=" + identifier + ", roles=" + roles + 
               ", authenticationTime=" + authenticationTime + 
               ", authenticationType=" + authenticationType + "]";
    }
}
