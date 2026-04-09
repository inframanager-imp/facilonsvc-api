package com.facilon.app.module.admin.controller;

import com.facilon.app.module.client.dto.AppointmentDto;
import com.facilon.app.module.client.service.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Admin controller for appointment management
 */
@RestController
@RequestMapping("/api/admin/appointments")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Admin Appointments", description = "Admin appointment management")
@PreAuthorize("hasRole('ADMIN')")
public class AdminAppointmentController {

    private final AppointmentService appointmentService;
    
    @GetMapping
    @Operation(summary = "Get all appointments with optional filters")
    public ResponseEntity<List<AppointmentDto>> getAllAppointments(
            @Parameter(description = "Filter by status")
            @RequestParam(required = false) String status,
            @Parameter(description = "Filter by office location")
            @RequestParam(required = false) String officeLocation
    ) {
        log.info("Admin fetching appointments: status={}, office={}", status, officeLocation);
        
        List<AppointmentDto> appointments = appointmentService.getAllAppointments();
        
        // Apply filters
        List<AppointmentDto> filtered = appointments.stream()
                .filter(apt -> status == null || status.equals(apt.getStatus()))
                .filter(apt -> officeLocation == null || officeLocation.equals(apt.getOfficeLocation()))
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(filtered);
    }
    
    @GetMapping("/today")
    @Operation(summary = "Get today's appointments")
    public ResponseEntity<List<AppointmentDto>> getTodaysAppointments() {
        List<AppointmentDto> today = appointmentService.getAllAppointments().stream()
                .filter(apt -> apt.getAppointmentDate().toLocalDate()
                        .equals(java.time.LocalDate.now()))
                .filter(apt -> "SCHEDULED".equals(apt.getStatus()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(today);
    }
    
    @GetMapping("/{appointmentId}")
    @Operation(summary = "Get appointment details by ID")
    public ResponseEntity<AppointmentDto> getAppointmentById(@PathVariable Long appointmentId) {
        return appointmentService.getAppointmentById(appointmentId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PutMapping("/{appointmentId}/complete")
    @Operation(summary = "Mark appointment as completed")
    public ResponseEntity<Void> completeAppointment(
            @PathVariable Long appointmentId,
            Authentication authentication
    ) {
        String adminUsername = authentication.getName();
        boolean updated = appointmentService.updateAppointmentStatus(appointmentId, "COMPLETED", adminUsername);
        
        if (updated) {
            log.info("Appointment {} marked completed by {}", appointmentId, adminUsername);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PutMapping("/{appointmentId}/no-show")
    @Operation(summary = "Mark appointment as no-show")
    public ResponseEntity<Void> markNoShow(
            @PathVariable Long appointmentId,
            Authentication authentication
    ) {
        String adminUsername = authentication.getName();
        boolean updated = appointmentService.updateAppointmentStatus(appointmentId, "NO_SHOW", adminUsername);
        
        if (updated) {
            log.info("Appointment {} marked as no-show by {}", appointmentId, adminUsername);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/stats")
    @Operation(summary = "Get appointment statistics")
    public ResponseEntity<AppointmentStats> getStats() {
        List<AppointmentDto> allAppointments = appointmentService.getAllAppointments();
        
        AppointmentStats stats = new AppointmentStats();
        stats.setTotal(allAppointments.size());
        stats.setScheduled(allAppointments.stream().filter(a -> "SCHEDULED".equals(a.getStatus())).count());
        stats.setCompleted(allAppointments.stream().filter(a -> "COMPLETED".equals(a.getStatus())).count());
        stats.setCancelled(allAppointments.stream().filter(a -> "CANCELLED".equals(a.getStatus())).count());
        stats.setNoShow(allAppointments.stream().filter(a -> "NO_SHOW".equals(a.getStatus())).count());
        
        return ResponseEntity.ok(stats);
    }
    
    // Stats DTO
    public static class AppointmentStats {
        private int total;
        private long scheduled;
        private long completed;
        private long cancelled;
        private long noShow;
        
        public int getTotal() { return total; }
        public void setTotal(int total) { this.total = total; }
        public long getScheduled() { return scheduled; }
        public void setScheduled(long scheduled) { this.scheduled = scheduled; }
        public long getCompleted() { return completed; }
        public void setCompleted(long completed) { this.completed = completed; }
        public long getCancelled() { return cancelled; }
        public void setCancelled(long cancelled) { this.cancelled = cancelled; }
        public long getNoShow() { return noShow; }
        public void setNoShow(long noShow) { this.noShow = noShow; }
    }
}
