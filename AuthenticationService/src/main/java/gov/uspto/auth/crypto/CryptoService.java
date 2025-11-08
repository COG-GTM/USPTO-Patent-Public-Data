package gov.uspto.auth.crypto;

/**
 * Interface for cryptographic operations.
 * 
 * This interface provides methods for password hashing, token generation,
 * and other cryptographic operations required for authentication.
 * 
 * NIST 800-53 Controls: IA-5(1) (Password-based Authentication), SC-13 (Cryptographic Protection)
 */
public interface CryptoService {

    /**
     * Hashes a password using a secure hashing algorithm.
     * 
     * @param password the password to hash
     * @return the hashed password
     * @throws CryptoException if hashing fails
     */
    String hashPassword(String password) throws CryptoException;

    /**
     * Verifies a password against a hash.
     * 
     * @param password the password to verify
     * @param hash the hash to verify against
     * @return true if the password matches the hash, false otherwise
     * @throws CryptoException if verification fails
     */
    boolean verifyPassword(String password, String hash) throws CryptoException;

    /**
     * Generates a secure random token.
     * 
     * @return the generated token
     * @throws CryptoException if token generation fails
     */
    String generateToken() throws CryptoException;

    /**
     * Generates a secure random salt.
     * 
     * @return the generated salt
     * @throws CryptoException if salt generation fails
     */
    byte[] generateSalt() throws CryptoException;

    /**
     * Encrypts data using a secure encryption algorithm.
     * 
     * @param data the data to encrypt
     * @param key the encryption key
     * @return the encrypted data
     * @throws CryptoException if encryption fails
     */
    byte[] encrypt(byte[] data, byte[] key) throws CryptoException;

    /**
     * Decrypts data using a secure decryption algorithm.
     * 
     * @param encryptedData the data to decrypt
     * @param key the decryption key
     * @return the decrypted data
     * @throws CryptoException if decryption fails
     */
    byte[] decrypt(byte[] encryptedData, byte[] key) throws CryptoException;
}
