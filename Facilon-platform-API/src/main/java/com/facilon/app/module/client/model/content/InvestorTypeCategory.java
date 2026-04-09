package com.facilon.app.module.client.model.content;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Investor type categories with eligibility criteria.
 * Global reference data (not tenant-scoped).
 */
@Entity
@Table(name = "investor_type_category")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvestorTypeCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "my_row_id")
    private Long myRowId;

    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "cat_code", columnDefinition = "TEXT")
    private String catCode;

    @Column(name = "register_as")
    private String registerAs;

    @Column(name = "nationality", columnDefinition = "TEXT")
    private String nationality;

    @Column(name = "resident")
    private String resident;

    @Column(name = "non_resident")
    private String nonResident;

    @Column(name = "pancard")
    private String pancard;

    @Column(name = "indian_origin")
    private String indianOrigin;

    @Column(name = "cat_name", columnDefinition = "TEXT")
    private String catName;

    @Column(name = "oci_card")
    private String ociCard;

    @Column(name = "country")
    private String country;

    @Column(name = "status", nullable = false)
    @Builder.Default
    private Integer status = 1;

    @Column(name = "api_exlcusion", nullable = false)
    @Builder.Default
    private Integer apiExclusion = 1;

    @Column(name = "created_at", nullable = false)
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
