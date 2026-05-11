package com.facilon.app.module.serviceprovider.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpUserConsentResponseDto {
    /**
     * Laravel `Crypt::encrypt(email)` token to forward to the final user form.
     * In Laravel `service_provider_step3_submit` does
     * {@code redirect()->route('admin.services_provider_user_register', ['service_provider_email' => $encryptedCode])}.
     */
    private String serviceProviderEmail;
    private String message;
    private boolean mailSent;
}
