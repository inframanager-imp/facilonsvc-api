package com.facilon.app.module.client.repository;

import com.facilon.app.module.client.model.master.MasterAccounts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MasterAccountsRepository extends JpaRepository<MasterAccounts, Long> {

    Optional<MasterAccounts> findByAccountId(String accountId);

    Optional<MasterAccounts> findFirstBySsBrokerValue(String ssBrokerValue);
}
