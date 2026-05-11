package com.facilon.app.module.serviceprovider.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Step-1 landing payload — what `register_sp.blade.php` greets the SP with
 * ("Dear {{ $fetch_scheduling_details->full_name }}, …").
 *
 * <p>Returned by `GET /api/clients/sp/landing?status=`. The next-step URL the React
 * "Continue" button should point at is also returned, mirroring Laravel's
 * {@code route('services_provider_user', ['unique_codes' => $status])}.</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpLandingDto {
    /** From powerapp_contacts.full_name (or "Partner" fallback like Laravel). */
    private String fullName;
    /** From powerapp_contacts.email — the same value `status` decrypted to. */
    private String email;
    /** Echo of `status` (so React doesn't have to re-encode on Continue click). */
    private String status;
}
