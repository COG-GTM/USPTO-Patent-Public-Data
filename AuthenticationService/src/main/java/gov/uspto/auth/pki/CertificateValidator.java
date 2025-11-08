package gov.uspto.auth.pki;

import gov.uspto.auth.authenticator.AuthenticatorValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Validator for X.509 certificates.
 * 
 * Validates certificates for:
 * - X.509 format compliance
 * - Validity period (notBefore/notAfter)
 * - Trust chain validation
 * - Certificate revocation (extensible via CRL/OCSP)
 * 
 * NIST 800-53 Controls: IA-5(2) (PKI-based Authentication)
 */
public class CertificateValidator implements AuthenticatorValidator<X509Certificate> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CertificateValidator.class);

    private final CertificateStore certificateStore;
    private String lastMessage;

    /**
     * Creates a certificate validator with the specified certificate store.
     * 
     * @param certificateStore the certificate store containing trusted certificates
     */
    public CertificateValidator(CertificateStore certificateStore) {
        if (certificateStore == null) {
            throw new IllegalArgumentException("Certificate store cannot be null");
        }
        this.certificateStore = certificateStore;
    }

    @Override
    public boolean test(X509Certificate certificate) {
        ValidationResult result = validate(certificate);
        this.lastMessage = result.getMessage();
        return result.isValid();
    }

    @Override
    public String getName() {
        return "CertificateValidator";
    }

    @Override
    public String getMessage() {
        return lastMessage != null ? lastMessage : "Certificate validation failed";
    }

    /**
     * Validates a certificate and returns detailed validation result.
     * 
     * @param certificate the certificate to validate
     * @return the validation result with detailed violations
     */
    public ValidationResult validate(X509Certificate certificate) {
        List<String> violations = new ArrayList<>();

        if (certificate == null) {
            violations.add("Certificate cannot be null");
            return new ValidationResult(false, "Certificate cannot be null", violations);
        }

        if (!validateFormat(certificate, violations)) {
            return new ValidationResult(false, "Certificate format validation failed", violations);
        }

        if (!validateValidity(certificate, violations)) {
            return new ValidationResult(false, "Certificate validity period validation failed", violations);
        }

        if (!validateTrustChain(certificate, violations)) {
            return new ValidationResult(false, "Certificate trust chain validation failed", violations);
        }

        boolean isValid = violations.isEmpty();
        String message = isValid ? "Certificate is valid" : "Certificate validation failed";

        return new ValidationResult(isValid, message, violations);
    }

    /**
     * Validates the certificate format.
     * 
     * @param certificate the certificate to validate
     * @param violations list to collect violations
     * @return true if format is valid, false otherwise
     */
    private boolean validateFormat(X509Certificate certificate, List<String> violations) {
        try {
            certificate.checkValidity();
            
            if (certificate.getSubjectX500Principal() == null) {
                violations.add("Certificate subject is null");
                return false;
            }

            if (certificate.getSerialNumber() == null) {
                violations.add("Certificate serial number is null");
                return false;
            }

            if (certificate.getIssuerX500Principal() == null) {
                violations.add("Certificate issuer is null");
                return false;
            }

            LOGGER.debug("Certificate format validation passed");
            return true;

        } catch (Exception e) {
            violations.add("Certificate format validation failed: " + e.getMessage());
            LOGGER.warn("Certificate format validation failed", e);
            return false;
        }
    }

    /**
     * Validates the certificate validity period.
     * 
     * @param certificate the certificate to validate
     * @param violations list to collect violations
     * @return true if validity period is valid, false otherwise
     */
    private boolean validateValidity(X509Certificate certificate, List<String> violations) {
        try {
            Date now = new Date();
            Date notBefore = certificate.getNotBefore();
            Date notAfter = certificate.getNotAfter();

            if (notBefore == null) {
                violations.add("Certificate notBefore date is null");
                return false;
            }

            if (notAfter == null) {
                violations.add("Certificate notAfter date is null");
                return false;
            }

            if (now.before(notBefore)) {
                violations.add("Certificate is not yet valid (notBefore: " + notBefore + ")");
                return false;
            }

            if (now.after(notAfter)) {
                violations.add("Certificate has expired (notAfter: " + notAfter + ")");
                return false;
            }

            certificate.checkValidity(now);
            LOGGER.debug("Certificate validity period validation passed");
            return true;

        } catch (CertificateExpiredException e) {
            violations.add("Certificate has expired: " + e.getMessage());
            LOGGER.warn("Certificate has expired", e);
            return false;
        } catch (CertificateNotYetValidException e) {
            violations.add("Certificate is not yet valid: " + e.getMessage());
            LOGGER.warn("Certificate is not yet valid", e);
            return false;
        } catch (Exception e) {
            violations.add("Certificate validity validation failed: " + e.getMessage());
            LOGGER.warn("Certificate validity validation failed", e);
            return false;
        }
    }

    /**
     * Validates the certificate trust chain.
     * 
     * @param certificate the certificate to validate
     * @param violations list to collect violations
     * @return true if trust chain is valid, false otherwise
     */
    private boolean validateTrustChain(X509Certificate certificate, List<String> violations) {
        try {
            if (certificateStore.getTrustedCertificateCount() == 0) {
                LOGGER.debug("No trusted certificates in store, skipping trust chain validation");
                return true;
            }

            boolean foundTrustedIssuer = false;
            String issuerDN = certificate.getIssuerX500Principal().getName();

            for (X509Certificate trustedCert : certificateStore.getAllTrustedCertificates().values()) {
                String trustedSubjectDN = trustedCert.getSubjectX500Principal().getName();
                
                if (issuerDN.equals(trustedSubjectDN)) {
                    try {
                        certificate.verify(trustedCert.getPublicKey());
                        foundTrustedIssuer = true;
                        LOGGER.debug("Certificate trust chain validation passed");
                        break;
                    } catch (Exception e) {
                        LOGGER.debug("Certificate signature verification failed against trusted cert: {}", 
                                trustedSubjectDN);
                    }
                }
            }

            if (!foundTrustedIssuer && certificateStore.getTrustedCertificateCount() > 0) {
                violations.add("Certificate issuer is not trusted: " + issuerDN);
                return false;
            }

            return true;

        } catch (Exception e) {
            violations.add("Certificate trust chain validation failed: " + e.getMessage());
            LOGGER.warn("Certificate trust chain validation failed", e);
            return false;
        }
    }

    /**
     * Gets the certificate store used by this validator.
     * 
     * @return the certificate store
     */
    public CertificateStore getCertificateStore() {
        return certificateStore;
    }

    /**
     * Validation result containing status, message, and violations.
     */
    public static class ValidationResult {
        private final boolean valid;
        private final String message;
        private final List<String> violations;

        public ValidationResult(boolean valid, String message, List<String> violations) {
            this.valid = valid;
            this.message = message;
            this.violations = new ArrayList<>(violations);
        }

        public boolean isValid() {
            return valid;
        }

        public String getMessage() {
            return message;
        }

        public List<String> getViolations() {
            return new ArrayList<>(violations);
        }

        @Override
        public String toString() {
            return "ValidationResult{" +
                    "valid=" + valid +
                    ", message='" + message + '\'' +
                    ", violations=" + violations +
                    '}';
        }
    }
}
