package com.facilon.app.module.client.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPersonalInformationDto {

    private Long id;
    private String investorUniqueId;
    private String investorId;
    private String investorFirstName;
    private String investorMiddleName;
    private String investorLastName;
    private String investorGender;
    private LocalDate userDob;

    // Gender and Marital Status
    private String gender;
    private String maritalStatus;

    // Maiden Name Fields
    private String maidenTitle;
    private String maidenName;
    private String maidenMiddleName;
    private String maidenLastName;

    // Birth Location
    private String cityOfDob;
    private String countryDob;
    private String citizenship;

    // Father's Details
    private String fatherNameTitle;
    private String fathersFirstName;
    private String fathersMiddleName;
    private String fathersLastName;

    // Mother's Details
    private String motherNameTitle;
    private String motherFirstName;
    private String motherMiddleName;
    private String motherLastName;

    // Spouse Details
    private String spouseNameTitle;
    private String spouseName;
    private String spouseMiddleName;
    private String spouseLastName;
    private String spouseMaidenName;

    private String addressLine1;
    private String addressLine2;
    private String addressLine3;
    private String userCity;
    private String userState;
    private String userCountry;
    private String userZipCode;
    private String corrAddressLine1;
    private String corrAddressLine2;
    private String corrAddressLine3;
    private String corrUserCity;
    private String corrUserState;
    private String corrUserCountry;
    private String corrUserZipCode;
    private String userAadharNo;
    @JsonProperty("panNumber")
    @JsonAlias("userPanNo")
    private String userPanNo;
    private String userOciCardNo;
    private LocalDate userOciIssueDate;
    private LocalDate userOciValidUpto;
    private String userVisaType;
    private String userVisaNumber;
    private LocalDate userVisaIssuerDate;
    private LocalDate userVisaExpiryDate;
    private String countryOfResidence;
    private String proofOfAddress;
    private String addressType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
