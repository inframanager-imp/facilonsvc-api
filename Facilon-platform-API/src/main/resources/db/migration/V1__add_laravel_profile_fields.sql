-- Migration Script: Add Laravel Profile Fields
-- Date: 2026-02-14
-- Description: Adds 23 new fields to support Laravel-aligned investor profile structure

-- Add 20 family-related fields to user_personal_information table
ALTER TABLE user_personal_information
    ADD COLUMN gender VARCHAR(20),
    ADD COLUMN marital_status VARCHAR(20),
    ADD COLUMN maiden_title VARCHAR(10),
    ADD COLUMN maiden_name VARCHAR(100),
    ADD COLUMN maiden_middle_name VARCHAR(100),
    ADD COLUMN maiden_last_name VARCHAR(100),
    ADD COLUMN city_of_dob VARCHAR(100),
    ADD COLUMN country_dob VARCHAR(100),
    ADD COLUMN father_name_title VARCHAR(10),
    ADD COLUMN fathers_first_name VARCHAR(100),
    ADD COLUMN fathers_middle_name VARCHAR(100),
    ADD COLUMN fathers_last_name VARCHAR(100),
    ADD COLUMN mother_name_title VARCHAR(10),
    ADD COLUMN mother_first_name VARCHAR(100),
    ADD COLUMN mother_middle_name VARCHAR(100),
    ADD COLUMN mother_last_name VARCHAR(100),
    ADD COLUMN spouse_name_title VARCHAR(10),
    ADD COLUMN spouse_name VARCHAR(100),
    ADD COLUMN spouse_middle_name VARCHAR(100),
    ADD COLUMN spouse_last_name VARCHAR(100),
    ADD COLUMN spouse_maiden_name VARCHAR(100);

-- Add 3 Laravel-specific fields to user_passport_details table
ALTER TABLE user_passport_details
    ADD COLUMN passport_nationality VARCHAR(100),
    ADD COLUMN passport_date_non_resident DATE,
    ADD COLUMN passport_no_years_abroad INTEGER;

-- Add indexes for commonly queried fields
CREATE INDEX idx_user_personal_gender ON user_personal_information(gender);
CREATE INDEX idx_user_personal_marital_status ON user_personal_information(marital_status);
CREATE INDEX idx_passport_nationality ON user_passport_details(passport_nationality);

-- Add comments for documentation
COMMENT ON COLUMN user_personal_information.gender IS 'Gender of investor (male, female, other)';
COMMENT ON COLUMN user_personal_information.marital_status IS 'Marital status (single, married, divorced, widowed)';
COMMENT ON COLUMN user_personal_information.maiden_title IS 'Title for maiden name (Mr, Mrs, Ms, Dr)';
COMMENT ON COLUMN user_passport_details.passport_nationality IS 'Nationality as stated on passport';
COMMENT ON COLUMN user_passport_details.passport_date_non_resident IS 'Date individual became non-resident';
COMMENT ON COLUMN user_passport_details.passport_no_years_abroad IS 'Number of years lived abroad';
