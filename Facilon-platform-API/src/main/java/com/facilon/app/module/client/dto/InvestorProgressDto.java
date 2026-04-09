package com.facilon.app.module.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvestorProgressDto {
    private String currentStep;
    private String[] completedSteps;
    private String[] pendingSteps;
    private Integer progressPercentage;
    private Map<String, SectionStatus> sections;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SectionStatus {
        private Boolean completed;
        private Boolean required;
        private String lastUpdated;
    }
}
