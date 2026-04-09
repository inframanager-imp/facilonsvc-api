package com.facilon.app.repository;

import com.facilon.app.model.TenantB2CConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TenantB2CConfigRepository extends JpaRepository<TenantB2CConfig, Long> {

    Optional<TenantB2CConfig> findByTenant_TenantId(Long tenantId);

    boolean existsByTenant_TenantId(Long tenantId);
}
