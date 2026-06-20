package com.facilon.app.module.client.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * kyc.storage.* flags for where/when Smart Upload stores the document file.
 *
 * Documents Center always uses Azure Blob when Blob + DOCUMENT_ENCRYPTION_KEY are
 * configured: the file is encrypted and held in kyc_documents.staged_content at
 * upload, then moved to Azure Blob when the investor clicks "Looks right".
 * Default ON; acts as a kill-switch back to immediate SharePoint/local storage.
 *
 * Plan: implemented_docs/kyc-blob-journey-consent-plan.md.
 */
@Configuration
@ConfigurationProperties(prefix = "kyc.storage")
@Data
public class KycStorageProperties {
    /** When true (default) and Blob+cipher configured: stage encrypted bytes at upload, push to Blob on confirm. */
    private boolean deferToConfirm = true;
}
