package com.facilon.app.module.client.controller;

import com.facilon.app.module.client.dto.ApprovalRequestDto;
import com.facilon.app.module.client.dto.ApprovalResponseDto;
import com.facilon.app.module.client.service.ApprovalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/investor/approvals")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Approval Management", description = "Investor approval request management endpoints")
public class ApprovalController {

    private final ApprovalService approvalService;

    @PostMapping("/request")
    @Operation(summary = "Create approval request")
    public ResponseEntity<ApprovalResponseDto> createApprovalRequest(@Valid @RequestBody ApprovalRequestDto dto) {
        log.info("Creating approval request for investor: {}, type: {}", dto.getInvestorId(), dto.getRequestType());
        ApprovalResponseDto response = approvalService.createApprovalRequest(dto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{investorId}")
    @Operation(summary = "Get all approval requests for investor")
    public ResponseEntity<List<ApprovalResponseDto>> getApprovalRequests(@PathVariable Long investorId) {
        log.info("Fetching approval requests for investor: {}", investorId);
        List<ApprovalResponseDto> requests = approvalService.getApprovalRequests(investorId);
        return ResponseEntity.ok(requests);
    }

    @GetMapping("/{investorId}/pending")
    @Operation(summary = "Get pending approval requests for investor")
    public ResponseEntity<List<ApprovalResponseDto>> getPendingApprovalRequests(@PathVariable Long investorId) {
        log.info("Fetching pending approval requests for investor: {}", investorId);
        List<ApprovalResponseDto> requests = approvalService.getPendingApprovalRequests(investorId);
        return ResponseEntity.ok(requests);
    }

    @GetMapping("/pending")
    @Operation(summary = "Get all pending approval requests (admin)")
    public ResponseEntity<List<ApprovalResponseDto>> getAllPendingRequests() {
        log.info("Fetching all pending approval requests");
        List<ApprovalResponseDto> requests = approvalService.getAllPendingRequests();
        return ResponseEntity.ok(requests);
    }

    @PostMapping("/{approvalId}/approve")
    @Operation(summary = "Approve approval request")
    public ResponseEntity<ApprovalResponseDto> approveRequest(
            @PathVariable Long approvalId,
            @RequestParam String approvedBy) {
        log.info("Approving request: {} by: {}", approvalId, approvedBy);
        ApprovalResponseDto response = approvalService.approveRequest(approvalId, approvedBy);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{approvalId}/reject")
    @Operation(summary = "Reject approval request")
    public ResponseEntity<ApprovalResponseDto> rejectRequest(
            @PathVariable Long approvalId,
            @RequestParam String rejectedBy,
            @RequestBody Map<String, String> body) {
        String rejectionReason = body.get("rejectionReason");
        log.info("Rejecting request: {} by: {}, reason: {}", approvalId, rejectedBy, rejectionReason);
        ApprovalResponseDto response = approvalService.rejectRequest(approvalId, rejectedBy, rejectionReason);
        return ResponseEntity.ok(response);
    }
}
