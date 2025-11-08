package gov.uspto.auth.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Configuration properties for the authentication service.
 * 
 * This class manages authentication policies, timeouts, retry limits, and other
 * configuration parameters. Configuration can be loaded from properties files
 * or environment variables.
 * 
 * NIST 800-53 Controls: IA-5 (Authenticator Management), AC-7 (Unsuccessful Logon Attempts)
 */
public class AuthenticationConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationConfig.class);

    private static final String DEFAULT_CONFIG_FILE = "authentication.properties";

    private int sessionTimeoutMinutes = 30;
    private int maxLoginAttempts = 3;
    private int accountLockoutDurationMinutes = 15;
    private int passwordMinLength = 12;
    private int passwordExpirationDays = 90;
    private boolean requirePasswordComplexity = true;
    private int tokenExpirationMinutes = 60;
    private boolean enableAuditLogging = true;

    /**
     * Creates a default authentication configuration.
     */
    public AuthenticationConfig() {
        loadDefaults();
    }

    /**
     * Creates an authentication configuration from a properties file.
     * 
     * @param configFile the configuration file path
     */
    public AuthenticationConfig(String configFile) {
        loadFromFile(configFile);
    }

    /**
     * Loads default configuration values.
     */
    private void loadDefaults() {
        LOGGER.info("Loading default authentication configuration");
    }

    /**
     * Loads configuration from a properties file.
     * 
     * @param configFile the configuration file path
     */
    private void loadFromFile(String configFile) {
        Properties props = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(configFile)) {
            if (input != null) {
                props.load(input);
                loadFromProperties(props);
                LOGGER.info("Loaded authentication configuration from file: {}", configFile);
            } else {
                LOGGER.warn("Configuration file not found: {}, using defaults", configFile);
                loadDefaults();
            }
        } catch (IOException e) {
            LOGGER.error("Error loading configuration file: {}", configFile, e);
            loadDefaults();
        }
    }

    /**
     * Loads configuration from properties.
     * 
     * @param props the properties to load from
     */
    private void loadFromProperties(Properties props) {
        sessionTimeoutMinutes = getIntProperty(props, "auth.session.timeout.minutes", sessionTimeoutMinutes);
        maxLoginAttempts = getIntProperty(props, "auth.max.login.attempts", maxLoginAttempts);
        accountLockoutDurationMinutes = getIntProperty(props, "auth.account.lockout.minutes", accountLockoutDurationMinutes);
        passwordMinLength = getIntProperty(props, "auth.password.min.length", passwordMinLength);
        passwordExpirationDays = getIntProperty(props, "auth.password.expiration.days", passwordExpirationDays);
        requirePasswordComplexity = getBooleanProperty(props, "auth.password.require.complexity", requirePasswordComplexity);
        tokenExpirationMinutes = getIntProperty(props, "auth.token.expiration.minutes", tokenExpirationMinutes);
        enableAuditLogging = getBooleanProperty(props, "auth.audit.logging.enabled", enableAuditLogging);
    }

    /**
     * Gets an integer property value with a default fallback.
     */
    private int getIntProperty(Properties props, String key, int defaultValue) {
        String value = System.getenv(key.replace('.', '_').toUpperCase());
        if (value == null) {
            value = props.getProperty(key);
        }
        if (value != null) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                LOGGER.warn("Invalid integer value for {}: {}, using default: {}", key, value, defaultValue);
            }
        }
        return defaultValue;
    }

    /**
     * Gets a boolean property value with a default fallback.
     */
    private boolean getBooleanProperty(Properties props, String key, boolean defaultValue) {
        String value = System.getenv(key.replace('.', '_').toUpperCase());
        if (value == null) {
            value = props.getProperty(key);
        }
        if (value != null) {
            return Boolean.parseBoolean(value);
        }
        return defaultValue;
    }

    public int getSessionTimeoutMinutes() {
        return sessionTimeoutMinutes;
    }

    public void setSessionTimeoutMinutes(int sessionTimeoutMinutes) {
        this.sessionTimeoutMinutes = sessionTimeoutMinutes;
    }

    public int getMaxLoginAttempts() {
        return maxLoginAttempts;
    }

    public void setMaxLoginAttempts(int maxLoginAttempts) {
        this.maxLoginAttempts = maxLoginAttempts;
    }

    public int getAccountLockoutDurationMinutes() {
        return accountLockoutDurationMinutes;
    }

    public void setAccountLockoutDurationMinutes(int accountLockoutDurationMinutes) {
        this.accountLockoutDurationMinutes = accountLockoutDurationMinutes;
    }

    public int getPasswordMinLength() {
        return passwordMinLength;
    }

    public void setPasswordMinLength(int passwordMinLength) {
        this.passwordMinLength = passwordMinLength;
    }

    public int getPasswordExpirationDays() {
        return passwordExpirationDays;
    }

    public void setPasswordExpirationDays(int passwordExpirationDays) {
        this.passwordExpirationDays = passwordExpirationDays;
    }

    public boolean isRequirePasswordComplexity() {
        return requirePasswordComplexity;
    }

    public void setRequirePasswordComplexity(boolean requirePasswordComplexity) {
        this.requirePasswordComplexity = requirePasswordComplexity;
    }

    public int getTokenExpirationMinutes() {
        return tokenExpirationMinutes;
    }

    public void setTokenExpirationMinutes(int tokenExpirationMinutes) {
        this.tokenExpirationMinutes = tokenExpirationMinutes;
    }

    public boolean isEnableAuditLogging() {
        return enableAuditLogging;
    }

    public void setEnableAuditLogging(boolean enableAuditLogging) {
        this.enableAuditLogging = enableAuditLogging;
    }

    @Override
    public String toString() {
        return "AuthenticationConfig{" +
                "sessionTimeoutMinutes=" + sessionTimeoutMinutes +
                ", maxLoginAttempts=" + maxLoginAttempts +
                ", accountLockoutDurationMinutes=" + accountLockoutDurationMinutes +
                ", passwordMinLength=" + passwordMinLength +
                ", passwordExpirationDays=" + passwordExpirationDays +
                ", requirePasswordComplexity=" + requirePasswordComplexity +
                ", tokenExpirationMinutes=" + tokenExpirationMinutes +
                ", enableAuditLogging=" + enableAuditLogging +
                '}';
    }
}
