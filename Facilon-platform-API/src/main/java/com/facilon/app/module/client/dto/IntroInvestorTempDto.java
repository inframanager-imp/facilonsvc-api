package com.facilon.app.module.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IntroInvestorTempDto {

    private Long id;
    private String introFirstName;
    private String introMiddleName;
    private String introLastName;
    private String introGender;
    private String introEmail;
    private String introMobile;
    private String introDvInvestorSsId;
    private String introDvNationality;
    private String introInvestorId;
    private String ssBrokerValue;
    private String brokerPreferredBank;
    private String investRouteValue;
    private String serviceProviderType;
    private String ssIpRecords;
    private String ssApplicableToSlt;
    private String ssInvestorTypeValue;
    private String ssProductValue;
    private String ssBrokeragePlanValue;
    private String investorRegisterAs;
    private String legalEntityFullName;
    private String incorpCountry;
    private String isdCode;
    private String emailOtp;
    private String mobileOtp;
    private String uniqueCodeDb;
    private Integer status;
    private String diffMobWhatsapp;
    private String ssContactId;
    private Integer agreeForWhatsapp;
    private String password;
    private String abandonStatus;
    private String abandonReason;
    private LocalDate abandonDate;
    private Integer loginStatus;
    private String introSchemeName;
    private String introCountryOfResidence;
    private Integer spType;
    private Boolean ssAccountOpening;
}
