package com.facilon.app.module.client.repository;

import com.facilon.app.module.client.model.content.InvestorTypeCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvestorTypeCategoryRepository extends JpaRepository<InvestorTypeCategory, Long> {

    List<InvestorTypeCategory> findByStatusOrderById(Integer status);
}
