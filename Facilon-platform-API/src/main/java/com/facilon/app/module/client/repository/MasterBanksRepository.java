package com.facilon.app.module.client.repository;

import com.facilon.app.module.client.model.master.MasterBanks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MasterBanksRepository extends JpaRepository<MasterBanks, Long> {
    Optional<MasterBanks> findFirstBySsBankId(String ssBankId);
}
