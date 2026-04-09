package com.facilon.app.repository;

import com.facilon.app.model.AuthorizedUser;
import com.facilon.app.model.UserGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface AuthorizedUserRepository extends JpaRepository<AuthorizedUser, Long> {
    Optional<AuthorizedUser> findByEmailIdOrLoginId(String emailId, String loginId);
    Optional<AuthorizedUser> findByLoginId(String loginId);
    Optional<AuthorizedUser> findByEmailId(String loginId);
    Optional<AuthorizedUser> findById(Long id);

    @Query("SELECT DISTINCT u FROM AuthorizedUser u " +
           "JOIN u.userGroups g " +
           "WHERE g.groupName = 'Forwarder'")
    List<AuthorizedUser> findUsersWithForwarderRole();

    @Query("SELECT DISTINCT u FROM AuthorizedUser u " +
           "JOIN u.userGroups g " +
           "WHERE g.groupName = :groupName")
    List<AuthorizedUser> findByUserGroups_GroupName(String groupName);

    List<AuthorizedUser> findByUserGroupsContaining(UserGroup group);

    List<AuthorizedUser> findByUserGroups(UserGroup group);
    
    // Count by tenant ID (for multi-tenant support)
    @Query("SELECT COUNT(u) FROM AuthorizedUser u WHERE u.tenant.id = :tenantId")
    long countByTenantId(@Param("tenantId") Long tenantId);
    
    // Count active users by tenant ID
    @Query("SELECT COUNT(u) FROM AuthorizedUser u WHERE u.tenant.id = :tenantId AND u.isActive = true")
    long countActiveUsersByTenantId(@Param("tenantId") Long tenantId);
    
    // Find users by tenant ID
    @Query("SELECT u FROM AuthorizedUser u WHERE u.tenant.id = :tenantId")
    List<AuthorizedUser> findByTenantId(@Param("tenantId") Long tenantId);
    
    // Find active users by tenant ID
    @Query("SELECT u FROM AuthorizedUser u WHERE u.tenant.id = :tenantId AND u.isActive = true")
    List<AuthorizedUser> findActiveUsersByTenantId(@Param("tenantId") Long tenantId);
    
    // Count active users globally
    long countByIsActiveTrue();
    
    // Find active users with pagination
    org.springframework.data.domain.Page<AuthorizedUser> findByIsActiveTrue(org.springframework.data.domain.Pageable pageable);
    
    // Find active users globally
    List<AuthorizedUser> findByIsActiveTrue();
    
    // Find users by user group
    @Query("SELECT u FROM AuthorizedUser u JOIN u.userGroups g WHERE g.id = :groupId AND u.isActive = true")
    List<AuthorizedUser> findByUserGroupsIdAndIsActiveTrue(@Param("groupId") Long groupId);
    
    // Find users by tenant ID with pagination
    @Query("SELECT u FROM AuthorizedUser u WHERE u.tenant.id = :tenantId")
    org.springframework.data.domain.Page<AuthorizedUser> findByTenantId(@Param("tenantId") Long tenantId, org.springframework.data.domain.Pageable pageable);
    
    // Find active users by tenant ID with pagination
    @Query("SELECT u FROM AuthorizedUser u WHERE u.tenant.id = :tenantId AND u.isActive = true")
    org.springframework.data.domain.Page<AuthorizedUser> findActiveUsersByTenantId(@Param("tenantId") Long tenantId, org.springframework.data.domain.Pageable pageable);
}
