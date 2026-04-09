package com.facilon.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tenant_graph_email_config")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantGraphEmailConfig extends TenantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "enabled", nullable = false)
    @Builder.Default
    private Boolean enabled = true;

    @Size(max = 256)
    @Column(name = "tenant_id_azure", nullable = false)
    private String tenantIdAzure;

    @Size(max = 256)
    @Column(name = "client_id", nullable = false)
    private String clientId;

    @Column(name = "client_secret", nullable = false, columnDefinition = "TEXT")
    private String clientSecret;

    @Size(max = 256)
    @Column(name = "sender_email", nullable = false)
    private String senderEmail;

    @Size(max = 256)
    @Column(name = "sender_name")
    @Builder.Default
    private String senderName = "Facilon Platform";

    @Column(name = "max_retries")
    @Builder.Default
    private Integer maxRetries = 3;

    @Column(name = "retry_delay_ms")
    @Builder.Default
    private Long retryDelayMs = 1000L;

    @Column(name = "connection_timeout")
    @Builder.Default
    private Integer connectionTimeout = 30000;

    @Column(name = "read_timeout")
    @Builder.Default
    private Integer readTimeout = 30000;

    @Column(name = "debug_mode")
    @Builder.Default
    private Boolean debugMode = false;
}
