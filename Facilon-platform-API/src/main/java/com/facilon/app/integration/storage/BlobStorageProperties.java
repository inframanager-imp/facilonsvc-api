package com.facilon.app.integration.storage;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * azure.blob.* configuration for encrypted KYC document storage.
 *
 * The connection string is a SECRET — it must never be logged. Default OFF so
 * the app boots without Azure Blob until a real connection string is supplied
 * via the AZURE_BLOB_CONNECTION_STRING environment variable.
 *
 * Plan: implemented_docs/kyc-blob-journey-consent-plan.md (Phase 1).
 */
@Configuration
@ConfigurationProperties(prefix = "azure.blob")
@Data
public class BlobStorageProperties {
    /** When false, AzureBlobStorage refuses to store/retrieve (used by the Smart Upload confirm step). */
    private boolean enabled = false;
    /** Azure Storage account connection string. SECRET — never log this value. */
    private String connectionString = "";
    /** Container that holds confirmed, encrypted KYC documents. */
    private String containerName = "kyc-documents";
}
