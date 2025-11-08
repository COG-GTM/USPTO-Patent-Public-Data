package gov.uspto.auth.core;

/**
 * Security-related constants for the authentication service.
 * 
 * This class defines constants for NIST 800-53 control mappings, authentication
 * timeout values, encryption parameters, and other security-related configuration.
 * 
 * NIST 800-53 Controls: Multiple (see individual constant documentation)
 */
public final class SecurityConstants {

    private SecurityConstants() {
    }

    /**
     * NIST 800-53 Control Identifiers
     */
    public static final class NIST_CONTROLS {
        public static final String IA_1 = "IA-1";
        public static final String IA_2 = "IA-2";
        public static final String IA_2_1 = "IA-2(1)";
        public static final String IA_2_2 = "IA-2(2)";
        public static final String IA_2_8 = "IA-2(8)";
        public static final String IA_2_12 = "IA-2(12)";
        public static final String IA_4 = "IA-4";
        public static final String IA_5 = "IA-5";
        public static final String IA_5_1 = "IA-5(1)";
        public static final String IA_5_2 = "IA-5(2)";
        public static final String IA_5_7 = "IA-5(7)";
        public static final String IA_8 = "IA-8";
        public static final String IA_9 = "IA-9";
        public static final String IA_11 = "IA-11";
        public static final String IA_12 = "IA-12";
        public static final String AC_2 = "AC-2";
        public static final String AC_7 = "AC-7";
        public static final String AU_2 = "AU-2";
        public static final String AU_3 = "AU-3";

        private NIST_CONTROLS() {
        }
    }

    /**
     * NIST 800-53 Control Descriptions
     */
    public static final class CONTROL_DESCRIPTIONS {
        public static final String IA_1_DESC = "Identification and Authentication Policy and Procedures";
        public static final String IA_2_DESC = "Identification and Authentication (Organizational Users)";
        public static final String IA_4_DESC = "Identifier Management";
        public static final String IA_5_DESC = "Authenticator Management";
        public static final String IA_9_DESC = "Service Identification and Authentication";
        public static final String IA_11_DESC = "Re-authentication";
        public static final String IA_12_DESC = "Identity Proofing";
        public static final String AC_7_DESC = "Unsuccessful Logon Attempts";
        public static final String AU_2_DESC = "Audit Events";

        private CONTROL_DESCRIPTIONS() {
        }
    }

    /**
     * Authentication timeout values (in minutes)
     */
    public static final class TIMEOUTS {
        public static final int DEFAULT_SESSION_TIMEOUT = 30;
        public static final int DEFAULT_TOKEN_EXPIRATION = 60;
        public static final int DEFAULT_REAUTHENTICATION_INTERVAL = 720;
        public static final int PRIVILEGED_SESSION_TIMEOUT = 15;
        public static final int SERVICE_TOKEN_EXPIRATION = 1440;

        private TIMEOUTS() {
        }
    }

    /**
     * Password policy constants
     */
    public static final class PASSWORD_POLICY {
        public static final int MIN_LENGTH = 12;
        public static final int MAX_LENGTH = 128;
        public static final int MIN_UPPERCASE = 1;
        public static final int MIN_LOWERCASE = 1;
        public static final int MIN_DIGITS = 1;
        public static final int MIN_SPECIAL_CHARS = 1;
        public static final int EXPIRATION_DAYS = 90;
        public static final int HISTORY_SIZE = 5;
        public static final int MAX_LOGIN_ATTEMPTS = 3;
        public static final int LOCKOUT_DURATION_MINUTES = 15;

        private PASSWORD_POLICY() {
        }
    }

    /**
     * Cryptographic parameters
     */
    public static final class CRYPTO {
        public static final String HASH_ALGORITHM = "bcrypt";
        public static final int BCRYPT_ROUNDS = 12;
        public static final String TOKEN_ALGORITHM = "SHA-256";
        public static final int TOKEN_LENGTH_BYTES = 32;
        public static final String ENCODING = "UTF-8";

        private CRYPTO() {
        }
    }

    /**
     * Audit event types
     */
    public static final class AUDIT_EVENTS {
        public static final String LOGIN_SUCCESS = "AUTH_LOGIN_SUCCESS";
        public static final String LOGIN_FAILURE = "AUTH_LOGIN_FAILURE";
        public static final String LOGOUT = "AUTH_LOGOUT";
        public static final String PASSWORD_CHANGE = "AUTH_PASSWORD_CHANGE";
        public static final String ACCOUNT_LOCKED = "AUTH_ACCOUNT_LOCKED";
        public static final String ACCOUNT_UNLOCKED = "AUTH_ACCOUNT_UNLOCKED";
        public static final String TOKEN_ISSUED = "AUTH_TOKEN_ISSUED";
        public static final String TOKEN_REVOKED = "AUTH_TOKEN_REVOKED";
        public static final String REAUTHENTICATION_REQUIRED = "AUTH_REAUTH_REQUIRED";

        private AUDIT_EVENTS() {
        }
    }

    /**
     * Authentication types
     */
    public static final class AUTH_TYPES {
        public static final String PASSWORD = "password";
        public static final String TOKEN = "token";
        public static final String CERTIFICATE = "certificate";
        public static final String BIOMETRIC = "biometric";
        public static final String MULTI_FACTOR = "multi_factor";
        public static final String SERVICE_ACCOUNT = "service_account";

        private AUTH_TYPES() {
        }
    }

    /**
     * Role names
     */
    public static final class ROLES {
        public static final String ADMIN = "ADMIN";
        public static final String USER = "USER";
        public static final String SERVICE = "SERVICE";
        public static final String AUDITOR = "AUDITOR";

        private ROLES() {
        }
    }
}
