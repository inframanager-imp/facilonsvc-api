package com.facilon.app.integration.dynamics;

import lombok.Builder;
import lombok.Data;

/**
 * Holds product-assignment fields returned from Dataverse {@code ss_investors}
 * entity, filtered by {@code ss_emailintroduceind eq '{email}'}.
 *
 * <p>Laravel equivalent: {@code DataverseController::insert_introduce_investor_details_temp}
 * which calls:
 * <pre>
 *   GET ss_investors?$filter=ss_name eq '{introduce_id}'
 * </pre>
 * and maps the response fields to {@code intro_investor_temp}.
 *
 * <p>Spring Boot uses email directly:
 * <pre>
 *   GET ss_investors?$filter=ss_emailintroduceind eq '{email}'
 * </pre>
 */
@Data
@Builder
public class DataverseInvestorAssignmentDto {

    /** ss_investorid — Dataverse primary key of the ss_investors record */
    private String ssInvestorId;

    /** ss_emailintroduceind — the introduced investor email used as the query key */
    private String introEmail;

    /** _ss_broker_value — service provider (broker or PMS) GUID → resolves to name via ss_brokers / ss_portfoliomanagers */
    private String ssBrokerValue;

    /** ss_serviceprovidertype — e.g. "100000000" = Broker, other = Portfolio Manager */
    private String serviceProviderType;

    /** _ss_product_value — product GUID → resolves to name via master_products / ss_products */
    private String ssProductValue;

    /** _ss_brokerageplan_value — plan GUID → resolves to name via master_plans / ss_plans */
    private String ssBrokeragePlanValue;

    /** _ss_investorroute_value — investment route GUID */
    private String investRouteValue;

    /** _ss_brokerpreferredbank_value — preferred bank GUID */
    private String brokerPreferredBank;

    /** _ss_investortype_value — investor type GUID */
    private String ssInvestorTypeValue;

    /** ss_name — Dataverse investor record name (e.g. "INV-2024-001") */
    private String introDvInvestorSsId;

    /** _ss_nationality_value — nationality GUID */
    private String introDvNationality;
}
