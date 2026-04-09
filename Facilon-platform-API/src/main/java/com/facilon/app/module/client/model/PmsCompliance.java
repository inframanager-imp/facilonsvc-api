package com.facilon.app.module.client.model;

import com.facilon.app.model.TenantEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "investor_pms_compliance")
public class PmsCompliance extends TenantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "investor_id")
    private Long investorId;

    @Column(name = "risk_disclosure_acknowledged")
    private Boolean riskDisclosureAcknowledged;

    @Column(name = "fee_structure_accepted")
    private Boolean feeStructureAccepted;

    @Column(name = "terms_accepted")
    private Boolean termsAccepted;

    @Column(name = "regulatory_disclosure_acknowledged")
    private Boolean regulatoryDisclosureAcknowledged;

    @Column(name = "conflict_of_interest_disclosed")
    private Boolean conflictOfInterestDisclosed;

    @Column(name = "performance_disclosure_acknowledged")
    private Boolean performanceDisclosureAcknowledged;

    @Column(name = "risk_disclosure_date")
    private LocalDateTime riskDisclosureDate;

    @Column(name = "fee_structure_date")
    private LocalDateTime feeStructureDate;

    @Column(name = "terms_accepted_date")
    private LocalDateTime termsAcceptedDate;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "user_agent")
    private String userAgent;

    @Column(name = "digital_signature")
    private String digitalSignature;

    @Column(name = "compliance_status")
    private String complianceStatus;
}
