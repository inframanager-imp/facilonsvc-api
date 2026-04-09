package com.facilon.app.repository;


import com.facilon.app.model.RoleList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleListRepository extends JpaRepository<RoleList, Long> {
    Optional<RoleList> findByLabel(String roleName);
    
    // Count active roles globally
    long countByIsActiveTrue();
    
    // Find active roles globally
    List<RoleList> findByIsActiveTrue();
}
