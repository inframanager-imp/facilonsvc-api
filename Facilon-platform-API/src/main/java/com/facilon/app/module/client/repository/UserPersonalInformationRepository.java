package com.facilon.app.module.client.repository;

import com.facilon.app.module.client.model.UserPersonalInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserPersonalInformationRepository extends JpaRepository<UserPersonalInformation, Long> {

    Optional<UserPersonalInformation> findByInvestorUniqueId(String investorUniqueId);

    @Query("SELECT u FROM UserPersonalInformation u WHERE u.tenant.id = :tenantId AND u.investorUniqueId = :investorUniqueId")
    Optional<UserPersonalInformation> findByTenantIdAndInvestorUniqueId(@Param("tenantId") Long tenantId, @Param("investorUniqueId") String investorUniqueId);
}
