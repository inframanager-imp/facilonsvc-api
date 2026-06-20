package com.facilon.app.integration.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * App-level AES-256-GCM encryption for KYC document bytes. The plaintext file
 * is encrypted before it ever leaves the JVM, so Azure Blob only ever stores
 * ciphertext (the key never goes to Azure).
 *
 * Wire format of the returned blob:
 *   [version:1][iv:12][ciphertext+gcmTag]
 * The 1-byte version lets us rotate keys/algorithms later without breaking the
 * decryption of documents written under an older key.
 *
 * Key: ${DOCUMENT_ENCRYPTION_KEY} — base64 of 32 raw bytes (AES-256). Keep it
 * STABLE; rotating it without a versioned key registry makes existing blobs
 * undecryptable. Plan: implemented_docs/kyc-blob-journey-consent-plan.md.
 */
@Slf4j
@Component
public class DocumentCipher {

    /** Current key version stamped into every blob. Bump when the key/algorithm changes. */
    private static final byte VERSION_1 = 1;
    private static final int IV_LENGTH = 12;       // 96-bit nonce recommended for GCM
    private static final int TAG_LENGTH_BITS = 128;
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";

    private final SecretKey key;
    private final SecureRandom random = new SecureRandom();

    public DocumentCipher(@Value("${document.encryption.key:}") String base64Key) {
        if (base64Key == null || base64Key.isBlank()) {
            this.key = null; // not configured — isConfigured() is false; use() throws clearly
            log.warn("DOCUMENT_ENCRYPTION_KEY not set — KYC document encryption is unavailable");
            return;
        }
        byte[] raw = Base64.getDecoder().decode(base64Key.trim());
        if (raw.length != 32) {
            throw new IllegalStateException(
                    "DOCUMENT_ENCRYPTION_KEY must decode to 32 bytes (AES-256), got " + raw.length);
        }
        this.key = new SecretKeySpec(raw, "AES");
    }

    /** True when a valid 32-byte key is configured. */
    public boolean isConfigured() {
        return key != null;
    }

    /** Encrypt plaintext bytes → versioned [version][iv][ciphertext+tag]. */
    public byte[] encrypt(byte[] plaintext) {
        requireKey();
        try {
            byte[] iv = new byte[IV_LENGTH];
            random.nextBytes(iv);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(TAG_LENGTH_BITS, iv));
            byte[] ciphertext = cipher.doFinal(plaintext);

            return ByteBuffer.allocate(1 + IV_LENGTH + ciphertext.length)
                    .put(VERSION_1)
                    .put(iv)
                    .put(ciphertext)
                    .array();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Document encryption failed", e);
        }
    }

    /** Decrypt a versioned blob produced by {@link #encrypt(byte[])}. */
    public byte[] decrypt(byte[] encrypted) {
        requireKey();
        try {
            ByteBuffer buf = ByteBuffer.wrap(encrypted);
            byte version = buf.get();
            if (version != VERSION_1) {
                throw new IllegalStateException("Unsupported document cipher version: " + version);
            }
            byte[] iv = new byte[IV_LENGTH];
            buf.get(iv);
            byte[] ciphertext = new byte[buf.remaining()];
            buf.get(ciphertext);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(TAG_LENGTH_BITS, iv));
            return cipher.doFinal(ciphertext);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Document decryption failed", e);
        }
    }

    private void requireKey() {
        if (key == null) {
            throw new IllegalStateException(
                    "DOCUMENT_ENCRYPTION_KEY is not configured — cannot encrypt/decrypt KYC documents");
        }
    }
}
