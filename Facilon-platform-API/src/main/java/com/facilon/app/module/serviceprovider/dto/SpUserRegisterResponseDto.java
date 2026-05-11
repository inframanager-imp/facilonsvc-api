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
    /** Set true once Slice C wires Graph /invitations; today always false. */
    private boolean b2bInviteSent;
}
