package gov.uspto.auth.mfa;

import gov.uspto.auth.authenticator.Authenticator;
import gov.uspto.auth.authenticator.AuthenticatorType;
import gov.uspto.auth.core.AuthenticationException;
import gov.uspto.auth.core.AuthenticationProvider;
import gov.uspto.auth.core.AuthenticationResult;
import gov.uspto.auth.core.Credential;
import gov.uspto.auth.core.Principal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Multi-factor authenticator coordinator.
 * 
 * Coordinates multiple authentication providers to enforce MFA policies:
 * - Validates credentials against multiple factors
 * - Enforces minimum factor requirements
 * - Ensures required authenticator types are satisfied
 * - Merges authentication results
 * 
 * NIST 800-53 Controls: IA-2(1) (Multi-factor Authentication)
 */
public class MultiFactorAuthenticator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MultiFactorAuthenticator.class);

    private final Map<AuthenticatorType, AuthenticationProvider> providers;
    private final MFAPolicy policy;

    /**
     * Creates a multi-factor authenticator.
     * 
     * @param policy the MFA policy
     */
    public MultiFactorAuthenticator(MFAPolicy policy) {
        if (policy == null) {
            throw new IllegalArgumentException("MFA policy cannot be null");
        }
        this.policy = policy;
        this.providers = new HashMap<>();
    }

    /**
     * Registers an authentication provider for a specific authenticator type.
     * 
     * @param type the authenticator type
     * @param provider the authentication provider
     */
    public void registerProvider(AuthenticatorType type, AuthenticationProvider provider) {
        if (type == null) {
            throw new IllegalArgumentException("Authenticator type cannot be null");
        }
        if (provider == null) {
            throw new IllegalArgumentException("Authentication provider cannot be null");
        }
        providers.put(type, provider);
        LOGGER.debug("Registered authentication provider for type: {}", type);
    }

    /**
     * Authenticates using multiple credentials according to the MFA policy.
     * 
     * @param credentials list of credentials to authenticate
     * @return the authentication result
     * @throws AuthenticationException if authentication fails
     */
    public AuthenticationResult authenticate(List<Credential> credentials) throws AuthenticationException {
        if (credentials == null || credentials.isEmpty()) {
            throw new IllegalArgumentException("Credentials cannot be null or empty");
        }

        LOGGER.debug("Attempting MFA authentication with {} credentials", credentials.size());

        if (policy.isMfaRequired() && credentials.size() < policy.getMinimumFactors()) {
            LOGGER.warn("MFA authentication failed: insufficient factors (required: {}, provided: {})", 
                    policy.getMinimumFactors(), credentials.size());
            return AuthenticationResult.failure("INSUFFICIENT_FACTORS", 
                    "MFA requires at least " + policy.getMinimumFactors() + " factors");
        }

        List<AuthenticationResult> results = new ArrayList<>();
        Set<AuthenticatorType> satisfiedTypes = new HashSet<>();
        String identifier = null;

        for (Credential credential : credentials) {
            if (identifier == null) {
                identifier = credential.getIdentifier();
            } else if (!identifier.equals(credential.getIdentifier())) {
                LOGGER.warn("MFA authentication failed: credentials have different identifiers");
                return AuthenticationResult.failure("IDENTIFIER_MISMATCH", 
                        "All credentials must be for the same identifier");
            }

            AuthenticationProvider provider = findProvider(credential);
            if (provider == null) {
                LOGGER.warn("No provider found for credential type: {}", credential.getCredentialType());
                return AuthenticationResult.failure("UNSUPPORTED_CREDENTIAL_TYPE", 
                        "No provider found for credential type: " + credential.getCredentialType());
            }

            try {
                AuthenticationResult result = provider.authenticate(credential);
                results.add(result);

                if (result.isSuccess()) {
                    AuthenticatorType type = getAuthenticatorType(credential);
                    if (type != null) {
                        satisfiedTypes.add(type);
                    }
                } else {
                    LOGGER.warn("MFA authentication failed: factor authentication failed - {}", 
                            result.getErrorMessage());
                    return result;
                }
            } catch (AuthenticationException e) {
                LOGGER.error("MFA authentication error for credential type: " + credential.getCredentialType(), e);
                return AuthenticationResult.failure("AUTHENTICATION_ERROR", 
                        "Authentication failed: " + e.getMessage());
            }
        }

        if (!validatePolicy(satisfiedTypes)) {
            LOGGER.warn("MFA authentication failed: policy requirements not satisfied");
            return AuthenticationResult.failure("POLICY_NOT_SATISFIED", 
                    "MFA policy requirements not satisfied");
        }

        if (results.isEmpty() || !results.get(0).isSuccess()) {
            return AuthenticationResult.failure("AUTHENTICATION_FAILED", "MFA authentication failed");
        }

        Principal principal = results.get(0).getPrincipal();
        if (principal != null) {
            principal = new Principal.Builder()
                    .identifier(principal.getIdentifier())
                    .name(principal.getName())
                    .roles(principal.getRoles())
                    .authenticationType("mfa")
                    .authenticationTime(Instant.now())
                    .build();
        }

        LOGGER.info("MFA authentication successful for identifier: {} with {} factors", 
                identifier, satisfiedTypes.size());

        return AuthenticationResult.success(principal, 
                "MFA authentication successful with " + satisfiedTypes.size() + " factors");
    }

    /**
     * Validates that the satisfied authenticator types meet the policy requirements.
     * 
     * @param satisfiedTypes the set of satisfied authenticator types
     * @return true if policy is satisfied, false otherwise
     */
    private boolean validatePolicy(Set<AuthenticatorType> satisfiedTypes) {
        if (policy.isMfaRequired() && satisfiedTypes.size() < policy.getMinimumFactors()) {
            LOGGER.debug("Policy validation failed: insufficient factors (required: {}, satisfied: {})", 
                    policy.getMinimumFactors(), satisfiedTypes.size());
            return false;
        }

        for (AuthenticatorType requiredType : policy.getRequiredTypes()) {
            if (!satisfiedTypes.contains(requiredType)) {
                LOGGER.debug("Policy validation failed: required type not satisfied: {}", requiredType);
                return false;
            }
        }

        for (AuthenticatorType satisfiedType : satisfiedTypes) {
            if (!policy.isTypeAllowed(satisfiedType)) {
                LOGGER.debug("Policy validation failed: type not allowed: {}", satisfiedType);
                return false;
            }
        }

        LOGGER.debug("Policy validation passed with {} satisfied factors", satisfiedTypes.size());
        return true;
    }

    /**
     * Finds an authentication provider that supports the given credential.
     * 
     * @param credential the credential
     * @return the authentication provider, or null if not found
     */
    private AuthenticationProvider findProvider(Credential credential) {
        for (AuthenticationProvider provider : providers.values()) {
            if (provider.supports(credential.getClass())) {
                return provider;
            }
        }
        return null;
    }

    /**
     * Gets the authenticator type for the given credential.
     * 
     * @param credential the credential
     * @return the authenticator type, or null if not determined
     */
    private AuthenticatorType getAuthenticatorType(Credential credential) {
        String credentialType = credential.getCredentialType();
        try {
            return AuthenticatorType.fromValue(credentialType);
        } catch (IllegalArgumentException e) {
            LOGGER.warn("Unknown credential type: {}", credentialType);
            return null;
        }
    }

    /**
     * Gets the MFA policy used by this authenticator.
     * 
     * @return the MFA policy
     */
    public MFAPolicy getPolicy() {
        return policy;
    }

    /**
     * Gets the registered authentication providers.
     * 
     * @return map of authenticator type to provider
     */
    public Map<AuthenticatorType, AuthenticationProvider> getProviders() {
        return new HashMap<>(providers);
    }
}
