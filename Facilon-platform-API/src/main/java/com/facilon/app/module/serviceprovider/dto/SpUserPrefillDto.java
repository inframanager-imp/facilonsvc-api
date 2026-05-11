package com.facilon.app.module.serviceprovider.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Pre-fill values for `services_provider_user_register.blade.php` — Laravel reads
 * `powerapp_contacts` by email and seeds the form (full_name, email, phone,
 * first_name, last_name).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpUserPrefillDto {
    private String serviceProviderEmail;   // echo of the encrypted token
    private String fullName;               // "Service Provider Name" readonly
    private String complianceEmail;        // pre-filled into Official Email
    private String compliancePhoneNo;      // pre-filled into Official Phone
    private String complianceName;         // pre-filled into First Name
    private String complianceLastName;     // pre-filled into Last Name
}
