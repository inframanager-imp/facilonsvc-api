package com.facilon.app.integration.storage;

import com.azure.core.util.BinaryData;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * {@link DocumentStorage} backed by Azure Blob Storage. Stores ciphertext only
 * (caller encrypts via {@link DocumentCipher}); the connection string comes from
 * {@link BlobStorageProperties} (global env var) and is never logged.
 *
 * Persisted URL form: {@code azureblob://{container}/{objectPath}}.
 * The container client is built lazily on first use so the app boots even when
 * Blob storage is not configured. Plan: implemented_docs/kyc-blob-journey-consent-plan.md.
 */
@Slf4j
@Component
public class AzureBlobStorage implements DocumentStorage {

    public static final String SCHEME = "azureblob";
    private static final String PREFIX = SCHEME + "://";

    private final BlobStorageProperties props;
    private volatile BlobContainerClient container;

    public AzureBlobStorage(BlobStorageProperties props) {
        this.props = props;
    }

    @Override
    public String scheme() {
        return SCHEME;
    }

    @Override
    public boolean supports(String url) {
        return url != null && url.startsWith(PREFIX);
    }

    @Override
    public String store(byte[] content, String objectPath) {
        String path = normalize(objectPath);
        BlobClient blob = container().getBlobClient(path);
        blob.upload(BinaryData.fromBytes(content), true);
        log.info("Stored encrypted document to Azure Blob: {}/{}", props.getContainerName(), path);
        return PREFIX + props.getContainerName() + "/" + path;
    }

    @Override
    public byte[] retrieve(String url) {
        return container().getBlobClient(objectPathOf(url)).downloadContent().toBytes();
    }

    @Override
    public void delete(String url) {
        boolean deleted = container().getBlobClient(objectPathOf(url)).deleteIfExists();
        if (deleted) {
            log.info("Deleted Azure Blob document: {}", url);
        }
    }

    // ---------- helpers ----------

    /** Extract {@code objectPath} from {@code azureblob://{container}/{objectPath}}. */
    private String objectPathOf(String url) {
        if (!supports(url)) {
            throw new IllegalArgumentException("Not an azureblob:// url: " + url);
        }
        String rest = url.substring(PREFIX.length());
        int slash = rest.indexOf('/');
        return slash < 0 ? "" : rest.substring(slash + 1);
    }

    private String normalize(String objectPath) {
        return objectPath == null ? "" : objectPath.replaceFirst("^/+", "");
    }

    private BlobContainerClient container() {
        BlobContainerClient c = container;
        if (c == null) {
            synchronized (this) {
                c = container;
                if (c == null) {
                    if (!props.isEnabled() || props.getConnectionString() == null
                            || props.getConnectionString().isBlank()) {
                        throw new IllegalStateException(
                                "Azure Blob storage is not configured/enabled "
                                        + "(set azure.blob.enabled=true and AZURE_BLOB_CONNECTION_STRING)");
                    }
                    BlobServiceClient service = new BlobServiceClientBuilder()
                            .connectionString(props.getConnectionString())
                            .buildClient();
                    c = service.getBlobContainerClient(props.getContainerName());
                    if (!c.exists()) {
                        c.createIfNotExists();
                    }
                    container = c;
                    log.info("Azure Blob container ready: {}", props.getContainerName());
                }
            }
        }
        return c;
    }
}
