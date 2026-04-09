package com.facilon.app.module.client.repository;

import com.facilon.app.module.client.model.content.InvestorsSolutions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvestorsSolutionsRepository extends JpaRepository<InvestorsSolutions, Long> {

    List<InvestorsSolutions> findByStatusOrderById(Integer status);

    List<InvestorsSolutions> findByTypeIdAndStatus(Integer typeId, Integer status);

    Optional<InvestorsSolutions> findBySlugUrlAndStatus(String slugUrl, Integer status);
}
