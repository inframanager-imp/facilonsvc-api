package com.facilon.app.repository;

import com.facilon.app.model.TenantDynamicsConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TenantDynamicsConfigRepository extends JpaRepository<TenantDynamicsConfig, Long> {

    Optional<TenantDynamicsConfig> findByTenant_TenantId(Long tenantId);

    boolean existsByTenant_TenantId(Long tenantId);
}
