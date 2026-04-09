package com.facilon.app.module.client.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Request DTO for creating/updating appointments
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentRequest {
    @NotBlank(message = "Office location is required")
    private String officeLocation;
    
    @NotNull(message = "Appointment date is required")
    @Future(message = "Appointment date must be in the future")
    private LocalDateTime appointmentDate;
    
    @NotBlank(message = "Time slot is required")
    private String timeSlot;
    
    private String notes;
}
