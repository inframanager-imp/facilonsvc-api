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

        seedGenders();
        seedTitles();
        seedCountries();
        seedNationalities();
        seedMarketTypes();
        seedIsdCodes();

        log.info("==========================================================");
        log.info("Master data seeding completed!");
        log.info("==========================================================");
    }

    private void seedGenders() {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM master_gender", Integer.class);
        if (count == null || count == 0) {
            log.info("Seeding master_gender table...");
            jdbcTemplate.execute(
                "INSERT INTO master_gender (id, ss_name, ss_genderid) VALUES " +
                "(1, 'Male', 'M'), " +
                "(2, 'Female', 'F'), " +
                "(3, 'Other', 'O'), " +
                "(4, 'Prefer not to say', 'N')"
            );
            log.info("✓ Inserted 4 gender records");
        } else {
            log.info("master_gender table already has {} records", count);
        }
    }

    private void seedTitles() {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM master_title", Integer.class);
        if (count == null || count == 0) {
            log.info("Seeding master_title table...");
            jdbcTemplate.execute(
                "INSERT INTO master_title (id, ss_name, ss_titleid) VALUES " +
                "(1, 'Mr.', 'MR'), " +
                "(2, 'Ms.', 'MS'), " +
                "(3, 'Mrs.', 'MRS'), " +
                "(4, 'Dr.', 'DR'), " +
                "(5, 'Prof.', 'PROF')"
            );
            log.info("✓ Inserted 5 title records");
        } else {
            log.info("master_title table already has {} records", count);
        }
    }

    private void seedCountries() {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM master_countries", Integer.class);
        if (count == null || count == 0) {
            log.info("Seeding master_countries table...");
            jdbcTemplate.execute(
                "INSERT INTO master_countries (id, ss_name, ss_country, ss_isdcode) VALUES " +
                "(240, 'India', 'IN', '91'), " +
                "(1, 'United States', 'US', '1'), " +
                "(44, 'United Kingdom', 'GB', '44'), " +
                "(61, 'Australia', 'AU', '61'), " +
                "(86, 'China', 'CN', '86'), " +
                "(65, 'Singapore', 'SG', '65'), " +
                "(971, 'United Arab Emirates', 'AE', '971'), " +
                "(49, 'Germany', 'DE', '49'), " +
                "(33, 'France', 'FR', '33'), " +
                "(81, 'Japan', 'JP', '81'), " +
                "(82, 'South Korea', 'KR', '82'), " +
                "(60, 'Malaysia', 'MY', '60'), " +
                "(66, 'Thailand', 'TH', '66'), " +
                "(84, 'Vietnam', 'VN', '84'), " +
                "(62, 'Indonesia', 'ID', '62'), " +
                "(63, 'Philippines', 'PH', '63'), " +
                "(977, 'Nepal', 'NP', '977'), " +
                "(880, 'Bangladesh', 'BD', '880'), " +
                "(94, 'Sri Lanka', 'LK', '94'), " +
                "(27, 'South Africa', 'ZA', '27')"
            );
            log.info("✓ Inserted 20 country records");
        } else {
            log.info("master_countries table already has {} records", count);
        }
    }

    private void seedNationalities() {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM master_nationality", Integer.class);
        if (count == null || count == 0) {
            log.info("Seeding master_nationality table...");
            jdbcTemplate.execute(
                "INSERT INTO master_nationality (id, ss_name, ss_nationality, ss_nationalityid) VALUES " +
                "(240, 'Indian', 'India', 'IN'), " +
                "(1, 'American', 'United States', 'US'), " +
                "(44, 'British', 'United Kingdom', 'GB'), " +
                "(61, 'Australian', 'Australia', 'AU'), " +
                "(86, 'Chinese', 'China', 'CN'), " +
                "(65, 'Singaporean', 'Singapore', 'SG'), " +
                "(971, 'Emirati', 'United Arab Emirates', 'AE'), " +
                "(49, 'German', 'Germany', 'DE'), " +
                "(33, 'French', 'France', 'FR'), " +
                "(81, 'Japanese', 'Japan', 'JP')"
            );
            log.info("✓ Inserted 10 nationality records");
        } else {
            log.info("master_nationality table already has {} records", count);
        }
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
