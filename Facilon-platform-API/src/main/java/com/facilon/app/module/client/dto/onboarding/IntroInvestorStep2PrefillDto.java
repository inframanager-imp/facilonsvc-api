package com.facilon.app.module.client.dto.onboarding;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IntroInvestorStep2PrefillDto {

    private String firstName;
    private String middleName;
    private String lastName;
    private String email;
    private String mobile;
    private String registerAs;
}
