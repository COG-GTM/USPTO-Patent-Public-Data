package gov.uspto.auth;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Configuration properties for the authentication framework.
 * 
 * This class manages authentication-related configuration including timeouts,
 * retry limits, and policy settings. Configuration can be loaded from properties
 * files or environment variables.
 * 
 * NIST 800-53 Controls:
 * - IA-5 (Authenticator Management)
 * - IA-11 (Re-authentication)
 */
public class AuthenticationConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationConfig.class);

    private static final String DEFAULT_CONFIG_FILE = "authentication.properties";
    
    private static final int DEFAULT_SESSION_TIMEOUT_MINUTES = 30;
    private static final int DEFAULT_MAX_LOGIN_ATTEMPTS = 3;
    private static final int DEFAULT_ACCOUNT_LOCKOUT_MINUTES = 15;
    private static final int DEFAULT_PASSWORD_MIN_LENGTH = 12;
    private static final int DEFAULT_TOKEN_EXPIRY_MINUTES = 60;

    private final Properties properties;

    public AuthenticationConfig() {
        this.properties = new Properties();
        loadDefaults();
        loadFromFile(DEFAULT_CONFIG_FILE);
        loadFromEnvironment();
    }

    public AuthenticationConfig(String configFile) {
        this.properties = new Properties();
        loadDefaults();
        loadFromFile(configFile);
        loadFromEnvironment();
    }

    private void loadDefaults() {
        properties.setProperty("auth.session.timeout.minutes", String.valueOf(DEFAULT_SESSION_TIMEOUT_MINUTES));
        properties.setProperty("auth.max.login.attempts", String.valueOf(DEFAULT_MAX_LOGIN_ATTEMPTS));
        properties.setProperty("auth.account.lockout.minutes", String.valueOf(DEFAULT_ACCOUNT_LOCKOUT_MINUTES));
        properties.setProperty("auth.password.min.length", String.valueOf(DEFAULT_PASSWORD_MIN_LENGTH));
        properties.setProperty("auth.token.expiry.minutes", String.valueOf(DEFAULT_TOKEN_EXPIRY_MINUTES));
        properties.setProperty("auth.require.strong.passwords", "true");
        properties.setProperty("auth.enable.mfa", "false");
    }

    private void loadFromFile(String configFile) {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(configFile)) {
            if (input != null) {
                properties.load(input);
                LOGGER.info("Loaded authentication configuration from {}", configFile);
            } else {
                LOGGER.debug("Configuration file {} not found, using defaults", configFile);
            }
        } catch (IOException e) {
            LOGGER.warn("Failed to load configuration file {}: {}", configFile, e.getMessage());
        }
    }

    private void loadFromEnvironment() {
        String sessionTimeout = System.getenv("AUTH_SESSION_TIMEOUT_MINUTES");
        if (sessionTimeout != null) {
            properties.setProperty("auth.session.timeout.minutes", sessionTimeout);
        }

        String maxAttempts = System.getenv("AUTH_MAX_LOGIN_ATTEMPTS");
        if (maxAttempts != null) {
            properties.setProperty("auth.max.login.attempts", maxAttempts);
        }

        String lockoutMinutes = System.getenv("AUTH_ACCOUNT_LOCKOUT_MINUTES");
        if (lockoutMinutes != null) {
            properties.setProperty("auth.account.lockout.minutes", lockoutMinutes);
        }

        String passwordMinLength = System.getenv("AUTH_PASSWORD_MIN_LENGTH");
        if (passwordMinLength != null) {
            properties.setProperty("auth.password.min.length", passwordMinLength);
        }

        String tokenExpiry = System.getenv("AUTH_TOKEN_EXPIRY_MINUTES");
        if (tokenExpiry != null) {
            properties.setProperty("auth.token.expiry.minutes", tokenExpiry);
        }

        String requireStrongPasswords = System.getenv("AUTH_REQUIRE_STRONG_PASSWORDS");
        if (requireStrongPasswords != null) {
            properties.setProperty("auth.require.strong.passwords", requireStrongPasswords);
        }

        String enableMfa = System.getenv("AUTH_ENABLE_MFA");
        if (enableMfa != null) {
            properties.setProperty("auth.enable.mfa", enableMfa);
        }
    }

    public int getSessionTimeoutMinutes() {
        return getIntProperty("auth.session.timeout.minutes", DEFAULT_SESSION_TIMEOUT_MINUTES);
    }

    public int getMaxLoginAttempts() {
        return getIntProperty("auth.max.login.attempts", DEFAULT_MAX_LOGIN_ATTEMPTS);
    }

    public int getAccountLockoutMinutes() {
        return getIntProperty("auth.account.lockout.minutes", DEFAULT_ACCOUNT_LOCKOUT_MINUTES);
    }

    public int getPasswordMinLength() {
        return getIntProperty("auth.password.min.length", DEFAULT_PASSWORD_MIN_LENGTH);
    }

    public int getTokenExpiryMinutes() {
        return getIntProperty("auth.token.expiry.minutes", DEFAULT_TOKEN_EXPIRY_MINUTES);
    }

    public boolean isRequireStrongPasswords() {
        return getBooleanProperty("auth.require.strong.passwords", true);
    }

    public boolean isEnableMfa() {
        return getBooleanProperty("auth.enable.mfa", false);
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    private int getIntProperty(String key, int defaultValue) {
        String value = properties.getProperty(key);
        if (value != null) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                LOGGER.warn("Invalid integer value for {}: {}, using default {}", key, value, defaultValue);
            }
        }
        return defaultValue;
    }

    private boolean getBooleanProperty(String key, boolean defaultValue) {
        String value = properties.getProperty(key);
        if (value != null) {
            return Boolean.parseBoolean(value);
        }
        return defaultValue;
    }

    @Override
    public String toString() {
        return "AuthenticationConfig [sessionTimeoutMinutes=" + getSessionTimeoutMinutes() + 
               ", maxLoginAttempts=" + getMaxLoginAttempts() + 
               ", accountLockoutMinutes=" + getAccountLockoutMinutes() + 
               ", passwordMinLength=" + getPasswordMinLength() + 
               ", tokenExpiryMinutes=" + getTokenExpiryMinutes() + 
               ", requireStrongPasswords=" + isRequireStrongPasswords() + 
               ", enableMfa=" + isEnableMfa() + "]";
    }
}
