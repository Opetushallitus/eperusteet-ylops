ALTER TABLE opetussuunnitelman_julkaisu_data DROP COLUMN opsData;
ALTER TABLE opetussuunnitelman_julkaisu_data ADD COLUMN opsData JSONB NOT NULL;
