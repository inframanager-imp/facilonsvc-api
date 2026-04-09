package com.facilon.app.module.client.repository;

import com.facilon.app.module.client.model.InvestorNextholder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvestorNextholderRepository extends JpaRepository<InvestorNextholder, Long> {

    List<InvestorNextholder> findByInvestorId(Long investorId);

    Optional<InvestorNextholder> findByIdAndInvestorId(Long id, Long investorId);

    long countByInvestorId(Long investorId);
}
