package com.facilon.app.module.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "investor_invitations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class InvestorInvitation implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "sender_id", nullable = false)
    private Long senderId; // ID of the investor sending the invitation

    @Column(name = "recipient_email", nullable = false)
    private String recipientEmail;

    @Column(name = "recipient_name", nullable = false)
    private String recipientName;

    @Column(name = "invitation_type", nullable = false)
    private String invitationType; // "nextholder", "pms_investor", "general"

    @Column(name = "invitation_token", unique = true)
    private String invitationToken;

    @Column(name = "status")
    private String status; // "pending", "sent", "opened", "accepted", "expired"

    @Column(name = "sent_date")
    private LocalDateTime sentDate;

    @Column(name = "opened_date")
    private LocalDateTime openedDate;

    @Column(name = "accepted_date")
    private LocalDateTime acceptedDate;

    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;

    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    @Column(name = "resend_count")
    @Builder.Default
    private Integer resendCount = 0;

    @Column(name = "last_resend_date")
    private LocalDateTime lastResendDate;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = "pending";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
