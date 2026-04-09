package com.facilon.app.module.client.controller;

import com.facilon.app.module.client.dto.*;
import com.facilon.app.module.client.repository.*;
import com.facilon.app.module.client.repository.InvestorsBannersRepository;
import com.facilon.app.module.client.repository.IsdCodeValuesRepository;
import com.facilon.app.module.client.repository.InvestorsSolutionsRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/clients/content")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Client Content", description = "Master data and content (public)")
public class ClientContentController {

    private final MasterCountriesRepository countriesRepository;
    private final MasterGenderRepository genderRepository;
    private final MasterNationalityRepository nationalityRepository;
    private final MasterMaritalStatusRepository maritalStatusRepository;
    private final MasterTitleRepository titleRepository;
    private final MasterStatesRepository statesRepository;
    private final MasterCitiesRepository citiesRepository;
    private final MasterInvestorTypesRepository investorTypesRepository;
    private final MarketTypeRepository marketTypeRepository;
    private final InvestorTypeCategoryRepository investorTypeCategoryRepository;
    private final InvestorsBannersRepository bannersRepository;
    private final InvestorsSolutionsRepository solutionsRepository;
    private final IsdCodeValuesRepository isdCodeValuesRepository;
    private final MasterPortfolioManagersRepository pmsManagersRepository;
    private final MasterPmsPlansRepository pmsPlansRepository;
    private final MasterPmsBanksRepository pmsBanksRepository;

    @GetMapping("/master/countries")
    @Operation(summary = "Get countries")
    public ResponseEntity<List<MasterCountryDto>> getCountries() {
        return ResponseEntity.ok(countriesRepository.findAll().stream()
                .map(c -> MasterCountryDto.builder().myRowId(c.getMyRowId()).id(c.getId()).ssName(c.getSsName()).ssCountry(c.getSsCountry()).ssIsdCode(c.getSsIsdCode()).build())
                .collect(Collectors.toList()));
    }

    @GetMapping("/master/genders")
    @Operation(summary = "Get genders")
    public ResponseEntity<List<MasterGenderDto>> getGenders() {
        return ResponseEntity.ok(genderRepository.findAll().stream()
                .map(g -> MasterGenderDto.builder().myRowId(g.getMyRowId()).id(g.getId()).ssName(g.getSsName()).ssGenderId(g.getSsGenderId()).build())
                .collect(Collectors.toList()));
    }

    @GetMapping("/master/nationalities")
    @Operation(summary = "Get nationalities")
    public ResponseEntity<List<MasterLookupDto>> getNationalities() {
        return ResponseEntity.ok(nationalityRepository.findAll().stream()
                .map(n -> MasterLookupDto.builder().myRowId(n.getMyRowId()).id(n.getMyRowId() != null ? n.getMyRowId() : 0L).name(n.getSsName()).build())
                .collect(Collectors.toList()));
    }

    @GetMapping("/master/marital-status")
    @Operation(summary = "Get marital status")
    public ResponseEntity<List<MasterLookupDto>> getMaritalStatus() {
        return ResponseEntity.ok(maritalStatusRepository.findAll().stream()
                .map(m -> MasterLookupDto.builder().myRowId(m.getMyRowId()).id(m.getMyRowId()).name(m.getSsName()).build())
                .collect(Collectors.toList()));
    }

    @GetMapping("/master/titles")
    @Operation(summary = "Get titles")
    public ResponseEntity<List<MasterTitleDto>> getTitles() {
        return ResponseEntity.ok(titleRepository.findAll().stream()
                .map(t -> MasterTitleDto.builder().myRowId(t.getMyRowId()).id(t.getId()).ssName(t.getSsName()).ssTitleId(t.getSsTitleId()).build())
                .collect(Collectors.toList()));
    }

    @GetMapping("/master/investor-types")
    @Operation(summary = "Get investor types")
    public ResponseEntity<List<MasterInvestorTypesDto>> getInvestorTypes() {
        return ResponseEntity.ok(investorTypesRepository.findAll().stream()
                .map(i -> MasterInvestorTypesDto.builder().myRowId(i.getMyRowId()).id(i.getId()).ssName(i.getSsName()).ssInvestorTypeId(i.getSsInvestorTypeId()).ssApplicableTo(i.getSsApplicableTo()).build())
                .collect(Collectors.toList()));
    }

    @GetMapping("/master/pms-managers")
    @Operation(summary = "Get PMS portfolio managers")
    public ResponseEntity<List<MasterPortfolioManagersDto>> getPmsManagers() {
        return ResponseEntity.ok(pmsManagersRepository.findAll().stream()
                .map(m -> MasterPortfolioManagersDto.builder()
                        .id(m.getId())
                        .ssName(m.getSsName())
                        .ssPortfolioManagerId(m.getSsPortfolioManagerId())
                        .ssServiceProviderType(m.getSsServiceProviderType())
                        .ssNameOfTheFirmValue(m.getSsNameOfTheFirmValue())
                        .build())
                .collect(Collectors.toList()));
    }

    @GetMapping("/master/pms-plans")
    @Operation(summary = "Get PMS plans")
    public ResponseEntity<List<MasterPmsPlansDto>> getPmsPlans() {
        return ResponseEntity.ok(pmsPlansRepository.findAll().stream()
                .map(p -> MasterPmsPlansDto.builder()
                        .id(p.getId())
                        .ssName(p.getSsName())
                        .ssInvestmentRouteValue(p.getSsInvestmentRouteValue())
                        .ssPlanDescription(p.getSsPlanDescription())
                        .versionNumber(p.getVersionNumber())
                        .ssPlanId(p.getSsPlanId())
                        .ssProductValue(p.getSsProductValue())
                        .ssPmsValue(p.getSsPmsValue())
                        .ssPreferredBankValue(p.getSsPreferredBankValue())
                        .ssSchemeValue(p.getSsSchemeValue())
                        .build())
                .collect(Collectors.toList()));
    }

    @GetMapping("/master/pms-banks")
    @Operation(summary = "Get PMS banks")
    public ResponseEntity<List<MasterPmsBanksDto>> getPmsBanks() {
        return ResponseEntity.ok(pmsBanksRepository.findAll().stream()
                .map(b -> MasterPmsBanksDto.builder()
                        .id(b.getId())
                        .ssName(b.getSsName())
                        .ssPmsBankId(b.getSsPmsBankId())
                        .ssPortfolioManagerValue(b.getSsPortfolioManagerValue())
                        .ssBankValue(b.getSsBankValue())
                        .build())
                .collect(Collectors.toList()));
    }

    @GetMapping("/master/isd-codes")
    @Operation(summary = "Get ISD codes (country dialing)")
    public ResponseEntity<List<IsdCodeValuesDto>> getIsdCodes() {
        return ResponseEntity.ok(isdCodeValuesRepository.findByStatusOrderByCodeValue(1).stream()
                .map(i -> IsdCodeValuesDto.builder()
                        .myRowId(i.getMyRowId())
                        .id(i.getId())
                        .codeValue(i.getCodeValue())
                        .countryName(i.getCountryName())
                        .countryCode(i.getCountryCode())
                        .nationality(i.getNationality())
                        .status(i.getStatus())
                        .build())
                .collect(Collectors.toList()));
    }

    @GetMapping("/master/market-types")
    @Operation(summary = "Get market types")
    public ResponseEntity<List<MarketTypeDto>> getMarketTypes() {
        return ResponseEntity.ok(marketTypeRepository.findAll().stream()
                .map(m -> MarketTypeDto.builder().myRowId(m.getMyRowId()).id(m.getId()).marketName(m.getMarketName()).status(m.getStatus()).build())
                .collect(Collectors.toList()));
    }

    @GetMapping("/banners")
    @Operation(summary = "Get banners")
    public ResponseEntity<List<InvestorsBannersDto>> getBanners() {
        return ResponseEntity.ok(bannersRepository.findAll().stream()
                .map(b -> InvestorsBannersDto.builder().myRowId(b.getMyRowId()).id(b.getId()).title(b.getTitle()).imageUpload(b.getImageUpload()).backgroundImage(b.getBackgroundImage()).description(b.getDescription()).status(b.getStatus()).build())
                .collect(Collectors.toList()));
    }

    @GetMapping("/solutions")
    @Operation(summary = "Get solutions")
    public ResponseEntity<List<InvestorsSolutionsDto>> getSolutions() {
        return ResponseEntity.ok(solutionsRepository.findAll().stream()
                .map(s -> InvestorsSolutionsDto.builder().myRowId(s.getMyRowId()).id(s.getId()).title(s.getTitle()).shortDesc(s.getShortDesc()).longDesc(s.getLongDesc()).build())
                .collect(Collectors.toList()));
    }
}
