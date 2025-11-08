package gov.uspto.auth.policy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Result of a policy validation operation.
 * 
 * NIST 800-53 Controls: IA-5 (Authenticator Management)
 */
public class PolicyValidationResult {

    private final boolean valid;
    private final List<String> violations;

    public PolicyValidationResult(boolean valid, List<String> violations) {
        this.valid = valid;
        this.violations = violations != null ? new ArrayList<>(violations) : new ArrayList<>();
    }

    public static PolicyValidationResult success() {
        return new PolicyValidationResult(true, Collections.emptyList());
    }

    public static PolicyValidationResult failure(List<String> violations) {
        return new PolicyValidationResult(false, violations);
    }

    public boolean isValid() {
        return valid;
    }

    public List<String> getViolations() {
        return Collections.unmodifiableList(violations);
    }

    @Override
    public String toString() {
        return "PolicyValidationResult{" +
                "valid=" + valid +
                ", violations=" + violations +
                '}';
    }
}
