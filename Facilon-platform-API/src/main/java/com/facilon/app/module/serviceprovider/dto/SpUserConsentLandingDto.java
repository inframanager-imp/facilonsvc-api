package com.facilon.app.module.serviceprovider.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Step-2 view payload — what `services_provider_user.blade.php` needs to render
 * the user-level Privacy & Consent page. Laravel passes `$unique_code` and
 * `$name_of_client` to the view.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpUserConsentLandingDto {
    private String uniqueCode;     // echo of `?unique_codes=` query param
    private String nameOfClient;   // powerapp_contacts.full_name
    private String consentVersion; // hardcoded "1.0" (matches Laravel)
    private String effectiveDate;  // today, "yyyy-MM-dd"
}
