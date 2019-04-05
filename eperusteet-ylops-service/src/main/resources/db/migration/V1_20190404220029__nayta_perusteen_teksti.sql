ALTER TABLE tekstikappaleviite ADD COLUMN nayta_perusteen_teksti BOOLEAN NOT NULL;
ALTER TABLE tekstikappaleviite_aud ADD COLUMN nayta_perusteen_teksti BOOLEAN NOT NULL;

ALTER TABLE lops2019_opintojakso ADD COLUMN laajuus int8 not null default 0;
ALTER TABLE lops2019_opintojakso_aud ADD COLUMN laajuus int8 not null default 0;
