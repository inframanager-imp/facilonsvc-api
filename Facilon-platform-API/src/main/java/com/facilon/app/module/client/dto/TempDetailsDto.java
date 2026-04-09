package com.facilon.app.module.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TempDetailsDto {

    private Long id;
    private String uniqueCode;
    private String registerAs;
    private String fullName;
    private String nationality;
    private String resident;
    private String pancard;
    private String indianOrigin;
    private String ociCard;
    private String entityName;
    private String legalCountry;
    private String entityRepName;
    private String repCapacity;
    private String regulation;
    private Integer agreeToWhatsapp;
    private Integer confirmation;
    private Integer agreeTerms;
}
