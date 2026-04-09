package com.facilon.app.module.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerificationAppointmentDto {

    @NotNull(message = "Appointment date is required")
    private String appointmentDate;

    @NotBlank(message = "Appointment time is required")
    private String appointmentTime;

    @NotBlank(message = "Verification type is required")
    private String verificationType; // "in_person", "video_call", "phone"

    @NotBlank(message = "Location is required")
    private String location;

    private String verifierName;

    private String meetingLink; // For video calls

    private String notes;

    private String status; // "scheduled", "completed", "cancelled", "rescheduled"
}
