-- Master Data Seed Script for Facilon Platform
-- Run this script to populate master data tables

-- ========================================
-- 1. Master Gender
-- ========================================
INSERT INTO master_gender (id, ss_name, ss_genderid) VALUES
(1, 'Male', 'M'),
(2, 'Female', 'F'),
(3, 'Other', 'O'),
(4, 'Prefer not to say', 'N')
ON DUPLICATE KEY UPDATE ss_name = VALUES(ss_name);

-- ========================================
-- 2. Master Title
-- ========================================
INSERT INTO master_title (id, ss_name, ss_titleid) VALUES
(1, 'Mr.', 'MR'),
(2, 'Ms.', 'MS'),
(3, 'Mrs.', 'MRS'),
(4, 'Dr.', 'DR'),
(5, 'Prof.', 'PROF')
ON DUPLICATE KEY UPDATE ss_name = VALUES(ss_name);

-- ========================================
-- 3. Master Countries (Sample - India and major countries)
-- ========================================
INSERT INTO master_countries (id, ss_name, ss_country, ss_isdcode) VALUES
(240, 'India', 'IN', '91'),
(1, 'United States', 'US', '1'),
(44, 'United Kingdom', 'GB', '44'),
(61, 'Australia', 'AU', '61'),
(86, 'China', 'CN', '86'),
(65, 'Singapore', 'SG', '65'),
(971, 'United Arab Emirates', 'AE', '971'),
(49, 'Germany', 'DE', '49'),
(33, 'France', 'FR', '33'),
(81, 'Japan', 'JP', '81'),
(82, 'South Korea', 'KR', '82'),
(60, 'Malaysia', 'MY', '60'),
(66, 'Thailand', 'TH', '66'),
(84, 'Vietnam', 'VN', '84'),
(62, 'Indonesia', 'ID', '62'),
(63, 'Philippines', 'PH', '63'),
(977, 'Nepal', 'NP', '977'),
(880, 'Bangladesh', 'BD', '880'),
(94, 'Sri Lanka', 'LK', '94'),
(27, 'South Africa', 'ZA', '27')
ON DUPLICATE KEY UPDATE ss_name = VALUES(ss_name), ss_isdcode = VALUES(ss_isdcode);

-- ========================================
-- 4. Master Nationality
-- ========================================
INSERT INTO master_nationality (id, ss_name, ss_nationality, ss_nationalityid) VALUES
(240, 'Indian', 'India', 'IN'),
(1, 'American', 'United States', 'US'),
(44, 'British', 'United Kingdom', 'GB'),
(61, 'Australian', 'Australia', 'AU'),
(86, 'Chinese', 'China', 'CN'),
(65, 'Singaporean', 'Singapore', 'SG'),
(971, 'Emirati', 'United Arab Emirates', 'AE'),
(49, 'German', 'Germany', 'DE'),
(33, 'French', 'France', 'FR'),
(81, 'Japanese', 'Japan', 'JP')
ON DUPLICATE KEY UPDATE ss_name = VALUES(ss_name);

-- ========================================
-- 5. Market Type
-- ========================================
INSERT INTO market_type (id, market_name, status) VALUES
(1, 'Stock Market', true),
(2, 'Mutual Funds', true),
(3, 'Commodities', true),
(4, 'Currency / Forex', true),
(5, 'Derivatives', true)
ON DUPLICATE KEY UPDATE market_name = VALUES(market_name);

-- ========================================
-- 6. ISD Code Values
-- ========================================
INSERT INTO isd_code_values (id, code_value, country_name, country_code, nationality, status) VALUES
(240, 91, 'India', 'IN', 'Indian', 1),
(1, 1, 'United States', 'US', 'American', 1),
(44, 44, 'United Kingdom', 'GB', 'British', 1),
(61, 61, 'Australia', 'AU', 'Australian', 1),
(86, 86, 'China', 'CN', 'Chinese', 1),
(65, 65, 'Singapore', 'SG', 'Singaporean', 1)
ON DUPLICATE KEY UPDATE country_name = VALUES(country_name);

-- ========================================
-- Verification Queries
-- ========================================
-- Run these to verify data was inserted:
SELECT 'master_gender' as table_name, COUNT(*) as record_count FROM master_gender
UNION ALL
SELECT 'master_title', COUNT(*) FROM master_title
UNION ALL
SELECT 'master_countries', COUNT(*) FROM master_countries
UNION ALL
SELECT 'master_nationality', COUNT(*) FROM master_nationality
UNION ALL
SELECT 'market_type', COUNT(*) FROM market_type
UNION ALL
SELECT 'isd_code_values', COUNT(*) FROM isd_code_values;
