package gov.uspto.auth.identity;

/**
 * Result of an identity proofing operation.
 * 
 * NIST 800-53 Controls: IA-12 (Identity Proofing)
 */
public class IdentityProofingResult {

    private final boolean success;
    private final String identityAssuranceLevel;
    private final String message;

    public IdentityProofingResult(boolean success, String identityAssuranceLevel, String message) {
        this.success = success;
        this.identityAssuranceLevel = identityAssuranceLevel;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getIdentityAssuranceLevel() {
        return identityAssuranceLevel;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "IdentityProofingResult{" +
                "success=" + success +
                ", identityAssuranceLevel='" + identityAssuranceLevel + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
