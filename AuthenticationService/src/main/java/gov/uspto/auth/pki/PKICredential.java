package gov.uspto.auth.pki;

import gov.uspto.auth.core.Credential;

import java.security.cert.X509Certificate;

/**
 * PKI certificate-based credential implementation.
 * 
 * Stores an X.509 certificate for PKI authentication.
 * 
 * NIST 800-53 Controls: IA-5(2) (PKI-based Authentication)
 */
public class PKICredential extends Credential {

    private final X509Certificate certificate;

    /**
     * Creates a PKI credential.
     * 
     * @param identifier the user or service identifier
     * @param certificate the X.509 certificate
     */
    public PKICredential(String identifier, X509Certificate certificate) {
        super(identifier);
        if (certificate == null) {
            throw new IllegalArgumentException("Certificate cannot be null");
        }
        this.certificate = certificate;
    }

    /**
     * Gets the X.509 certificate.
     * 
     * @return the certificate
     */
    public X509Certificate getCertificate() {
        return certificate;
    }

    @Override
    public boolean isValid() {
        if (certificate == null) {
            return false;
        }
        try {
            certificate.checkValidity();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String getCredentialType() {
        return "pki_cert";
    }

    @Override
    public void clear() {
    }

    @Override
    public String toString() {
        return "PKICredential{" +
                "identifier='" + getIdentifier() + '\'' +
                ", subjectDN='" + (certificate != null ? certificate.getSubjectX500Principal().getName() : "null") + '\'' +
                '}';
    }
}
