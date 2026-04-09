package com.facilon.app.module.client.service;

import com.facilon.app.module.client.dto.AbandonJourneyDto;
import com.facilon.app.module.client.dto.JourneyDiscontinueDto;
import com.facilon.app.module.client.model.IntroInvestorTemp;
import com.facilon.app.module.client.model.Investor;
import com.facilon.app.module.client.repository.IntroInvestorTempRepository;
import com.facilon.app.module.client.repository.InvestorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class JourneyManagementService {

        private final InvestorRepository investorRepository;
        private final IntroInvestorTempRepository introInvestorTempRepository;

        /**
         * Discontinue investor journey
         */
        @Transactional
        public void discontinueJourney(String uniqueCode, JourneyDiscontinueDto dto) {
                Investor investor = investorRepository.findByUniqueCode(uniqueCode)
                                .orElseThrow(() -> new RuntimeException(
                                                "Investor not found with unique code: " + uniqueCode));

                // TODO: Update investor status to discontinued
                // The Investor entity doesn't have accountStatus field
                // This should be tracked in a separate status table or added to Investor entity
                // investor.setAccountStatus("discontinued");
                investorRepository.save(investor);

                log.info("Journey discontinued for investor: {} with reason: {}", uniqueCode, dto.getReason());

                // TODO: In production, update Dataverse if needed
                // - Update investor status in Dataverse
                // - Send notification emails
        }

        /**
         * Abandon investor journey
         * Aligned with Laravel implementation: updates Dataverse and local database
         */
        @Transactional
        public void abandonJourney(AbandonJourneyDto dto) {
                // Find the intro investor temp record
                IntroInvestorTemp introInvestor = introInvestorTempRepository
                                .findByIntroInvestorIdAndBrokerIdAndProductId(
                                                dto.getInvestorId(),
                                                dto.getBrokerId(),
                                                dto.getProductId())
                                .orElseThrow(() -> new RuntimeException(
                                                String.format("Investor product not found for investor: %d, broker: %d, product: %d",
                                                                dto.getInvestorId(), dto.getBrokerId(),
                                                                dto.getProductId())));

                // Update abandon fields
                introInvestor.setAbandonStatus("True");
                introInvestor.setAbandonReason(
                                dto.getAbandonReason() != null ? dto.getAbandonReason() : "On Request Abandon");
                introInvestor.setAbandonDate(LocalDate.now());

                introInvestorTempRepository.save(introInvestor);

                log.info("Journey abandoned for investor: {}, broker: {}, product: {}",
                                dto.getInvestorId(), dto.getBrokerId(), dto.getProductId());

                // TODO: In production, integrate with Dataverse
                // - Get access token for Dataverse
                // - Find investor product in Dataverse
                // - Update ss_abandonproduct = true
                // - Update ss_abandonreason
        }

        /**
         * Get journey status for investor
         */
        public String getJourneyStatus(String uniqueCode) {
                Investor investor = investorRepository.findByUniqueCode(uniqueCode)
                                .orElseThrow(() -> new RuntimeException(
                                                "Investor not found with unique code: " + uniqueCode));

                // TODO: Return actual account status from proper field
                return "active"; // investor.getAccountStatus();
        }
}
