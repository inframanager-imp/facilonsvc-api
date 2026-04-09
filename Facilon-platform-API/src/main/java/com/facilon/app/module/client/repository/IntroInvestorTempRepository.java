package com.facilon.app.module.client.repository;

import com.facilon.app.module.client.model.IntroInvestorTemp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IntroInvestorTempRepository extends JpaRepository<IntroInvestorTemp, Long> {

    Optional<IntroInvestorTemp> findByUniqueCodeDb(String uniqueCodeDb);

    Optional<IntroInvestorTemp> findByIntroEmail(String introEmail);
    
    Optional<IntroInvestorTemp> findByIntroDvInvestorSsId(String introDvInvestorSsId);

    @Query("SELECT i FROM IntroInvestorTemp i WHERE i.tenant.tenantId = :tenantId ORDER BY i.id DESC")
    List<IntroInvestorTemp> findByTenantId(@Param("tenantId") Long tenantId);

    @Query("SELECT i FROM IntroInvestorTemp i WHERE i.introInvestorId = :investorId AND i.ssBrokerValue = :brokerId AND i.ssProductValue = :productId")
    Optional<IntroInvestorTemp> findByIntroInvestorIdAndBrokerIdAndProductId(
            @Param("investorId") Long investorId,
            @Param("brokerId") Long brokerId,
            @Param("productId") Long productId);
}
