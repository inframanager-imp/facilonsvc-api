package com.facilon.app.repository;

import com.facilon.app.model.TenantSharePointConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TenantSharePointConfigRepository extends JpaRepository<TenantSharePointConfig, Long> {

    Optional<TenantSharePointConfig> findByTenant_TenantId(Long tenantId);

    boolean existsByTenant_TenantId(Long tenantId);
}
