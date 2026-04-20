package com.facilon.app.module.serviceagent.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Payload for the Service Agent self-service profile edit
 * ({@code PUT /api/service-agents/me/profile}).
 *
 * <p>Only the fields an SA is allowed to update themselves are exposed here.
 * Administrative fields ({@code agentCode}, {@code isActive},
 * {@code serviceProviderId}, {@code onboardingStatus}, {@code email} used for
 * login, etc.) are deliberately omitted so a compromised SA cannot elevate
 * privileges or change their login email without going through an admin.
 *
 * <p>All fields are optional — null means "leave the existing value unchanged".
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceAgentProfileUpdateDto {

    /** Display name shown on the dashboard and in delegation emails. */
    private String fullName;

    /** Contact mobile (NOT the login identifier). */
    private String mobile;

    /** Operating region — free text (e.g. "Mumbai-Metro", "South-India"). */
    private String assignedRegion;

    /** Investor segment served (e.g. "HNI", "Retail"). */
    private String assignedSegment;

    /** Uploaded profile photo URL (stored elsewhere; this is just the reference). */
    private String photoUrl;

    /** PAN — regex enforced at the validator layer, not here. */
    private String panNumber;

    /** Address-proof document URL. */
    private String addressProofUrl;

    /** SEBI / IRDA / other regulator registration number, if applicable. */
    private String registrationNumber;

    /** Long-form contact address (for the signature block on PDFs). */
    private String agentType;
}
