package com.facilon.app.module.client.repository;

import com.facilon.app.module.client.model.InvestorApprovalRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvestorApprovalRequestRepository extends JpaRepository<InvestorApprovalRequest, Long> {

    List<InvestorApprovalRequest> findByInvestorIdOrderByRequestedAtDesc(Long investorId);

    List<InvestorApprovalRequest> findByStatusOrderByRequestedAtDesc(String status);

    List<InvestorApprovalRequest> findByInvestorIdAndStatusOrderByRequestedAtDesc(Long investorId, String status);
}
