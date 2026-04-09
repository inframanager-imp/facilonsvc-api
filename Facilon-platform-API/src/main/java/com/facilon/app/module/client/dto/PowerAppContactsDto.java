package com.facilon.app.module.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PowerAppContactsDto {

    private Long id;
    private String powerAppContactId;
    private String fullName;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String brokerId;
    private String accountId;
    private String broker;
    private String inviteRedeemUrl;
    private String b2cStatus;
    private String errorMessage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
