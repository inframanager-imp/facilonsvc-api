-- Add `name_on_aadhaar` to investor_residential_status so the Aadhaar Details
-- section (RI flow) can capture the name exactly as printed on the Aadhaar
-- card. Stored alongside the rest of the Aadhaar fields (user_aadhar_no,
-- aadhar_number) that already live on this table per V10's Laravel-parity
-- migration — keeps the Aadhaar Details tab's save path single-table.
ALTER TABLE `investor_residential_status`
    ADD COLUMN `name_on_aadhaar` TEXT NULL;
