package com.facilon.app.module.client.service;

import com.facilon.app.config.TenantContextHolder;
import com.facilon.app.module.client.dto.IntroInvestorTempDto;
import com.facilon.app.module.client.dto.InvestorDto;
import com.facilon.app.module.client.model.IntroInvestorTemp;
import com.facilon.app.module.client.model.Investor;
import com.facilon.app.module.client.repository.IntroInvestorTempRepository;
import com.facilon.app.module.client.repository.InvestorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service for broker/introduced investor management.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class BrokerService {

    private final IntroInvestorTempRepository introTempRepository;
    private final InvestorRepository investorRepository;
    private final ClientService clientService;

    /**
     * List all introduced investors (in-progress registrations) in current tenant.
     */
    public List<IntroInvestorTempDto> listIntroducedInvestors() {
        Long tenantId = TenantContextHolder.getContext().getTenant().getTenantId();
        return introTempRepository.findByTenantId(tenantId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Get status of investor by unique code (checks both IntroInvestorTemp and Investor).
     */
    public Map<String, Object> getIntroducedInvestorStatus(String uniqueCode) {
        Map<String, Object> result = new HashMap<>();
        result.put("uniqueCode", uniqueCode);

        Optional<IntroInvestorTemp> temp = introTempRepository.findByUniqueCodeDb(uniqueCode);
        if (temp.isPresent()) {
            IntroInvestorTemp t = temp.get();
            result.put("stage", "IN_PROGRESS");
            result.put("source", "IntroInvestorTemp");
            result.put("status", t.getStatus());
            result.put("email", t.getIntroEmail());
            result.put("name", buildName(t.getIntroFirstName(), t.getIntroMiddleName(), t.getIntroLastName()));
        } else {
            Optional<Investor> inv = investorRepository.findByUniqueCode(uniqueCode);
            if (inv.isPresent()) {
                Investor i = inv.get();
                Long tenantId = TenantContextHolder.getContext().getTenant().getTenantId();
                if (i.getTenant() != null && i.getTenant().getTenantId().equals(tenantId)) {
                    result.put("stage", "REGISTERED");
                    result.put("source", "Investor");
                    result.put("verifyStatus", i.getVerifyStatus());
                    result.put("email", i.getAuthorizedUser().getEmailId());
                    result.put("name", i.getAuthorizedUser().getFirstName() + " " + i.getAuthorizedUser().getLastName());
                    result.put("investorId", i.getId());
                } else {
                    result.put("stage", "NOT_FOUND");
                }
            } else {
                result.put("stage", "NOT_FOUND");
            }
        }
        return result;
    }

    /**
     * List registered investors in current tenant (for broker dashboard).
     */
    public Page<InvestorDto> listInvestors(Pageable pageable) {
        return clientService.listClients(pageable);
    }

    /**
     * Search investors in current tenant.
     */
    public Page<InvestorDto> searchInvestors(String searchTerm, Pageable pageable) {
        return clientService.searchClients(searchTerm, pageable);
    }

    private String buildName(String first, String middle, String last) {
        StringBuilder sb = new StringBuilder();
        if (first != null) sb.append(first);
        if (middle != null && !middle.isBlank()) sb.append(" ").append(middle);
        if (last != null && !last.isBlank()) sb.append(" ").append(last);
        return sb.toString().trim();
    }

    private IntroInvestorTempDto toDto(IntroInvestorTemp t) {
        return IntroInvestorTempDto.builder()
                .id(t.getId())
                .introFirstName(t.getIntroFirstName())
                .introMiddleName(t.getIntroMiddleName())
                .introLastName(t.getIntroLastName())
                .introGender(t.getIntroGender())
                .introEmail(t.getIntroEmail())
                .introMobile(t.getIntroMobile())
                .uniqueCodeDb(t.getUniqueCodeDb())
                .status(t.getStatus())
                .ssBrokerValue(t.getSsBrokerValue())
                .brokerPreferredBank(t.getBrokerPreferredBank())
                .investorRegisterAs(t.getInvestorRegisterAs())
                .legalEntityFullName(t.getLegalEntityFullName())
                .incorpCountry(t.getIncorpCountry())
                .build();
    }
}
