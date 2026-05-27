package com.facilon.app.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Seeds master data tables on application startup if they are empty.
 * This ensures dropdown data is available for frontend forms.
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
@Order(1)
public class MasterDataSeeder implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) {
        log.info("==========================================================");
        log.info("Checking and seeding master data tables...");
        log.info("==========================================================");

        // NOTE: master_gender, master_title, master_countries and master_nationality are sourced
        // live from Dataverse by DataverseMasterSyncService (syncGenders / syncTitles /
        // syncCountries / syncNationality). They are intentionally NOT seeded here — a static seed
        // would shadow the real Dataverse rows (incl. their GUID keys) until the first sync runs.
        // (master_nationality previously seeded ISO codes like 'IN'/'US', which never matched the
        // Dataverse ss_nationalities GUIDs — that was the nationality-resolution bug.)
        seedMarketTypes();
        seedIsdCodes();

        log.info("==========================================================");
        log.info("Master data seeding completed!");
        log.info("==========================================================");
    }

    private void seedMarketTypes() {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM market_type", Integer.class);
        if (count == null || count == 0) {
            log.info("Seeding market_type table...");
            jdbcTemplate.execute(
                "INSERT INTO market_type (id, market_name, status, created_at, updated_at) VALUES " +
                "(1, 'Stock Market', true, NOW(), NOW()), " +
                "(2, 'Mutual Funds', true, NOW(), NOW()), " +
                "(3, 'Commodities', true, NOW(), NOW()), " +
                "(4, 'Currency / Forex', true, NOW(), NOW()), " +
                "(5, 'Derivatives', true, NOW(), NOW())"
            );
            log.info("✓ Inserted 5 market type records");
        } else {
            log.info("market_type table already has {} records", count);
        }
    }

    private void seedIsdCodes() {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM isd_code_values", Integer.class);
        if (count == null || count == 0) {
            log.info("Seeding isd_code_values table...");
            jdbcTemplate.execute(
                "INSERT INTO isd_code_values (id, code_value, country_name, country_code, nationality, status) VALUES " +
                "(240, 91, 'India', 'IN', 'Indian', 1), " +
                "(1, 1, 'United States', 'US', 'American', 1), " +
                "(44, 44, 'United Kingdom', 'GB', 'British', 1), " +
                "(61, 61, 'Australia', 'AU', 'Australian', 1), " +
                "(86, 86, 'China', 'CN', 'Chinese', 1), " +
                "(65, 65, 'Singapore', 'SG', 'Singaporean', 1)"
            );
            log.info("✓ Inserted 6 ISD code records");
        } else {
            log.info("isd_code_values table already has {} records", count);
        }
    }
}
