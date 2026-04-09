package com.facilon.app.module.client.controller;

import com.facilon.app.module.client.dto.AppointmentDto;
import com.facilon.app.module.client.dto.AppointmentRequest;
import com.facilon.app.module.client.service.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * REST API for appointment management
 */
@RestController
@RequestMapping("/api/clients/appointments")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Client Appointments", description = "Investor appointment scheduling for office visits")
public class AppointmentController {

    private final AppointmentService appointmentService;
    
    @PostMapping
    @Operation(summary = "Create new appointment")
    public ResponseEntity<AppointmentDto> createAppointment(
            @Valid @RequestBody AppointmentRequest request,
            Authentication authentication
    ) {
        String investorCode = authentication.getName();
        // In real scenario, fetch investor details from user service/database
        String investorName = "Investor Name"; // TODO: Get from session/database
        String investorEmail = "investor@example.com"; // TODO: Get from session/database
        String investorMobile = "+91 9876543210"; // TODO: Get from session/database
        
        AppointmentDto appointment = appointmentService.createAppointment(
                investorCode, investorName, investorEmail, investorMobile, request
        );
        
        log.info("Appointment created: {}", appointment.getId());
        return ResponseEntity.ok(appointment);
    }
    
    @GetMapping
    @Operation(summary = "Get all appointments for current investor")
    public ResponseEntity<List<AppointmentDto>> getMyAppointments(Authentication authentication) {
        String investorCode = authentication.getName();
        List<AppointmentDto> appointments = appointmentService.getInvestorAppointments(investorCode);
        return ResponseEntity.ok(appointments);
    }
    
    @GetMapping("/{appointmentId}")
    @Operation(summary = "Get appointment by ID")
    public ResponseEntity<AppointmentDto> getAppointmentById(
            @PathVariable Long appointmentId,
            Authentication authentication
    ) {
        return appointmentService.getAppointmentById(appointmentId)
                .filter(apt -> apt.getInvestorCode().equals(authentication.getName()))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PutMapping("/{appointmentId}/cancel")
    @Operation(summary = "Cancel appointment")
    public ResponseEntity<Void> cancelAppointment(
            @PathVariable Long appointmentId,
            Authentication authentication
    ) {
        String investorCode = authentication.getName();
        boolean cancelled = appointmentService.cancelAppointment(appointmentId, investorCode);
        
        if (cancelled) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PutMapping("/{appointmentId}/reschedule")
    @Operation(summary = "Reschedule appointment")
    public ResponseEntity<AppointmentDto> rescheduleAppointment(
            @PathVariable Long appointmentId,
            @Valid @RequestBody AppointmentRequest request,
            Authentication authentication
    ) {
        try {
            String investorCode = authentication.getName();
            AppointmentDto appointment = appointmentService.rescheduleAppointment(
                    appointmentId, investorCode, request
            );
            return ResponseEntity.ok(appointment);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/available-slots")
    @Operation(summary = "Get available time slots for a date and office")
    public ResponseEntity<List<String>> getAvailableSlots(
            @Parameter(description = "Office location (MUMBAI, DELHI, BANGALORE)")
            @RequestParam String officeLocation,
            @Parameter(description = "Date (YYYY-MM-DD)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        List<String> slots = appointmentService.getAvailableSlots(officeLocation, date);
        return ResponseEntity.ok(slots);
    }
}
