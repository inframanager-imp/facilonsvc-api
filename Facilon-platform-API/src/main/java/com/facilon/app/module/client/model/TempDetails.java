package com.facilon.app.module.client.model;

import com.facilon.app.model.TenantEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Temporary details storage during investor onboarding.
 * Tenant-scoped.
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "temp_details")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TempDetails extends TenantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "unique_code")
    private String uniqueCode;

    @Column(name = "register_as")
    private String registerAs;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "nationality")
    private String nationality;

    @Column(name = "resident")
    private String resident;

    @Column(name = "pancard")
    private String pancard;

    @Column(name = "indian_origin")
    private String indianOrigin;

    @Column(name = "oci_card")
    private String ociCard;

    @Column(name = "entity_name")
    private String entityName;

    @Column(name = "legal_country")
    private String legalCountry;

    @Column(name = "entity_rep_name")
    private String entityRepName;

    @Column(name = "rep_capacity")
    private String repCapacity;

    @Column(name = "regulation")
    private String regulation;

    @Column(name = "agree_to_watsapp")
    private Integer agreeToWhatsapp;

    @Column(name = "confirmation")
    private Integer confirmation;

    @Column(name = "agree_terms")
    private Integer agreeTerms;
}
