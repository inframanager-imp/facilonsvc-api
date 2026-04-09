package com.facilon.app.module.client.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for updating investor (client) verification status (admin operation).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvestorVerificationDto {

    @NotNull(message = "Verification status is required (1=verified, 2=pending, 3=rejected)")
    private Integer verifyStatus;
    
    private String verificationNotes;
    private String verifiedBy;
}
