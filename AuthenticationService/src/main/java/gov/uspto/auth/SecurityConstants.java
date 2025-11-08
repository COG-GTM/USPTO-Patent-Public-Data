package gov.uspto.auth;

/**
 * Security-related constants for the authentication framework.
 * 
 * This class defines constants for NIST 800-53 control mappings, authentication
 * timeout values, encryption parameters, and other security-related settings.
 * 
 * NIST 800-53 Control Mappings:
 * - IA-1: Identification and Authentication Policy and Procedures
 * - IA-2: Identification and Authentication (Organizational Users)
 * - IA-4: Identifier Management
 * - IA-5: Authenticator Management
 * - IA-9: Service Identification and Authentication
 * - IA-11: Re-authentication
 * - IA-12: Identity Proofing
 */
public final class SecurityConstants {

    private SecurityConstants() {
    }

    /**
     * NIST 800-53 Control Identifiers
     */
    public static final class NistControls {
        public static final String IA_1 = "IA-1";
        public static final String IA_2 = "IA-2";
        public static final String IA_4 = "IA-4";
        public static final String IA_5 = "IA-5";
        public static final String IA_9 = "IA-9";
        public static final String IA_11 = "IA-11";
        public static final String IA_12 = "IA-12";

        private NistControls() {
        }
    }

    /**
     * Authentication timeout values (in minutes)
     */
    public static final class Timeouts {
        public static final int DEFAULT_SESSION_TIMEOUT = 30;
        public static final int PRIVILEGED_SESSION_TIMEOUT = 15;
        public static final int TOKEN_EXPIRY = 60;
        public static final int REFRESH_TOKEN_EXPIRY = 10080;
        public static final int PASSWORD_RESET_TOKEN_EXPIRY = 60;

        private Timeouts() {
        }
    }

    /**
     * Password policy constants
     */
    public static final class PasswordPolicy {
        public static final int MIN_LENGTH = 12;
        public static final int MAX_LENGTH = 128;
        public static final int MIN_UPPERCASE = 1;
        public static final int MIN_LOWERCASE = 1;
        public static final int MIN_DIGITS = 1;
        public static final int MIN_SPECIAL_CHARS = 1;
        public static final int PASSWORD_HISTORY_SIZE = 5;
        public static final int MAX_PASSWORD_AGE_DAYS = 90;

        private PasswordPolicy() {
        }
    }

    /**
     * Account lockout policy constants
     */
    public static final class LockoutPolicy {
        public static final int MAX_LOGIN_ATTEMPTS = 3;
        public static final int LOCKOUT_DURATION_MINUTES = 15;
        public static final int FAILED_ATTEMPT_WINDOW_MINUTES = 15;

        private LockoutPolicy() {
        }
    }

    /**
     * Encryption and hashing parameters
     */
    public static final class Crypto {
        public static final String PASSWORD_HASH_ALGORITHM = "bcrypt";
        public static final int BCRYPT_LOG_ROUNDS = 12;
        public static final String TOKEN_HASH_ALGORITHM = "SHA-256";
        public static final String ENCRYPTION_ALGORITHM = "AES";
        public static final int ENCRYPTION_KEY_SIZE = 256;

        private Crypto() {
        }
    }

    /**
     * Authentication types
     */
    public static final class AuthType {
        public static final String PASSWORD = "PASSWORD";
        public static final String TOKEN = "TOKEN";
        public static final String CERTIFICATE = "CERTIFICATE";
        public static final String API_KEY = "API_KEY";
        public static final String BIOMETRIC = "BIOMETRIC";
        public static final String MFA = "MFA";

        private AuthType() {
        }
    }

    /**
     * Role names
     */
    public static final class Roles {
        public static final String ADMIN = "ADMIN";
        public static final String USER = "USER";
        public static final String SERVICE = "SERVICE";
        public static final String AUDITOR = "AUDITOR";

        private Roles() {
        }
    }

    /**
     * Audit event types for NIST 800-53 AU-2 compliance
     */
    public static final class AuditEvents {
        public static final String LOGIN_SUCCESS = "LOGIN_SUCCESS";
        public static final String LOGIN_FAILURE = "LOGIN_FAILURE";
        public static final String LOGOUT = "LOGOUT";
        public static final String PASSWORD_CHANGE = "PASSWORD_CHANGE";
        public static final String ACCOUNT_LOCKED = "ACCOUNT_LOCKED";
        public static final String ACCOUNT_UNLOCKED = "ACCOUNT_UNLOCKED";
        public static final String PRIVILEGE_ESCALATION = "PRIVILEGE_ESCALATION";
        public static final String TOKEN_ISSUED = "TOKEN_ISSUED";
        public static final String TOKEN_REVOKED = "TOKEN_REVOKED";

        private AuditEvents() {
        }
    }
}
