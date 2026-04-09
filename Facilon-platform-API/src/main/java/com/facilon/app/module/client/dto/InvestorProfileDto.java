package com.facilon.app.module.client.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Data
@Builder
public class InvestorProfileDto {
    private Long id;
    private String uniqueCode;
    private String email;
    private String firstName;
    private String middleName;
    private String lastName;
    private String fullName;
    private LocalDate dateOfBirth;
    private String gender;
    private String mobilePhone;
    private String whatsappNumber;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String postalCode;
    private String nationality;
    private String citizenship;
    private String panNumber;
    private String passportNumber;
    private String accountStatus;
    private Boolean passwordChangeStatus;
    private String registrationType;
}
