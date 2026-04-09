package com.facilon.app.module.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for nomination details.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserNominationDto {

    private Boolean appointNominee;

    private String nomineeFirstName;
    private String nomineeMiddleName;
    private String nomineeLastName;
    private String nomineeName1;
    private String nomineeRelationship;
    private String nomineeRelation1;
    private String nomineeDob;
    private String nomineeDob1;
    private String nomineeEmail;
    private String nomineeEmail1;
    private String nomineeMobile;
    private String nomineeMobile1;
    private String nomineeAddress;
    private String nomineeCity;
    private String nomineeState;
    private String nomineePostalCode;
    private Integer nomineeShare; // Percentage share
    private Integer nomineeShare1;

    // Nominee 1 address (Laravel parity)
    private String nomineeAddress1;
    private String nomineeCity1;
    private String nomineeState1;
    private String nomineeCountry1;
    private String nomineePincode1;

    private String nomineeName2;
    private String nomineeRelation2;
    private String nomineeMobile2;
    private String nomineeEmail2;
    private String nomineeDob2;
    private Integer nomineeShare2;
    private String nomineeAddress2;
    private String nomineeCity2;
    private String nomineeState2;
    private String nomineeCountry2;
    private String nomineePincode2;

    private String nomineeName3;
    private String nomineeRelation3;
    private String nomineeMobile3;
    private String nomineeEmail3;
    private String nomineeDob3;
    private Integer nomineeShare3;
    private String nomineeAddress3;
    private String nomineeCity3;
    private String nomineeState3;
    private String nomineeCountry3;
    private String nomineePincode3;

    private Boolean isMinor;
    private String guardianName1; // If nominee is minor
    private String guardianName; // If nominee is minor
    private String guardianRelationship;
    private String nomineeDocType1;
    private String nomineeDocNo1;
    private Integer nomineeCountrycode1;
    private String guardianDocType1;
    private String guardianDocNo1;
    private Integer guardianCountrycode1;
    private String guardianMobile1;
    private String guardianEmail1;
    private String guardianDob1;
    private String guardianPanNo1;
    private String nomineeDocType2;
    private String nomineeDocNo2;
    private Integer nomineeCountrycode2;
    private String guardianName2;
    private String guardianDocType2;
    private String guardianDocNo2;
    private Integer guardianCountrycode2;
    private String guardianMobile2;
    private String guardianEmail2;
    private String guardianDob2;
    private String guardianPanNo2;
    private String nomineeDocType3;
    private String nomineeDocNo3;
    private Integer nomineeCountrycode3;
    private String guardianName3;
    private String guardianDocType3;
    private String guardianDocNo3;
    private Integer guardianCountrycode3;
    private String guardianMobile3;
    private String guardianEmail3;
    private String guardianDob3;
    private String guardianPanNo3;
}
