package com.facilon.app.module.client.controller;

import com.facilon.app.module.client.dto.AbandonJourneyDto;
import com.facilon.app.module.client.dto.JourneyDiscontinueDto;
import com.facilon.app.module.client.service.JourneyManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/investor/journey")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Journey Management", description = "Investor journey management endpoints")
public class JourneyManagementController {

    private final JourneyManagementService journeyManagementService;

    @PostMapping("/discontinue/{uniqueCode}")
    @Operation(summary = "Discontinue investor journey")
    public ResponseEntity<Map<String, String>> discontinueJourney(
            @PathVariable String uniqueCode,
            @Valid @RequestBody JourneyDiscontinueDto dto) {
        log.info("Discontinue journey request for investor: {}", uniqueCode);
        journeyManagementService.discontinueJourney(uniqueCode, dto);
        return ResponseEntity.ok(Map.of(
                "message", "Journey discontinued successfully",
                "status", "success"));
    }

    @PostMapping("/abandon")
    @Operation(summary = "Abandon investor journey")
    public ResponseEntity<Map<String, String>> abandonJourney(@Valid @RequestBody AbandonJourneyDto dto) {
        log.info("Abandon journey request for investor: {}, broker: {}, product: {}",
                dto.getInvestorId(), dto.getBrokerId(), dto.getProductId());
        journeyManagementService.abandonJourney(dto);
        return ResponseEntity.ok(Map.of(
                "message", "Journey abandoned successfully",
                "status", "success"));
    }

    @GetMapping("/status/{uniqueCode}")
    @Operation(summary = "Get journey status")
    public ResponseEntity<Map<String, String>> getJourneyStatus(@PathVariable String uniqueCode) {
        String status = journeyManagementService.getJourneyStatus(uniqueCode);
        return ResponseEntity.ok(Map.of(
                "uniqueCode", uniqueCode,
                "status", status));
    }
}
