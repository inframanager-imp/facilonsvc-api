package com.facilon.app.module.client.dto.onboarding;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchRegistrationDto {

    @NotBlank
    private String brokerCode; // Broker/SP identifier

    @NotEmpty
    @Size(min = 1, max = 100)
    @Valid
    private List<BatchInvestorDto> investors;
}
