package com.facilon.app.module.client.dto.onboarding;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MainStepDto {

    @NotBlank(message = "Full name is required")
    private String fullName; // For individual=person name, for entity=legal entity name

    @NotNull(message = "Register as is required")
    private Integer registerAs; // 1=Individual/Self, 2=Legal Entity

    @NotNull(message = "Market is required")
    private Integer market;

    // For Legal Entity (registerAs=2)
    private Integer countryOfIncorporation; // Mandatory for legal entity
    private Integer countryOfTaxResidency; // Mandatory for legal entity
    private String entityWebsite; // Optional - format: www.xxxx.xxx
}
