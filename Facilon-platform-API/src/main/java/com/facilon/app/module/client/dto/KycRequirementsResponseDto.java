package com.facilon.app.module.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KycRequirementsResponseDto {

    /** Investor type used to compute the matrix: NRI, OCI, RESIDENT_INDIVIDUAL, ... */
    private String investorType;

    /** Has the investor opted into Aadhaar e-sign (makes Aadhaar mandatory for NRIs). */
    private Boolean usesAadhaarEsign;

    /** Has the investor chosen Aadhaar as the address proof (drives Aadhaar address capture). */
    private Boolean usesAadhaarForAddress;

    private List<KycRequirementSlotDto> slots;

    /** Overall progress: mandatory slots in VALID state / total mandatory slots. */
    private Integer mandatoryTotal;
    private Integer mandatoryComplete;

    /** Convenience: count of slots currently in EXPIRED state (drives dashboard red-dot badge). */
    private Integer expiredCount;

    /** Set when every mandatory slot is VALID AND investor-confirmed. */
    private LocalDateTime kycVerifiedAt;
}
