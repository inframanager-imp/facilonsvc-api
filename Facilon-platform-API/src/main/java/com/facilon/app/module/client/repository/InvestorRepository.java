package com.facilon.app.module.client.repository;

import com.facilon.app.module.client.model.Investor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvestorRepository extends JpaRepository<Investor, Long> {

    // Find by authorized user
    Optional<Investor> findByAuthorizedUser_Id(Long authorizedUserId);
    
    // Find by authorized user and tenant
    @Query("SELECT i FROM Investor i WHERE i.authorizedUser.id = :authorizedUserId AND i.tenant.id = :tenantId")
    Optional<Investor> findByAuthorizedUserIdAndTenantId(@Param("authorizedUserId") Long authorizedUserId, @Param("tenantId") Long tenantId);
    
    // Find by unique code
    Optional<Investor> findByUniqueCode(String uniqueCode);
    
    // Find by tenant
    @Query("SELECT i FROM Investor i WHERE i.tenant.id = :tenantId")
    List<Investor> findByTenantId(@Param("tenantId") Long tenantId);
    
    // Find by tenant with pagination
    @Query("SELECT i FROM Investor i WHERE i.tenant.id = :tenantId")
    Page<Investor> findByTenantId(@Param("tenantId") Long tenantId, Pageable pageable);
    
    // Find active investors by tenant
    @Query("SELECT i FROM Investor i WHERE i.tenant.id = :tenantId AND i.verifyStatus = :status")
    List<Investor> findByTenantIdAndVerifyStatus(@Param("tenantId") Long tenantId, @Param("status") Integer status);
    
    // Count investors by tenant
    @Query("SELECT COUNT(i) FROM Investor i WHERE i.tenant.id = :tenantId")
    Long countByTenantId(@Param("tenantId") Long tenantId);
    
    // Count verified investors by tenant
    @Query("SELECT COUNT(i) FROM Investor i WHERE i.tenant.id = :tenantId AND i.verifyStatus = 1")
    Long countVerifiedByTenantId(@Param("tenantId") Long tenantId);
    
    // Search investors by name or email
    @Query("SELECT i FROM Investor i " +
           "WHERE i.tenant.id = :tenantId " +
           "AND (LOWER(i.authorizedUser.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(i.authorizedUser.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(i.authorizedUser.emailId) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(i.uniqueCode) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Investor> searchInvestors(@Param("tenantId") Long tenantId, 
                                   @Param("searchTerm") String searchTerm, 
                                   Pageable pageable);
}
