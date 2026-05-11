package com.facilon.app.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Laravel {@code Crypt::encrypt} / {@code Crypt::decrypt} parity for the SP-onboarding
 * registration link tokens minted by Laravel and consumed (or now also minted) by Spring.
 *
 * <p>Wire format Laravel uses: base64( JSON{ iv, value, mac, tag } )
 * — AES-256-CBC + HMAC-SHA256 keyed with {@code APP_KEY}.</p>
 *
 * <p>APP_KEY is read from {@code investor.introduced.laravel-app-key}
 * (same key the introduced-investor flow already uses). Static accessors are kept so
 * existing callers don't need refactoring; the bean's {@code @PostConstruct} populates
 * the static fields from config exactly once.</p>
 */
@Component
@Slf4j
public class LaravelEncryptionUtil {

    private static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final String MAC_ALGORITHM = "HmacSHA256";
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final SecureRandom secureRandom = new SecureRandom();

    /** Populated from {@code investor.introduced.laravel-app-key} on bean init. */
    private static volatile byte[] keyBytes;

    @Value("${investor.introduced.laravel-app-key:}")
    private String configuredAppKey;

    @PostConstruct
    void init() throws Exception {
        if (configuredAppKey == null || configuredAppKey.isBlank()) {
            log.warn("LaravelEncryptionUtil: investor.introduced.laravel-app-key is not set; "
                    + "encrypt/decrypt calls will fail until configured");
            return;
        }
        keyBytes = deriveKey(configuredAppKey);
        log.info("LaravelEncryptionUtil initialised (key len {} bytes)", keyBytes.length);
    }

    // ─── Decrypt ────────────────────────────────────────────────────────────────

    /**
     * Decrypt a Laravel {@code Crypt::encrypt}-format token.
     */
    public static String decrypt(String encryptedPayload) throws Exception {
        ensureKey();
        byte[] decodedBytes = Base64.getDecoder().decode(encryptedPayload);
        String jsonPayload = new String(decodedBytes, StandardCharsets.UTF_8);

        JsonNode json = objectMapper.readTree(jsonPayload);
        String ivBase64 = json.get("iv").asText();
        String valueBase64 = json.get("value").asText();
        String mac = json.get("mac").asText();

        String computedMac = computeMac(ivBase64, valueBase64, keyBytes);
        if (!mac.equalsIgnoreCase(computedMac)) {
            throw new SecurityException("MAC verification failed - data may be tampered");
        }

        byte[] iv = Base64.getDecoder().decode(ivBase64);
        byte[] encryptedData = Base64.getDecoder().decode(valueBase64);

        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);

        byte[] decryptedBytes = cipher.doFinal(encryptedData);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    // ─── Encrypt ────────────────────────────────────────────────────────────────

    /**
     * Encrypt a plaintext string using Laravel's {@code Crypt::encrypt} wire format.
     * Laravel serializes the plaintext with PHP {@code serialize()} before encrypting;
     * for the SP-onboarding flow the plaintext is always a string, so we emit
     * {@code s:<len>:"<value>";} which is what {@code unserialize()} accepts.
     */
    public static String encrypt(String plaintext) throws Exception {
        ensureKey();

        // PHP serialize() format for a string: s:LEN:"VALUE";
        // (LEN is byte length of VALUE in its serialized form.)
        byte[] plaintextBytes = plaintext.getBytes(StandardCharsets.UTF_8);
        String serialized = "s:" + plaintextBytes.length + ":\"" + plaintext + "\";";
        byte[] toEncrypt = serialized.getBytes(StandardCharsets.UTF_8);

        byte[] iv = new byte[16];
        secureRandom.nextBytes(iv);

        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
        byte[] cipherBytes = cipher.doFinal(toEncrypt);

        String ivBase64 = Base64.getEncoder().encodeToString(iv);
        String valueBase64 = Base64.getEncoder().encodeToString(cipherBytes);
        String mac = computeMac(ivBase64, valueBase64, keyBytes);

        // Preserve key order to match Laravel's emitted JSON exactly (iv, value, mac, tag)
        Map<String, String> envelope = new LinkedHashMap<>();
        envelope.put("iv", ivBase64);
        envelope.put("value", valueBase64);
        envelope.put("mac", mac);
        envelope.put("tag", "");

        String json = objectMapper.writeValueAsString(envelope);
        return Base64.getEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8));
    }

    // ─── Internals ──────────────────────────────────────────────────────────────

    private static void ensureKey() {
        if (keyBytes == null) {
            throw new IllegalStateException(
                    "LaravelEncryptionUtil not initialised - investor.introduced.laravel-app-key missing");
        }
    }

    private static byte[] deriveKey(String appKey) throws Exception {
        if (appKey.startsWith("base64:")) {
            return Base64.getDecoder().decode(appKey.substring(7));
        }
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        return digest.digest(appKey.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Laravel MAC: {@code hash_hmac('sha256', iv_base64 . value_base64, key)}
     * (Laravel 9+ no longer prefixes the iv string.)
     */
    private static String computeMac(String ivBase64, String valueBase64, byte[] key) throws Exception {
        Mac mac = Mac.getInstance(MAC_ALGORITHM);
        SecretKeySpec macKey = new SecretKeySpec(key, MAC_ALGORITHM);
        mac.init(macKey);
        byte[] macBytes = mac.doFinal((ivBase64 + valueBase64).getBytes(StandardCharsets.UTF_8));
        return bytesToHex(macBytes);
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder hex = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            hex.append(String.format("%02x", b));
        }
        return hex.toString();
    }
}
