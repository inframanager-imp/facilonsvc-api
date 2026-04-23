package com.facilon.app.module.client.repository;

import com.facilon.app.module.client.model.KycDocumentDiscrepancy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KycDocumentDiscrepancyRepository extends JpaRepository<KycDocumentDiscrepancy, Long> {

    List<KycDocumentDiscrepancy> findByKycDocumentId(Long kycDocumentId);

    void deleteByKycDocumentId(Long kycDocumentId);

    long countByKycDocumentIdAndSeverity(Long kycDocumentId, String severity);
}
