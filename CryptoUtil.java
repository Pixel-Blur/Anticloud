import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.KeySpec;
import java.util.Base64;
import javax.crypto.*;
import javax.crypto.spec.*;

public class CryptoUtil {
    private static final String HASH_ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final String ENCRYPTION_ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static final int KEY_LENGTH = 256;
    private static final int IV_LENGTH = 16;
    private static final int SALT_LENGTH = 16;
    private static final int HASH_ITERATIONS = 100000;

    // Hash password for user authentication
    public static String hashPassword(String password, byte[] salt) {
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance(HASH_ALGORITHM);
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, HASH_ITERATIONS, KEY_LENGTH);
            SecretKey key = factory.generateSecret(spec);
            return Base64.getEncoder().encodeToString(key.getEncoded());
        } catch (Exception e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    // Generate random salt
    public static byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return salt;
    }

    // Derive encryption key from master password and salt
    public static SecretKey deriveKey(String masterPassword, byte[] salt) {
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance(HASH_ALGORITHM);
            KeySpec spec = new PBEKeySpec(masterPassword.toCharArray(), salt, HASH_ITERATIONS, KEY_LENGTH);
            SecretKey tmp = factory.generateSecret(spec);
            return new SecretKeySpec(tmp.getEncoded(), ENCRYPTION_ALGORITHM);
        } catch (Exception e) {
            throw new RuntimeException("Error deriving key", e);
        }
    }

    // Generate random IV
    public static byte[] generateIV() {
        SecureRandom random = new SecureRandom();
        byte[] iv = new byte[IV_LENGTH];
        random.nextBytes(iv);
        return iv;
    }

    // Encrypt data
    public static EncryptedData encrypt(String plaintext, SecretKey key) {
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            byte[] iv = generateIV();
            cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));
            byte[] encrypted = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
            return new EncryptedData(encrypted, iv);
        } catch (Exception e) {
            throw new RuntimeException("Error encrypting data", e);
        }
    }

    // Decrypt data
    public static String decrypt(EncryptedData encryptedData, SecretKey key) {
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(encryptedData.getIv()));
            byte[] decrypted = cipher.doFinal(encryptedData.getData());
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Error decrypting data", e);
        }
    }

    // Container for encrypted data
    public static class EncryptedData {
        private final byte[] data;
        private final byte[] iv;

        public EncryptedData(byte[] data, byte[] iv) {
            this.data = data;
            this.iv = iv;
        }

        public byte[] getData() { return data; }
        public byte[] getIv() { return iv; }
    }
}