package com.facilon.app.module.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataConsentDto {

    private Long id;

    @NotBlank(message = "Consent type is required")
    private String consentType; // "data_sharing", "marketing", "third_party", "terms_conditions"

    @NotNull(message = "Consent status is required")
    private Boolean consentGiven;

    private String consentDate;

    private String consentDocumentUrl;

    private String ipAddress;

    private String userAgent;

    private Boolean revoked;

    private String revokedDate;

    private String revokedReason;

    private String purpose; // Description of why consent is needed

    private String expiryDate; // Optional expiry date for consent
}
