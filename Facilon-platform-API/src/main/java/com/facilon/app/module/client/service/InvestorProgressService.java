package com.facilon.app.module.client.service;

import com.facilon.app.module.client.dto.AccountDetailsDto;
import com.facilon.app.module.client.dto.InvestorDashboardDto;
import com.facilon.app.module.client.dto.InvestorProgressDto;
import com.facilon.app.module.client.dto.JourneyListItemDto;
import com.facilon.app.integration.dynamics.DataverseInvestorAssignmentDto;
import com.facilon.app.integration.dynamics.DynamicsCrmService;
import com.facilon.app.module.client.model.*;
import com.facilon.app.module.client.model.master.MasterAccounts;
import com.facilon.app.module.client.model.master.MasterBrokers;
import com.facilon.app.module.client.model.master.MasterCountries;
import com.facilon.app.module.client.model.master.MasterCountryOfResidence;
import com.facilon.app.module.client.model.master.MasterInvestorTypes;
import com.facilon.app.module.client.model.master.MasterNationality;
import com.facilon.app.module.client.model.master.MasterPlans;
import com.facilon.app.module.client.model.master.MasterProducts;
import com.facilon.app.module.client.model.master.MasterServiceProviderType;
import com.facilon.app.module.client.repository.*;
import com.facilon.app.module.serviceagent.model.ServiceAgent;
import com.facilon.app.module.serviceagent.model.InvestorServiceAgentDelegation;
import com.facilon.app.module.serviceagent.repository.InvestorServiceAgentDelegationRepository;
import com.facilon.app.module.serviceagent.repository.ServiceAgentRepository;
import com.facilon.app.model.AuthorizedUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class InvestorProgressService {

        private final InvestorRepository investorRepository;
        private final InvestorInformationStatusRepository informationStatusRepository;
        private final IntroInvestorTempRepository introInvestorTempRepository;
        private final InvestorBankDetailsRepository bankDetailsRepository;
        private final KycDocumentsRepository kycDocumentsRepository;
        private final InvestorPhysicalSubmissionRepository physicalSubmissionRepository;
        private final UserPersonalInformationRepository personalInformationRepository;
        private final InvestorConsentsRepository investorConsentsRepository;
        private final MasterBrokersRepository masterBrokersRepository;
        private final MasterAccountsRepository masterAccountsRepository;
        private final MasterProductsRepository masterProductsRepository;
        private final MasterPlansRepository masterPlansRepository;
        private final MasterServiceProviderTypeRepository masterServiceProviderTypeRepository;
        private final MasterCountriesRepository masterCountriesRepository;
        private final MasterCountryOfResidenceRepository masterCountryOfResidenceRepository;
        private final MasterNationalityRepository masterNationalityRepository;
        private final MasterInvestorTypesRepository masterInvestorTypesRepository;
        private final DynamicsCrmService dynamicsCrmService;
        private final InvestorServiceAgentDelegationRepository delegationRepository;
        private final ServiceAgentRepository serviceAgentRepository;

        public InvestorProgressDto getInvestorProgress(String uniqueCode) {
                Investor investor = investorRepository.findByUniqueCode(uniqueCode)
                                .orElseThrow(() -> new RuntimeException("Investor not found"));

                InvestorInformationStatus infoStatus = informationStatusRepository
                                .findByInvestorEmail(investor.getAuthorizedUser() != null
                                                ? investor.getAuthorizedUser().getEmailId()
                                                : "")
                                .orElse(new InvestorInformationStatus());

                Map<String, InvestorProgressDto.SectionStatus> sections = new LinkedHashMap<>();

                // Personal Information
                sections.put("personalInfo", InvestorProgressDto.SectionStatus.builder()
                                .completed(Integer.valueOf(1).equals(infoStatus.getPersonalInfo()))
                                .required(true)
                                .lastUpdated(null)
                                .build());

                // Passport Information
                sections.put("passport", InvestorProgressDto.SectionStatus.builder()
                                .completed(Integer.valueOf(1).equals(infoStatus.getPassport()))
                                .required(true)
                                .lastUpdated(null)
                                .build());

                // Residential Status
                sections.put("residential", InvestorProgressDto.SectionStatus.builder()
                                .completed(Integer.valueOf(1).equals(infoStatus.getResidentialStatus()))
                                .required(true)
                                .lastUpdated(null)
                                .build());

                // Tax Information
                sections.put("taxInfo", InvestorProgressDto.SectionStatus.builder()
                                .completed(Integer.valueOf(1).equals(infoStatus.getTaxInformation()))
                                .required(true)
                                .lastUpdated(null)
                                .build());

                // Bank Details
                sections.put("bankDetails", InvestorProgressDto.SectionStatus.builder()
                                .completed(Integer.valueOf(1).equals(infoStatus.getBankDetails()))
                                .required(true)
                                .lastUpdated(null)
                                .build());

                // Contact Details
                sections.put("contactDetails", InvestorProgressDto.SectionStatus.builder()
                                .completed(Integer.valueOf(1).equals(infoStatus.getContactDetails()))
                                .required(true)
                                .lastUpdated(null)
                                .build());

                // Nomination (Optional)
                sections.put("nomination", InvestorProgressDto.SectionStatus.builder()
                                .completed(Integer.valueOf(1).equals(infoStatus.getNomination()))
                                .required(false)
                                .lastUpdated(null)
                                .build());

                // Risk Profile
                sections.put("riskProfile", InvestorProgressDto.SectionStatus.builder()
                                .completed(Integer.valueOf(1).equals(infoStatus.getRiskProfile()))
                                .required(true)
                                .lastUpdated(null)
                                .build());

                // Calculate progress
                long completedCount = sections.values().stream()
                                .filter(s -> s.getRequired() && s.getCompleted())
                                .count();
                long requiredCount = sections.values().stream()
                                .filter(InvestorProgressDto.SectionStatus::getRequired)
                                .count();
                int progressPercentage = requiredCount > 0 ? (int) ((completedCount * 100) / requiredCount) : 0;

                // Determine current step and pending steps
                List<String> completedSteps = new ArrayList<>();
                List<String> pendingSteps = new ArrayList<>();

                completedSteps.add("registration");

                if (completedCount == requiredCount) {
                        completedSteps.add("information");
                        pendingSteps.add("documents");
                        pendingSteps.add("verification");
                } else {
                        pendingSteps.add("information");
                        pendingSteps.add("documents");
                        pendingSteps.add("verification");
                }

                String currentStep = pendingSteps.isEmpty() ? "completed" : pendingSteps.get(0);

                return InvestorProgressDto.builder()
                                .currentStep(currentStep)
                                .completedSteps(completedSteps.toArray(new String[0]))
                                .pendingSteps(pendingSteps.toArray(new String[0]))
                                .progressPercentage(progressPercentage)
                                .sections(sections)
                                .build();
        }

        /**
         * @param investorEmail optional; used to load {@code intro_investor_temp} by {@code intro_email} when
         *                      {@code unique_code_db} is missing (same as Laravel matching on email).
         */
        public InvestorDashboardDto getInvestorDashboard(String uniqueCode, String investorEmail) {
                Investor investor = investorRepository.findByUniqueCode(uniqueCode)
                                .orElseThrow(() -> new RuntimeException("Investor not found"));

                InvestorProgressDto progress = getInvestorProgress(uniqueCode);

                // ── Step 1: Local lookup (fast, no HTTP) ──────────────────────────────
                IntroInvestorTemp introInvestor = introInvestorTempRepository.findByUniqueCodeDb(uniqueCode).orElse(null);
                if (introInvestor == null && investorEmail != null && !investorEmail.isBlank()) {
                        introInvestor = introInvestorTempRepository.findByIntroEmail(investorEmail.trim()).orElse(null);
                }

                // ── Step 2: Dataverse fallback — called ONCE, then persisted locally ──
                // Mirror of Laravel's insert_introduce_investor_details_temp:
                //   GET ss_investors?$filter=ss_emailintroduceind eq '{email}'
                //   → save to intro_investor_temp so subsequent loads are local-DB-only
                if (introInvestor == null && investorEmail != null && !investorEmail.isBlank()) {
                        introInvestor = fetchAndPersistIntroFromDv(investorEmail, uniqueCode);
                        if (introInvestor != null) {
                                log.info("[getInvestorDashboard] Persisted intro_investor_temp from Dataverse for email={}", investorEmail);
                        }
                }

                // ── Step 2b: Local row exists (e.g. from registration) but assignment GUIDs were never
                // filled — Dataverse was only called when intro row was missing. Refresh by email
                // and merge broker / product / plan from ss_investors (same as Laravel insert_introduce...).
                if (introInvestor != null && investorEmail != null && !investorEmail.isBlank()
                                && needsDataverseAssignmentBackfill(introInvestor)) {
                        IntroInvestorTemp merged = mergeIntroInvestorTempFromDataverse(introInvestor, investorEmail, uniqueCode);
                        if (merged != null) {
                                introInvestor = merged;
                        }
                }

                // ── Step 3: Backfill investor DV IDs from local intro record (no HTTP) ─
                investor = backfillInvestorDvIds(investor, introInvestor);

                InvestorConsents consents = investorConsentsRepository.findByInvestorUniqueId(uniqueCode).orElse(null);

                // Build basic investor info
                AuthorizedUser user = investor.getAuthorizedUser();

                // Some registration paths persist these on the personal-info row rather than the
                // investor row, so fall back to it when the investor columns are null.
                UserPersonalInformation personalInfo = personalInformationRepository
                                .findByInvestorUniqueId(uniqueCode).orElse(null);
                Integer nationalityId = investor.getNationality() != null
                                ? investor.getNationality()
                                : parseCountryId(personalInfo != null ? personalInfo.getCitizenship() : null);
                Integer residenceId = investor.getCountryOfResidence() != null
                                ? investor.getCountryOfResidence()
                                : parseCountryId(personalInfo != null ? personalInfo.getCountryOfResidence() : null);
                String middleName = personalInfo != null ? personalInfo.getInvestorMiddleName() : null;
                // Middle name: personal-info row first, then the Dataverse mirror (broker-entered).
                if ((middleName == null || middleName.isBlank()) && introInvestor != null) {
                        middleName = trimToNull(introInvestor.getIntroMiddleName());
                }

                // Country of residence id lives in master_country_of_residence (introduced flow);
                // resolve there first, then the Dataverse GUID, then the legacy master_countries
                // table (older/self-register rows), and finally "NA".
                String residenceName = resolveCountryOfResidenceNameById(investor.getCountryOfResidence());
                if (isUnresolvedName(residenceName) && introInvestor != null
                                && trimToNull(introInvestor.getIntroCountryOfResidence()) != null) {
                        residenceName = resolveCountryOfResidenceNameByGuid(
                                        introInvestor.getIntroCountryOfResidence().trim());
                }
                if (isUnresolvedName(residenceName)) {
                        String legacy = resolveCountryName(residenceId);
                        if (!isUnresolvedName(legacy)) {
                                residenceName = legacy;
                        }
                }
                if (isUnresolvedName(residenceName)) {
                        residenceName = "NA";
                }

                // Nationality id lives in master_nationality (introduced flow); same resolution
                // chain as above.
                String nationalityName = resolveNationalityNameById(investor.getNationality());
                if (isUnresolvedName(nationalityName) && introInvestor != null
                                && trimToNull(introInvestor.getIntroDvNationality()) != null) {
                        nationalityName = resolveNationalityNameByGuid(introInvestor.getIntroDvNationality().trim());
                }
                if (isUnresolvedName(nationalityName)) {
                        String legacy = resolveCountryName(nationalityId);
                        if (!isUnresolvedName(legacy)) {
                                nationalityName = legacy;
                        }
                }
                if (isUnresolvedName(nationalityName)) {
                        nationalityName = "NA";
                }

                // Investor type: the RAW enum constant (e.g. RESIDENT_INDIVIDUAL, NRI) — the
                // frontend's per-type tab/field visibility keys off this exact value, so it must
                // NOT be a display name. Fall back to the Dataverse mirror's investor-type GUID
                // (master_investor_types) only when the investor row's column is empty.
                String investorTypeName = trimToNull(investor.getInvestorType());
                if (investorTypeName == null && introInvestor != null
                                && trimToNull(introInvestor.getSsInvestorTypeValue()) != null) {
                        String byGuid = resolveInvestorTypeNameByGuid(introInvestor.getSsInvestorTypeValue().trim());
                        if (byGuid != null) {
                                investorTypeName = byGuid;
                        }
                }
                // Normalise to the canonical enum constant (e.g. "Resident - Individual" →
                // RESIDENT_INDIVIDUAL) so the frontend's per-type tab/field visibility always
                // matches, regardless of whether the value came from the investor row or
                // master_investor_types.ss_name.
                investorTypeName = canonicalInvestorType(investorTypeName);

                log.info("[getInvestorDashboard] uniqueCode={} resolved → nationality='{}', countryOfResidence='{}', "
                                + "investorType='{}', middleName='{}' (investor.nationalityId={}, investor.countryOfResidenceId={}, "
                                + "intro.dvNationality={}, intro.countryOfResidence={}, intro.investorTypeGuid={})",
                                uniqueCode, nationalityName, residenceName, investorTypeName, middleName,
                                investor.getNationality(), investor.getCountryOfResidence(),
                                introInvestor != null ? introInvestor.getIntroDvNationality() : null,
                                introInvestor != null ? introInvestor.getIntroCountryOfResidence() : null,
                                introInvestor != null ? introInvestor.getSsInvestorTypeValue() : null);

                InvestorDashboardDto.InvestorBasicInfo basicInfo = InvestorDashboardDto.InvestorBasicInfo.builder()
                                .name(user != null ? user.getFirstName() + " " +
                                                (user.getLastName() != null ? user.getLastName() : "") : "")
                                .firstName(user != null ? user.getFirstName() : "")
                                .middleName(middleName)
                                .lastName(user != null ? user.getLastName() : "")
                                .email(user != null ? user.getEmailId() : "")
                                .mobileNumber(user != null ? user.getMobilePhone() : "")
                                .nationality(nationalityName)
                                .countryOfResidence(residenceName)
                                .investorType(investorTypeName)
                                .uniqueCode(investor.getUniqueCode())
                                .status(investor.getVerifyStatus())
                                .registerAs(investor.getRegisterAs() != null ? investor.getRegisterAs().toString()
                                                : "Individual")
                                .build();

                // Build next steps based on incomplete sections
                List<String> nextSteps = new ArrayList<>();
                progress.getSections().forEach((key, section) -> {
                        if (section.getRequired() && !section.getCompleted()) {
                                String stepName = getSectionDisplayName(key);
                                nextSteps.add("Complete " + stepName);
                        }
                });

                if (nextSteps.isEmpty()) {
                        nextSteps.add("Upload KYC Documents");
                        nextSteps.add("Complete Verification");
                }

                InvestorDashboardDto.ProductAssignment productAssignment = buildProductAssignment(introInvestor);
                boolean onboardingEnabled = Boolean.TRUE.equals(productAssignment.getAssigned());

                // Recent activity (placeholder - can be enhanced with actual activity tracking)
                List<InvestorDashboardDto.ActivityItem> recentActivity = new ArrayList<>();
                recentActivity.add(InvestorDashboardDto.ActivityItem.builder()
                                .action("Registration Completed")
                                .timestamp(formatDateTime(investor.getCreatedAt()))
                                .description("Successfully registered as an investor")
                                .build());

                // Option 2: Build account summary
                InvestorDashboardDto.AccountSummary accountSummary = buildAccountSummary(uniqueCode);
                InvestorDashboardDto.AccountSnapshot snapshot = InvestorDashboardDto.AccountSnapshot.builder()
                                .investorId(investor.getUniqueCode())
                                .primaryJurisdiction(residenceName)
                                .eligibility(onboardingEnabled ? "Eligible" : "Partially Eligible")
                                .lastActivity(formatDateTime(investor.getUpdatedAt() != null ? investor.getUpdatedAt() : investor.getCreatedAt()))
                                .build();
                InvestorDashboardDto.ActionsAlerts actionsAlerts = InvestorDashboardDto.ActionsAlerts.builder()
                                .actionRequired(onboardingEnabled ? "None" : "Product assignment pending from Service Provider")
                                .onboardingStatus(onboardingEnabled ? "Ready" : "Blocked")
                                .restrictions(onboardingEnabled
                                                ? "You can proceed with KYC and document flow."
                                                : "KYC and onboarding submission are blocked until assignment is available.")
                                .build();
                List<InvestorDashboardDto.ApplicationItem> applications = buildApplications(onboardingEnabled);
                List<InvestorDashboardDto.ConsentItem> consentCenter = buildConsentCenter(consents, introInvestor, onboardingEnabled);
                // Look up the real service agent assigned to this investor via delegation table.
                // Service Agent (person) ≠ Service Provider (broker firm).
                List<InvestorServiceAgentDelegation> activeDelegations =
                                delegationRepository.findByInvestorIdAndIsActiveTrue(investor.getId());
                String agentName = null;
                String agentScope = null;
                String agentExpiry = "-";
                String agentStatus = "Not Assigned";
                if (!activeDelegations.isEmpty()) {
                        InvestorServiceAgentDelegation activeDelegation = activeDelegations.get(0);
                        ServiceAgent sa = serviceAgentRepository.findById(activeDelegation.getServiceAgentId())
                                        .orElse(null);
                        agentName = sa != null ? sa.getFullName() : null;
                        agentScope = activeDelegation.getScope();
                        agentExpiry = activeDelegation.getValidTo() != null
                                        ? activeDelegation.getValidTo().toString() : "-";
                        agentStatus = "Active";
                }
                InvestorDashboardDto.DelegationInfo delegation = InvestorDashboardDto.DelegationInfo.builder()
                                .serviceAgent(agentName != null ? agentName : "None assigned")
                                .scope(agentScope != null ? agentScope : "-")
                                .expiry(agentExpiry)
                                .status(agentStatus)
                                .note(activeDelegations.isEmpty() ? null : "Delegation details will be enriched in next phase.")
                                .build();

                return InvestorDashboardDto.builder()
                                .investor(basicInfo)
                                .progress(progress)
                                .recentActivity(recentActivity)
                                .nextSteps(nextSteps)
                                .accountSummary(accountSummary)
                                .accountSnapshot(snapshot)
                                .actionsAlerts(actionsAlerts)
                                .productAssignment(productAssignment)
                                .applications(applications)
                                .consentCenter(consentCenter)
                                .delegation(delegation)
                                .build();
        }

        /**
         * Builds product-assignment DTO.
         *
         * <p>Priority:
         * <ol>
         *   <li>Read GUIDs from local {@code intro_investor_temp} row (fast, no HTTP).
         *   <li>If the row is absent, call Dataverse
         *       {@code ss_investors?$filter=ss_emailintroduceind eq '{email}'}
         *       (same as Laravel {@code DataverseController::insert_introduce_investor_details_temp}).
         * </ol>
         * In both cases, display names are resolved from local master tables first,
         * then from live Dataverse calls as a final fallback (same as the blade cURL calls).
         */
        private InvestorDashboardDto.ProductAssignment buildProductAssignment(
                        IntroInvestorTemp introInvestor) {

                // ── Extract GUIDs from local intro_investor_temp ──────────────────────
                // Dataverse fallback is handled upstream in getInvestorDashboard via
                // fetchAndPersistIntroFromDv(), so introInvestor is always populated
                // when a Dataverse record exists. No HTTP calls in this method.
                if (introInvestor == null) {
                        log.warn("[buildProductAssignment] introInvestor is NULL");
                        return InvestorDashboardDto.ProductAssignment.builder()
                                        .assigned(false).source("none")
                                        .message("No assignment record found in Dataverse for this investor.")
                                        .build();
                }

                String brokerValue       = trimToNull(introInvestor.getSsBrokerValue());
                String productCode       = trimToNull(introInvestor.getSsProductValue());
                String planGuid          = trimToNull(introInvestor.getSsBrokeragePlanValue());
                String providerTypeCode  = trimToNull(introInvestor.getServiceProviderType());
                String routeValue        = trimToNull(introInvestor.getInvestRouteValue());
                String schemeNameFallback = trimToNull(introInvestor.getIntroSchemeName());
                String source = "Local DB";

                log.info("[buildProductAssignment] Email: {}, BrokerValue: {}, ProductCode: {}, ProviderType: {}, PlanGuid: {}", 
                        introInvestor.getIntroEmail(), brokerValue, productCode, providerTypeCode, planGuid);

                // ── STEP 2: Resolve display names ─────────────────────────────────────
                String providerName      = resolveServiceProviderDisplayName(brokerValue, providerTypeCode);
                String providerTypeLabel = resolveServiceProviderTypeLabel(providerTypeCode);
                String productName       = resolveProductName(productCode, schemeNameFallback);
                String planName          = resolvePlanDisplayName(planGuid);

                log.info("[buildProductAssignment] Resolved - ProviderName: {}, ProviderTypeLabel: {}, ProductName: {}, PlanName: {}", 
                        providerName, providerTypeLabel, productName, planName);

                boolean assigned = brokerValue != null && productCode != null;
                String message;
                if (assigned) {
                        message = "Assignment available.";
                } else if (brokerValue != null || productCode != null) {
                        message = "Partial assignment: service provider or product details pending.";
                } else {
                        message = "Service provider product assignment is pending.";
                }

                return InvestorDashboardDto.ProductAssignment.builder()
                                .assigned(assigned)
                                .source(source)
                                .serviceProviderName(providerName != null ? providerName : "-")
                                .serviceProviderType(providerTypeLabel != null ? providerTypeLabel
                                                : (providerTypeCode != null ? providerTypeCode : "-"))
                                .productName(productName)
                                .productCode(productCode)
                                .planName(planName)
                                .routeName(routeValue)
                                .message(message)
                                .build();
        }

        /**
         * Backfills {@code dv_investor_ss_id} and {@code dv_contact_id} in the {@code investors}
         * table whenever they are null.
         *
         * <p>Laravel sets these at the moment the investor registers via the introduction link:
         * <pre>
         *   investors.dv_investor_ss_id  = intro_investor_temp.intro_dv_investor_ss_id
         *                                  (= ss_name from Dataverse ss_investors, e.g. "INV-2024-001")
         *   investors.dv_contact_id      = intro_investor_temp.ss_contactid
         *                                  (= Dataverse contacts.contactid, set after Azure AD contact sync)
         * </pre>
         *
         * <p>In Spring Boot some investors may have registered before the introduction record
         * existed, or the fields may simply not have been populated. This method fills the gap
         * without requiring a separate admin action:
         * <ol>
         *   <li>Read from local {@code intro_investor_temp} (fast, no HTTP).
         *   <li>If still null, call Dataverse {@code ss_investors?$filter=ss_emailintroduceind eq '{email}'}
         *       to get {@code ss_name} for {@code dv_investor_ss_id}.
         * </ol>
         * Changes are only persisted when at least one field actually changes.
         */
        /**
         * Backfills {@code dv_investor_ss_id} and {@code dv_contact_id} from the local
         * {@code intro_investor_temp} row only. No HTTP calls.
         * Dataverse is handled upstream by {@link #fetchAndPersistIntroFromDv}.
         */
        private Investor backfillInvestorDvIds(Investor investor, IntroInvestorTemp introInvestor) {
                if (introInvestor == null) return investor;
                boolean changed = false;

                if ((investor.getDvInvestorSsId() == null || investor.getDvInvestorSsId().isBlank())
                                && trimToNull(introInvestor.getIntroDvInvestorSsId()) != null) {
                        investor.setDvInvestorSsId(introInvestor.getIntroDvInvestorSsId().trim());
                        changed = true;
                        log.info("[backfillInvestorDvIds] Set dv_investor_ss_id={} for uniqueCode={}",
                                        investor.getDvInvestorSsId(), investor.getUniqueCode());
                }

                if ((investor.getDvContactId() == null || investor.getDvContactId().isBlank())
                                && trimToNull(introInvestor.getSsContactId()) != null) {
                        investor.setDvContactId(introInvestor.getSsContactId().trim());
                        changed = true;
                        log.info("[backfillInvestorDvIds] Set dv_contact_id={} for uniqueCode={}",
                                        investor.getDvContactId(), investor.getUniqueCode());
                }

                if (changed) investor = investorRepository.save(investor);
                return investor;
        }

        /**
         * Calls Dataverse {@code ss_investors?$filter=ss_emailintroduceind eq '{email}'}
         * exactly once, maps the response to a new {@code intro_investor_temp} row, and
         * persists it so all subsequent dashboard loads are served from local DB.
         *
         * <p>Mirrors Laravel's {@code DataverseController::insert_introduce_investor_details_temp}.
         *
         * @return the saved entity, or {@code null} if Dataverse had no record for this email
         */
        /**
         * True when we should call Dataverse by email and merge into the existing local row.
         * Primary case: row exists (e.g. from registration) but {@code ss_broker_value} was never set,
         * so the dashboard never showed the service provider name.
         */
        private boolean needsDataverseAssignmentBackfill(IntroInvestorTemp intro) {
                return trimToNull(intro.getSsBrokerValue()) == null;
        }

        /**
         * Merges non-null assignment fields from Dataverse into an existing {@code intro_investor_temp} row.
         * Used when the row was created without broker/product GUIDs (registration path) but
         * {@code ss_investors} already has them for this email.
         */
        private IntroInvestorTemp mergeIntroInvestorTempFromDataverse(IntroInvestorTemp existing, String email,
                        String uniqueCode) {
                try {
                        Optional<DataverseInvestorAssignmentDto> dvOpt =
                                        dynamicsCrmService.fetchInvestorAssignmentByEmail(email);
                        if (dvOpt.isEmpty()) {
                                log.info("[mergeIntroInvestorTempFromDataverse] No Dataverse ss_investors row for email={}",
                                                email);
                                return existing;
                        }
                        DataverseInvestorAssignmentDto dv = dvOpt.get();
                        boolean changed = false;

                        if (trimToNull(existing.getSsBrokerValue()) == null && trimToNull(dv.getSsBrokerValue()) != null) {
                                existing.setSsBrokerValue(dv.getSsBrokerValue());
                                changed = true;
                        }
                        if (trimToNull(existing.getSsProductValue()) == null && trimToNull(dv.getSsProductValue()) != null) {
                                existing.setSsProductValue(dv.getSsProductValue());
                                changed = true;
                        }
                        if (trimToNull(existing.getSsBrokeragePlanValue()) == null
                                        && trimToNull(dv.getSsBrokeragePlanValue()) != null) {
                                existing.setSsBrokeragePlanValue(dv.getSsBrokeragePlanValue());
                                changed = true;
                        }
                        if (trimToNull(existing.getServiceProviderType()) == null
                                        && trimToNull(dv.getServiceProviderType()) != null) {
                                existing.setServiceProviderType(dv.getServiceProviderType());
                                changed = true;
                        }
                        if (trimToNull(existing.getInvestRouteValue()) == null
                                        && trimToNull(dv.getInvestRouteValue()) != null) {
                                existing.setInvestRouteValue(dv.getInvestRouteValue());
                                changed = true;
                        }
                        if (trimToNull(existing.getBrokerPreferredBank()) == null
                                        && trimToNull(dv.getBrokerPreferredBank()) != null) {
                                existing.setBrokerPreferredBank(dv.getBrokerPreferredBank());
                                changed = true;
                        }
                        if (trimToNull(existing.getSsInvestorTypeValue()) == null
                                        && trimToNull(dv.getSsInvestorTypeValue()) != null) {
                                existing.setSsInvestorTypeValue(dv.getSsInvestorTypeValue());
                                changed = true;
                        }
                        if (trimToNull(existing.getIntroDvNationality()) == null
                                        && trimToNull(dv.getIntroDvNationality()) != null) {
                                existing.setIntroDvNationality(dv.getIntroDvNationality());
                                changed = true;
                        }
                        if (trimToNull(existing.getIntroInvestorId()) == null
                                        && trimToNull(dv.getSsInvestorId()) != null) {
                                existing.setIntroInvestorId(dv.getSsInvestorId());
                                changed = true;
                        }
                        if (trimToNull(existing.getIntroDvInvestorSsId()) == null
                                        && trimToNull(dv.getIntroDvInvestorSsId()) != null) {
                                existing.setIntroDvInvestorSsId(dv.getIntroDvInvestorSsId());
                                changed = true;
                        }
                        if (trimToNull(existing.getIntroEmail()) == null && trimToNull(email) != null) {
                                existing.setIntroEmail(email.trim());
                                changed = true;
                        }
                        if (trimToNull(existing.getUniqueCodeDb()) == null && trimToNull(uniqueCode) != null) {
                                existing.setUniqueCodeDb(uniqueCode);
                                changed = true;
                        }

                        if (!changed) {
                                log.info("[mergeIntroInvestorTempFromDataverse] Dataverse returned no new fields for email={} "
                                                + "(brokerInDv={})", email, dv.getSsBrokerValue());
                                return existing;
                        }

                        log.info("[mergeIntroInvestorTempFromDataverse] Merged Dataverse assignment for email={} "
                                        + "broker={} product={} plan={}",
                                        email, existing.getSsBrokerValue(), existing.getSsProductValue(),
                                        existing.getSsBrokeragePlanValue());
                        return introInvestorTempRepository.save(existing);
                } catch (Exception e) {
                        log.warn("[mergeIntroInvestorTempFromDataverse] Failed for email={}: {}", email, e.getMessage());
                        return existing;
                }
        }

        private IntroInvestorTemp fetchAndPersistIntroFromDv(String email, String uniqueCode) {
                try {
                        Optional<DataverseInvestorAssignmentDto> dvOpt =
                                        dynamicsCrmService.fetchInvestorAssignmentByEmail(email);
                        if (dvOpt.isEmpty()) {
                                log.info("[fetchAndPersistIntroFromDv] No Dataverse record for email={}", email);
                                return null;
                        }
                        DataverseInvestorAssignmentDto dv = dvOpt.get();
                        log.info("[fetchAndPersistIntroFromDv] Persisting intro_investor_temp from Dataverse: "
                                        + "email={} ssId={} broker={} product={}",
                                        email, dv.getIntroDvInvestorSsId(), dv.getSsBrokerValue(), dv.getSsProductValue());

                        IntroInvestorTemp intro = IntroInvestorTemp.builder()
                                        .introEmail(email)
                                        .uniqueCodeDb(uniqueCode)
                                        .introDvInvestorSsId(dv.getIntroDvInvestorSsId())
                                        .introInvestorId(dv.getSsInvestorId())
                                        .ssBrokerValue(dv.getSsBrokerValue())
                                        .serviceProviderType(dv.getServiceProviderType())
                                        .ssProductValue(dv.getSsProductValue())
                                        .ssBrokeragePlanValue(dv.getSsBrokeragePlanValue())
                                        .investRouteValue(dv.getInvestRouteValue())
                                        .brokerPreferredBank(dv.getBrokerPreferredBank())
                                        .ssInvestorTypeValue(dv.getSsInvestorTypeValue())
                                        .introDvNationality(dv.getIntroDvNationality())
                                        .status(0)
                                        .build();
                        return introInvestorTempRepository.save(intro);
                } catch (Exception e) {
                        log.warn("[fetchAndPersistIntroFromDv] Failed for email={}: {}", email, e.getMessage());
                        return null;
                }
        }

        /**
         * Resolve product display name: master_products first, then Dataverse ss_products live.
         */
        private String resolveProductName(String productGuid, String schemeNameFallback) {
                if (productGuid != null) {
                        Optional<MasterProducts> p = masterProductsRepository.findBySsProductId(productGuid);
                        if (p.isPresent() && p.get().getSsName() != null && !p.get().getSsName().isBlank()) {
                                return p.get().getSsName();
                        }
                        // Fallback to Dataverse live call (same as blade checking product)
                        Optional<String> dvName = dynamicsCrmService.fetchProductName(productGuid);
                        if (dvName.isPresent()) return dvName.get();
                }
                return schemeNameFallback;
        }

        /**
         * Laravel {@code investor-details.blade.php}: {@code master_brokers} / Dataverse {@code ss_brokers} |
         * {@code ss_portfoliomanagers} → {@code _ss_nameofthefirm_value} → {@code master_accounts.name}.
         */
        private String resolveServiceProviderDisplayName(String brokerValue, String serviceProviderType) {
                log.info("[resolveServiceProviderDisplayName] START - BrokerValue: {}, ServiceProviderType: {}", 
                        brokerValue, serviceProviderType);
                
                if (brokerValue == null) {
                        log.warn("[resolveServiceProviderDisplayName] brokerValue is NULL - returning null");
                        return null;
                }
                
                // Step 1: Try master_accounts by ss_broker_value
                Optional<MasterAccounts> byBrokerCol = masterAccountsRepository.findFirstBySsBrokerValue(brokerValue);
                log.info("[resolveServiceProviderDisplayName] master_accounts lookup by ss_broker_value: found={}", 
                        byBrokerCol.isPresent());
                if (byBrokerCol.isPresent() && byBrokerCol.get().getName() != null
                                && !byBrokerCol.get().getName().isBlank()) {
                        String name = byBrokerCol.get().getName();
                        log.info("[resolveServiceProviderDisplayName] SUCCESS from master_accounts: {}", name);
                        return name;
                }
                
                // Step 2: Try master_brokers by ss_broker_id
                Optional<MasterBrokers> brokerRow = masterBrokersRepository.findBySsBrokerId(brokerValue);
                log.info("[resolveServiceProviderDisplayName] master_brokers lookup by ss_broker_id: found={}", 
                        brokerRow.isPresent());
                if (brokerRow.isPresent()) {
                        MasterBrokers mb = brokerRow.get();
                        String firmLookup = trimToNull(mb.getSsNameOfTheFirmValue());
                        log.info("[resolveServiceProviderDisplayName] master_brokers firmLookup: {}", firmLookup);
                        if (firmLookup != null) {
                                Optional<MasterAccounts> acc = masterAccountsRepository.findByAccountId(firmLookup);
                                log.info("[resolveServiceProviderDisplayName] master_accounts lookup by firmLookup: found={}", 
                                        acc.isPresent());
                                if (acc.isPresent() && acc.get().getName() != null && !acc.get().getName().isBlank()) {
                                        String name = acc.get().getName();
                                        log.info("[resolveServiceProviderDisplayName] SUCCESS from master_accounts via firm: {}", name);
                                        return name;
                                }
                        }
                        if (mb.getSsName() != null && !mb.getSsName().isBlank()) {
                                String name = mb.getSsName();
                                log.info("[resolveServiceProviderDisplayName] SUCCESS from master_brokers ss_name: {}", name);
                                return name;
                        }
                }
                
                // Step 3: Dataverse fallback
                log.info("[resolveServiceProviderDisplayName] Calling Dataverse API for brokerValue: {}", brokerValue);
                Optional<String> dvResult = dynamicsCrmService.fetchServiceProviderName(
                                brokerValue, serviceProviderType);
                log.info("[resolveServiceProviderDisplayName] Dataverse result: present={}, value={}", 
                        dvResult.isPresent(), dvResult.orElse("null"));
                if (dvResult.isPresent()) {
                        String value = dvResult.get();
                        // If Dataverse returned a name directly → use it
                        // If it returned a GUID fallback → try master_accounts before giving up
                        if (value.contains("-") && value.length() == 36) {
                                // Looks like a GUID → try master_accounts lookup
                                log.info("[resolveServiceProviderDisplayName] Dataverse returned GUID, looking up in master_accounts");
                                Optional<MasterAccounts> acc = masterAccountsRepository.findByAccountId(value);
                                if (acc.isPresent() && acc.get().getName() != null
                                                && !acc.get().getName().isBlank()) {
                                        String name = acc.get().getName();
                                        log.info("[resolveServiceProviderDisplayName] SUCCESS from Dataverse+master_accounts: {}", name);
                                        return name;
                                }
                                // GUID still unresolved — don't show raw GUID to user
                                log.warn("[resolveServiceProviderDisplayName] GUID unresolved, returning null");
                                return null;
                        }
                        // Non-GUID string = already resolved name
                        log.info("[resolveServiceProviderDisplayName] SUCCESS from Dataverse (direct name): {}", value);
                        return value;
                }
                log.warn("[resolveServiceProviderDisplayName] FAILED - All lookups returned null");
                return null;
        }

        /**
         * Laravel blade: master_service_provider_type WHERE ss_provider_id = '{code}' → ss_name
         *
         * <p>Note: Laravel has NO Dataverse sync for master_service_provider_type.
         * The values are Dynamics 365 option-set constants. We seed them via
         * DataverseMasterSyncService.seedServiceProviderTypes(), but also hardcode
         * them here as a fallback so the dashboard always works.
         */
        private String resolveServiceProviderTypeLabel(String providerTypeCode) {
                if (providerTypeCode == null) return null;
                String code = providerTypeCode.trim();

                // Primary: local master table (populated by sync or manual seed)
                Optional<String> fromTable = masterServiceProviderTypeRepository
                                .findBySsProviderId(code)
                                .map(MasterServiceProviderType::getSsName)
                                .filter(n -> n != null && !n.isBlank());
                if (fromTable.isPresent()) return fromTable.get();

                // Fallback: Dynamics 365 hardcoded option-set values for ss_serviceprovidertype
                return switch (code) {
                        case "100000000" -> "Broker";
                        case "100000001" -> "Bank";
                        case "100000002" -> "Insurance";
                        case "100000003" -> "Portfolio Manager";
                        default -> null;
                };
        }

        /** @deprecated use {@link #resolveProductName(String, String)} */
        private String resolveProductDisplayName(IntroInvestorTemp intro, String productGuid) {
                return resolveProductName(productGuid,
                                intro != null ? trimToNull(intro.getIntroSchemeName()) : null);
        }

        private String resolvePlanDisplayName(String planGuid) {
                if (planGuid == null) return null;
                Optional<String> local = masterPlansRepository.findBySsPlanId(planGuid)
                                .map(MasterPlans::getSsName)
                                .filter(n -> n != null && !n.isBlank());
                if (local.isPresent()) return local.get();
                // Dataverse live fallback — ss_plans or ss_portfoliomanagerplans
                return dynamicsCrmService.fetchPlanName(planGuid).orElse(planGuid);
        }

        /**
         * Parses a numeric country id stored as a String. Some registration paths persist
         * {@code country_of_residence}/{@code citizenship} as text on the personal-info row.
         */
        private Integer parseCountryId(String value) {
                if (value == null || value.isBlank()) {
                        return null;
                }
                try {
                        return Integer.valueOf(value.trim());
                } catch (NumberFormatException ignored) {
                        return null;
                }
        }

        /**
         * Normalises any investor-type label — the enum constant, its display name, or the
         * master_investor_types.ss_name — to the canonical {@link InvestorType} enum constant the
         * frontend keys its visibility rules off (e.g. "Resident - Individual" →
         * RESIDENT_INDIVIDUAL). Returns the raw value unchanged when nothing matches, so the
         * frontend falls back to "show all tabs" rather than hiding the wrong ones.
         */
        private String canonicalInvestorType(String raw) {
                if (raw == null || raw.isBlank()) {
                        return null;
                }
                String value = raw.trim();
                for (InvestorType t : InvestorType.values()) {
                        if (t.name().equalsIgnoreCase(value) || t.getDisplayName().equalsIgnoreCase(value)) {
                                return t.name();
                        }
                }
                return value;
        }

        /**
         * Resolves a Dataverse country-of-residence GUID (as stored in intro_investor_temp) to its
         * display name via master_country_of_residence. Returns {@code null} when it cannot be
         * resolved, so the caller keeps its existing value/placeholder.
         */
        private String resolveCountryOfResidenceNameByGuid(String guid) {
                if (guid == null || guid.isBlank() || masterCountryOfResidenceRepository == null) {
                        return null;
                }
                try {
                        String name = masterCountryOfResidenceRepository.findBySsCountryId(guid.trim())
                                        .map(MasterCountryOfResidence::getSsName)
                                        .filter(n -> n != null && !n.isBlank())
                                        .orElse(null);
                        if (name == null && dynamicsCrmService != null) {
                                // Not seeded locally — resolve live from Dataverse (ss_countryofresidences).
                                name = trimToNull(dynamicsCrmService.fetchCountryOfResidenceName(guid.trim()));
                        }
                        log.info("[resolve] countryOfResidence GUID={} → name={}", guid, name);
                        return name;
                } catch (Exception ignored) {
                        return null;
                }
        }

        /**
         * Resolves a Dataverse nationality GUID (intro_investor_temp.intro_dv_nationality) to its
         * display name via master_nationality. Returns {@code null} when unresolvable.
         */
        private String resolveNationalityNameByGuid(String guid) {
                if (guid == null || guid.isBlank() || masterNationalityRepository == null) {
                        return null;
                }
                try {
                        String name = masterNationalityRepository.findBySsNationalityId(guid.trim())
                                        .map(MasterNationality::getSsName)
                                        .filter(n -> n != null && !n.isBlank())
                                        .orElse(null);
                        if (name == null && dynamicsCrmService != null) {
                                // Not seeded locally — resolve live from Dataverse (ss_countries/ss_nationalities).
                                name = trimToNull(dynamicsCrmService.fetchNationalityName(guid.trim()));
                        }
                        log.info("[resolve] nationality GUID={} → name={}", guid, name);
                        return name;
                } catch (Exception ignored) {
                        return null;
                }
        }

        /**
         * Resolves a Dataverse investor-type GUID (intro_investor_temp.ss_investortype_value) to its
         * display name via master_investor_types. Returns {@code null} when unresolvable.
         */
        private String resolveInvestorTypeNameByGuid(String guid) {
                if (guid == null || guid.isBlank() || masterInvestorTypesRepository == null) {
                        return null;
                }
                try {
                        String name = masterInvestorTypesRepository.findBySsInvestorTypeId(guid.trim())
                                        .map(MasterInvestorTypes::getSsName)
                                        .filter(n -> n != null && !n.isBlank())
                                        .orElse(null);
                        log.info("[resolve] investorType GUID={} → name={}", guid, name);
                        return name;
                } catch (Exception ignored) {
                        return null;
                }
        }

        /** True when a resolved name is missing, the "NA" placeholder, or a bare numeric id. */
        private static boolean isUnresolvedName(String name) {
                return name == null || name.isBlank() || "NA".equalsIgnoreCase(name) || name.matches("\\d+");
        }

        /** Resolves a master_nationality local id to its display name. */
        private String resolveNationalityNameById(Integer id) {
                if (id == null || masterNationalityRepository == null) {
                        return null;
                }
                try {
                        return masterNationalityRepository.findById(id.longValue())
                                        .map(MasterNationality::getSsName)
                                        .filter(name -> name != null && !name.isBlank())
                                        .orElse(null);
                } catch (Exception ignored) {
                        return null;
                }
        }

        /** Resolves a master_country_of_residence local id to its display name. */
        private String resolveCountryOfResidenceNameById(Integer id) {
                if (id == null || masterCountryOfResidenceRepository == null) {
                        return null;
                }
                try {
                        return masterCountryOfResidenceRepository.findById(id.longValue())
                                        .map(MasterCountryOfResidence::getSsName)
                                        .filter(name -> name != null && !name.isBlank())
                                        .orElse(null);
                } catch (Exception ignored) {
                        return null;
                }
        }

        private String resolveCountryName(Integer countryId) {
                if (countryId == null) {
                        return "NA";
                }
                try {
                        return masterCountriesRepository.findById(countryId)
                                        .map(MasterCountries::getSsName)
                                        .orElse(String.valueOf(countryId));
                } catch (Exception ignored) {
                        return String.valueOf(countryId);
                }
        }

        private List<InvestorDashboardDto.ApplicationItem> buildApplications(boolean onboardingEnabled) {
                List<InvestorDashboardDto.ApplicationItem> apps = new ArrayList<>();
                apps.add(InvestorDashboardDto.ApplicationItem.builder()
                                .code("status")
                                .name("Facilon Status")
                                .status("Enabled")
                                .enabled(true)
                                .actionRoute("/investor/progress")
                                .build());
                apps.add(InvestorDashboardDto.ApplicationItem.builder()
                                .code("onboard")
                                .name("Facilon Onboard")
                                .status(onboardingEnabled ? "Enabled" : "Blocked")
                                .enabled(onboardingEnabled)
                                .blockReason(onboardingEnabled ? null : "Product assignment pending from Service Provider")
                                .actionRoute(onboardingEnabled ? "/investor/journey" : null)
                                .build());
                apps.add(InvestorDashboardDto.ApplicationItem.builder()
                                .code("dsr")
                                .name("Data Subject Rights")
                                .status("Enabled")
                                .enabled(true)
                                .actionRoute("/investor/dsr-center")
                                .build());
                apps.add(InvestorDashboardDto.ApplicationItem.builder()
                                .code("report")
                                .name("Facilon Report")
                                .status("Coming Soon")
                                .enabled(false)
                                .blockReason("Disabled - coming soon")
                                .build());
                return apps;
        }

        private List<InvestorDashboardDto.ConsentItem> buildConsentCenter(
                        InvestorConsents consents,
                        IntroInvestorTemp introInvestor,
                        boolean onboardingEnabled) {
                List<InvestorDashboardDto.ConsentItem> items = new ArrayList<>();
                items.add(InvestorDashboardDto.ConsentItem.builder()
                                .consent("Platform Terms")
                                .scope("Facilon")
                                .status(toStatus(consents != null && Boolean.TRUE.equals(consents.getTermsAccepted())))
                                .actionRequired(consents == null || !Boolean.TRUE.equals(consents.getTermsAccepted()))
                                .build());
                items.add(InvestorDashboardDto.ConsentItem.builder()
                                .consent("Data Sharing")
                                .scope(trimToNull(resolveServiceProviderDisplayName(
                                                introInvestor != null ? introInvestor.getSsBrokerValue() : null,
                                                introInvestor != null ? introInvestor.getServiceProviderType() : null))
                                                != null
                                                                ? resolveServiceProviderDisplayName(
                                                                                introInvestor.getSsBrokerValue(),
                                                                                introInvestor.getServiceProviderType())
                                                                : "Service Provider")
                                .status(onboardingEnabled ? "Active" : "Missing")
                                .actionRequired(!onboardingEnabled)
                                .build());
                items.add(InvestorDashboardDto.ConsentItem.builder()
                                .consent("Privacy Policy")
                                .scope("Facilon")
                                .status(toStatus(consents != null && Boolean.TRUE.equals(consents.getPrivacyPolicyAccepted())))
                                .actionRequired(consents == null || !Boolean.TRUE.equals(consents.getPrivacyPolicyAccepted()))
                                .build());
                return items;
        }

        private String toStatus(boolean value) {
                return value ? "Active" : "Missing";
        }

        /** Returns the first non-blank value, or null if all are blank. */
        private String firstNonBlank(String... values) {
                if (values == null) return null;
                for (String v : values) {
                        String t = trimToNull(v);
                        if (t != null) return t;
                }
                return null;
        }

        private String trimToNull(String value) {
                if (value == null) {
                        return null;
                }
                String trimmed = value.trim();
                return trimmed.isEmpty() ? null : trimmed;
        }

        /**
         * Build account summary for dashboard (Option 2)
         */
        private InvestorDashboardDto.AccountSummary buildAccountSummary(String uniqueCode) {
                // Get account opening status
                IntroInvestorTemp introInvestor = introInvestorTempRepository
                                .findByUniqueCodeDb(uniqueCode)
                                .orElse(null);

                // Get verification status
                UserPersonalInformation personalInfo = personalInformationRepository
                                .findByInvestorUniqueId(uniqueCode)
                                .orElse(null);

                // Get document counts
                Integer kycDocCount = kycDocumentsRepository
                                .countByInvestorUniqueIdAndUploadType(uniqueCode, 1);
                Integer onboardingDocCount = kycDocumentsRepository
                                .countByInvestorUniqueIdAndUploadType(uniqueCode, 2);

                // Get physical submission
                InvestorPhysicalSubmission physicalSubmission = physicalSubmissionRepository
                                .findByInvestorUniqueId(uniqueCode)
                                .orElse(null);

                // Get bank details
                InvestorBankDetails bankDetails = bankDetailsRepository
                                .findByInvestorUniqueIdAndIsPrimary(uniqueCode, true)
                                .or(() -> bankDetailsRepository.findFirstByInvestorUniqueId(uniqueCode))
                                .orElse(null);

                InvestorDashboardDto.BankInfo bankInfo = null;
                if (bankDetails != null) {
                        bankInfo = InvestorDashboardDto.BankInfo.builder()
                                        .bankName(bankDetails.getBankName())
                                        .accountNumber(maskAccountNumber(bankDetails.getAccountNumber()))
                                        .ifscCode(bankDetails.getIfscCode())
                                        .hasBankDetails(true)
                                        .build();
                }

                return InvestorDashboardDto.AccountSummary.builder()
                                .accountOpeningStatus(introInvestor != null ? introInvestor.getSsAccountOpening() : false)
                                .verificationDone(personalInfo != null && personalInfo.getSsVerificationDone() != null
                                                ? personalInfo.getSsVerificationDone().equals("1")
                                                : false)
                                .physicalSubmissionDone(physicalSubmission != null)
                                .kycDocumentsUploaded(kycDocCount)
                                .kycDocumentsRequired(4) // TODO: Get from configuration
                                .onboardingDocumentsUploaded(onboardingDocCount)
                                .onboardingDocumentsRequired(2) // TODO: Get from configuration
                                .bankInfo(bankInfo)
                                .build();
        }

        private String formatDateTime(LocalDateTime dateTime) {
                if (dateTime == null)
                        return null;
                return dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        }

        private String getSectionDisplayName(String key) {
                Map<String, String> displayNames = Map.of(
                                "personalInfo", "Personal Information",
                                "passport", "Passport Information",
                                "residential", "Residential Status",
                                "taxInfo", "Tax Information",
                                "bankDetails", "Bank Details",
                                "contactDetails", "Contact Details",
                                "nomination", "Nomination Details",
                                "riskProfile", "Risk Profile");
                return displayNames.getOrDefault(key, key);
        }

        /**
         * Get comprehensive account details for investor
         * Aligned with Laravel account-details.blade.php
         */
        /**
         * Lists all onboarding journeys (assigned products) for an investor.
         * Real source: Dataverse ss_investorproducts filtered by the investor GUID;
         * display names (product/scheme/plan/provider) are resolved via local master tables.
         * Returns an empty list (never null) when no Dataverse GUID or no products exist.
         */
        public List<JourneyListItemDto> getInvestorJourneys(String uniqueCode) {
                Investor investor = investorRepository.findByUniqueCode(uniqueCode)
                                .orElseThrow(() -> new RuntimeException("Investor not found with unique code: " + uniqueCode));

                IntroInvestorTemp introInvestor = introInvestorTempRepository
                                .findByUniqueCodeDb(uniqueCode)
                                .orElse(null);

                // Resolve the investor's Dataverse GUID (same fallback order as getAccountDetails)
                String investorGuid = introInvestor != null ? trimToNull(introInvestor.getIntroInvestorId()) : null;
                if (investorGuid == null) investorGuid = trimToNull(investor.getDvInvestorSsId());
                if (investorGuid == null && introInvestor != null) investorGuid = trimToNull(introInvestor.getIntroDvInvestorSsId());

                if (investorGuid == null) {
                        log.warn("[getInvestorJourneys] No Dataverse investor GUID for uniqueCode={}", uniqueCode);
                        return new ArrayList<>();
                }

                List<Map<String, String>> rows = dynamicsCrmService.fetchInvestorProductsFromDataverse(investorGuid);
                List<JourneyListItemDto> journeys = new ArrayList<>();

                String schemeFallback = introInvestor != null ? trimToNull(introInvestor.getIntroSchemeName()) : null;
                String providerTypeCode = introInvestor != null ? trimToNull(introInvestor.getServiceProviderType()) : null;

                // Laravel parity: the canonical "Completed" signal is the local
                // intro_investor_temp.ss_account_opening flag (investor-details-home.blade.php),
                // not the Dataverse ss_investorproduct flag (which may be unset).
                boolean introOpened = introInvestor != null && Boolean.TRUE.equals(introInvestor.getSsAccountOpening());
                String introProductValue = introInvestor != null ? trimToNull(introInvestor.getSsProductValue()) : null;

                for (Map<String, String> row : rows) {
                        String journeyId = trimToNull(row.get("id"));
                        String productGuid = trimToNull(row.get("ss_product_value"));
                        String brokerGuid = trimToNull(row.get("ss_broker_value"));
                        String planGuid = trimToNull(row.get("ss_plan_value"));

                        // Prefer the denormalized names on ss_investorproduct; fall back to master-table resolution.
                        String scheme = firstNonBlank(trimToNull(row.get("schemeName")), schemeFallback);
                        String product = firstNonBlank(trimToNull(row.get("productName")),
                                        resolveProductName(productGuid, scheme));
                        String provider = firstNonBlank(trimToNull(row.get("brokerName")),
                                        resolveServiceProviderDisplayName(brokerGuid, providerTypeCode));
                        String plan = firstNonBlank(trimToNull(row.get("planName")), trimToNull(row.get("planPmName")));
                        if (plan == null) plan = resolvePlanDisplayName(planGuid);

                        boolean abandoned = "true".equalsIgnoreCase(row.getOrDefault("ss_abandonproduct", "false"));
                        boolean opened = "1".equals(row.get("ss_accountopening"));
                        // Fall back to the local intro flag (Laravel's source of truth) for the matching
                        // product, or when there is only a single journey.
                        if (!opened && introOpened
                                        && ((introProductValue != null && introProductValue.equalsIgnoreCase(productGuid))
                                                        || rows.size() == 1)) {
                                opened = true;
                        }
                        String status = abandoned ? "ABANDONED" : (opened ? "COMPLETED" : "IN PROGRESS");

                        // Real progress: 6 onboarding stages, account opening = final stage.
                        int done = 0;
                        if ("1".equals(row.get("st_personal"))) done++;
                        if ("1".equals(row.get("st_kyc"))) done++;
                        if ("1".equals(row.get("st_onboarding"))) done++;
                        if ("1".equals(row.get("st_submitted"))) done++;
                        if ("1".equals(row.get("st_verification"))) done++;
                        if (opened) done++;
                        int progress = opened ? 100 : Math.round((done * 100f) / 6f);

                        journeys.add(JourneyListItemDto.builder()
                                        .journeyId(journeyId)
                                        .serviceProviderName(provider)
                                        .product(product)
                                        .productCode(trimToNull(row.get("name")))
                                        .scheme(scheme)
                                        .plan(plan)
                                        .status(status)
                                        .progress(progress)
                                        .actionRoute(journeyId != null ? "/investor/journey/" + journeyId : "/investor/journey")
                                        .build());
                }

                log.info("[getInvestorJourneys] {} journey(s) for uniqueCode={}", journeys.size(), uniqueCode);
                return journeys;
        }

        public AccountDetailsDto getAccountDetails(String uniqueCode) {
                Investor investor = investorRepository.findByUniqueCode(uniqueCode)
                                .orElseThrow(() -> new RuntimeException("Investor not found with unique code: " + uniqueCode));

                AuthorizedUser user = investor.getAuthorizedUser();
                
                // Get personal information
                UserPersonalInformation personalInfo = personalInformationRepository
                                .findByInvestorUniqueId(uniqueCode)
                                .orElse(null);
                
                // Get information status for progress tracking
                InvestorInformationStatus infoStatus = informationStatusRepository
                                .findByInvestorEmail(user != null ? user.getEmailId() : "")
                                .orElse(new InvestorInformationStatus());
                
                // Get intro investor temp for account opening status
                IntroInvestorTemp introInvestor = introInvestorTempRepository
                                .findByUniqueCodeDb(uniqueCode)
                                .orElse(null);
                
                // Get bank details from local DB
                InvestorBankDetails bankDetails = bankDetailsRepository
                                .findByInvestorUniqueIdAndIsPrimary(uniqueCode, true)
                                .or(() -> bankDetailsRepository.findFirstByInvestorUniqueId(uniqueCode))
                                .orElse(null);
                
                // Fetch specialized account details from Dataverse (matches Laravel account_details_show logic line 2041)
                Map<String, String> dataverseAccountDetails = null;
                if (introInvestor != null && introInvestor.getSsBrokerValue() != null && introInvestor.getSsProductValue() != null) {
                        // Use intro_investorid which is the actual Dataverse GUID (matches Laravel $temp->intro_investorid)
                        String investorGuid = introInvestor.getIntroInvestorId();
                        
                        // Fallback options if intro_investorid is not available
                        if (investorGuid == null || investorGuid.isBlank()) {
                                investorGuid = investor.getDvInvestorSsId();
                        }
                        if (investorGuid == null || investorGuid.isBlank()) {
                                investorGuid = introInvestor.getIntroDvInvestorSsId();
                        }
                        
                        if (investorGuid != null && !investorGuid.isBlank()) {
                                log.info("📡 Fetching account details from Dataverse for investor: {} (broker: {}, product: {})", 
                                                investorGuid, introInvestor.getSsBrokerValue(), introInvestor.getSsProductValue());
                                dataverseAccountDetails = dynamicsCrmService.fetchAccountDetailsFromDataverse(
                                                investorGuid,
                                                introInvestor.getSsBrokerValue(),
                                                introInvestor.getSsProductValue()
                                );
                        } else {
                                log.warn("⚠️ No valid Dataverse investor GUID found for uniqueCode: {}", uniqueCode);
                        }
                }
                
                // Count KYC documents (upload_type = 1)
                Integer kycDocCount = kycDocumentsRepository
                                .countByInvestorUniqueIdAndUploadType(uniqueCode, 1);
                
                // Count onboarding documents (upload_type = 2)
                Integer onboardingDocCount = kycDocumentsRepository
                                .countByInvestorUniqueIdAndUploadType(uniqueCode, 2);
                
                // Get physical submission status
                InvestorPhysicalSubmission physicalSubmission = physicalSubmissionRepository
                                .findByInvestorUniqueId(uniqueCode)
                                .orElse(null);
                
                // Build full name
                String fullName = buildFullName(personalInfo, user);
                
                // Build progress info
                AccountDetailsDto.ProgressInfo progressInfo = AccountDetailsDto.ProgressInfo.builder()
                                .personalInfoComplete(Integer.valueOf(1).equals(infoStatus.getPersonalInfo()))
                                .passportComplete(Integer.valueOf(1).equals(infoStatus.getPassport()))
                                .residentialComplete(Integer.valueOf(1).equals(infoStatus.getResidentialStatus()))
                                .taxInfoComplete(Integer.valueOf(1).equals(infoStatus.getTaxInformation()))
                                .bankDetailsComplete(Integer.valueOf(1).equals(infoStatus.getBankDetails()))
                                .contactDetailsComplete(Integer.valueOf(1).equals(infoStatus.getContactDetails()))
                                .nominationComplete(Integer.valueOf(1).equals(infoStatus.getNomination()))
                                .riskProfileComplete(Integer.valueOf(1).equals(infoStatus.getRiskProfile()))
                                .kycDocumentsUploaded(kycDocCount)
                                .kycDocumentsRequired(4) // TODO: Get from configuration or Dataverse
                                .onboardingDocumentsUploaded(onboardingDocCount)
                                .onboardingDocumentsRequired(2) // TODO: Get from configuration or Dataverse
                                .verificationDone(personalInfo != null && personalInfo.getSsVerificationDone() != null
                                                ? personalInfo.getSsVerificationDone().equals("1")
                                                : false)
                                .verificationDoneBy(personalInfo != null ? personalInfo.getSsVerificationDoneBy() : null)
                                .verificationDateTime(personalInfo != null && personalInfo.getSsVerificationDateandTime() != null
                                                ? personalInfo.getSsVerificationDateandTime().toString()
                                                : null)
                                .physicalSubmissionDone(physicalSubmission != null)
                                .physicalSubmissionMethod(physicalSubmission != null ? physicalSubmission.getPhysicalSubmission() : null)
                                .courierName(physicalSubmission != null ? physicalSubmission.getCourierName() : null)
                                .dispatchDate(physicalSubmission != null && physicalSubmission.getDispatchDate() != null
                                                ? physicalSubmission.getDispatchDate().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"))
                                                : null)
                                .awbNumber(physicalSubmission != null ? physicalSubmission.getAwbNumber() : null)
                                .completionPercentage(calculateCompletionPercentage(infoStatus))
                                .build();
                
                // Build bank account info (prioritize Dataverse data, fallback to local DB)
                AccountDetailsDto.BankAccountInfo bankAccountInfo = null;
                if (dataverseAccountDetails != null) {
                        // Use Dataverse data (matches Laravel exactly)
                        bankAccountInfo = AccountDetailsDto.BankAccountInfo.builder()
                                        .bankName(dataverseAccountDetails.get("ss_nameofbank"))
                                        .branchAddress(dataverseAccountDetails.get("ss_bankaddress"))
                                        .accountNumber(dataverseAccountDetails.get("ss_bankaccountnumber"))
                                        .swiftCode(dataverseAccountDetails.get("ss_swiftcode"))
                                        .ifscCode(dataverseAccountDetails.get("ss_ifsccode"))
                                        .safeKeepingAccountNo(dataverseAccountDetails.get("ss_safekeepingcustodyaccountno"))
                                        .tradingAccountNo(dataverseAccountDetails.get("ss_tradingaccountno"))
                                        .pmsAccountFolioNo(dataverseAccountDetails.get("ss_pmsaccountfoliono"))
                                        // NEW: Depository accounts from Dataverse
                                        .nsdlDpId(dataverseAccountDetails.get("ss_depositorynsdldpid"))
                                        .nsdlAccountNo(dataverseAccountDetails.get("ss_depositorynsdlaccountno"))
                                        .cdslAccountNo(dataverseAccountDetails.get("ss_depositorycsdlaccountno"))
                                        .accountType(null) // Not in Dataverse
                                        .isPrimary(true)
                                        .build();
                } else if (bankDetails != null) {
                        // Fallback to local DB data (limited fields)
                        bankAccountInfo = AccountDetailsDto.BankAccountInfo.builder()
                                        .bankName(bankDetails.getBankName())
                                        .branchAddress(bankDetails.getBankAddress())
                                        .accountNumber(maskAccountNumber(bankDetails.getAccountNumber()))
                                        .swiftCode(bankDetails.getSwiftCode())
                                        .ifscCode(bankDetails.getIfscCode())
                                        .accountType(bankDetails.getAccountType())
                                        .isPrimary(bankDetails.getIsPrimary())
                                        // Local DB doesn't have specialized accounts
                                        .safeKeepingAccountNo("-")
                                        .tradingAccountNo("-")
                                        .pmsAccountFolioNo("-")
                                        .nsdlDpId("-")
                                        .nsdlAccountNo("-")
                                        .cdslAccountNo("-")
                                        .build();
                }
                
                // Determine account opening status (prioritize Dataverse, fallback to local)
                Boolean accountOpeningStatus = false;
                if (dataverseAccountDetails != null) {
                        String statusStr = dataverseAccountDetails.get("ss_accountopening");
                        accountOpeningStatus = "1".equals(statusStr); // 1 = Completed, 2 = Pending, 0 = Unknown
                } else if (introInvestor != null) {
                        accountOpeningStatus = introInvestor.getSsAccountOpening();
                }
                
                return AccountDetailsDto.builder()
                                .uniqueCode(uniqueCode)
                                .fullName(fullName)
                                .email(user != null ? user.getEmailId() : null)
                                .mobile(user != null ? user.getMobilePhone() : null)
                                .registrationType(investor.getRegisterAs() != null 
                                                ? investor.getRegisterAs().toString() 
                                                : "Individual")
                                .investorCategory(null) // TODO: Map from investor type
                                .investorCategoryCode(investor.getInvestorType())
                                .registrationDate(investor.getCreatedAt() != null 
                                                ? investor.getCreatedAt().toLocalDate() 
                                                : null)
                                .lastUpdated(investor.getUpdatedAt() != null 
                                                ? investor.getUpdatedAt().toLocalDate() 
                                                : null)
                                .accountOpeningStatus(accountOpeningStatus)
                                .progress(progressInfo)
                                .bankAccount(bankAccountInfo)
                                .build();
        }
        
        private String buildFullName(UserPersonalInformation personalInfo, AuthorizedUser user) {
                if (personalInfo != null && personalInfo.getInvestorFirstName() != null) {
                        StringBuilder sb = new StringBuilder(personalInfo.getInvestorFirstName());
                        if (personalInfo.getInvestorMiddleName() != null && !personalInfo.getInvestorMiddleName().isEmpty()) {
                                sb.append(" ").append(personalInfo.getInvestorMiddleName());
                        }
                        if (personalInfo.getInvestorLastName() != null && !personalInfo.getInvestorLastName().isEmpty()) {
                                sb.append(" ").append(personalInfo.getInvestorLastName());
                        }
                        return sb.toString();
                } else if (user != null) {
                        String firstName = user.getFirstName() != null ? user.getFirstName() : "";
                        String lastName = user.getLastName() != null ? user.getLastName() : "";
                        return (firstName + " " + lastName).trim();
                }
                return "Unknown";
        }
        
        private String maskAccountNumber(String accountNumber) {
                if (accountNumber == null || accountNumber.length() <= 4) {
                        return accountNumber;
                }
                int visibleDigits = 4;
                int maskLength = accountNumber.length() - visibleDigits;
                return "*".repeat(maskLength) + accountNumber.substring(maskLength);
        }
        
        private Integer calculateCompletionPercentage(InvestorInformationStatus status) {
                int total = 8; // Total required sections
                int completed = 0;
                if (Integer.valueOf(1).equals(status.getPersonalInfo())) completed++;
                if (Integer.valueOf(1).equals(status.getPassport())) completed++;
                if (Integer.valueOf(1).equals(status.getResidentialStatus())) completed++;
                if (Integer.valueOf(1).equals(status.getTaxInformation())) completed++;
                if (Integer.valueOf(1).equals(status.getBankDetails())) completed++;
                if (Integer.valueOf(1).equals(status.getContactDetails())) completed++;
                if (Integer.valueOf(1).equals(status.getNomination())) completed++;
                if (Integer.valueOf(1).equals(status.getRiskProfile())) completed++;
                return (completed * 100) / total;
        }
}
