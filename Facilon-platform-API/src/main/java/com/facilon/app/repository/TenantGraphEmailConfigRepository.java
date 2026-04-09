package com.facilon.app.repository;

import com.facilon.app.model.TenantGraphEmailConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TenantGraphEmailConfigRepository extends JpaRepository<TenantGraphEmailConfig, Long> {

    Optional<TenantGraphEmailConfig> findByTenant_TenantId(Long tenantId);

    boolean existsByTenant_TenantId(Long tenantId);
}
