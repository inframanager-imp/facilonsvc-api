package com.facilon.app.module.client.repository;

import com.facilon.app.module.client.model.KycDocuments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KycDocumentsRepository extends JpaRepository<KycDocuments, Long> {

    List<KycDocuments> findByInvestorUniqueIdAndDeletedAtIsNull(String investorUniqueId);

    Optional<KycDocuments> findByIdAndDeletedAtIsNull(Long id);

    @Query("SELECT k FROM KycDocuments k WHERE k.tenant.tenantId = :tenantId AND k.investorUniqueId = :investorUniqueId AND k.deletedAt IS NULL")
    List<KycDocuments> findByTenantIdAndInvestorUniqueId(@Param("tenantId") Long tenantId, @Param("investorUniqueId") String investorUniqueId);

    @Query("SELECT k FROM KycDocuments k WHERE k.tenant.tenantId = :tenantId AND k.deletedAt IS NULL")
    List<KycDocuments> findByTenantId(@Param("tenantId") Long tenantId);

    @Query("SELECT COUNT(k) FROM KycDocuments k WHERE k.investorUniqueId = :investorUniqueId AND k.uploadType = :uploadType AND k.deletedAt IS NULL")
    Integer countByInvestorUniqueIdAndUploadType(@Param("investorUniqueId") String investorUniqueId, @Param("uploadType") Integer uploadType);

    /**
     * Find the KYC row used for the final-submission "Investor Information" document,
     * matching either by document type or by the Dataverse document-master id.
     * Mirrors Laravel's upsert logic (InnerPageController.php lines 4749-4763).
     */
    @Query("SELECT k FROM KycDocuments k WHERE k.investorUniqueId = :investorUniqueId " +
            "AND k.deletedAt IS NULL " +
            "AND (k.documentType = :documentType OR k.documentMasterId = :documentMasterId)")
    Optional<KycDocuments> findInvestorInformationRecord(@Param("investorUniqueId") String investorUniqueId,
                                                         @Param("documentType") String documentType,
                                                         @Param("documentMasterId") String documentMasterId);

    /** Distinct investor unique IDs that have non-deleted kyc_documents (for background status sync). */
    @Query("SELECT DISTINCT k.investorUniqueId FROM KycDocuments k WHERE k.deletedAt IS NULL AND k.investorUniqueId IS NOT NULL")
    List<String> findDistinctInvestorUniqueIdsWithNonDeletedDocs();

    /** Find by Dataverse document-master ID (non-deleted). Used for status sync. */
    Optional<KycDocuments> findByDocumentMasterIdAndDeletedAtIsNull(String documentMasterId);
}
