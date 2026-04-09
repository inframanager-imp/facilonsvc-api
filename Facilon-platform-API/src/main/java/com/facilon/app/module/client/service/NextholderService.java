package com.facilon.app.module.client.service;

import com.facilon.app.module.client.dto.NextholderDto;
import com.facilon.app.module.client.model.Investor;
import com.facilon.app.module.client.model.InvestorNextholder;
import com.facilon.app.module.client.repository.InvestorNextholderRepository;
import com.facilon.app.module.client.repository.InvestorRepository;
import com.facilon.app.exception.ResourceNotFoundException;
import com.facilon.app.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NextholderService {

    private final InvestorRepository investorRepository;
    private final InvestorNextholderRepository nextholderRepository;

    private static final int MAX_NEXTHOLDERS = 5;
    private static final int MINOR_AGE_THRESHOLD = 18;

    @Transactional
    public NextholderDto createNextholder(String uniqueCode, NextholderDto dto) {
        Investor investor = getInvestorByUniqueCode(uniqueCode);

        log.info("Creating nextholder for investor: {}", uniqueCode);

        // Validate maximum nextholders
        long currentCount = nextholderRepository.countByInvestorId(investor.getId());
        if (currentCount >= MAX_NEXTHOLDERS) {
            throw new BusinessException("Maximum " + MAX_NEXTHOLDERS + " nextholders allowed");
        }

        // Check if minor and validate guardian info
        boolean isMinor = isMinor(dto.getDateOfBirth());
        dto.setIsMinor(isMinor);

        if (isMinor && (dto.getGuardianName() == null || dto.getGuardianName().isEmpty())) {
            throw new BusinessException("Guardian information is required for minors");
        }

        InvestorNextholder nextholder = mapToEntity(dto);
        nextholder.setInvestorId(investor.getId());
        nextholder.setStatus("pending");

        InvestorNextholder saved = nextholderRepository.save(nextholder);
        log.info("Nextholder created successfully with id: {}", saved.getId());

        return mapToDto(saved);
    }

    @Transactional(readOnly = true)
    public List<NextholderDto> getNextholders(String uniqueCode) {
        Investor investor = getInvestorByUniqueCode(uniqueCode);
        log.info("Fetching nextholders for investor: {}", uniqueCode);

        List<InvestorNextholder> nextholders = nextholderRepository.findByInvestorId(investor.getId());
        return nextholders.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public NextholderDto getNextholder(String uniqueCode, Long nextholderId) {
        Investor investor = getInvestorByUniqueCode(uniqueCode);
        log.info("Fetching nextholder {} for investor: {}", nextholderId, uniqueCode);

        InvestorNextholder nextholder = nextholderRepository
                .findByIdAndInvestorId(nextholderId, investor.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Nextholder not found"));
        return mapToDto(nextholder);
    }

    @Transactional
    public NextholderDto updateNextholder(String uniqueCode, Long nextholderId, NextholderDto dto) {
        Investor investor = getInvestorByUniqueCode(uniqueCode);
        log.info("Updating nextholder {} for investor: {}", nextholderId, uniqueCode);

        InvestorNextholder nextholder = nextholderRepository
                .findByIdAndInvestorId(nextholderId, investor.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Nextholder not found"));

        boolean isMinor = isMinor(dto.getDateOfBirth());

        nextholder.setFullName(dto.getFullName());
        nextholder.setRelationship(dto.getRelationship());
        nextholder.setDateOfBirth(dto.getDateOfBirth());
        nextholder.setEmail(dto.getEmail());
        nextholder.setMobileNumber(dto.getPhoneNumber()); // Mapped to phoneNumber in DTO
        nextholder.setIdentificationType(dto.getIdentificationType());
        nextholder.setIdentificationNumber(dto.getIdentificationNumber());
        nextholder.setAddress(dto.getAddress());
        nextholder.setCity(dto.getCity());
        nextholder.setCountry(dto.getCountry());
        nextholder.setPostalCode(dto.getPostalCode());
        nextholder.setIsMinor(isMinor);
        nextholder.setGuardianName(dto.getGuardianName());
        nextholder.setGuardianRelationship(dto.getGuardianRelationship());

        InvestorNextholder saved = nextholderRepository.save(nextholder);
        log.info("Nextholder updated successfully");
        return mapToDto(saved);
    }

    @Transactional
    public void deleteNextholder(String uniqueCode, Long nextholderId) {
        Investor investor = getInvestorByUniqueCode(uniqueCode);
        log.info("Deleting nextholder {} for investor: {}", nextholderId, uniqueCode);

        InvestorNextholder nextholder = nextholderRepository
                .findByIdAndInvestorId(nextholderId, investor.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Nextholder not found"));

        nextholderRepository.delete(nextholder);
        log.info("Nextholder deleted successfully");
    }

    private boolean isMinor(LocalDate dateOfBirth) {
        if (dateOfBirth == null) {
            return false;
        }
        int age = Period.between(dateOfBirth, LocalDate.now()).getYears();
        return age < MINOR_AGE_THRESHOLD;
    }

    private Investor getInvestorByUniqueCode(String uniqueCode) {
        return investorRepository.findByUniqueCode(uniqueCode)
                .orElseThrow(() -> new ResourceNotFoundException("Investor not found with unique code: " + uniqueCode));
    }

    private NextholderDto mapToDto(InvestorNextholder entity) {
        return NextholderDto.builder()
                .id(entity.getId())
                .fullName(entity.getFullName())
                .relationship(entity.getRelationship())
                .dateOfBirth(entity.getDateOfBirth())
                .email(entity.getEmail())
                .phoneNumber(entity.getMobileNumber())
                .identificationType(entity.getIdentificationType())
                .identificationNumber(entity.getIdentificationNumber())
                .address(entity.getAddress())
                .city(entity.getCity())
                .country(entity.getCountry())
                .postalCode(entity.getPostalCode())
                .isMinor(entity.getIsMinor())
                .guardianName(entity.getGuardianName())
                .guardianRelationship(entity.getGuardianRelationship())
                .consentGiven(entity.getConsentGiven())
                .status(entity.getStatus())
                .build();
    }

    private InvestorNextholder mapToEntity(NextholderDto dto) {
        return InvestorNextholder.builder()
                .fullName(dto.getFullName())
                .relationship(dto.getRelationship())
                .dateOfBirth(dto.getDateOfBirth())
                .email(dto.getEmail())
                .mobileNumber(dto.getPhoneNumber())
                .identificationType(dto.getIdentificationType())
                .identificationNumber(dto.getIdentificationNumber())
                .address(dto.getAddress())
                .city(dto.getCity())
                .country(dto.getCountry())
                .postalCode(dto.getPostalCode())
                .isMinor(dto.getIsMinor())
                .guardianName(dto.getGuardianName())
                .guardianRelationship(dto.getGuardianRelationship())
                .build();
    }
}
