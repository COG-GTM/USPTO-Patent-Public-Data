package gov.uspto.auth.crypto;

/**
 * Interface for cryptographic operations.
 * 
 * This interface defines operations for password hashing, token generation,
 * encryption, and other cryptographic functions required for authentication.
 * 
 * NIST 800-53 Controls:
 * - IA-5 (Authenticator Management)
 * - SC-13 (Cryptographic Protection)
 * 
 * Future implementation should include:
 * - Password hashing (bcrypt, scrypt, argon2)
 * - Token generation and validation
 * - Encryption and decryption
 * - Digital signatures
 * - Random number generation
 */
public interface CryptoService {

    /**
     * Hashes a password using a secure algorithm.
     * 
     * @param password the password to hash
     * @return the hashed password
     */
    String hashPassword(String password);

    /**
     * Verifies a password against a hash.
     * 
     * @param password the password to verify
     * @param hash the hash to verify against
     * @return true if the password matches the hash, false otherwise
     */
    boolean verifyPassword(String password, String hash);

    /**
     * Generates a secure random token.
     * 
     * @param length the length of the token
     * @return the generated token
     */
    String generateToken(int length);

    /**
     * Generates a cryptographic hash of data.
     * 
     * @param data the data to hash
     * @param algorithm the hash algorithm (SHA-256, SHA-512, etc.)
     * @return the hash as a hex string
     */
    String hash(String data, String algorithm);

    /**
     * Encrypts data using a symmetric key.
     * 
     * @param data the data to encrypt
     * @param key the encryption key
     * @return the encrypted data
     */
    byte[] encrypt(byte[] data, byte[] key);

    /**
     * Decrypts data using a symmetric key.
     * 
     * @param encryptedData the data to decrypt
     * @param key the decryption key
     * @return the decrypted data
     */
    byte[] decrypt(byte[] encryptedData, byte[] key);

    /**
     * Generates a secure random key.
     * 
     * @param keySize the key size in bits
     * @return the generated key
     */
    byte[] generateKey(int keySize);
}
