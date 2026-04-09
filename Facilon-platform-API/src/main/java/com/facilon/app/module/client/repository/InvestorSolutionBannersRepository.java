package com.facilon.app.module.client.repository;

import com.facilon.app.module.client.model.content.InvestorSolutionBanners;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvestorSolutionBannersRepository extends JpaRepository<InvestorSolutionBanners, Long> {

    List<InvestorSolutionBanners> findByStatusOrderById(Integer status);

    List<InvestorSolutionBanners> findBySolutionIdAndStatus(Integer solutionId, Integer status);
}
