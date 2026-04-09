package com.facilon.app.module.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactDetailsDto {

    @NotBlank(message = "Address Line 1 is required")
    private String addressLine1;

    private String addressLine2;
    private String addressLine3;

    @NotBlank(message = "City is required")
    private String userCity;

    @NotBlank(message = "State is required")
    private String userState;

    private String userCountry;

    @NotBlank(message = "Zip code is required")
    private String userZipCode;

    // Correspondence Address
    private String corrAddressSameAsPerm; // "1" for Yes (checkbox value usually) or "true"

    // Conditional Correspondence Address Fields
    private String corrAddressLine1;
    private String corrAddressLine2;
    private String corrAddressLine3;
    private String corrUserCity;
    private String corrUserState;
    private String corrUserCountry;
    private String corrUserZipCode;

    // Permanent Address proof (if different from Residential Status logic, but
    // usually Residential controls proof)
    private String addressProof;
}
