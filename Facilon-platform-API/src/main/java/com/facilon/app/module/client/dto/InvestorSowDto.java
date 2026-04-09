package com.facilon.app.module.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvestorSowDto {
    private Long id;
    private Long investorId;
    private Long templateId;
    private Map<String, Object> sowData; // JSON filled data
    private String status;
    private LocalDateTime submittedDate;
    private LocalDateTime approvedDate;
    private Long approvedBy;
    private String rejectionReason;
    private String digitalSignature;
    private LocalDateTime signedDate;
    private LocalDateTime createdDate;
}
