package com.facilon.app.module.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Aggregated investor profile for admin view (personal info + passport + experience + consents).
 * InvestorDto is fetched separately.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvestorFullProfileDto {

    private UserPersonalInformationDto personalInfo;
    private UserPassportDetailsDto passport;
    private InvestorExperienceDto experience;
    private InvestorConsentsDto consents;
}
