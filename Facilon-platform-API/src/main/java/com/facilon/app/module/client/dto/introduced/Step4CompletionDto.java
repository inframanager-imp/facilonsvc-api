package com.facilon.app.module.client.dto.introduced;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Step4CompletionDto {
    @NotBlank(message = "Unique code is required")
    private String uniqueCode;
    
    @NotBlank(message = "Self or Legal Entity selection is required")
    private String selfOrLegalEntity; // "Self" or "Legal Entity"
    
    @NotBlank(message = "Nationality is required")
    private String nationality; // GUID from master_nationality
    
    private String countryOfResidence; // GUID from master_country_of_residence
    
    private String panCardStatus; // "Yes" or "No"
    
    private String indianOrigin; // "Yes" or "No"
    
    private String ociCardStatus; // "Yes" or "No"
    
    // Legal Entity fields
    private String entityName;
    private String legalPanCard;
    private String representativeCapacity;
    private String securityRegulated;
    
    @NotNull(message = "Terms acceptance is required")
    private Boolean termsAccepted;
    
    @NotNull(message = "Privacy policy acceptance is required")
    private Boolean privacyPolicyAccepted;
    
    private Boolean agreeForWhatsapp;
    
    private Boolean agreeForMarketing;
    
    private String whatsappNumber;
}
