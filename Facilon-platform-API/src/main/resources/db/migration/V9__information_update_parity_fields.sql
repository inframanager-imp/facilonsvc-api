-- Add Laravel information-update.blade.php parity fields to user_personal_information
-- and investor_bank_details tables.

-- user_personal_information: salutation + residential status + tax block
ALTER TABLE `user_personal_information`
    ADD COLUMN `name_title` TEXT NULL,
    ADD COLUMN `residential_status` TEXT NULL,
    ADD COLUMN `person_origin` TEXT NULL,
    ADD COLUMN `aadhar_number_option` TEXT NULL,
    ADD COLUMN `aadhar_number` TEXT NULL,
    ADD COLUMN `oci_available` TEXT NULL,
    ADD COLUMN `date_of_oci` DATE NULL,
    ADD COLUMN `tax_info` TEXT NULL,
    ADD COLUMN `tax_pan_no` TEXT NULL,
    ADD COLUMN `us_person_fatca` TEXT NULL;

-- investor_bank_details: structured bank branch address + MICR
ALTER TABLE `investor_bank_details`
    ADD COLUMN `bank_details_city` TEXT NULL,
    ADD COLUMN `bank_details_state` TEXT NULL,
    ADD COLUMN `bank_details_country` TEXT NULL,
    ADD COLUMN `bank_details_zip_code` TEXT NULL,
    ADD COLUMN `bank_details_micr` TEXT NULL;
