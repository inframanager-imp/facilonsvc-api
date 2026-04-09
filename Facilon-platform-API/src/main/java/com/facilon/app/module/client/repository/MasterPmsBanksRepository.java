package com.facilon.app.module.client.repository;

import com.facilon.app.module.client.model.master.MasterPmsBanks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MasterPmsBanksRepository extends JpaRepository<MasterPmsBanks, Long> {
}
