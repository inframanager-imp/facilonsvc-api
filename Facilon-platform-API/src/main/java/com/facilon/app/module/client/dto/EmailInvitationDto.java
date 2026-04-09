package com.facilon.app.module.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailInvitationDto {

    private Long id;

    @NotBlank(message = "Recipient email is required")
    @Email(message = "Valid email is required")
    private String recipientEmail;

    @NotBlank(message = "Recipient name is required")
    private String recipientName;

    @NotBlank(message = "Invitation type is required")
    private String invitationType; // "nextholder", "pms_investor", "general"

    private String invitationToken;

    private String status; // "pending", "sent", "opened", "accepted", "expired"

    private String sentDate;

    private String openedDate;

    private String acceptedDate;

    private String expiryDate;

    private String message; // Custom message to include in invitation

    private Integer resendCount;

    private String lastResendDate;
}
