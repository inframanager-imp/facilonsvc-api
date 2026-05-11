package com.facilon.app.module.serviceprovider.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpUserRegisterResponseDto {
    private boolean success;
    private String message;
    private boolean welcomeMailSent;
    /** True when the Graph /invitations call returned 2xx. */
    private boolean b2bInviteSent;
    /** Azure AD object id of the invited B2B guest, populated when {@link #b2bInviteSent} is true. */
    private String invitedUserId;
}
