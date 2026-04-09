package com.facilon.app.module.client.validator;

import com.facilon.app.module.client.dto.*;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Objects;

@Component
public class InvestorValidator {

    public void validatePersonalInformation(PersonalInformationDto dto) {
        // Validation mostly handled by annotations
    }

    public void validatePassportInformation(PassportInformationDto dto) {
        // Validation mostly handled by annotations
    }

    public void validateResidentialStatus(ResidentialStatusDto dto) {
        // If Visa is selected (based on type or internal logic), validate visa details
        // Assuming there's a way to distinguish. For now, check if fields are populated
        // if others are empty?
        // Or if 'residentialStatus' implies it.
        // The Laravel code checked user type value. Here we might need to rely on
        // what's present.

        if (Boolean.TRUE.equals(hasText(dto.getUserVisaNumber()))) {
            require(dto.getUserVisaIssuerDate(), "Visa issuer date");
            require(dto.getUserVisaExpiryDate(), "Visa expiry date");
        }

        if (Boolean.TRUE.equals(hasText(dto.getUserOciCardNo()))) {
            require(dto.getUserOciIssueDate(), "OCI issue date");
        }
    }

    public void validateTaxInformation(TaxInformationDto dto) {
        // Validation mostly handled by annotations
    }

    public void validateBankDetails(BankDetailsDto dto) {
        if ("yes".equalsIgnoreCase(dto.getSettlementAccountType())) {
            require(dto.getBeneficiaryName(), "Beneficiary name");
            require(dto.getBankAccountNumber(), "Account number");
            require(dto.getBankIfscCode(), "IFSC code");
            // Bank name, branch name, etc are @NotBlank
        }

        if ("yes".equalsIgnoreCase(dto.getRbiApproval())) {
            require(dto.getRbiApprovalOrderNumber(), "RBI approval order number");
            require(dto.getRbiApprovalDate(), "RBI approval date");
        }
    }

    public void validateContactDetails(ContactDetailsDto dto) {
        // Correspondence Address logic
        if (!"true".equalsIgnoreCase(dto.getCorrAddressSameAsPerm()) && !"1".equals(dto.getCorrAddressSameAsPerm())) {
            require(dto.getCorrAddressLine1(), "Correspondence address line 1");
            require(dto.getCorrUserCity(), "Correspondence city");
            require(dto.getCorrUserState(), "Correspondence state");
            require(dto.getCorrUserZipCode(), "Correspondence zip code");
        }
    }

    public void validateNominationDetails(NominationDetailsDto dto) {
        // Ensure at least one nominee if appointment is true
        if (Boolean.TRUE.equals(dto.getAppointNominee())) {
            if (!hasText(dto.getNomineeName1())) {
                throw new IllegalArgumentException("At least one nominee is required");
            }
        }

        // Validate Nominee 1
        if (hasText(dto.getNomineeName1())) {
            validateNominee(
                    dto.getNomineeName1(), dto.getNomineeRelation1(), dto.getNomineeDob1(), dto.getNomineeShare1(),
                    dto.getGuardianName1(), dto.getGuardianPanNo1(), 1);
        }
        // Validate Nominee 2
        if (hasText(dto.getNomineeName2())) {
            validateNominee(
                    dto.getNomineeName2(), dto.getNomineeRelation2(), dto.getNomineeDob2(), dto.getNomineeShare2(),
                    dto.getGuardianName2(), dto.getGuardianPanNo2(), 2);
        }
        // Validate Nominee 3
        if (hasText(dto.getNomineeName3())) {
            validateNominee(
                    dto.getNomineeName3(), dto.getNomineeRelation3(), dto.getNomineeDob3(), dto.getNomineeShare3(),
                    dto.getGuardianName3(), dto.getGuardianPanNo3(), 3);
        }
    }

    private void validateNominee(String name, String relation, Object dob, Double share,
            String guardianName, String guardianPan, int index) {
        require(relation, "Relationship for nominee " + index);
        require(dob, "Date of birth for nominee " + index);
        require(share, "Share percentage for nominee " + index);

        // If minor logic needed, would check DOB.
        // For now, if guardian name is provided, validate guardian PAN?
        if (hasText(guardianName)) {
            require(guardianPan, "Guardian PAN for nominee " + index);
        }
    }

    public void validateRiskProfile(RiskProfileDto dto) {
        if (dto.getInvestmentExperienceIn() == null || dto.getInvestmentExperienceIn().length == 0) {
            throw new IllegalArgumentException("Investment experience selection is required");
        }
    }

    // Helper methods
    private boolean hasText(String str) {
        return StringUtils.hasText(str);
    }

    private void require(Object val, String fieldName) {
        if (val == null || (val instanceof String && !StringUtils.hasText((String) val))) {
            throw new IllegalArgumentException(fieldName + " is required");
        }
    }
}
