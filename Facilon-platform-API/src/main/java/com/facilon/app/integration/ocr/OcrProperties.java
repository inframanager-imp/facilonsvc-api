package com.facilon.app.integration.ocr;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * ai.ocr.* configuration for the ORCReader HTTP client.
 * Plan: KYC_DOCUMENT_PLAN.md §3.5 & §5.
 */
@Configuration
@ConfigurationProperties(prefix = "ai.ocr")
@Data
public class OcrProperties {
    private String baseUrl = "http://localhost:8080";
    private String endpoint = "/api/kyc/extract";
    private String provider = "mistral";
    private int timeoutSeconds = 60;
    private boolean applyOcr = true;
}
