package com.facilon.app.module.serviceprovider.model;

import com.facilon.app.model.TenantEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Mirrors Laravel `service_provider_users` written by
 * `BrokerController::service_provider_user_register_store` — the final user form
 * captures (first_name, last_name, designation, official_email, official_phone, consent).
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "service_provider_users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceProviderUser extends TenantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "service_provider_name", nullable = false)
    private String serviceProviderName;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "designation")
    private String designation;

    @Column(name = "official_email", nullable = false, unique = true)
    private String officialEmail;

    @Column(name = "official_phone", nullable = false, length = 32)
    private String officialPhone;

    @Column(name = "consent", nullable = false)
    private Boolean consent;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
