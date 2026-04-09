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
public class IntroduceInvestorEmailsDto {

    private Long id;
    private String name;
    private String code;
    private String emailId;
    private Integer autoEmail;
    private Integer autoMailSentCount;
    private Integer manualEmail;
    private LocalDateTime createdAt;
}
