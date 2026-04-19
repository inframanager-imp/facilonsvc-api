package com.facilon.app.module.client.repository;

import com.facilon.app.module.client.model.master.MasterGender;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MasterGenderRepository extends JpaRepository<MasterGender, Long> {

    /** Resolve Dataverse {@code ss_genderid} GUID from local business {@code id}. */
    @Query("SELECT m FROM MasterGender m WHERE m.id = :id")
    Optional<MasterGender> findByBusinessId(@Param("id") Integer id);
}
