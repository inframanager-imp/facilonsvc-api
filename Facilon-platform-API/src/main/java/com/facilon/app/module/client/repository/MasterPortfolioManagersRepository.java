package com.facilon.app.module.client.repository;

import com.facilon.app.module.client.model.master.MasterPortfolioManagers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MasterPortfolioManagersRepository extends JpaRepository<MasterPortfolioManagers, Long> {
}
