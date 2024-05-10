ALTER TABLE opetussuunnitelma ADD COLUMN IF NOT EXISTS organisaatiotaso VARCHAR(255);
ALTER TABLE opetussuunnitelma_aud ADD COLUMN IF NOT EXISTS organisaatiotaso VARCHAR(255);
