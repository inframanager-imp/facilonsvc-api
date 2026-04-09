package com.facilon.app.module.client.repository;

import com.facilon.app.module.client.model.InvestorBankDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvestorBankDetailsRepository extends JpaRepository<InvestorBankDetails, Long> {

    List<InvestorBankDetails> findByInvestorUniqueId(String investorUniqueId);

    Optional<InvestorBankDetails> findByInvestorUniqueIdAndIsPrimaryTrue(String investorUniqueId);

    Optional<InvestorBankDetails> findByInvestorUniqueIdAndIsPrimary(String investorUniqueId, Boolean isPrimary);

    Optional<InvestorBankDetails> findFirstByInvestorUniqueId(String investorUniqueId);
}
