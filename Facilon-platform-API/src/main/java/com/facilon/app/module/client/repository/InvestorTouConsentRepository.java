package com.facilon.app.module.client.repository;

import com.facilon.app.module.client.model.InvestorTouConsent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvestorTouConsentRepository extends JpaRepository<InvestorTouConsent, Long> {
}
