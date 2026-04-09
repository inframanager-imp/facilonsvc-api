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
public class KycDocumentsDto {

    private Long id;
    private String investorUniqueId;
    private String ssInvestorId;
    private String status;
    private String documentUrl;
    private String documentType;
    private String docDescription;
    private Integer uploadType;
    private String documentMasterId;
    private String reason;
    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;
}
