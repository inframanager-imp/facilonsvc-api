-- Uppercase every dropdown-backed column so existing rows match the new
-- "dropdown values are UPPERCASE" UI convention. After this migration the
-- front-end option `value` strings and the DB values are identical, so the
-- <select> element re-renders its selection correctly on reload.
--
-- IMPORTANT notes
--  * Free-text columns (names, addresses, city, state, country free-text,
--    citizenship, occupation descriptions, nominee names, etc.) are NOT
--    touched here — they keep whatever case the user typed. CSS
--    `text-transform: uppercase` handles the visual concern.
--  * Numeric-id columns (marital_status='1'/'2', gross_income='100000', etc.)
--    are NOT touched; those were never letter-cased to begin with.
--  * Backend comparisons use equalsIgnoreCase throughout, so even if a row
--    were missed by this migration the application would still function;
--    this is purely a one-time normalisation so dropdowns render their
--    selected option correctly.

-- ============================================================
-- user_personal_information — titles + gender
-- ============================================================
UPDATE `user_personal_information`
   SET `name_title` = UPPER(`name_title`)
 WHERE `name_title` IS NOT NULL AND `name_title` <> '';

UPDATE `user_personal_information`
   SET `maiden_title` = UPPER(`maiden_title`)
 WHERE `maiden_title` IS NOT NULL AND `maiden_title` <> '';

UPDATE `user_personal_information`
   SET `father_name_title` = UPPER(`father_name_title`)
 WHERE `father_name_title` IS NOT NULL AND `father_name_title` <> '';

UPDATE `user_personal_information`
   SET `mother_name_title` = UPPER(`mother_name_title`)
 WHERE `mother_name_title` IS NOT NULL AND `mother_name_title` <> '';

UPDATE `user_personal_information`
   SET `spouse_name_title` = UPPER(`spouse_name_title`)
 WHERE `spouse_name_title` IS NOT NULL AND `spouse_name_title` <> '';

UPDATE `user_personal_information`
   SET `investor_gender` = UPPER(`investor_gender`)
 WHERE `investor_gender` IS NOT NULL AND `investor_gender` <> '';

UPDATE `user_personal_information`
   SET `gender` = UPPER(`gender`)
 WHERE `gender` IS NOT NULL AND `gender` <> '';

-- country_of_residence / citizenship are free-text so default India values
-- set by the server stamp would be "India"; normalise to UPPER too since the
-- UI defaults to "INDIA" on RI.
UPDATE `user_personal_information`
   SET `country_of_residence` = UPPER(`country_of_residence`)
 WHERE `country_of_residence` IS NOT NULL
   AND `country_of_residence` <> ''
   AND `country_of_residence` = 'India';

UPDATE `user_personal_information`
   SET `citizenship` = UPPER(`citizenship`)
 WHERE `citizenship` IS NOT NULL
   AND `citizenship` <> ''
   AND `citizenship` = 'India';

-- ============================================================
-- investor_bank_details — account type, country, settlement flag
-- ============================================================
UPDATE `investor_bank_details`
   SET `account_type` = UPPER(`account_type`)
 WHERE `account_type` IS NOT NULL AND `account_type` <> '';

UPDATE `investor_bank_details`
   SET `bank_details_country` = UPPER(`bank_details_country`)
 WHERE `bank_details_country` IS NOT NULL AND `bank_details_country` <> '';

UPDATE `investor_bank_details`
   SET `settlement_account_type` = UPPER(`settlement_account_type`)
 WHERE `settlement_account_type` IS NOT NULL AND `settlement_account_type` <> '';

UPDATE `investor_bank_details`
   SET `rbi_approval` = UPPER(`rbi_approval`)
 WHERE `rbi_approval` IS NOT NULL AND `rbi_approval` <> '';

-- ============================================================
-- investor_contact_details — proof of address, address type, preferred contact
-- ============================================================
UPDATE `investor_contact_details`
   SET `proof_of_address` = UPPER(`proof_of_address`)
 WHERE `proof_of_address` IS NOT NULL AND `proof_of_address` <> '';

UPDATE `investor_contact_details`
   SET `address_type` = UPPER(`address_type`)
 WHERE `address_type` IS NOT NULL AND `address_type` <> '';

UPDATE `investor_contact_details`
   SET `preferred_contact_method` = UPPER(`preferred_contact_method`)
 WHERE `preferred_contact_method` IS NOT NULL AND `preferred_contact_method` <> '';

-- ============================================================
-- investor_residential_status — status, yes/no flags, proof options
-- ============================================================
UPDATE `investor_residential_status`
   SET `residential_status` = UPPER(`residential_status`)
 WHERE `residential_status` IS NOT NULL AND `residential_status` <> '';

UPDATE `investor_residential_status`
   SET `aadhar_number_option` = UPPER(`aadhar_number_option`)
 WHERE `aadhar_number_option` IS NOT NULL AND `aadhar_number_option` <> '';

UPDATE `investor_residential_status`
   SET `oci_available` = UPPER(`oci_available`)
 WHERE `oci_available` IS NOT NULL AND `oci_available` <> '';

UPDATE `investor_residential_status`
   SET `person_origin` = UPPER(`person_origin`)
 WHERE `person_origin` IS NOT NULL AND `person_origin` <> '';

UPDATE `investor_residential_status`
   SET `proof_of_address` = UPPER(`proof_of_address`)
 WHERE `proof_of_address` IS NOT NULL AND `proof_of_address` <> '';

UPDATE `investor_residential_status`
   SET `user_type_of_proof` = UPPER(`user_type_of_proof`)
 WHERE `user_type_of_proof` IS NOT NULL AND `user_type_of_proof` <> '';

-- ============================================================
-- investor_tax_information — yes/no flags, TIN type, residency
-- ============================================================
UPDATE `investor_tax_information`
   SET `tax_info` = UPPER(`tax_info`)
 WHERE `tax_info` IS NOT NULL AND `tax_info` <> '';

UPDATE `investor_tax_information`
   SET `us_person_fatca` = UPPER(`us_person_fatca`)
 WHERE `us_person_fatca` IS NOT NULL AND `us_person_fatca` <> '';

-- ============================================================
-- investor_nomination — relationship, doc types
-- ============================================================
UPDATE `investor_nomination`
   SET `relationship` = UPPER(`relationship`)
 WHERE `relationship` IS NOT NULL AND `relationship` <> '';

UPDATE `investor_nomination`
   SET `nominee_doc_type` = UPPER(`nominee_doc_type`)
 WHERE `nominee_doc_type` IS NOT NULL AND `nominee_doc_type` <> '';

UPDATE `investor_nomination`
   SET `guardian_doc_type` = UPPER(`guardian_doc_type`)
 WHERE `guardian_doc_type` IS NOT NULL AND `guardian_doc_type` <> '';

UPDATE `investor_nomination`
   SET `guardian_relationship` = UPPER(`guardian_relationship`)
 WHERE `guardian_relationship` IS NOT NULL AND `guardian_relationship` <> '';

-- ============================================================
-- investor_risk_profile — occupation, source of funds, education, inv experience
-- ============================================================
UPDATE `investor_risk_profile`
   SET `source_of_funds` = UPPER(`source_of_funds`)
 WHERE `source_of_funds` IS NOT NULL AND `source_of_funds` <> '';

UPDATE `investor_risk_profile`
   SET `source_of_wealth` = UPPER(`source_of_wealth`)
 WHERE `source_of_wealth` IS NOT NULL AND `source_of_wealth` <> '';

UPDATE `investor_risk_profile`
   SET `educational_qualification` = UPPER(`educational_qualification`)
 WHERE `educational_qualification` IS NOT NULL AND `educational_qualification` <> '';

UPDATE `investor_risk_profile`
   SET `occupation` = UPPER(`occupation`)
 WHERE `occupation` IS NOT NULL AND `occupation` <> '';

UPDATE `investor_risk_profile`
   SET `investment_experience_years` = UPPER(`investment_experience_years`)
 WHERE `investment_experience_years` IS NOT NULL AND `investment_experience_years` <> '';

UPDATE `investor_risk_profile`
   SET `investment_experience_in` = UPPER(`investment_experience_in`)
 WHERE `investment_experience_in` IS NOT NULL AND `investment_experience_in` <> '';

-- investor_experience duplicates some of the same columns under a legacy
-- table name — normalise there too so whichever one the code reads from is
-- consistent.
UPDATE `investor_experience`
   SET `source_of_funds` = UPPER(`source_of_funds`)
 WHERE `source_of_funds` IS NOT NULL AND `source_of_funds` <> '';

UPDATE `investor_experience`
   SET `educational_qualification` = UPPER(`educational_qualification`)
 WHERE `educational_qualification` IS NOT NULL AND `educational_qualification` <> '';

UPDATE `investor_experience`
   SET `occupation` = UPPER(`occupation`)
 WHERE `occupation` IS NOT NULL AND `occupation` <> '';

UPDATE `investor_experience`
   SET `years_of_experience` = UPPER(`years_of_experience`)
 WHERE `years_of_experience` IS NOT NULL AND `years_of_experience` <> '';
