package gov.uspto.auth.pki;

import gov.uspto.auth.authenticator.Authenticator;
import gov.uspto.auth.authenticator.AuthenticatorStatus;
import gov.uspto.auth.authenticator.AuthenticatorType;

import java.security.cert.X509Certificate;
import java.time.Instant;
import java.util.UUID;

/**
 * PKI certificate-based authenticator implementation.
 * 
 * Stores certificate metadata including:
 * - Subject distinguished name
 * - Serial number
 * - Certificate fingerprint (SHA-256)
 * - Validity period
 * - Status (active, expired, revoked)
 * 
 * NIST 800-53 Controls: IA-5(2) (PKI-based Authentication)
 */
public class PKIAuthenticator implements Authenticator {

    private final String id;
    private final String identifier;
    private final String subjectDN;
    private final String serialNumber;
    private final String certificateFingerprint;
    private final Instant createdAt;
    private final Instant updatedAt;
    private final Instant expiresAt;
    private final AuthenticatorStatus status;

    private PKIAuthenticator(Builder builder) {
        this.id = builder.id;
        this.identifier = builder.identifier;
        this.subjectDN = builder.subjectDN;
        this.serialNumber = builder.serialNumber;
        this.certificateFingerprint = builder.certificateFingerprint;
        this.createdAt = builder.createdAt;
        this.updatedAt = builder.updatedAt;
        this.expiresAt = builder.expiresAt;
        this.status = builder.status;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public AuthenticatorType getType() {
        return AuthenticatorType.PKI_CERT;
    }

    @Override
    public AuthenticatorStatus getStatus() {
        return status;
    }

    @Override
    public Instant getCreatedAt() {
        return createdAt;
    }

    @Override
    public Instant getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public Instant getExpiresAt() {
        return expiresAt;
    }

    @Override
    public boolean isExpired() {
        if (expiresAt == null) {
            return false;
        }
        return Instant.now().isAfter(expiresAt);
    }

    @Override
    public boolean isActive() {
        if (status != AuthenticatorStatus.ACTIVE) {
            return false;
        }
        return !isExpired();
    }

    /**
     * Gets the certificate subject distinguished name.
     * 
     * @return the subject DN
     */
    public String getSubjectDN() {
        return subjectDN;
    }

    /**
     * Gets the certificate serial number.
     * 
     * @return the serial number
     */
    public String getSerialNumber() {
        return serialNumber;
    }

    /**
     * Gets the certificate fingerprint (SHA-256).
     * 
     * @return the certificate fingerprint
     */
    public String getCertificateFingerprint() {
        return certificateFingerprint;
    }

    /**
     * Creates a builder for a new PKI authenticator.
     * 
     * @param identifier the user or service identifier
     * @param certificate the X.509 certificate
     * @return a new builder
     */
    public static Builder builder(String identifier, X509Certificate certificate) {
        return new Builder(identifier, certificate);
    }

    /**
     * Creates a builder from an existing authenticator (for updates).
     * 
     * @param authenticator the existing authenticator
     * @return a new builder with values from the existing authenticator
     */
    public static Builder from(PKIAuthenticator authenticator) {
        Builder builder = new Builder();
        builder.id = authenticator.id;
        builder.identifier = authenticator.identifier;
        builder.subjectDN = authenticator.subjectDN;
        builder.serialNumber = authenticator.serialNumber;
        builder.certificateFingerprint = authenticator.certificateFingerprint;
        builder.createdAt = authenticator.createdAt;
        builder.updatedAt = authenticator.updatedAt;
        builder.expiresAt = authenticator.expiresAt;
        builder.status = authenticator.status;
        return builder;
    }

    /**
     * Builder for creating PKIAuthenticator instances.
     */
    public static class Builder {
        private String id;
        private String identifier;
        private String subjectDN;
        private String serialNumber;
        private String certificateFingerprint;
        private Instant createdAt;
        private Instant updatedAt;
        private Instant expiresAt;
        private AuthenticatorStatus status;

        private Builder() {
        }

        private Builder(String identifier, X509Certificate certificate) {
            if (identifier == null || identifier.trim().isEmpty()) {
                throw new IllegalArgumentException("Identifier cannot be null or empty");
            }
            if (certificate == null) {
                throw new IllegalArgumentException("Certificate cannot be null");
            }
            this.identifier = identifier;
            this.subjectDN = certificate.getSubjectX500Principal().getName();
            this.serialNumber = certificate.getSerialNumber().toString(16);
            this.certificateFingerprint = calculateFingerprint(certificate);
            this.id = UUID.randomUUID().toString();
            this.createdAt = Instant.now();
            this.updatedAt = Instant.now();
            this.expiresAt = certificate.getNotAfter().toInstant();
            this.status = AuthenticatorStatus.ACTIVE;
        }

        private String calculateFingerprint(X509Certificate certificate) {
            try {
                java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
                byte[] digest = md.digest(certificate.getEncoded());
                StringBuilder sb = new StringBuilder();
                for (byte b : digest) {
                    sb.append(String.format("%02x", b));
                }
                return sb.toString();
            } catch (Exception e) {
                throw new IllegalArgumentException("Failed to calculate certificate fingerprint", e);
            }
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder updatedAt(Instant updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Builder expiresAt(Instant expiresAt) {
            this.expiresAt = expiresAt;
            return this;
        }

        public Builder status(AuthenticatorStatus status) {
            this.status = status;
            return this;
        }

        public PKIAuthenticator build() {
            if (identifier == null || identifier.trim().isEmpty()) {
                throw new IllegalArgumentException("Identifier is required");
            }
            if (subjectDN == null || subjectDN.trim().isEmpty()) {
                throw new IllegalArgumentException("Subject DN is required");
            }
            if (serialNumber == null || serialNumber.trim().isEmpty()) {
                throw new IllegalArgumentException("Serial number is required");
            }
            if (certificateFingerprint == null || certificateFingerprint.trim().isEmpty()) {
                throw new IllegalArgumentException("Certificate fingerprint is required");
            }
            return new PKIAuthenticator(this);
        }
    }

    @Override
    public String toString() {
        return "PKIAuthenticator{" +
                "id='" + id + '\'' +
                ", identifier='" + identifier + '\'' +
                ", subjectDN='" + subjectDN + '\'' +
                ", serialNumber='" + serialNumber + '\'' +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", expiresAt=" + expiresAt +
                '}';
    }
}
