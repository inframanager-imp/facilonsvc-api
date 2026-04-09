package com.facilon.app.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

/**
 * Utility for decrypting Laravel encrypted strings.
 * Laravel uses AES-256-CBC encryption with HMAC-SHA256 for authentication.
 * 
 * Encrypted payload format (base64-encoded JSON):
 * {
 *   "iv": "base64-encoded-initialization-vector",
 *   "value": "base64-encoded-encrypted-data",
 *   "mac": "hmac-sha256-signature",
 *   "tag": ""
 * }
 * 
 * HARDCODED KEY FOR NOW - Will be made dynamic later.
 */
@Slf4j
public class LaravelEncryptionUtil {

    // TODO: Move to database configuration (tenant_encryption_config table)
    private static final String HARDCODED_APP_KEY = "base64:YOUR_LARAVEL_APP_KEY_HERE";
    private static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final String MAC_ALGORITHM = "HmacSHA256";
    
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Decrypt a Laravel encrypted string.
     * 
     * @param encryptedPayload Base64-encoded JSON payload from Laravel Crypt::encryptString()
     * @return Decrypted plaintext string
     * @throws Exception if decryption fails
     */
    public static String decrypt(String encryptedPayload) throws Exception {
        // Step 1: Base64 decode the payload to get JSON
        byte[] decodedBytes = Base64.getDecoder().decode(encryptedPayload);
        String jsonPayload = new String(decodedBytes, StandardCharsets.UTF_8);
        
        // Step 2: Parse JSON to extract iv, value, mac
        JsonNode json = objectMapper.readTree(jsonPayload);
        String ivBase64 = json.get("iv").asText();
        String valueBase64 = json.get("value").asText();
        String mac = json.get("mac").asText();
        
        // Step 3: Derive encryption key from APP_KEY
        byte[] keyBytes = deriveKey(HARDCODED_APP_KEY);
        
        // Step 4: Verify MAC (authentication)
        String computedMac = computeMac(ivBase64, valueBase64, keyBytes);
        if (!mac.equalsIgnoreCase(computedMac)) {
            throw new SecurityException("MAC verification failed - data may be tampered");
        }
        
        // Step 5: Decrypt the value
        byte[] iv = Base64.getDecoder().decode(ivBase64);
        byte[] encryptedData = Base64.getDecoder().decode(valueBase64);
        
        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
        
        byte[] decryptedBytes = cipher.doFinal(encryptedData);
        String decryptedText = new String(decryptedBytes, StandardCharsets.UTF_8);
        
        log.debug("Successfully decrypted Laravel token");
        return decryptedText;
    }

    /**
     * Derive 32-byte encryption key from Laravel APP_KEY.
     * If APP_KEY starts with "base64:", decode it. Otherwise, hash it with SHA-256.
     */
    private static byte[] deriveKey(String appKey) throws Exception {
        if (appKey.startsWith("base64:")) {
            String base64Key = appKey.substring(7);
            return Base64.getDecoder().decode(base64Key);
        } else {
            // Hash the key with SHA-256 to get 32 bytes
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return digest.digest(appKey.getBytes(StandardCharsets.UTF_8));
        }
    }

    /**
     * Compute HMAC-SHA256 MAC for authentication.
     * Laravel uses: hash_hmac('sha256', hash('sha256', "base64:{$iv}") . $value, $key)
     */
    private static String computeMac(String ivBase64, String valueBase64, byte[] key) throws Exception {
        // Laravel's MAC payload format
        String payload = hash("sha256", "base64:" + ivBase64) + valueBase64;
        
        Mac mac = Mac.getInstance(MAC_ALGORITHM);
        SecretKeySpec macKey = new SecretKeySpec(key, MAC_ALGORITHM);
        mac.init(macKey);
        
        byte[] macBytes = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(macBytes);
    }

    /**
     * Hash a string using the specified algorithm (for MAC computation).
     */
    private static String hash(String algorithm, String data) throws Exception {
        MessageDigest digest = MessageDigest.getInstance(algorithm);
        byte[] hashBytes = digest.digest(data.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(hashBytes);
    }

    /**
     * Convert byte array to hexadecimal string.
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder hex = new StringBuilder();
        for (byte b : bytes) {
            hex.append(String.format("%02x", b));
        }
        return hex.toString();
    }

    /**
     * Update the hardcoded APP_KEY (temporary method until DB-based config).
     * This will be replaced by fetching from tenant_encryption_config table.
     */
    public static void setAppKey(String appKey) {
        // TODO: Remove this once DB-based encryption config is implemented
        log.warn("Using hardcoded APP_KEY - move to DB configuration");
    }
}
