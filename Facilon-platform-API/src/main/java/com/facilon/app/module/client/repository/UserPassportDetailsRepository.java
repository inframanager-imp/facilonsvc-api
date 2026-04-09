package com.facilon.app.module.client.repository;

import com.facilon.app.module.client.model.UserPassportDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserPassportDetailsRepository extends JpaRepository<UserPassportDetails, Long> {

    Optional<UserPassportDetails> findByInvestorUniqueCode(String investorUniqueCode);
}
