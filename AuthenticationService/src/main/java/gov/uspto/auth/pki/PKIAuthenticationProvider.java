package gov.uspto.auth.pki;

import gov.uspto.auth.core.AuthenticationException;
import gov.uspto.auth.core.AuthenticationProvider;
import gov.uspto.auth.core.AuthenticationResult;
import gov.uspto.auth.core.Credential;
import gov.uspto.auth.core.Principal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.cert.X509Certificate;
import java.time.Instant;

/**
 * Authentication provider for PKI certificate-based authentication.
 * 
 * Validates X.509 certificates against:
 * - Certificate format and validity period
 * - Trust chain to trusted CA certificates
 * - Certificate revocation status (extensible)
 * 
 * NIST 800-53 Controls: IA-5(2) (PKI-based Authentication)
 */
public class PKIAuthenticationProvider implements AuthenticationProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(PKIAuthenticationProvider.class);

    private final CertificateValidator certificateValidator;
    private final CertificateStore certificateStore;

    /**
     * Creates a PKI authentication provider.
     * 
     * @param certificateValidator the certificate validator
     * @param certificateStore the certificate store
     */
    public PKIAuthenticationProvider(
            CertificateValidator certificateValidator,
            CertificateStore certificateStore) {
        if (certificateValidator == null) {
            throw new IllegalArgumentException("Certificate validator cannot be null");
        }
        if (certificateStore == null) {
            throw new IllegalArgumentException("Certificate store cannot be null");
        }
        this.certificateValidator = certificateValidator;
        this.certificateStore = certificateStore;
    }

    @Override
    public AuthenticationResult authenticate(Credential credential) throws AuthenticationException {
        if (credential == null) {
            throw new IllegalArgumentException("Credential cannot be null");
        }

        if (!(credential instanceof PKICredential)) {
            throw new AuthenticationException("INVALID_CREDENTIAL_TYPE", 
                    "Expected PKICredential but got " + credential.getClass().getSimpleName());
        }

        PKICredential pkiCredential = (PKICredential) credential;
        String identifier = pkiCredential.getIdentifier();
        X509Certificate certificate = pkiCredential.getCertificate();

        LOGGER.debug("Attempting PKI authentication for identifier: {}", identifier);

        CertificateValidator.ValidationResult validationResult = certificateValidator.validate(certificate);

        if (!validationResult.isValid()) {
            LOGGER.warn("Certificate validation failed for identifier {}: {}", 
                    identifier, validationResult.getMessage());
            return AuthenticationResult.failure("CERTIFICATE_INVALID", 
                    "Certificate validation failed: " + validationResult.getMessage());
        }

        String subjectDN = certificate.getSubjectX500Principal().getName();
        LOGGER.info("PKI authentication successful for identifier: {} (subject: {})", identifier, subjectDN);

        Principal principal = new Principal.Builder()
                .identifier(identifier)
                .name(subjectDN)
                .authenticationType("pki_cert")
                .authenticationTime(Instant.now())
                .build();

        return AuthenticationResult.success(principal, "PKI certificate authentication successful");
    }

    @Override
    public boolean supports(Class<? extends Credential> credentialClass) {
        return PKICredential.class.isAssignableFrom(credentialClass);
    }

    @Override
    public String getProviderName() {
        return "PKIAuthenticationProvider";
    }

    /**
     * Gets the certificate validator used by this provider.
     * 
     * @return the certificate validator
     */
    public CertificateValidator getCertificateValidator() {
        return certificateValidator;
    }

    /**
     * Gets the certificate store used by this provider.
     * 
     * @return the certificate store
     */
    public CertificateStore getCertificateStore() {
        return certificateStore;
    }
}
