package com.facilon.app.module.client.repository;

import com.facilon.app.module.client.model.KycProfileImportAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KycProfileImportAuditRepository extends JpaRepository<KycProfileImportAudit, Long> {

    List<KycProfileImportAudit> findByInvestorUniqueIdOrderByImportedAtDesc(String investorUniqueId);

    List<KycProfileImportAudit> findBySourceDocumentId(Long sourceDocumentId);
}
