package com.facilon.app.module.client.repository;

import com.facilon.app.module.client.model.master.MasterNationality;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MasterNationalityRepository extends JpaRepository<MasterNationality, Long> {
    Optional<MasterNationality> findBySsNationalityId(String ssNationalityId);
}
