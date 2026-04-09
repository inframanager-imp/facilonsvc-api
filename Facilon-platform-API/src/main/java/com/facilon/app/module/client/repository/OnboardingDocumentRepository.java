package com.facilon.app.module.client.repository;

import com.facilon.app.module.client.model.OnboardingDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OnboardingDocumentRepository extends JpaRepository<OnboardingDocument, Long> {

    List<OnboardingDocument> findByInvestorUniqueId(String investorUniqueId);

    Optional<OnboardingDocument> findByInvestorUniqueIdAndDocumentType(String investorUniqueId, String documentType);

    List<OnboardingDocument> findByStatus(String status);
}
