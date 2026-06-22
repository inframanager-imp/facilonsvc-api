package com.facilon.app.repository;

import com.facilon.app.model.Tenant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TenantRepository extends JpaRepository<Tenant, Long> {
    // Count active tenants
    long countByIsActiveTrue();

    // Find only active tenants with pagination
    Page<Tenant> findByIsActiveTrue(Pageable pageable);

    // Find all active tenants (used by cron jobs that fan out per tenant)
    List<Tenant> findByIsActiveTrue();
}
