package com.facilon.app.module.client.repository;

import com.facilon.app.module.client.model.content.InvestorsBanners;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvestorsBannersRepository extends JpaRepository<InvestorsBanners, Long> {

    List<InvestorsBanners> findByStatusOrderById(Integer status);

    List<InvestorsBanners> findBySectionIdAndStatus(Integer sectionId, Integer status);
}
