package com.facilon.app.module.client.repository;

import com.facilon.app.module.client.model.InvestorNomination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvestorNominationRepository extends JpaRepository<InvestorNomination, Long> {

    List<InvestorNomination> findByInvestorUniqueId(String investorUniqueId);
}
