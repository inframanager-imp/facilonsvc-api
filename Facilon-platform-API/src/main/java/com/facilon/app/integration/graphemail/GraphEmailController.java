package com.facilon.app.integration.graphemail;

import com.facilon.app.integration.graphemail.dto.GraphEmailRequestDto;
import com.facilon.app.integration.graphemail.dto.GraphEmailResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * REST Controller for Graph Email Service
 * This can be extracted as a microservice endpoint later
 * 
 * Endpoints:
 * - POST /api/graph-email/send - Send email
 * - POST /api/graph-email/send-otp - Send OTP email
 * - POST /api/graph-email/send-welcome - Send welcome email
 * - POST /api/graph-email/send-with-attachments - Send email with attachments
 * - GET /api/graph-email/health - Health check
 */
@RestController
@RequestMapping("/api/graph-email")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Graph Email", description = "Microsoft Graph API Email Operations")
@ConditionalOnProperty(name = "graph.email.enabled", havingValue = "true")
public class GraphEmailController {

    private final GraphEmailService graphEmailService;
    private final GraphEmailTokenProvider tokenProvider;

    @PostMapping("/send")
    @Operation(summary = "Send email via Microsoft Graph API", 
               description = "Send email using Microsoft 365 Graph API with HTML content support")
    public ResponseEntity<GraphEmailResponseDto> sendEmail(@RequestBody GraphEmailRequestDto request) {
        try {
            log.info("Received Graph email request to: {}", request.getToEmail());

            boolean sent = graphEmailService.sendEmail(
                request.getToEmail(),
                request.getSubject(),
                request.getHtmlContent(),
                request.getCcEmails(),
                request.getBccEmails(),
                request.getFromEmail(),
                request.getReplyToEmail()
            );

            if (sent) {
                return ResponseEntity.ok(GraphEmailResponseDto.success(null));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(GraphEmailResponseDto.error("SEND_FAILED", "Failed to send email"));
            }
        } catch (Exception e) {
            log.error("Error sending email: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GraphEmailResponseDto.error("EXCEPTION", e.getMessage()));
        }
    }

    @PostMapping("/send-otp")
    @Operation(summary = "Send OTP email", description = "Send OTP verification email to user")
    public ResponseEntity<GraphEmailResponseDto> sendOtpEmail(
            @RequestParam String toEmail,
            @RequestParam String firstName,
            @RequestParam String otp) {
        try {
            log.info("Sending OTP email to: {}", toEmail);

            boolean sent = graphEmailService.sendOtpEmail(toEmail, firstName, otp);

            if (sent) {
                return ResponseEntity.ok(GraphEmailResponseDto.success(null));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(GraphEmailResponseDto.error("SEND_FAILED", "Failed to send OTP email"));
            }
        } catch (Exception e) {
            log.error("Error sending OTP email: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GraphEmailResponseDto.error("EXCEPTION", e.getMessage()));
        }
    }

    @PostMapping("/send-welcome")
    @Operation(summary = "Send welcome email", description = "Send welcome email to new user")
    public ResponseEntity<GraphEmailResponseDto> sendWelcomeEmail(
            @RequestParam String toEmail,
            @RequestParam String firstName,
            @RequestParam String lastName) {
        try {
            log.info("Sending welcome email to: {}", toEmail);

            boolean sent = graphEmailService.sendWelcomeEmail(toEmail, firstName, lastName);

            if (sent) {
                return ResponseEntity.ok(GraphEmailResponseDto.success(null));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(GraphEmailResponseDto.error("SEND_FAILED", "Failed to send welcome email"));
            }
        } catch (Exception e) {
            log.error("Error sending welcome email: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GraphEmailResponseDto.error("EXCEPTION", e.getMessage()));
        }
    }

    @PostMapping("/send-with-attachments")
    @Operation(summary = "Send email with attachments", 
               description = "Send email with file attachments via Graph API")
    public ResponseEntity<GraphEmailResponseDto> sendEmailWithAttachments(
            @RequestParam String toEmail,
            @RequestParam String subject,
            @RequestParam String htmlContent,
            @RequestParam("files") List<MultipartFile> files) {
        try {
            log.info("Sending email with {} attachments to: {}", files.size(), toEmail);

            boolean sent = graphEmailService.sendEmailWithAttachments(
                toEmail, subject, htmlContent, files
            );

            if (sent) {
                return ResponseEntity.ok(GraphEmailResponseDto.success(null));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(GraphEmailResponseDto.error("SEND_FAILED", "Failed to send email with attachments"));
            }
        } catch (Exception e) {
            log.error("Error sending email with attachments: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GraphEmailResponseDto.error("EXCEPTION", e.getMessage()));
        }
    }

    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Check if Graph Email service is operational")
    public ResponseEntity<GraphEmailResponseDto> healthCheck() {
        try {
            boolean tokenValid = tokenProvider.isTokenValid();
            
            if (tokenValid) {
                return ResponseEntity.ok(GraphEmailResponseDto.builder()
                        .success(true)
                        .message("Graph Email service is operational (token cached)")
                        .build());
            } else {
                // Try to get new token
                String token = tokenProvider.getGraphToken();
                if (token != null && !token.isEmpty()) {
                    return ResponseEntity.ok(GraphEmailResponseDto.builder()
                            .success(true)
                            .message("Graph Email service is operational (token refreshed)")
                            .build());
                } else {
                    return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                            .body(GraphEmailResponseDto.error("TOKEN_FAILED", "Unable to obtain access token"));
                }
            }
        } catch (Exception e) {
            log.error("Health check failed: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(GraphEmailResponseDto.error("HEALTH_CHECK_FAILED", e.getMessage()));
        }
    }

    @PostMapping("/invalidate-token")
    @Operation(summary = "Invalidate cached token", description = "Force refresh of access token")
    public ResponseEntity<GraphEmailResponseDto> invalidateToken() {
        try {
            tokenProvider.invalidateToken();
            return ResponseEntity.ok(GraphEmailResponseDto.builder()
                    .success(true)
                    .message("Token invalidated successfully")
                    .build());
        } catch (Exception e) {
            log.error("Error invalidating token: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GraphEmailResponseDto.error("INVALIDATE_FAILED", e.getMessage()));
        }
    }
}
