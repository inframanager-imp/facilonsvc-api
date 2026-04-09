package com.facilon.app.module.client.repository;

import com.facilon.app.module.client.model.InvestorContactDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InvestorContactDetailsRepository extends JpaRepository<InvestorContactDetails, Long> {

    Optional<InvestorContactDetails> findByInvestorUniqueId(String investorUniqueId);

    Optional<InvestorContactDetails> findByEmailAddress(String emailAddress);
}
