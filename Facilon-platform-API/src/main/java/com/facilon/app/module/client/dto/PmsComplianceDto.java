package com.facilon.app.module.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PmsComplianceDto {

    private Long id;

    @NotNull(message = "Risk disclosure acknowledgment is required")
    private Boolean riskDisclosureAcknowledged;

    @NotNull(message = "Fee structure acceptance is required")
    private Boolean feeStructureAccepted;

    @NotNull(message = "Terms and conditions acceptance is required")
    private Boolean termsAccepted;

    private Boolean regulatoryDisclosureAcknowledged;

    private Boolean conflictOfInterestDisclosed;

    private Boolean performanceDisclosureAcknowledged;

    private String riskDisclosureDate;

    private String feeStructureDate;

    private String termsAcceptedDate;

    private String ipAddress;

    private String userAgent;

    private String digitalSignature;

    private String complianceStatus; // "pending", "completed", "expired"
}
