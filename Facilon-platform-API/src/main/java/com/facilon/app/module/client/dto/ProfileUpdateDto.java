package com.facilon.app.module.client.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class ProfileUpdateDto {
    private String firstName;
    private String middleName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String gender;
    private String mobilePhone;
    private String whatsappNumber;
}
