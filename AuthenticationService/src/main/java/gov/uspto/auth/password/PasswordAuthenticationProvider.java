package gov.uspto.auth.password;

import gov.uspto.auth.authenticator.AuthenticatorException;
import gov.uspto.auth.authenticator.AuthenticatorManager;
import gov.uspto.auth.authenticator.AuthenticatorStatus;
import gov.uspto.auth.core.AuthenticationException;
import gov.uspto.auth.core.AuthenticationProvider;
import gov.uspto.auth.core.AuthenticationResult;
import gov.uspto.auth.core.Credential;
import gov.uspto.auth.core.Principal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;

/**
 * Authentication provider for password-based authentication.
 * 
 * Integrates with AuthenticatorManager to:
 * - Verify passwords against stored hashes
 * - Track failed authentication attempts
 * - Enforce account lockout policies
 * - Update authenticator status
 * 
 * NIST 800-53 Controls: IA-5(1) (Password-based Authentication)
 */
public class PasswordAuthenticationProvider implements AuthenticationProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(PasswordAuthenticationProvider.class);

    private final AuthenticatorManager authenticatorManager;
    private final PasswordHasher passwordHasher;
    private final PasswordPolicy policy;

    /**
     * Creates a password authentication provider.
     * 
     * @param authenticatorManager the authenticator manager
     * @param passwordHasher the password hasher
     * @param policy the password policy
     */
    public PasswordAuthenticationProvider(
            AuthenticatorManager authenticatorManager,
            PasswordHasher passwordHasher,
            PasswordPolicy policy) {
        if (authenticatorManager == null) {
            throw new IllegalArgumentException("Authenticator manager cannot be null");
        }
        if (passwordHasher == null) {
            throw new IllegalArgumentException("Password hasher cannot be null");
        }
        if (policy == null) {
            throw new IllegalArgumentException("Password policy cannot be null");
        }
        this.authenticatorManager = authenticatorManager;
        this.passwordHasher = passwordHasher;
        this.policy = policy;
    }

    @Override
    public AuthenticationResult authenticate(Credential credential) throws AuthenticationException {
        if (credential == null) {
            throw new IllegalArgumentException("Credential cannot be null");
        }

        if (!(credential instanceof PasswordCredential)) {
            throw new AuthenticationException("INVALID_CREDENTIAL_TYPE", 
                    "Expected PasswordCredential but got " + credential.getClass().getSimpleName());
        }

        PasswordCredential passwordCredential = (PasswordCredential) credential;
        String identifier = passwordCredential.getIdentifier();

        LOGGER.debug("Attempting password authentication for identifier: {}", identifier);

        try {
            PasswordAuthenticator authenticator = (PasswordAuthenticator) authenticatorManager.getAuthenticator(
                    identifier, 
                    gov.uspto.auth.authenticator.AuthenticatorType.PASSWORD);

            if (authenticator == null) {
                LOGGER.warn("No password authenticator found for identifier: {}", identifier);
                return AuthenticationResult.failure("AUTHENTICATOR_NOT_FOUND", 
                        "No password authenticator found for identifier");
            }

            if (authenticator.getStatus() == AuthenticatorStatus.REVOKED) {
                LOGGER.warn("Attempted authentication with revoked authenticator: {}", identifier);
                return AuthenticationResult.failure("AUTHENTICATOR_REVOKED", 
                        "Authenticator has been revoked");
            }

            if (authenticator.isLocked()) {
                LOGGER.warn("Attempted authentication with locked authenticator: {}", identifier);
                return AuthenticationResult.failure("AUTHENTICATOR_LOCKED", 
                        "Account is locked due to too many failed attempts");
            }

            if (authenticator.isExpired()) {
                LOGGER.warn("Attempted authentication with expired authenticator: {}", identifier);
                return AuthenticationResult.failure("AUTHENTICATOR_EXPIRED", 
                        "Password has expired and must be changed");
            }

            boolean passwordMatches = passwordHasher.verifyPassword(
                    passwordCredential.getPassword(), 
                    authenticator.getPasswordHash());

            if (passwordMatches) {
                LOGGER.info("Password authentication successful for identifier: {}", identifier);
                
                Principal principal = new Principal.Builder()
                        .identifier(identifier)
                        .name(identifier)
                        .authenticationType("password")
                        .authenticationTime(Instant.now())
                        .build();

                return AuthenticationResult.success(principal, "Password authentication successful");
            } else {
                LOGGER.warn("Password authentication failed for identifier: {}", identifier);
                return AuthenticationResult.failure("INVALID_PASSWORD", 
                        "Invalid password");
            }

        } catch (AuthenticatorException e) {
            LOGGER.error("Authenticator error during password authentication for identifier: " + identifier, e);
            throw new AuthenticationException("AUTHENTICATOR_ERROR", 
                    "Failed to authenticate due to authenticator error", e);
        } finally {
            passwordCredential.clear();
        }
    }

    @Override
    public boolean supports(Class<? extends Credential> credentialClass) {
        return PasswordCredential.class.isAssignableFrom(credentialClass);
    }

    @Override
    public String getProviderName() {
        return "PasswordAuthenticationProvider";
    }

    /**
     * Gets the password policy used by this provider.
     * 
     * @return the password policy
     */
    public PasswordPolicy getPolicy() {
        return policy;
    }
}
