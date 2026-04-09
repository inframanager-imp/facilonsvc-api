package com.facilon.app.module.client.repository;

import com.facilon.app.module.client.model.InvestorTaxInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InvestorTaxInformationRepository extends JpaRepository<InvestorTaxInformation, Long> {

    Optional<InvestorTaxInformation> findByInvestorUniqueId(String investorUniqueId);

    Optional<InvestorTaxInformation> findByPanNumber(String panNumber);
}
