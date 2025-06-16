CREATE INDEX IF NOT EXISTS tekstikappaleviite_vanhempi_id_indx on tekstikappaleviite (vanhempi_id);
CREATE INDEX IF NOT EXISTS tekstikappaleviite_tekstikappale_id_indx on tekstikappaleviite (tekstikappale_id);
CREATE INDEX IF NOT EXISTS tekstikappale_tunniste_indx on tekstikappale (tunniste);
CREATE INDEX IF NOT EXISTS opetussuunnitelma_tekstit_id_index on opetussuunnitelma (tekstit_id);