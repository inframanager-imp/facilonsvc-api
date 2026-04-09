package com.facilon.app.module.client.model;

import com.facilon.app.model.TenantEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Tracks investor login sessions for both local JWT and Azure B2C
 * authentication
 */
@Entity
@Table(name = "investor_sessions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class InvestorSession extends TenantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "investor_id", nullable = false)
    private Long investorId;

    @Column(name = "session_id", unique = true, nullable = false)
    private String sessionId; // UUID

    @Column(name = "jwt_token_hash")
    private String jwtTokenHash; // Hashed JWT for security

    @Column(name = "login_method", length = 50)
    private String loginMethod; // "local" or "azure-b2c"

    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    @Column(name = "login_time")
    private LocalDateTime loginTime;

    @Column(name = "last_activity_time")
    private LocalDateTime lastActivityTime;

    @Column(name = "logout_time")
    private LocalDateTime logoutTime;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "logout_reason", length = 50)
    private String logoutReason; // "manual", "timeout", "forced", "replaced"

    @PrePersist
    protected void onCreate() {
        if (loginTime == null) {
            loginTime = LocalDateTime.now();
        }
        if (lastActivityTime == null) {
            lastActivityTime = LocalDateTime.now();
        }
        if (isActive == null) {
            isActive = true;
        }
    }
}
