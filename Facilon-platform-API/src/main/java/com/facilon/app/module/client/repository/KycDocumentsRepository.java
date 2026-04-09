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
}
