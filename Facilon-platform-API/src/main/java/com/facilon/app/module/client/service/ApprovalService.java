package com.facilon.app.module.client.service;

import com.facilon.app.module.client.dto.ApprovalRequestDto;
import com.facilon.app.module.client.dto.ApprovalResponseDto;
import com.facilon.app.module.client.model.InvestorApprovalRequest;
import com.facilon.app.module.client.repository.InvestorApprovalRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApprovalService {

    private final InvestorApprovalRequestRepository approvalRepository;

    /**
     * Create a new approval request
     */
    @Transactional
    public ApprovalResponseDto createApprovalRequest(ApprovalRequestDto dto) {
        InvestorApprovalRequest request = InvestorApprovalRequest.builder()
                .investorId(dto.getInvestorId())
                .requestType(dto.getRequestType())
                .currentStatus(dto.getCurrentStatus())
                .requestedStatus(dto.getRequestedStatus())
                .reason(dto.getReason())
                .requestedBy(dto.getRequestedBy())
                .status("pending")
                .requestedAt(LocalDateTime.now())
                .build();

        InvestorApprovalRequest saved = approvalRepository.save(request);
        log.info("Approval request created: ID={}, Type={}, Investor={}",
                saved.getId(), saved.getRequestType(), saved.getInvestorId());

        return mapToDto(saved);
    }

    /**
     * Get all approval requests for an investor
     */
    public List<ApprovalResponseDto> getApprovalRequests(Long investorId) {
        List<InvestorApprovalRequest> requests = approvalRepository
                .findByInvestorIdOrderByRequestedAtDesc(investorId);
        return requests.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    /**
     * Get pending approval requests for an investor
     */
    public List<ApprovalResponseDto> getPendingApprovalRequests(Long investorId) {
        List<InvestorApprovalRequest> requests = approvalRepository
                .findByInvestorIdAndStatusOrderByRequestedAtDesc(investorId, "pending");
        return requests.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    /**
     * Get all pending approval requests (for admin)
     */
    public List<ApprovalResponseDto> getAllPendingRequests() {
        List<InvestorApprovalRequest> requests = approvalRepository
                .findByStatusOrderByRequestedAtDesc("pending");
        return requests.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    /**
     * Approve an approval request
     */
    @Transactional
    public ApprovalResponseDto approveRequest(Long approvalId, String approvedBy) {
        InvestorApprovalRequest request = approvalRepository.findById(approvalId)
                .orElseThrow(() -> new RuntimeException("Approval request not found: " + approvalId));

        if (!"pending".equals(request.getStatus())) {
            throw new RuntimeException("Approval request is not pending: " + approvalId);
        }

        request.setStatus("approved");
        request.setApprovedBy(approvedBy);
        request.setApprovedAt(LocalDateTime.now());

        InvestorApprovalRequest saved = approvalRepository.save(request);
        log.info("Approval request approved: ID={}, ApprovedBy={}", approvalId, approvedBy);

        // TODO: In production, trigger the actual status change or action
        // - Update investor status if it's a status change request
        // - Approve document if it's a document approval request
        // - Send notification emails

        return mapToDto(saved);
    }

    /**
     * Reject an approval request
     */
    @Transactional
    public ApprovalResponseDto rejectRequest(Long approvalId, String rejectedBy, String rejectionReason) {
        InvestorApprovalRequest request = approvalRepository.findById(approvalId)
                .orElseThrow(() -> new RuntimeException("Approval request not found: " + approvalId));

        if (!"pending".equals(request.getStatus())) {
            throw new RuntimeException("Approval request is not pending: " + approvalId);
        }

        request.setStatus("rejected");
        request.setApprovedBy(rejectedBy);
        request.setApprovedAt(LocalDateTime.now());
        request.setRejectionReason(rejectionReason);

        InvestorApprovalRequest saved = approvalRepository.save(request);
        log.info("Approval request rejected: ID={}, RejectedBy={}, Reason={}",
                approvalId, rejectedBy, rejectionReason);

        // TODO: Send rejection notification email

        return mapToDto(saved);
    }

    /**
     * Map entity to DTO
     */
    private ApprovalResponseDto mapToDto(InvestorApprovalRequest request) {
        return ApprovalResponseDto.builder()
                .id(request.getId())
                .investorId(request.getInvestorId())
                .requestType(request.getRequestType())
                .currentStatus(request.getCurrentStatus())
                .requestedStatus(request.getRequestedStatus())
                .reason(request.getReason())
                .status(request.getStatus())
                .requestedBy(request.getRequestedBy())
                .requestedAt(request.getRequestedAt())
                .approvedBy(request.getApprovedBy())
                .approvedAt(request.getApprovedAt())
                .rejectionReason(request.getRejectionReason())
                .build();
    }
}
