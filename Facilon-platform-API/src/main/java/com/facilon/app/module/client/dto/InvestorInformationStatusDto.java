package com.facilon.app.module.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvestorInformationStatusDto {

    private Long id;
    private String investorEmail;
    private String brokerId;
    private Integer bankDetails;
    private Integer bankYesNo;
    private Integer personalInfo;
    private Integer passport;
    private Integer residentialStatus;
    private Integer taxInformation;
    private Integer contactDetails;
    private Integer nomination;
    private Integer riskProfile;
}
