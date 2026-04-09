package com.facilon.app.repository;

import com.facilon.app.model.UserGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserGroupRepository extends JpaRepository<UserGroup, Long> {
    Optional<UserGroup> findByGroupName(String groupName);

    @Query(value = "SELECT DISTINCT g.* FROM user_group g " +
           "LEFT JOIN group_role gr ON g.group_id = gr.group_id " +
           "LEFT JOIN role_list r ON gr.role_list_id = r.role_list_id " +
           "WHERE (:groupName IS NULL OR g.group_name ILIKE CONCAT('%', :groupName, '%')) " +
           "AND (:roleLabel IS NULL OR EXISTS (SELECT 1 FROM group_role gr2 " +
           "JOIN role_list r2 ON gr2.role_list_id = r2.role_list_id " +
           "WHERE r2.label ILIKE :roleLabel AND gr2.group_id = g.group_id))", 
           nativeQuery = true)
    List<UserGroup> findByFilters(@Param("groupName") String groupName, @Param("roleLabel") String roleLabel);
    
    // Count active user groups globally
    long countByIsActiveTrue();
    
    // Find active user groups globally
    List<UserGroup> findByIsActiveTrue();
}
