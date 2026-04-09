package com.facilon.app.module.client.repository;

import com.facilon.app.module.client.model.master.MasterPlans;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MasterPlansRepository extends JpaRepository<MasterPlans, Long> {

    Optional<MasterPlans> findBySsPlanId(String ssPlanId);
}
