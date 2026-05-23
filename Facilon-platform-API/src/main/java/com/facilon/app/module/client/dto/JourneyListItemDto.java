package com.facilon.app.module.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * One onboarding journey (a product the investor was assigned) for the
 * investor "My Onboarding Status" / journeys list. Sourced from Dataverse
 * ss_investorproducts with display names resolved via local master tables.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JourneyListItemDto {

    /** Dataverse ss_investorproducts GUID — used to route to the specific journey. */
    private String journeyId;

    private String serviceProviderName;
    private String product;
    /** ss_investorproduct record code (ss_name auto-number). */
    private String productCode;
    private String scheme;
    private String plan;

    /** Human-readable status: IN PROGRESS, COMPLETED, ABANDONED. */
    private String status;

    /** Real completion percentage (0-100) derived from onboarding-stage flags. */
    private Integer progress;

    /** Frontend route to continue/view this journey. */
    private String actionRoute;
}
