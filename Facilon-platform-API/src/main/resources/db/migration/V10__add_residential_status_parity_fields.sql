-- Add missing Laravel parity fields to investor_residential_status and
-- investor_tax_information. These fields exist in the DTOs but had no
-- backing columns, so values were silently discarded on save.

ALTER TABLE `investor_residential_status`
    ADD COLUMN `person_origin` TEXT NULL,
    ADD COLUMN `proof_of_address` TEXT NULL,
    ADD COLUMN `aadhar_number_option` TEXT NULL,
    ADD COLUMN `aadhar_number` TEXT NULL,
    ADD COLUMN `user_aadhar_no` TEXT NULL,
    ADD COLUMN `oci_available` TEXT NULL,
    ADD COLUMN `date_of_oci` DATE NULL;

ALTER TABLE `investor_tax_information`
    ADD COLUMN `tax_info` TEXT NULL,
    ADD COLUMN `tax_pan_no` TEXT NULL,
    ADD COLUMN `us_person_fatca` TEXT NULL;
