package gov.uspto.auth.password;

import gov.uspto.auth.core.AuthenticationConfig;

/**
 * Password policy enforcing NIST 800-53 IA-5(1) requirements.
 * 
 * Configurable policy parameters include:
 * - Minimum length (default: 12 characters)
 * - Complexity requirements (uppercase, lowercase, digit, special character)
 * - Password history (prevent reuse of last N passwords, default: 5)
 * - Password expiration (default: 90 days)
 * - Account lockout after failed attempts (default: 10 attempts)
 * 
 * NIST 800-53 Controls: IA-5(1) (Password-based Authentication)
 */
public class PasswordPolicy {

    private final int minLength;
    private final boolean requireUppercase;
    private final boolean requireLowercase;
    private final boolean requireDigit;
    private final boolean requireSpecialChar;
    private final int passwordHistorySize;
    private final int passwordExpiryDays;
    private final int maxFailedAttempts;
    private final int lockoutWindowMinutes;
    private final int lockoutDurationMinutes;

    private PasswordPolicy(Builder builder) {
        this.minLength = builder.minLength;
        this.requireUppercase = builder.requireUppercase;
        this.requireLowercase = builder.requireLowercase;
        this.requireDigit = builder.requireDigit;
        this.requireSpecialChar = builder.requireSpecialChar;
        this.passwordHistorySize = builder.passwordHistorySize;
        this.passwordExpiryDays = builder.passwordExpiryDays;
        this.maxFailedAttempts = builder.maxFailedAttempts;
        this.lockoutWindowMinutes = builder.lockoutWindowMinutes;
        this.lockoutDurationMinutes = builder.lockoutDurationMinutes;
    }

    /**
     * Creates a default NIST 800-53 compliant password policy.
     * 
     * @return the default policy
     */
    public static PasswordPolicy createDefault() {
        return new Builder().build();
    }

    /**
     * Creates a password policy from configuration.
     * 
     * @param config the authentication configuration
     * @return the configured policy
     */
    public static PasswordPolicy fromConfig(AuthenticationConfig config) {
        return new Builder()
                .minLength(config.getPasswordMinLength())
                .requireUppercase(config.isRequirePasswordComplexity())
                .requireLowercase(config.isRequirePasswordComplexity())
                .requireDigit(config.isRequirePasswordComplexity())
                .requireSpecialChar(config.isRequirePasswordComplexity())
                .passwordHistorySize(5)
                .passwordExpiryDays(config.getPasswordExpirationDays())
                .maxFailedAttempts(config.getMaxLoginAttempts())
                .lockoutWindowMinutes(15)
                .lockoutDurationMinutes(config.getAccountLockoutDurationMinutes())
                .build();
    }

    public int getMinLength() {
        return minLength;
    }

    public boolean isRequireUppercase() {
        return requireUppercase;
    }

    public boolean isRequireLowercase() {
        return requireLowercase;
    }

    public boolean isRequireDigit() {
        return requireDigit;
    }

    public boolean isRequireSpecialChar() {
        return requireSpecialChar;
    }

    public int getPasswordHistorySize() {
        return passwordHistorySize;
    }

    public int getPasswordExpiryDays() {
        return passwordExpiryDays;
    }

    public int getMaxFailedAttempts() {
        return maxFailedAttempts;
    }

    public int getLockoutWindowMinutes() {
        return lockoutWindowMinutes;
    }

    public int getLockoutDurationMinutes() {
        return lockoutDurationMinutes;
    }

    /**
     * Builder for creating PasswordPolicy instances.
     */
    public static class Builder {
        private int minLength = 12;
        private boolean requireUppercase = true;
        private boolean requireLowercase = true;
        private boolean requireDigit = true;
        private boolean requireSpecialChar = true;
        private int passwordHistorySize = 5;
        private int passwordExpiryDays = 90;
        private int maxFailedAttempts = 10;
        private int lockoutWindowMinutes = 15;
        private int lockoutDurationMinutes = 30;

        public Builder minLength(int minLength) {
            if (minLength < 8) {
                throw new IllegalArgumentException("Minimum length must be at least 8 characters");
            }
            this.minLength = minLength;
            return this;
        }

        public Builder requireUppercase(boolean requireUppercase) {
            this.requireUppercase = requireUppercase;
            return this;
        }

        public Builder requireLowercase(boolean requireLowercase) {
            this.requireLowercase = requireLowercase;
            return this;
        }

        public Builder requireDigit(boolean requireDigit) {
            this.requireDigit = requireDigit;
            return this;
        }

        public Builder requireSpecialChar(boolean requireSpecialChar) {
            this.requireSpecialChar = requireSpecialChar;
            return this;
        }

        public Builder passwordHistorySize(int passwordHistorySize) {
            if (passwordHistorySize < 0) {
                throw new IllegalArgumentException("Password history size cannot be negative");
            }
            this.passwordHistorySize = passwordHistorySize;
            return this;
        }

        public Builder passwordExpiryDays(int passwordExpiryDays) {
            if (passwordExpiryDays < 0) {
                throw new IllegalArgumentException("Password expiry days cannot be negative");
            }
            this.passwordExpiryDays = passwordExpiryDays;
            return this;
        }

        public Builder maxFailedAttempts(int maxFailedAttempts) {
            if (maxFailedAttempts < 1) {
                throw new IllegalArgumentException("Max failed attempts must be at least 1");
            }
            this.maxFailedAttempts = maxFailedAttempts;
            return this;
        }

        public Builder lockoutWindowMinutes(int lockoutWindowMinutes) {
            if (lockoutWindowMinutes < 0) {
                throw new IllegalArgumentException("Lockout window minutes cannot be negative");
            }
            this.lockoutWindowMinutes = lockoutWindowMinutes;
            return this;
        }

        public Builder lockoutDurationMinutes(int lockoutDurationMinutes) {
            if (lockoutDurationMinutes < 0) {
                throw new IllegalArgumentException("Lockout duration minutes cannot be negative");
            }
            this.lockoutDurationMinutes = lockoutDurationMinutes;
            return this;
        }

        public PasswordPolicy build() {
            return new PasswordPolicy(this);
        }
    }

    @Override
    public String toString() {
        return "PasswordPolicy{" +
                "minLength=" + minLength +
                ", requireUppercase=" + requireUppercase +
                ", requireLowercase=" + requireLowercase +
                ", requireDigit=" + requireDigit +
                ", requireSpecialChar=" + requireSpecialChar +
                ", passwordHistorySize=" + passwordHistorySize +
                ", passwordExpiryDays=" + passwordExpiryDays +
                ", maxFailedAttempts=" + maxFailedAttempts +
                ", lockoutWindowMinutes=" + lockoutWindowMinutes +
                ", lockoutDurationMinutes=" + lockoutDurationMinutes +
                '}';
    }
}
