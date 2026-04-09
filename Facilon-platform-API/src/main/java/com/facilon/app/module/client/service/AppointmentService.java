package com.facilon.app.module.client.service;

import com.facilon.app.module.client.dto.AppointmentDto;
import com.facilon.app.module.client.dto.AppointmentRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Service for managing office visit appointments
 * TODO: Migrate to database persistence (JPA entities + repositories)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentService {

    // In-memory storage (TODO: migrate to DB)
    private final Map<Long, AppointmentDto> appointmentsById = new ConcurrentHashMap<>();
    private final Map<String, List<Long>> appointmentsByInvestor = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);
    
    // Office configurations
    private static final Map<String, String[]> TIME_SLOTS = Map.of(
            "MUMBAI", new String[]{
                    "10:00 AM - 10:30 AM", "10:30 AM - 11:00 AM", "11:00 AM - 11:30 AM",
                    "11:30 AM - 12:00 PM", "12:00 PM - 12:30 PM", "02:00 PM - 02:30 PM",
                    "02:30 PM - 03:00 PM", "03:00 PM - 03:30 PM", "03:30 PM - 04:00 PM",
                    "04:00 PM - 04:30 PM", "04:30 PM - 05:00 PM", "05:00 PM - 05:30 PM"
            },
            "DELHI", new String[]{
                    "10:00 AM - 10:30 AM", "10:30 AM - 11:00 AM", "11:00 AM - 11:30 AM",
                    "11:30 AM - 12:00 PM", "12:00 PM - 12:30 PM", "02:00 PM - 02:30 PM",
                    "02:30 PM - 03:00 PM", "03:00 PM - 03:30 PM", "03:30 PM - 04:00 PM",
                    "04:00 PM - 04:30 PM", "04:30 PM - 05:00 PM", "05:00 PM - 05:30 PM"
            },
            "BANGALORE", new String[]{
                    "10:00 AM - 10:30 AM", "10:30 AM - 11:00 AM", "11:00 AM - 11:30 AM",
                    "11:30 AM - 12:00 PM", "12:00 PM - 12:30 PM", "02:00 PM - 02:30 PM",
                    "02:30 PM - 03:00 PM", "03:00 PM - 03:30 PM", "03:30 PM - 04:00 PM",
                    "04:00 PM - 04:30 PM", "04:30 PM - 05:00 PM", "05:00 PM - 05:30 PM"
            }
    );
    
    private static final int MAX_APPOINTMENTS_PER_SLOT = 3; // Allow 3 appointments per slot
    
    /**
     * Create new appointment
     */
    public AppointmentDto createAppointment(String investorCode, String investorName, 
                                           String investorEmail, String investorMobile,
                                           AppointmentRequest request) {
        // Validate slot availability
        if (!isSlotAvailable(request.getOfficeLocation(), request.getAppointmentDate(), request.getTimeSlot())) {
            throw new RuntimeException("Selected time slot is not available");
        }
        
        Long appointmentId = idGenerator.getAndIncrement();
        AppointmentDto appointment = new AppointmentDto();
        appointment.setId(appointmentId);
        appointment.setInvestorCode(investorCode);
        appointment.setInvestorName(investorName);
        appointment.setInvestorEmail(investorEmail);
        appointment.setInvestorMobile(investorMobile);
        appointment.setOfficeLocation(request.getOfficeLocation());
        appointment.setAppointmentDate(request.getAppointmentDate());
        appointment.setTimeSlot(request.getTimeSlot());
        appointment.setStatus("SCHEDULED");
        appointment.setPurpose("In-person document verification");
        appointment.setNotes(request.getNotes());
        appointment.setCreatedAt(LocalDateTime.now());
        appointment.setCreatedBy(investorCode);
        
        appointmentsById.put(appointmentId, appointment);
        appointmentsByInvestor.computeIfAbsent(investorCode, k -> new ArrayList<>()).add(appointmentId);
        
        log.info("Appointment created: {} for investor: {}", appointmentId, investorCode);
        return appointment;
    }
    
    /**
     * Get investor appointments
     */
    public List<AppointmentDto> getInvestorAppointments(String investorCode) {
        List<Long> appointmentIds = appointmentsByInvestor.getOrDefault(investorCode, Collections.emptyList());
        return appointmentIds.stream()
                .map(appointmentsById::get)
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(AppointmentDto::getAppointmentDate).reversed())
                .collect(Collectors.toList());
    }
    
    /**
     * Get appointment by ID
     */
    public Optional<AppointmentDto> getAppointmentById(Long id) {
        return Optional.ofNullable(appointmentsById.get(id));
    }
    
    /**
     * Cancel appointment
     */
    public boolean cancelAppointment(Long appointmentId, String investorCode) {
        AppointmentDto appointment = appointmentsById.get(appointmentId);
        if (appointment == null || !appointment.getInvestorCode().equals(investorCode)) {
            return false;
        }
        
        if ("COMPLETED".equals(appointment.getStatus())) {
            throw new RuntimeException("Cannot cancel completed appointment");
        }
        
        appointment.setStatus("CANCELLED");
        appointment.setUpdatedAt(LocalDateTime.now());
        appointment.setUpdatedBy(investorCode);
        
        log.info("Appointment {} cancelled by investor: {}", appointmentId, investorCode);
        return true;
    }
    
    /**
     * Reschedule appointment
     */
    public AppointmentDto rescheduleAppointment(Long appointmentId, String investorCode, AppointmentRequest request) {
        AppointmentDto appointment = appointmentsById.get(appointmentId);
        if (appointment == null || !appointment.getInvestorCode().equals(investorCode)) {
            throw new RuntimeException("Appointment not found");
        }
        
        if ("COMPLETED".equals(appointment.getStatus()) || "CANCELLED".equals(appointment.getStatus())) {
            throw new RuntimeException("Cannot reschedule " + appointment.getStatus().toLowerCase() + " appointment");
        }
        
        // Validate new slot availability
        if (!isSlotAvailableExcludingAppointment(request.getOfficeLocation(), request.getAppointmentDate(), 
                                                  request.getTimeSlot(), appointmentId)) {
            throw new RuntimeException("Selected time slot is not available");
        }
        
        appointment.setOfficeLocation(request.getOfficeLocation());
        appointment.setAppointmentDate(request.getAppointmentDate());
        appointment.setTimeSlot(request.getTimeSlot());
        appointment.setNotes(request.getNotes());
        appointment.setUpdatedAt(LocalDateTime.now());
        appointment.setUpdatedBy(investorCode);
        
        log.info("Appointment {} rescheduled by investor: {}", appointmentId, investorCode);
        return appointment;
    }
    
    /**
     * Get available time slots for a date and office
     */
    public List<String> getAvailableSlots(String officeLocation, LocalDate date) {
        String[] allSlots = TIME_SLOTS.getOrDefault(officeLocation, new String[0]);
        
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
        
        // Get booked appointments for this date and office
        Map<String, Long> slotBookings = appointmentsById.values().stream()
                .filter(apt -> officeLocation.equals(apt.getOfficeLocation()))
                .filter(apt -> "SCHEDULED".equals(apt.getStatus()))
                .filter(apt -> !apt.getAppointmentDate().isBefore(startOfDay) && 
                             !apt.getAppointmentDate().isAfter(endOfDay))
                .collect(Collectors.groupingBy(
                        AppointmentDto::getTimeSlot,
                        Collectors.counting()
                ));
        
        // Filter out fully booked slots
        return Arrays.stream(allSlots)
                .filter(slot -> slotBookings.getOrDefault(slot, 0L) < MAX_APPOINTMENTS_PER_SLOT)
                .collect(Collectors.toList());
    }
    
    /**
     * Get all appointments (Admin function)
     */
    public List<AppointmentDto> getAllAppointments() {
        return appointmentsById.values().stream()
                .sorted(Comparator.comparing(AppointmentDto::getAppointmentDate).reversed())
                .collect(Collectors.toList());
    }
    
    /**
     * Update appointment status (Admin function)
     */
    public boolean updateAppointmentStatus(Long appointmentId, String status, String adminUsername) {
        AppointmentDto appointment = appointmentsById.get(appointmentId);
        if (appointment == null) {
            return false;
        }
        
        appointment.setStatus(status);
        appointment.setUpdatedAt(LocalDateTime.now());
        appointment.setUpdatedBy(adminUsername);
        
        log.info("Appointment {} status updated to {} by {}", appointmentId, status, adminUsername);
        return true;
    }
    
    // Helper methods
    
    private boolean isSlotAvailable(String officeLocation, LocalDateTime date, String timeSlot) {
        long bookingsCount = appointmentsById.values().stream()
                .filter(apt -> officeLocation.equals(apt.getOfficeLocation()))
                .filter(apt -> "SCHEDULED".equals(apt.getStatus()))
                .filter(apt -> apt.getAppointmentDate().toLocalDate().equals(date.toLocalDate()))
                .filter(apt -> timeSlot.equals(apt.getTimeSlot()))
                .count();
        
        return bookingsCount < MAX_APPOINTMENTS_PER_SLOT;
    }
    
    private boolean isSlotAvailableExcludingAppointment(String officeLocation, LocalDateTime date, 
                                                        String timeSlot, Long excludeAppointmentId) {
        long bookingsCount = appointmentsById.values().stream()
                .filter(apt -> !apt.getId().equals(excludeAppointmentId))
                .filter(apt -> officeLocation.equals(apt.getOfficeLocation()))
                .filter(apt -> "SCHEDULED".equals(apt.getStatus()))
                .filter(apt -> apt.getAppointmentDate().toLocalDate().equals(date.toLocalDate()))
                .filter(apt -> timeSlot.equals(apt.getTimeSlot()))
                .count();
        
        return bookingsCount < MAX_APPOINTMENTS_PER_SLOT;
    }
}
