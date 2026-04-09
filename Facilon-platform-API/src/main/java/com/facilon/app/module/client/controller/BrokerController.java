package com.facilon.app.module.client.controller;

import com.facilon.app.annotations.CurrentTenant;
import com.facilon.app.module.client.dto.IntroInvestorTempDto;
import com.facilon.app.module.client.dto.InvestorDto;
import com.facilon.app.module.client.service.BrokerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Broker/Service Provider controller - Dashboard and introduced investor management.
 * Accessible by BROKER and ADMIN roles.
 */
@RestController
@RequestMapping("/api/broker")
@RequiredArgsConstructor
@Slf4j
@CurrentTenant
@PreAuthorize("hasAnyRole('ADMIN', 'PLATFORM_SUPER_ADMIN', 'BROKER')")
@Tag(name = "Broker", description = "Broker dashboard and introduced investor management")
public class BrokerController {

    private final BrokerService brokerService;

    @GetMapping("/introduced-investors")
    @Operation(summary = "List introduced investors", description = "List all introduced investors (in-progress registrations) in current tenant")
    public ResponseEntity<List<IntroInvestorTempDto>> listIntroducedInvestors() {
        log.info("List introduced investors");
        return ResponseEntity.ok(brokerService.listIntroducedInvestors());
    }

    @GetMapping("/introduced-investors/status/{uniqueCode}")
    @Operation(summary = "Get introduced investor status", description = "Get status of an introduced investor by unique code")
    public ResponseEntity<Map<String, Object>> getIntroducedInvestorStatus(
            @Parameter(description = "Unique code from registration") @PathVariable String uniqueCode) {
        log.info("Get introduced investor status for code: {}", uniqueCode);
        return ResponseEntity.ok(brokerService.getIntroducedInvestorStatus(uniqueCode));
    }

    @GetMapping("/investors")
    @Operation(summary = "List investors", description = "List registered investors in current tenant")
    public ResponseEntity<Page<InvestorDto>> listInvestors(Pageable pageable) {
        log.info("Broker list investors");
        return ResponseEntity.ok(brokerService.listInvestors(pageable));
    }

    @GetMapping("/investors/search")
    @Operation(summary = "Search investors", description = "Search investors by name, email, or unique code")
    public ResponseEntity<Page<InvestorDto>> searchInvestors(
            @Parameter(description = "Search term") @RequestParam String searchTerm,
            Pageable pageable) {
        log.info("Broker search investors: {}", searchTerm);
        return ResponseEntity.ok(brokerService.searchInvestors(searchTerm, pageable));
    }
}
