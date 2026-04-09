package com.facilon.app.module.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for office visit appointments
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentDto {
    private Long id;
    private String investorCode;
    private String investorName;
    private String investorEmail;
    private String investorMobile;
    private String officeLocation;        // MUMBAI, DELHI, BANGALORE
    private LocalDateTime appointmentDate;
    private String timeSlot;              // e.g., "10:00 AM - 10:30 AM"
    private String status;                // SCHEDULED, COMPLETED, CANCELLED, NO_SHOW
    private String purpose;               // In-person document verification
    private String notes;                 // Additional notes
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
