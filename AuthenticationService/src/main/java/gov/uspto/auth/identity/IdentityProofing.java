package gov.uspto.auth.identity;

import java.util.Map;

/**
 * Interface for identity proofing operations.
 * 
 * This interface defines operations for verifying the identity of users
 * before granting access to systems, in compliance with NIST 800-53.
 * 
 * NIST 800-53 Control: IA-12 (Identity Proofing)
 * 
 * Future implementation should include:
 * - Identity verification levels (IAL1, IAL2, IAL3)
 * - Document verification
 * - Biometric verification
 * - Knowledge-based verification
 * - Identity proofing audit trail
 */
public interface IdentityProofing {

    /**
     * Initiates an identity proofing process.
     * 
     * @param identifier the identifier for the user
     * @param proofingLevel the required identity assurance level
     * @return a proofing session identifier
     */
    String initiateProofing(String identifier, IdentityAssuranceLevel proofingLevel);

    /**
     * Submits identity evidence for verification.
     * 
     * @param sessionId the proofing session identifier
     * @param evidenceType the type of evidence being submitted
     * @param evidence the evidence data
     * @return true if evidence was accepted, false otherwise
     */
    boolean submitEvidence(String sessionId, EvidenceType evidenceType, Map<String, String> evidence);

    /**
     * Completes the identity proofing process.
     * 
     * @param sessionId the proofing session identifier
     * @return the proofing result
     */
    ProofingResult completeProofing(String sessionId);

    /**
     * Verifies that an identifier has completed identity proofing.
     * 
     * @param identifier the identifier to check
     * @param requiredLevel the required identity assurance level
     * @return true if the identifier has been proofed at the required level
     */
    boolean isIdentityProofed(String identifier, IdentityAssuranceLevel requiredLevel);

    /**
     * Identity Assurance Levels based on NIST 800-63.
     */
    enum IdentityAssuranceLevel {
        IAL1,
        IAL2,
        IAL3
    }

    /**
     * Types of identity evidence.
     */
    enum EvidenceType {
        GOVERNMENT_ID,
        BIOMETRIC,
        KNOWLEDGE_BASED,
        DOCUMENT,
        REFERENCE
    }

    /**
     * Result of identity proofing process.
     */
    class ProofingResult {
        private final boolean success;
        private final IdentityAssuranceLevel achievedLevel;
        private final String message;

        public ProofingResult(boolean success, IdentityAssuranceLevel achievedLevel, String message) {
            this.success = success;
            this.achievedLevel = achievedLevel;
            this.message = message;
        }

        public boolean isSuccess() {
            return success;
        }

        public IdentityAssuranceLevel getAchievedLevel() {
            return achievedLevel;
        }

        public String getMessage() {
            return message;
        }
    }
}
