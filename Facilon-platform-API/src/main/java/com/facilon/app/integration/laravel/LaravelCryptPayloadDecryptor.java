package com.facilon.app.integration.laravel;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.HexFormat;

/**
 * Decrypts values produced by Laravel {@code Crypt::encrypt()} (Illuminate Encrypter),
 * matching {@code introduce-investor1/{introduced_id}} where {@code introduced_id} is encrypted.
 * <p>
 * Configure {@code investor.introduced.laravel-app-key} with the same value as Laravel {@code APP_KEY}
 * (including the {@code base64:} prefix). Cipher defaults to {@code aes-256-cbc} or can be set explicitly.
 */
@Component
@Slf4j
public class LaravelCryptPayloadDecryptor {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Value("${investor.introduced.laravel-app-key:}")
    private String laravelAppKey;

    @Value("${investor.introduced.laravel-cipher:}")
    private String laravelCipher;

    /**
     * True if {@code payload} decodes to JSON containing Laravel encryption fields.
     */
    public boolean isLaravelEncryptedPayload(String payload) {
        if (!StringUtils.hasText(payload)) {
            return false;
        }
        try {
            byte[] decoded = Base64.getDecoder().decode(payload.trim());
            JsonNode root = MAPPER.readTree(decoded);
            return root.hasNonNull("iv") && root.hasNonNull("value") && root.hasNonNull("mac")
                    && root.get("iv").isTextual()
                    && root.get("value").isTextual()
                    && root.get("mac").isTextual();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Decrypt Laravel payload to the plain introduce id (Dataverse {@code ss_name}), unserializing PHP string format if needed.
     */
    public String decrypt(String payload) {
        if (!StringUtils.hasText(laravelAppKey)) {
            throw new IllegalStateException(
                    "Laravel-encrypted introduce URL was used but investor.introduced.laravel-app-key is not set. "
                            + "Copy APP_KEY from the Laravel .env into this property.");
        }
        byte[] keyBytes = parseAppKey(laravelAppKey.trim());
        String cipherName = resolveCipher(keyBytes);

        try {
            byte[] outer = Base64.getDecoder().decode(payload.trim());
            JsonNode root = MAPPER.readTree(outer);
            String ivB64 = root.get("iv").asText();
            String valueB64 = root.get("value").asText();
            String macHex = root.get("mac").asText();

            if (!MessageDigest.isEqual(
                    macHex.toLowerCase().getBytes(StandardCharsets.UTF_8),
                    computeMacHex(ivB64, valueB64, keyBytes).getBytes(StandardCharsets.UTF_8))) {
                throw new IllegalArgumentException("Invalid Laravel encryption MAC");
            }

            byte[] iv = Base64.getDecoder().decode(ivB64);
            byte[] cipherBytes = Base64.getDecoder().decode(valueB64);

            String javaCipher = toJavaCipherName(cipherName);
            Cipher cipher = Cipher.getInstance(javaCipher);
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(keyBytes, "AES"), new IvParameterSpec(iv));
            byte[] decrypted = cipher.doFinal(cipherBytes);
            String inner = new String(decrypted, StandardCharsets.UTF_8);
            return unserializePhpString(inner);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.warn("Laravel decrypt failed: {}", e.getMessage());
            throw new IllegalArgumentException("Could not decrypt introduce link. Check APP_KEY and cipher match Laravel.", e);
        }
    }

    private static String computeMacHex(String ivB64, String valueB64, byte[] key) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(key, "HmacSHA256"));
        byte[] data = (ivB64 + valueB64).getBytes(StandardCharsets.UTF_8);
        return HexFormat.of().formatHex(mac.doFinal(data));
    }

    private String resolveCipher(byte[] keyBytes) {
        if (StringUtils.hasText(laravelCipher)) {
            return laravelCipher.trim().toLowerCase();
        }
        return switch (keyBytes.length) {
            case 16 -> "aes-128-cbc";
            case 32 -> "aes-256-cbc";
            default -> throw new IllegalStateException(
                    "Unsupported key length " + keyBytes.length + "; set investor.introduced.laravel-cipher explicitly");
        };
    }

    private static String toJavaCipherName(String laravelCipher) {
        return switch (laravelCipher.toLowerCase()) {
            case "aes-128-cbc" -> "AES/CBC/PKCS5Padding";
            case "aes-256-cbc" -> "AES/CBC/PKCS5Padding";
            default -> throw new IllegalArgumentException("Unsupported cipher: " + laravelCipher);
        };
    }

    /**
     * Laravel APP_KEY: {@code base64:...} (decoded to raw key bytes).
     */
    static byte[] parseAppKey(String appKey) {
        if (appKey.startsWith("base64:")) {
            return Base64.getDecoder().decode(appKey.substring("base64:".length()));
        }
        if (appKey.length() == 44 && appKey.matches("[A-Za-z0-9+/=]+")) {
            return Base64.getDecoder().decode(appKey);
        }
        return appKey.getBytes(StandardCharsets.UTF_8);
    }

    /**
     * PHP serialize for strings: {@code s:N:"content";} — content length N in bytes (ASCII-safe for investor codes).
     */
    static String unserializePhpString(String serialized) {
        if (serialized == null) {
            return null;
        }
        String s = serialized.trim();
        if (s.startsWith("s:")) {
            int lenColon = s.indexOf(':', 2);
            if (lenColon < 0) {
                return s;
            }
            int len = Integer.parseInt(s.substring(2, lenColon));
            int openQuote = s.indexOf('"', lenColon);
            if (openQuote < 0) {
                return s;
            }
            int start = openQuote + 1;
            return s.substring(start, start + len);
        }
        return s;
    }
}
