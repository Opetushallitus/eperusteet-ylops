CREATE INDEX IF NOT EXISTS tekstikappaleviite_vanhempi_id_index on tekstikappaleviite (vanhempi_id);
CREATE INDEX IF NOT EXISTS tekstikappaleviite_tekstikappale_id_index on tekstikappaleviite (tekstikappale_id);
CREATE INDEX IF NOT EXISTS tekstikappale_tunniste_index on tekstikappale (tunniste);
CREATE INDEX IF NOT EXISTS opetussuunnitelma_tekstit_id_index on opetussuunnitelma (tekstit_id);