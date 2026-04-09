package com.facilon.app.module.client.repository;

import com.facilon.app.module.client.model.master.MasterBrokerBanks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MasterBrokerBanksRepository extends JpaRepository<MasterBrokerBanks, Long> {
    /** Look up a broker-bank association by its GUID (intro_investor_temp.broker_prefferedbank). */
    Optional<MasterBrokerBanks> findFirstBySsBrokerBankId(String ssBrokerBankId);
}
