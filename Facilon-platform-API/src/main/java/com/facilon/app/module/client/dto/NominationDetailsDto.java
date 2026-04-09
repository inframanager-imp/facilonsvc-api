package com.facilon.app.module.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NominationDetailsDto {

    private Boolean appointNominee; // "true" or "false"

    // Nominee 1
    private String nomineeName1;
    private String nomineeRelation1;
    private String nomineeMobile1;
    private String nomineeEmail1;
    private LocalDate nomineeDob1;
    private String nomineeDocType1;
    private String nomineeDocNo1;
    private Double nomineeShare1;

    // Guardian 1 (if Minor)
    private String guardianName1;
    private String guardianRelation1; // Not explicitly in Laravel controller but logical
    private String guardianDocType1;
    private String guardianDocNo1;
    private String guardianMobile1;
    private String guardianEmail1;
    private String guardianPanNo1;
    private LocalDate guardianDob1;

    // Nominee 2
    private String nomineeName2;
    private String nomineeRelation2;
    private String nomineeMobile2;
    private String nomineeEmail2;
    private LocalDate nomineeDob2;
    private String nomineeDocType2;
    private String nomineeDocNo2;
    private Double nomineeShare2;

    // Guardian 2
    private String guardianName2;
    private String guardianRelation2;
    private String guardianDocType2;
    private String guardianDocNo2;
    private String guardianMobile2;
    private String guardianEmail2;
    private String guardianPanNo2;
    private LocalDate guardianDob2;

    // Nominee 3
    private String nomineeName3;
    private String nomineeRelation3;
    private String nomineeMobile3;
    private String nomineeEmail3;
    private LocalDate nomineeDob3;
    private String nomineeDocType3;
    private String nomineeDocNo3;
    private Double nomineeShare3;

    // Guardian 3
    private String guardianName3;
    private String guardianRelation3;
    private String guardianDocType3;
    private String guardianDocNo3;
    private String guardianMobile3;
    private String guardianEmail3;
    private String guardianPanNo3;
    private LocalDate guardianDob3;

    // Metadata
    private Integer nomination;
    private String personalInfoTab;

    // Country Codes (Laravel had specific fields for these)
    private String nomineeCountryCode1;
    private String guardianCountryCode1;
    private String nomineeCountryCode2;
    private String guardianCountryCode2;
    private String nomineeCountryCode3;
    private String guardianCountryCode3;
}
