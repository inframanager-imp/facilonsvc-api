package com.facilon.app.module.client.repository;

import com.facilon.app.module.client.model.IntroPmsInvestorTemp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IntroPmsInvestorTempRepository extends JpaRepository<IntroPmsInvestorTemp, Long> {

    Optional<IntroPmsInvestorTemp> findByUniqueCodeDb(String uniqueCodeDb);
}
