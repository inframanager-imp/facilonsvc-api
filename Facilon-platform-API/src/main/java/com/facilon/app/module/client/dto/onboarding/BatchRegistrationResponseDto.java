package com.facilon.app.module.client.dto.onboarding;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchRegistrationResponseDto {

    private int totalCount;
    private int successCount;
    private int failureCount;

    @Builder.Default
    private List<BatchSuccessItemDto> successes = new ArrayList<>();

    @Builder.Default
    private List<BatchFailureItemDto> failures = new ArrayList<>();

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BatchSuccessItemDto {
        private String email;
        private String uniqueCode;
        private Long investorId;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BatchFailureItemDto {
        private String email;
        private String reason;
    }
}
