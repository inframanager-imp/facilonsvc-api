package com.facilon.app.module.client.dto.onboarding;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IntroInvestorResponseDto {

    private String uniqueCode;
    private Integer investorId;
    private Integer step;
    private String message;
}
