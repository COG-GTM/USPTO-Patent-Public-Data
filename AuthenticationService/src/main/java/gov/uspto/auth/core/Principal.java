package gov.uspto.auth.core;

import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents an authenticated user or service principal.
 * 
 * A principal is an entity that has been successfully authenticated and contains
 * information about the user or service, including their identifier, roles,
 * and authentication metadata.
 * 
 * NIST 800-53 Controls: IA-2 (Identification and Authentication), AC-2 (Account Management)
 */
public class Principal {

    private final String identifier;
    private final String name;
    private final Set<String> roles;
    private final Instant authenticationTime;
    private final String authenticationType;
    private final boolean serviceAccount;

    private Principal(Builder builder) {
        this.identifier = builder.identifier;
        this.name = builder.name;
        this.roles = Collections.unmodifiableSet(new HashSet<>(builder.roles));
        this.authenticationTime = builder.authenticationTime != null ? builder.authenticationTime : Instant.now();
        this.authenticationType = builder.authenticationType;
        this.serviceAccount = builder.serviceAccount;
    }

    /**
     * Gets the unique identifier for this principal.
     * 
     * @return the principal identifier
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Gets the display name for this principal.
     * 
     * @return the principal name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the roles assigned to this principal.
     * 
     * @return an unmodifiable set of role names
     */
    public Set<String> getRoles() {
        return roles;
    }

    /**
     * Checks if this principal has the specified role.
     * 
     * @param role the role to check
     * @return true if the principal has the role, false otherwise
     */
    public boolean hasRole(String role) {
        return roles.contains(role);
    }

    /**
     * Checks if this principal has any of the specified roles.
     * 
     * @param roles the roles to check
     * @return true if the principal has at least one of the roles, false otherwise
     */
    public boolean hasAnyRole(String... roles) {
        for (String role : roles) {
            if (this.roles.contains(role)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the timestamp when this principal was authenticated.
     * 
     * @return the authentication timestamp
     */
    public Instant getAuthenticationTime() {
        return authenticationTime;
    }

    /**
     * Gets the type of authentication used (e.g., "password", "token", "certificate").
     * 
     * @return the authentication type
     */
    public String getAuthenticationType() {
        return authenticationType;
    }

    /**
     * Checks if this principal represents a service account.
     * 
     * @return true if this is a service account, false if it's a user account
     */
    public boolean isServiceAccount() {
        return serviceAccount;
    }

    @Override
    public String toString() {
        return "Principal{" +
                "identifier='" + identifier + '\'' +
                ", name='" + name + '\'' +
                ", roles=" + roles +
                ", authenticationTime=" + authenticationTime +
                ", authenticationType='" + authenticationType + '\'' +
                ", serviceAccount=" + serviceAccount +
                '}';
    }

    /**
     * Builder for creating Principal instances.
     */
    public static class Builder {
        private String identifier;
        private String name;
        private Set<String> roles = new HashSet<>();
        private Instant authenticationTime;
        private String authenticationType;
        private boolean serviceAccount = false;

        public Builder identifier(String identifier) {
            this.identifier = identifier;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder addRole(String role) {
            this.roles.add(role);
            return this;
        }

        public Builder roles(Set<String> roles) {
            this.roles = new HashSet<>(roles);
            return this;
        }

        public Builder authenticationTime(Instant authenticationTime) {
            this.authenticationTime = authenticationTime;
            return this;
        }

        public Builder authenticationType(String authenticationType) {
            this.authenticationType = authenticationType;
            return this;
        }

        public Builder serviceAccount(boolean serviceAccount) {
            this.serviceAccount = serviceAccount;
            return this;
        }

        public Principal build() {
            if (identifier == null || identifier.trim().isEmpty()) {
                throw new IllegalArgumentException("Principal identifier is required");
            }
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("Principal name is required");
            }
            return new Principal(this);
        }
    }
}
