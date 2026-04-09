package com.facilon.app.module.client.service;

import com.facilon.app.config.TenantContextHolder;
import com.facilon.app.model.AuthorizedUser;
import com.facilon.app.module.client.model.Investor;
import com.facilon.app.model.Tenant;
import com.facilon.app.module.client.dto.InvestorCreateDto;
import com.facilon.app.module.client.dto.InvestorDto;
import com.facilon.app.module.client.dto.InvestorUpdateDto;
import com.facilon.app.module.client.dto.InvestorVerificationDto;
import com.facilon.app.repository.AuthorizedUserRepository;
import com.facilon.app.module.client.repository.InvestorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for managing clients/investors.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ClientService {

    private final InvestorRepository investorRepository;
    private final AuthorizedUserRepository authorizedUserRepository;

    /**
     * Create a new client/investor for an existing authorized user.
     */
    public InvestorDto createClient(InvestorCreateDto dto) {
        log.info("Creating client/investor for user ID: {}", dto.getAuthorizedUserId());
        
        // Get authorized user
        AuthorizedUser user = authorizedUserRepository.findById(dto.getAuthorizedUserId())
                .orElseThrow(() -> new RuntimeException("Authorized user not found: " + dto.getAuthorizedUserId()));
        
        // Check if client/investor already exists for this user
        if (investorRepository.findByAuthorizedUser_Id(user.getId()).isPresent()) {
            throw new RuntimeException("Client/Investor already exists for this user");
        }
        
        // Get tenant from context
        Tenant tenant = TenantContextHolder.getContext().getTenant();
        
        // Build investor (tenant is from parent TenantEntity; set after build since Builder doesn't include inherited fields)
        Investor investor = Investor.builder()
                .authorizedUser(user)
                .uniqueCode(dto.getUniqueCode())
                .registerAs(dto.getRegisterAs())
                .market(dto.getMarket())
                .nationality(dto.getNationality())
                .residenceType(dto.getResidenceType())
                .pancardStatus(dto.getPancardStatus())
                .indianOrigin(dto.getIndianOrigin())
                .ociCardStatus(dto.getOciCardStatus())
                .entityName(dto.getEntityName())
                .incorpCountry(dto.getIncorpCountry())
                .entityNameRepresentative(dto.getEntityNameRepresentative())
                .companyCapacity(dto.getCompanyCapacity())
                .securityRegulated(dto.getSecurityRegulated())
                .registrationId(dto.getRegistrationId())
                .confirmation(dto.getConfirmation())
                .termsRead(dto.getTermsRead())
                .verifyStatus(2) // Pending verification
                .dvInvestorSsId(dto.getDvInvestorSsId())
                .build();
        investor.setTenant(tenant);
        
        investor = investorRepository.save(investor);
        log.info("Created client/investor with ID: {}", investor.getId());
        
        return convertToDto(investor);
    }

    /**
     * Get client/investor by ID (tenant-scoped).
     */
    public InvestorDto getClientById(Long investorId) {
        Tenant tenant = TenantContextHolder.getContext().getTenant();
        Investor investor = investorRepository.findById(investorId)
                .orElseThrow(() -> new RuntimeException("Client/Investor not found: " + investorId));
        
        // Verify tenant
        if (!investor.getTenant().getTenantId().equals(tenant.getTenantId())) {
            throw new RuntimeException("Client/Investor not found in current tenant");
        }
        
        return convertToDto(investor);
    }

    /**
     * Get client/investor by authorized user ID (tenant-scoped).
     */
    public InvestorDto getClientByUserId(Long userId) {
        Tenant tenant = TenantContextHolder.getContext().getTenant();
        Investor investor = investorRepository.findByAuthorizedUser_Id(userId)
                .orElseThrow(() -> new RuntimeException("Client/Investor not found for user: " + userId));
        
        // Verify tenant
        if (!investor.getTenant().getTenantId().equals(tenant.getTenantId())) {
            throw new RuntimeException("Client/Investor not found in current tenant");
        }
        
        return convertToDto(investor);
    }

    /**
     * Get client profile for current authenticated user.
     */
    public InvestorDto getMyClientProfile(Long currentUserId) {
        return getClientByUserId(currentUserId);
    }

    /**
     * Update client/investor information.
     */
    public InvestorDto updateClient(Long investorId, InvestorUpdateDto dto) {
        Investor investor = investorRepository.findById(investorId)
                .orElseThrow(() -> new RuntimeException("Client/Investor not found: " + investorId));
        
        // Update fields
        if (dto.getMarket() != null) investor.setMarket(dto.getMarket());
        if (dto.getNationality() != null) investor.setNationality(dto.getNationality());
        if (dto.getResidenceType() != null) investor.setResidenceType(dto.getResidenceType());
        if (dto.getPancardStatus() != null) investor.setPancardStatus(dto.getPancardStatus());
        if (dto.getIndianOrigin() != null) investor.setIndianOrigin(dto.getIndianOrigin());
        if (dto.getOciCardStatus() != null) investor.setOciCardStatus(dto.getOciCardStatus());
        if (dto.getEntityName() != null) investor.setEntityName(dto.getEntityName());
        if (dto.getIncorpCountry() != null) investor.setIncorpCountry(dto.getIncorpCountry());
        if (dto.getEntityNameRepresentative() != null) investor.setEntityNameRepresentative(dto.getEntityNameRepresentative());
        if (dto.getCompanyCapacity() != null) investor.setCompanyCapacity(dto.getCompanyCapacity());
        if (dto.getSecurityRegulated() != null) investor.setSecurityRegulated(dto.getSecurityRegulated());
        if (dto.getRegistrationId() != null) investor.setRegistrationId(dto.getRegistrationId());
        if (dto.getConfirmation() != null) investor.setConfirmation(dto.getConfirmation());
        if (dto.getTermsRead() != null) investor.setTermsRead(dto.getTermsRead());
        
        investor = investorRepository.save(investor);
        log.info("Updated client/investor ID: {}", investor.getId());
        
        return convertToDto(investor);
    }

    /**
     * Update verification status (admin operation).
     */
    public InvestorDto updateVerificationStatus(Long investorId, InvestorVerificationDto dto) {
        Investor investor = investorRepository.findById(investorId)
                .orElseThrow(() -> new RuntimeException("Client/Investor not found: " + investorId));
        
        investor.setVerifyStatus(dto.getVerifyStatus());
        investor = investorRepository.save(investor);
        
        log.info("Updated client/investor {} verification status to: {}", investorId, dto.getVerifyStatus());
        
        return convertToDto(investor);
    }

    /**
     * List all clients/investors in current tenant (with pagination).
     */
    public Page<InvestorDto> listClients(Pageable pageable) {
        Tenant tenant = TenantContextHolder.getContext().getTenant();
        Page<Investor> investors = investorRepository.findByTenantId(tenant.getTenantId(), pageable);
        return investors.map(this::convertToDto);
    }

    /**
     * Search clients/investors in current tenant.
     */
    public Page<InvestorDto> searchClients(String searchTerm, Pageable pageable) {
        Tenant tenant = TenantContextHolder.getContext().getTenant();
        Page<Investor> investors = investorRepository.searchInvestors(tenant.getTenantId(), searchTerm, pageable);
        return investors.map(this::convertToDto);
    }

    /**
     * Convert Investor entity to DTO.
     */
    private InvestorDto convertToDto(Investor investor) {
        AuthorizedUser user = investor.getAuthorizedUser();
        
        return InvestorDto.builder()
                .id(investor.getId())
                .authorizedUserId(user.getId())
                .tenantId(investor.getTenant().getTenantId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .emailId(user.getEmailId())
                .mobilePhone(user.getMobilePhone())
                .uniqueCode(investor.getUniqueCode())
                .registerAs(investor.getRegisterAs())
                .market(investor.getMarket())
                .nationality(investor.getNationality())
                .residenceType(investor.getResidenceType())
                .pancardStatus(investor.getPancardStatus())
                .indianOrigin(investor.getIndianOrigin())
                .ociCardStatus(investor.getOciCardStatus())
                .entityName(investor.getEntityName())
                .incorpCountry(investor.getIncorpCountry())
                .entityNameRepresentative(investor.getEntityNameRepresentative())
                .companyCapacity(investor.getCompanyCapacity())
                .securityRegulated(investor.getSecurityRegulated())
                .registrationId(investor.getRegistrationId())
                .confirmation(investor.getConfirmation())
                .termsRead(investor.getTermsRead())
                .verifyStatus(investor.getVerifyStatus())
                .verifyStatusLabel(getVerifyStatusLabel(investor.getVerifyStatus()))
                .dvInvestorSsId(investor.getDvInvestorSsId())
                .createdAt(investor.getCreatedAt())
                .updatedAt(investor.getUpdatedAt())
                .build();
    }

    private String getVerifyStatusLabel(Integer status) {
        if (status == null) return "Unknown";
        return switch (status) {
            case 1 -> "Verified";
            case 2 -> "Pending";
            case 3 -> "Rejected";
            default -> "Unknown";
        };
    }
}
