package com.facilon.app.module.client.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * kyc.validation.* feature flags for Smart Upload document-type enforcement.
 *
 * All default to {@code false} so the code ships dormant (current warn-only
 * behaviour). Enable per environment - typically only after ORCReader's
 * OCI/Passport disambiguation has been verified, otherwise legitimate OCI
 * uploads classified as "passport" would be rejected.
 *
 * <ul>
 *   <li>{@code block-type-mismatch} - when the OCR-detected type is unknown or
 *       does not match the slot, reject the upload (HTTP 409) instead of warning.</li>
 *   <li>{@code require-id-field} - require the slot's defining ID number
 *       (PAN / Aadhaar / Passport / OCI) to be extracted and well-formed.</li>
 *   <li>{@code block-on-ocr-failure} - when ORCReader is unreachable or returns
 *       an error, reject the upload. WARNING: enabling this halts all KYC
 *       uploads during an OCR outage.</li>
 * </ul>
 */
@Configuration
@ConfigurationProperties(prefix = "kyc.validation")
@Data
public class KycValidationProperties {
    private boolean blockTypeMismatch = false;
    private boolean requireIdField = false;
    private boolean blockOnOcrFailure = false;
}
