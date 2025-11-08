package gov.uspto.auth.identity;

import java.util.Map;

/**
 * Interface for identity proofing operations.
 * 
 * This interface provides methods for verifying the identity of users and services
 * in compliance with NIST 800-53 IA-12 (Identity Proofing).
 * 
 * NIST 800-53 Controls: IA-12 (Identity Proofing)
 */
public interface IdentityProofing {

    /**
     * Performs identity proofing for a user or service.
     * 
     * @param identifier the identifier to proof
     * @param proofingData the data used for identity proofing
     * @return the identity proofing result
     * @throws IdentityProofingException if proofing fails
     */
    IdentityProofingResult performProofing(String identifier, Map<String, Object> proofingData) 
            throws IdentityProofingException;

    /**
     * Gets the identity assurance level for an identifier.
     * 
     * @param identifier the identifier to check
     * @return the identity assurance level (IAL1, IAL2, IAL3)
     */
    String getIdentityAssuranceLevel(String identifier);

    /**
     * Verifies identity documents.
     * 
     * @param identifier the identifier
     * @param documentType the type of document
     * @param documentData the document data
     * @return true if document is verified, false otherwise
     * @throws IdentityProofingException if verification fails
     */
    boolean verifyIdentityDocument(String identifier, String documentType, byte[] documentData) 
            throws IdentityProofingException;
}
