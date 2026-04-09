package com.facilon.app.module.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvestorPhysicalSubmissionDto {

    private Long id;
    private String physicalSubmissionValue;
    private String courierName;
    private LocalDate dispatchDate;
    private String awbNumber;
    private String investorUniqueId;
    private String ssInvestorId;
    private LocalDateTime createdAt;
}
