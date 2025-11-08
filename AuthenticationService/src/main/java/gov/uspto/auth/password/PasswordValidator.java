package gov.uspto.auth.password;

import gov.uspto.auth.authenticator.AuthenticatorValidator;
import gov.uspto.auth.policy.PolicyValidationResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Validator for password credentials against password policy.
 * 
 * Validates passwords for:
 * - Minimum length
 * - Complexity requirements (uppercase, lowercase, digit, special character)
 * - Other policy-defined constraints
 * 
 * NIST 800-53 Controls: IA-5(1) (Password-based Authentication)
 */
public class PasswordValidator implements AuthenticatorValidator<char[]> {

    private final PasswordPolicy policy;
    private String lastMessage;

    /**
     * Creates a password validator with the specified policy.
     * 
     * @param policy the password policy to enforce
     */
    public PasswordValidator(PasswordPolicy policy) {
        if (policy == null) {
            throw new IllegalArgumentException("Password policy cannot be null");
        }
        this.policy = policy;
    }

    @Override
    public boolean test(char[] password) {
        PolicyValidationResult result = validate(password);
        List<String> violations = result.getViolations();
        this.lastMessage = violations.isEmpty() ? "Password is valid" : String.join("; ", violations);
        return result.isValid();
    }

    @Override
    public String getName() {
        return "PasswordValidator";
    }

    @Override
    public String getMessage() {
        return lastMessage != null ? lastMessage : "Password validation failed";
    }

    /**
     * Validates a password and returns detailed validation result.
     * 
     * @param password the password to validate
     * @return the validation result with detailed violations
     */
    public PolicyValidationResult validate(char[] password) {
        List<String> violations = new ArrayList<>();

        if (password == null || password.length == 0) {
            violations.add("Password cannot be null or empty");
            return new PolicyValidationResult(false, violations);
        }

        String passwordStr = new String(password);

        if (passwordStr.length() < policy.getMinLength()) {
            violations.add("Password must be at least " + policy.getMinLength() + " characters long");
        }

        if (policy.isRequireUppercase() && !containsUppercase(passwordStr)) {
            violations.add("Password must contain at least one uppercase letter");
        }

        if (policy.isRequireLowercase() && !containsLowercase(passwordStr)) {
            violations.add("Password must contain at least one lowercase letter");
        }

        if (policy.isRequireDigit() && !containsDigit(passwordStr)) {
            violations.add("Password must contain at least one digit");
        }

        if (policy.isRequireSpecialChar() && !containsSpecialChar(passwordStr)) {
            violations.add("Password must contain at least one special character");
        }

        boolean isValid = violations.isEmpty();
        return new PolicyValidationResult(isValid, violations);
    }

    /**
     * Validates a password from a String.
     * 
     * @param password the password to validate
     * @return the validation result
     */
    public PolicyValidationResult validate(String password) {
        if (password == null) {
            return validate((char[]) null);
        }
        return validate(password.toCharArray());
    }

    private boolean containsUppercase(String str) {
        for (char c : str.toCharArray()) {
            if (Character.isUpperCase(c)) {
                return true;
            }
        }
        return false;
    }

    private boolean containsLowercase(String str) {
        for (char c : str.toCharArray()) {
            if (Character.isLowerCase(c)) {
                return true;
            }
        }
        return false;
    }

    private boolean containsDigit(String str) {
        for (char c : str.toCharArray()) {
            if (Character.isDigit(c)) {
                return true;
            }
        }
        return false;
    }

    private boolean containsSpecialChar(String str) {
        String specialChars = "!@#$%^&*()_+-=[]{}|;:,.<>?/~`";
        for (char c : str.toCharArray()) {
            if (specialChars.indexOf(c) >= 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the password policy used by this validator.
     * 
     * @return the password policy
     */
    public PasswordPolicy getPolicy() {
        return policy;
    }
}
