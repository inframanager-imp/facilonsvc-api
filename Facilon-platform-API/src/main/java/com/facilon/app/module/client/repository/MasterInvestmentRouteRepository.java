package com.facilon.app.module.client.repository;

import com.facilon.app.module.client.model.master.MasterInvestmentRoute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MasterInvestmentRouteRepository extends JpaRepository<MasterInvestmentRoute, Long> {
}
