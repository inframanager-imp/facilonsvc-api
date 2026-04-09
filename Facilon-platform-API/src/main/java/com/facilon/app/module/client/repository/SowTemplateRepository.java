package com.facilon.app.module.client.repository;

import com.facilon.app.module.client.model.SowTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SowTemplateRepository extends JpaRepository<SowTemplate, Long> {

    List<SowTemplate> findByIsActiveTrue();

    Optional<SowTemplate> findByApplicableForAndIsActiveTrue(String applicableFor);

    List<SowTemplate> findByApplicableForInAndIsActiveTrue(List<String> applicableFor);

    Optional<SowTemplate> findTopByApplicableForAndIsActiveTrueOrderByCreatedDateDesc(String applicableFor);
}
