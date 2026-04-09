package com.facilon.app.module.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerificationStatusDto {

    private String currentStatus; // "pending", "in_progress", "completed", "rejected"

    private PhysicalSubmissionInfo physicalSubmission;

    private VerificationAppointmentInfo appointment;

    private String verifiedBy;

    private String verifiedAt;

    private String rejectionReason;

    private String nextSteps;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PhysicalSubmissionInfo {
        private Boolean submitted;
        private String submittedAt;
        private String trackingNumber; // awbNumber
        private String status;
        
        // Additional fields for form pre-filling
        private String physicalSubmission; // "inperson" or "courier"
        private String courierName;
        private String dispatchDate; // Format: yyyy-MM-dd
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VerificationAppointmentInfo {
        private Boolean scheduled;
        private String appointmentDate;
        private String appointmentTime;
        private String verificationType;
        private String location;
        private String status;
    }
}
