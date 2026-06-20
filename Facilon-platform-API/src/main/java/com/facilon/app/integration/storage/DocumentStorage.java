package com.facilon.app.integration.storage;

/**
 * Pluggable backend for storing document bytes. Each implementation owns a URL
 * scheme (e.g. {@code azureblob://}, {@code sharepoint://}) so the persisted
 * {@code document_url} string encodes which backend holds the file, and reads
 * can be routed by prefix.
 *
 * Implementations store bytes verbatim — encryption is applied by the caller
 * via {@link DocumentCipher} before {@link #store}, so the backend never sees
 * plaintext. Plan: implemented_docs/kyc-blob-journey-consent-plan.md (Phase 1).
 */
public interface DocumentStorage {

    /** The URL scheme this backend owns, without {@code ://} (e.g. "azureblob"). */
    String scheme();

    /** True when {@code url} belongs to this backend (prefix match on the scheme). */
    boolean supports(String url);

    /**
     * Store the given (already-encrypted) bytes at {@code objectPath} and return
     * the canonical URL ({@code scheme://...}) to persist in {@code document_url}.
     */
    String store(byte[] content, String objectPath);

    /** Retrieve the stored bytes for a URL previously returned by {@link #store}. */
    byte[] retrieve(String url);

    /** Delete the stored object; a missing object is treated as already deleted. */
    void delete(String url);
}
