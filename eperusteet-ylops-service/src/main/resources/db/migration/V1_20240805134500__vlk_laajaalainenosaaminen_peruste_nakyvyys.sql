ALTER TABLE vlkok_laaja_osaaminen ADD COLUMN nayta_perusteen_paatason_lao BOOLEAN default true not null;
ALTER TABLE vlkok_laaja_osaaminen ADD COLUMN nayta_perusteen_vlk_tarkennettu_lao BOOLEAN default false not null;

ALTER TABLE vlkok_laaja_osaaminen_aud ADD COLUMN nayta_perusteen_paatason_lao BOOLEAN NULL;
ALTER TABLE vlkok_laaja_osaaminen_aud ADD COLUMN nayta_perusteen_vlk_tarkennettu_lao BOOLEAN NULL;