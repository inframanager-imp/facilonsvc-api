package com.facilon.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "tenant")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Tenant extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tenant_id")
    private Long tenantId;

    @Size(max = 256)
    @Column(name = "name")
    private String tenantName;

    @Size(max = 256)
    @Column(name = "email_domain")
    private String emailDomain;

    @Size(max = 256)
    @Column(name = "address")
    private String address;

    @Size(max = 256)
    @Column(name = "state")
    private String state;

    @Size(max = 256)
    @Column(name = "postal_code")
    private String postalCode;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Size(max = 256)
    @Column(name = "country")
    private String country;
}
