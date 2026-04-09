package com.facilon.app.repository;

import com.facilon.app.model.Tenant;
import com.facilon.app.model.AuthorizedUser;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Admin Repository - Aggregates data from multiple services
 * This repository acts as a facade for admin operations across different modules
 */
@Repository
public interface AdminRepository extends JpaRepository<Tenant, Long> {
    
    // Tenant operations
    Optional<Tenant> findByTenantName(String tenantName);
    Optional<Tenant> findByEmailDomain(String emailDomain);
    List<Tenant> findByIsActiveTrue();
    
    // User operations (from Auth Service)
    @Query("SELECT COUNT(u) FROM AuthorizedUser u WHERE u.tenant.id = :tenantId")
    Long countUsersByTenantId(@Param("tenantId") Long tenantId);
    
    @Query("SELECT u FROM AuthorizedUser u WHERE u.tenant.id = :tenantId AND u.isActive = true")
    List<AuthorizedUser> findActiveUsersByTenantId(@Param("tenantId") Long tenantId);
    
    @Query("SELECT u FROM AuthorizedUser u WHERE u.emailId = :email AND u.isActive = true")
    Optional<AuthorizedUser> findActiveUserByEmail(@Param("email") String email);
    
    // Recent activities queries
    @Query("SELECT u FROM AuthorizedUser u WHERE u.tenant.id = :tenantId ORDER BY u.lastLogin DESC")
    List<AuthorizedUser> getRecentUserLogins(@Param("tenantId") Long tenantId);
    

    // Multi-tenant search queries
    @Query("SELECT t FROM Tenant t WHERE t.tenantName LIKE %:searchTerm% OR t.emailDomain LIKE %:searchTerm%")
    List<Tenant> searchTenants(@Param("searchTerm") String searchTerm);
    
    @Query("SELECT u FROM AuthorizedUser u WHERE u.tenant.id = :tenantId AND " +
           "(u.firstName LIKE %:searchTerm% OR u.lastName LIKE %:searchTerm% OR u.emailId LIKE %:searchTerm%)")
    List<AuthorizedUser> searchUsers(@Param("tenantId") Long tenantId, @Param("searchTerm") String searchTerm);
    

    // Health check queries
    @Query("SELECT COUNT(t) FROM Tenant t WHERE t.isActive = true")
    Long getActiveTenantCount();
    
    @Query("SELECT COUNT(u) FROM AuthorizedUser u WHERE u.isActive = true")
    Long getActiveUserCount();
}
