package gov.uspto.auth.pki;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory certificate store for trusted certificates.
 * 
 * Stores trusted CA certificates and user certificates for PKI authentication.
 * Provides retrieval by alias and certificate validation support.
 * 
 * NIST 800-53 Controls: IA-5(2) (PKI-based Authentication)
 */
public class CertificateStore {

    private static final Logger LOGGER = LoggerFactory.getLogger(CertificateStore.class);

    private final Map<String, X509Certificate> trustedCertificates;
    private final Map<String, X509Certificate> userCertificates;

    /**
     * Creates a new certificate store.
     */
    public CertificateStore() {
        this.trustedCertificates = new ConcurrentHashMap<>();
        this.userCertificates = new ConcurrentHashMap<>();
    }

    /**
     * Adds a trusted CA certificate to the store.
     * 
     * @param alias the certificate alias
     * @param certificate the trusted certificate
     */
    public void addTrustedCertificate(String alias, X509Certificate certificate) {
        if (alias == null || alias.trim().isEmpty()) {
            throw new IllegalArgumentException("Certificate alias cannot be null or empty");
        }
        if (certificate == null) {
            throw new IllegalArgumentException("Certificate cannot be null");
        }
        trustedCertificates.put(alias, certificate);
        LOGGER.debug("Added trusted certificate with alias: {}", alias);
    }

    /**
     * Gets a trusted CA certificate by alias.
     * 
     * @param alias the certificate alias
     * @return the trusted certificate, or null if not found
     */
    public X509Certificate getTrustedCertificate(String alias) {
        return trustedCertificates.get(alias);
    }

    /**
     * Removes a trusted CA certificate from the store.
     * 
     * @param alias the certificate alias
     * @return the removed certificate, or null if not found
     */
    public X509Certificate removeTrustedCertificate(String alias) {
        X509Certificate removed = trustedCertificates.remove(alias);
        if (removed != null) {
            LOGGER.debug("Removed trusted certificate with alias: {}", alias);
        }
        return removed;
    }

    /**
     * Checks if a trusted CA certificate exists with the given alias.
     * 
     * @param alias the certificate alias
     * @return true if the certificate exists, false otherwise
     */
    public boolean hasTrustedCertificate(String alias) {
        return trustedCertificates.containsKey(alias);
    }

    /**
     * Gets all trusted CA certificates.
     * 
     * @return map of alias to certificate
     */
    public Map<String, X509Certificate> getAllTrustedCertificates() {
        return new ConcurrentHashMap<>(trustedCertificates);
    }

    /**
     * Adds a user certificate to the store.
     * 
     * @param identifier the user or service identifier
     * @param certificate the user certificate
     */
    public void addUserCertificate(String identifier, X509Certificate certificate) {
        if (identifier == null || identifier.trim().isEmpty()) {
            throw new IllegalArgumentException("Identifier cannot be null or empty");
        }
        if (certificate == null) {
            throw new IllegalArgumentException("Certificate cannot be null");
        }
        userCertificates.put(identifier, certificate);
        LOGGER.debug("Added user certificate for identifier: {}", identifier);
    }

    /**
     * Gets a user certificate by identifier.
     * 
     * @param identifier the user or service identifier
     * @return the user certificate, or null if not found
     */
    public X509Certificate getUserCertificate(String identifier) {
        return userCertificates.get(identifier);
    }

    /**
     * Removes a user certificate from the store.
     * 
     * @param identifier the user or service identifier
     * @return the removed certificate, or null if not found
     */
    public X509Certificate removeUserCertificate(String identifier) {
        X509Certificate removed = userCertificates.remove(identifier);
        if (removed != null) {
            LOGGER.debug("Removed user certificate for identifier: {}", identifier);
        }
        return removed;
    }

    /**
     * Checks if a user certificate exists for the given identifier.
     * 
     * @param identifier the user or service identifier
     * @return true if the certificate exists, false otherwise
     */
    public boolean hasUserCertificate(String identifier) {
        return userCertificates.containsKey(identifier);
    }

    /**
     * Gets all user certificates.
     * 
     * @return map of identifier to certificate
     */
    public Map<String, X509Certificate> getAllUserCertificates() {
        return new ConcurrentHashMap<>(userCertificates);
    }

    /**
     * Clears all trusted CA certificates from the store.
     */
    public void clearTrustedCertificates() {
        trustedCertificates.clear();
        LOGGER.debug("Cleared all trusted certificates");
    }

    /**
     * Clears all user certificates from the store.
     */
    public void clearUserCertificates() {
        userCertificates.clear();
        LOGGER.debug("Cleared all user certificates");
    }

    /**
     * Clears all certificates from the store.
     */
    public void clearAll() {
        clearTrustedCertificates();
        clearUserCertificates();
        LOGGER.debug("Cleared all certificates");
    }

    /**
     * Gets the total number of trusted CA certificates.
     * 
     * @return the trusted certificate count
     */
    public int getTrustedCertificateCount() {
        return trustedCertificates.size();
    }

    /**
     * Gets the total number of user certificates.
     * 
     * @return the user certificate count
     */
    public int getUserCertificateCount() {
        return userCertificates.size();
    }
}
