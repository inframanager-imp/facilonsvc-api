-- Proof-of-identity document type (Aadhaar, PAN, Passport, etc.) on passport / identity section
ALTER TABLE user_passport_details
    ADD COLUMN document_type VARCHAR(100);

COMMENT ON COLUMN user_passport_details.document_type IS 'Selected proof-of-identity document type (e.g. Passport, PAN, Aadhaar)';
