package com.facilon.app.module.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for contact details.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserContactDetailsDto {

    private String addressLine1;
    private String addressLine2;
    private String addressLine3;
    private String userCity;
    private String userState;
    private String userCountry;
    private String userZipCode;

    private String corrAddressSameAsPerm;
    private String corrAddressLine1;
    private String corrAddressLine2;
    private String corrAddressLine3;
    private String corrUserCity;
    private String corrUserState;
    private String corrUserCountry;
    private String corrUserZipCode;

    private String emailPrimary;
    private String emailSecondary;
    private String mobilePrimary;
    private String mobileSecondary;
    private String isdCode;
    private String primaryPhone;
    private String secondaryPhone;
    private String email;
    private String alternateEmail;
    private String whatsappNumber;
    private String landlineNumber;
    private String preferredContactMethod; // email, mobile, whatsapp
    private String preferredContactTime;
    private String proofOfAddress;
    private String addressType;
}
