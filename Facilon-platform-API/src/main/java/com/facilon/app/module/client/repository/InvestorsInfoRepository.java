package com.facilon.app.module.client.repository;

import com.facilon.app.module.client.model.content.InvestorsInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvestorsInfoRepository extends JpaRepository<InvestorsInfo, Long> {

    List<InvestorsInfo> findByStatusOrderById(Integer status);

    Optional<InvestorsInfo> findBySlugUrlAndStatus(String slugUrl, Integer status);
}
