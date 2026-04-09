package com.facilon.app.module.client.repository;

import com.facilon.app.module.client.model.master.MasterCountryOfResidence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MasterCountryOfResidenceRepository extends JpaRepository<MasterCountryOfResidence, Long> {
    Optional<MasterCountryOfResidence> findBySsCountryId(String ssCountryId);
}
