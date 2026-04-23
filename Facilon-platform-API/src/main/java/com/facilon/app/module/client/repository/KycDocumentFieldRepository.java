package com.facilon.app.module.client.repository;

import com.facilon.app.module.client.model.KycDocumentField;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KycDocumentFieldRepository extends JpaRepository<KycDocumentField, Long> {

    List<KycDocumentField> findByKycDocumentId(Long kycDocumentId);

    void deleteByKycDocumentId(Long kycDocumentId);
}
