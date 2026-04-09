package com.facilon.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tenant_sharepoint_config")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantSharePointConfig extends TenantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "enabled", nullable = false)
    @Builder.Default
    private Boolean enabled = false;

    @Size(max = 512)
    @Column(name = "site_hostname")
    private String siteHostname;

    @Size(max = 512)
    @Column(name = "site_path")
    @Builder.Default
    private String sitePath = "/sites/FacilonInvestor";

    @Size(max = 256)
    @Column(name = "tenant_id_azure")
    private String tenantIdAzure;

    @Size(max = 256)
    @Column(name = "client_id")
    private String clientId;

    @Column(name = "client_secret", columnDefinition = "TEXT")
    private String clientSecret;
}
