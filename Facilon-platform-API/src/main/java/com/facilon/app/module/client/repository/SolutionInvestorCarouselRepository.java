package com.facilon.app.module.client.repository;

import com.facilon.app.module.client.model.content.SolutionInvestorCarousel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SolutionInvestorCarouselRepository extends JpaRepository<SolutionInvestorCarousel, Long> {

    List<SolutionInvestorCarousel> findByStatusOrderById(Integer status);

    List<SolutionInvestorCarousel> findBySolutionIdAndStatus(Integer solutionId, Integer status);
}
