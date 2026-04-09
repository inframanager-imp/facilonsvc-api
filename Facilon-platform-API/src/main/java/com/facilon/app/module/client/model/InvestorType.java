package com.facilon.app.module.client.model;

/**
 * Enum representing different types of investors based on registration parameters.
 * Mapping from docs/Mapping_for_typeof_investore.csv
 */
public enum InvestorType {
    
    /**
     * Resident - Individual
     * Individual, Indian nationality, India residence, has PAN, has Aadhaar
     * Document: CKYC - Individual
     * Anchor: Broker for Direct, Portfolio Manager for PMS, Bank Optional
     */
    RESIDENT_INDIVIDUAL("Resident - Individual", "CKYC - Individual", "Individual", true, true, true, false, null, false),
    
    /**
     * Resident Non-Individuals
     * Entity, incorporated in India, has PAN
     * Document: CKYC - Non-Individual
     * Anchor: Broker for Direct, Portfolio Manager for PMS, Bank Optional
     */
    RESIDENT_NON_INDIVIDUAL("Resident Non-Individuals", "CKYC - Non-Individual", "Entity", false, true, true, false, null, false),
    
    /**
     * NRI (Non-Resident Indian)
     * Individual, Indian nationality, Non-India residence, has Passport, has PAN
     * Document: CKYC - Individual
     * Anchor: Broker for Direct, Custodian Required, Portfolio Manager for PMS, Bank Required
     */
    NRI("NRI", "CKYC - Individual", "Individual", true, false, true, true, null, false),
    
    /**
     * OCI (Overseas Citizen of India)
     * Individual, Non-Indian nationality, Non-India residence, has Passport, has PAN, Person of Indian Origin, has OCI card
     * Document: CKYC - Individual
     * Anchor: Broker for Direct, Custodian Required, Portfolio Manager for PMS, Bank Required
     */
    OCI("OCI", "CKYC - Individual", "Individual", false, false, true, true, true, true),
    
    /**
     * Foreign Nationals (Non-OCI)
     * Individual, Non-Indian nationality, Non-India residence, has Passport, NOT Person of Indian Origin
     * Document: CAF
     * Anchor: Custodian for Direct, Portfolio Manager for PMS, Bank Required
     */
    FOREIGN_NATIONAL("Foreign Nationals (Non-OCI)", "CAF", "Individual", false, false, false, true, false, false),
    
    /**
     * Foreign Non-Individuals
     * Entity, incorporated Non-India
     * Document: CAF
     * Anchor: Custodian for Direct, Portfolio Manager for PMS, Bank Required
     */
    FOREIGN_NON_INDIVIDUAL("Foreign Non-Individuals", "CAF", "Entity", false, false, false, false, null, false);
    
    private final String displayName;
    private final String documentType; // CKYC - Individual, CKYC - Non-Individual, or CAF
    private final String investorCategory; // Individual or Entity
    private final boolean isIndianNationality;
    private final boolean isIndiaResident;
    private final boolean requiresPan;
    private final boolean requiresPassport;
    private final Boolean isPersonOfIndianOrigin; // null if not applicable
    private final boolean requiresOciCard;
    
    InvestorType(String displayName, String documentType, String investorCategory,
                 boolean isIndianNationality, boolean isIndiaResident, boolean requiresPan,
                 boolean requiresPassport, Boolean isPersonOfIndianOrigin, boolean requiresOciCard) {
        this.displayName = displayName;
        this.documentType = documentType;
        this.investorCategory = investorCategory;
        this.isIndianNationality = isIndianNationality;
        this.isIndiaResident = isIndiaResident;
        this.requiresPan = requiresPan;
        this.requiresPassport = requiresPassport;
        this.isPersonOfIndianOrigin = isPersonOfIndianOrigin;
        this.requiresOciCard = requiresOciCard;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getDocumentType() {
        return documentType;
    }
    
    public String getInvestorCategory() {
        return investorCategory;
    }
    
    public boolean isIndianNationality() {
        return isIndianNationality;
    }
    
    public boolean isIndiaResident() {
        return isIndiaResident;
    }
    
    public boolean requiresPan() {
        return requiresPan;
    }
    
    public boolean requiresPassport() {
        return requiresPassport;
    }
    
    public Boolean getIsPersonOfIndianOrigin() {
        return isPersonOfIndianOrigin;
    }
    
    public boolean requiresOciCard() {
        return requiresOciCard;
    }
    
    /**
     * Determine investor type based on registration parameters
     */
    public static InvestorType determineType(
            boolean isIndividual,
            boolean isIndianCitizenship,
            boolean isIndiaIncorporation,
            boolean isIndiaResidence,
            boolean hasPan,
            Boolean isPersonOfIndianOrigin,
            boolean hasOciCard) {
        
        if (isIndividual) {
            // Individual investor logic
            if (isIndianCitizenship) {
                if (isIndiaResidence) {
                    // Resident - Individual
                    return RESIDENT_INDIVIDUAL;
                } else {
                    // NRI (Indian living abroad)
                    return NRI;
                }
            } else {
                // Non-Indian individual
                if (isPersonOfIndianOrigin != null && isPersonOfIndianOrigin) {
                    // Person of Indian Origin
                    if (hasOciCard && hasPan) {
                        return OCI;
                    } else {
                        // PIO without OCI - still categorize as OCI type but may need documents
                        return OCI;
                    }
                } else {
                    // Foreign National (Non-OCI)
                    return FOREIGN_NATIONAL;
                }
            }
        } else {
            // Entity investor logic
            if (isIndiaIncorporation) {
                // Resident Non-Individuals
                return RESIDENT_NON_INDIVIDUAL;
            } else {
                // Foreign Non-Individuals
                return FOREIGN_NON_INDIVIDUAL;
            }
        }
    }
    
    /**
     * Check if this investor type requires a custodian
     */
    public boolean requiresCustodian() {
        return this == NRI || this == OCI || this == FOREIGN_NATIONAL || this == FOREIGN_NON_INDIVIDUAL;
    }
    
    /**
     * Check if this investor type requires a bank
     */
    public boolean requiresBank() {
        return this == NRI || this == OCI || this == FOREIGN_NATIONAL || this == FOREIGN_NON_INDIVIDUAL;
    }
    
    /**
     * Check if broker is anchor for direct investment
     */
    public boolean isBrokerAnchorForDirect() {
        return this == RESIDENT_INDIVIDUAL || this == RESIDENT_NON_INDIVIDUAL || this == NRI || this == OCI;
    }
    
    /**
     * Check if custodian is anchor for direct investment
     */
    public boolean isCustodianAnchorForDirect() {
        return this == FOREIGN_NATIONAL || this == FOREIGN_NON_INDIVIDUAL;
    }
    
    /**
     * Check if portfolio manager is anchor for PMS
     */
    public boolean isPortfolioManagerAnchorForPMS() {
        return true; // All types have PM as anchor for PMS
    }
}
