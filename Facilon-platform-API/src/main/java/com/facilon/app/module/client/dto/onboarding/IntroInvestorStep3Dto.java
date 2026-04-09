package com.facilon.app.module.client.dto.onboarding;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IntroInvestorStep3Dto {

    private String brokerPreferredBank;
    private String schemeName;
    private String accountNumber;
}
