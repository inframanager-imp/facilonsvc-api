package com.facilon.app.module.client.controller;

import com.facilon.app.module.client.dto.SessionDto;
import com.facilon.app.module.client.dto.SessionStatusDto;
import com.facilon.app.module.client.model.InvestorSession;
import com.facilon.app.module.client.service.SessionManagementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * REST Controller for investor session management
 * Supports both local JWT and Azure B2C authentication
 */
@Slf4j
@RestController
@RequestMapping("/api/investor/session")
public class InvestorSessionController {

    @Autowired
    private SessionManagementService sessionManagementService;

    /**
     * Logout current session
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(HttpServletRequest request) {
        try {
            String token = extractToken(request);
            if (token != null) {
                sessionManagementService.logoutByToken(token, "manual");
            }

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Logged out successfully");
            response.put("success", true);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error during logout", e);
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Logout failed",
                    "error", e.getMessage()));
        }
    }

    /**
     * Get current session status
     */
    @GetMapping("/status")
    public ResponseEntity<?> getSessionStatus(HttpServletRequest request) {
        try {
            String token = extractToken(request);
            if (token == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "No token provided"));
            }

            Optional<InvestorSession> sessionOpt = sessionManagementService.getSessionByToken(token);
            if (sessionOpt.isPresent()) {
                InvestorSession session = sessionOpt.get();
                SessionStatusDto status = sessionManagementService.getSessionStatus(session.getSessionId());
                return ResponseEntity.ok(status);
            }

            return ResponseEntity.badRequest().body(Map.of("error", "Session not found"));
        } catch (Exception e) {
            log.error("Error getting session status", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get all active sessions for current investor
     */
    @GetMapping("/active")
    public ResponseEntity<?> getActiveSessions(HttpServletRequest request) {
        try {
            String token = extractToken(request);
            if (token == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "No token provided"));
            }

            Optional<InvestorSession> currentSessionOpt = sessionManagementService.getSessionByToken(token);

            if (currentSessionOpt.isPresent()) {
                InvestorSession currentSession = currentSessionOpt.get();
                List<SessionDto> sessions = sessionManagementService.getActiveSessions(
                        currentSession.getInvestorId(),
                        currentSession.getSessionId());
                return ResponseEntity.ok(sessions);
            }

            return ResponseEntity.badRequest().body(Map.of("error", "Session not found"));
        } catch (Exception e) {
            log.error("Error getting active sessions", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Logout from all devices except current
     */
    @PostMapping("/logout-all")
    public ResponseEntity<Map<String, Object>> logoutAllDevices(HttpServletRequest request) {
        try {
            String token = extractToken(request);
            if (token == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "No token provided"));
            }

            Optional<InvestorSession> currentSessionOpt = sessionManagementService.getSessionByToken(token);
            if (currentSessionOpt.isPresent()) {
                InvestorSession currentSession = currentSessionOpt.get();
                int count = sessionManagementService.logoutAllSessions(
                        currentSession.getInvestorId(),
                        currentSession.getSessionId());

                Map<String, Object> response = new HashMap<>();
                response.put("message", "Logged out from all other devices");
                response.put("count", count);
                response.put("success", true);

                return ResponseEntity.ok(response);
            }

            return ResponseEntity.badRequest().body(Map.of("error", "Session not found"));
        } catch (Exception e) {
            log.error("Error logging out all devices", e);
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Failed to logout all devices",
                    "error", e.getMessage()));
        }
    }

    /**
     * Logout specific session by session ID
     */
    @PostMapping("/logout/{sessionId}")
    public ResponseEntity<Map<String, Object>> logoutSession(
            @PathVariable String sessionId,
            HttpServletRequest request) {
        try {
            String token = extractToken(request);
            if (token == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "No token provided"));
            }

            // Verify the session belongs to the current investor
            Optional<InvestorSession> currentSessionOpt = sessionManagementService.getSessionByToken(token);
            Optional<InvestorSession> targetSessionOpt = sessionManagementService.getSessionByToken(token);

            if (currentSessionOpt.isPresent()) {
                sessionManagementService.logout(sessionId, "forced");

                Map<String, Object> response = new HashMap<>();
                response.put("message", "Session logged out successfully");
                response.put("success", true);

                return ResponseEntity.ok(response);
            }

            return ResponseEntity.badRequest().body(Map.of("error", "Unauthorized"));
        } catch (Exception e) {
            log.error("Error logging out session", e);
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Failed to logout session",
                    "error", e.getMessage()));
        }
    }

    // Helper method to extract JWT token from request
    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
