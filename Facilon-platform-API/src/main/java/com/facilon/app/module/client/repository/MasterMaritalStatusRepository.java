package com.facilon.app.module.client.repository;

import com.facilon.app.module.client.model.master.MasterMaritalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MasterMaritalStatusRepository extends JpaRepository<MasterMaritalStatus, Long> {

    /** Resolve Dataverse {@code ss_maritialstatusid} GUID from local business {@code id}. */
    @Query("SELECT m FROM MasterMaritalStatus m WHERE m.id = :id")
    Optional<MasterMaritalStatus> findByBusinessId(@Param("id") Integer id);
}
