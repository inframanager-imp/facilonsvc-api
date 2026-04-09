package com.facilon.app.module.client.model.master;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Master service provider types (Broker, Bank, PMS, etc.).
 * Global reference data.
 */
@Entity
@Table(name = "master_service_provider_type")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MasterServiceProviderType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "my_row_id")
    private Long myRowId;

    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "ss_name", columnDefinition = "TEXT")
    private String ssName;

    @Column(name = "ss_provider_id", columnDefinition = "TEXT")
    private String ssProviderId;
}
