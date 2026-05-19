package com.facilon.app.module.client.dto.onboarding;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response payload for {@code GET /api/clients/onboarding/setpassword/details/{azureUserId}}.
 *
 * <p>Returns the per-tenant B2C config needed by the React setpassword page to construct an MSAL
 * authority URL and pick the correct user flow (reset-password for first login, signin for
 * returning users — same rule as FISP {@code SetpasswordComponent}).</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SetPasswordDetailsDto {
    /** Echo of the Azure AD object id supplied in the path. */
    private String azureUserId;
    /** B2C tenant name (e.g. {@code fsdevb2c}); the authority host becomes {@code {name}.b2clogin.com}. */
    private String b2cTenantName;
    /** Azure app registration client id used by MSAL on the frontend. */
    private String clientId;
    /** User flow run for returning sign-ins (FISP {@code profile_1}). */
    private String signupSigninPolicy;
    /** User flow run for the first-time setpassword step (FISP {@code profile_2}). */
    private String resetPasswordPolicy;
    /** MSAL redirect URI configured for this tenant. */
    private String redirectUri;
    /** OAuth scope string (space-separated) to request. */
    private String scope;
    /** {@code true} when the user has at least one prior {@code last_login}; tells the page to use the signin policy instead of the reset policy. */
    private boolean hasLoggedInBefore;
    /** Display name for the user, useful for greeting on the setpassword page. */
    private String displayName;
}
