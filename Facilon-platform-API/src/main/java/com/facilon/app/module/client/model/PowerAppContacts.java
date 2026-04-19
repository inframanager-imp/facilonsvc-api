package com.facilon.app.module.client.model;

import com.facilon.app.model.TenantEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * PowerApp contacts synchronized from Microsoft PowerApps.
 * Tenant-scoped.
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "powerapp_contacts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PowerAppContacts extends TenantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "powerapp_contact_id", unique = true, length = 100)
    private String powerAppContactId;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "first_name", length = 150)
    private String firstName;

    @Column(name = "last_name", length = 150)
    private String lastName;

    @Column(name = "email")
    private String email;

    @Column(name = "phone", length = 50)
    private String phone;

    @Column(name = "broker_id", length = 100)
    private String brokerId;

    @Column(name = "account_id", length = 100)
    private String accountId;

    @Column(name = "broker")
    private String broker;

    /**
     * Service-provider contact type resolved from Dataverse {@code ss_contacttype} option set:
     * 100000000=Portfolio Manager, 100000001=Broker, 100000002=Custodian, 100000003=Bank.
     * Matches Laravel {@code SyncNewServiceProviders::$contactTypeMap}.
     */
    @Column(name = "contact_type", length = 50)
    private String contactType;

    @Column(name = "invite_redeem_url", columnDefinition = "TEXT")
    private String inviteRedeemUrl;

    @Column(name = "b2c_status")
    private String b2cStatus;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
