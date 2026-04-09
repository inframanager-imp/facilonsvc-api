package com.facilon.app.module.client.model;

import com.facilon.app.model.TenantEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "introduced_registration_sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class IntroducedRegistrationSession extends TenantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "unique_code", unique = true, nullable = false, length = 50)
    private String uniqueCode;

    @Column(name = "dataverse_investor_id", nullable = false, length = 100)
    private String dataverseInvestorId; // Display name like INV-1744

    @Column(name = "dataverse_investor_guid", nullable = false, length = 100)
    private String dataverseInvestorGuid; // Actual GUID

    @Column(name = "email", nullable = false, length = 255)
    private String email;

    @Column(name = "mobile_number", length = 20)
    private String mobileNumber;

    @Column(name = "country_code", length = 10)
    private String countryCode;

    @Column(name = "first_name", length = 100)
    private String firstName;

    @Column(name = "middle_name", length = 100)
    private String middleName;

    @Column(name = "last_name", length = 100)
    private String lastName;

    @Column(name = "gender", length = 20)
    private String gender;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "current_step", nullable = false)
    @Builder.Default
    private Integer currentStep = 0;

    @Column(name = "consent_given", nullable = false)
    @Builder.Default
    private Boolean consentGiven = false;

    @Column(name = "consent_given_at")
    private LocalDateTime consentGivenAt;

    @Column(name = "email_otp_verified", nullable = false)
    @Builder.Default
    private Boolean emailOtpVerified = false;

    @Column(name = "mobile_otp_verified", nullable = false)
    @Builder.Default
    private Boolean mobileOtpVerified = false;

    @Column(name = "registration_completed", nullable = false)
    @Builder.Default
    private Boolean registrationCompleted = false;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;
}
