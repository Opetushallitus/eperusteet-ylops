ALTER TABLE tekstikappaleviite ADD COLUMN nayta_perusteen_teksti BOOLEAN DEFAULT TRUE;
ALTER TABLE tekstikappaleviite_aud ADD COLUMN nayta_perusteen_teksti BOOLEAN DEFAULT TRUE;

ALTER TABLE lops2019_opintojakso ADD COLUMN laajuus int8 default 0;
ALTER TABLE lops2019_opintojakso_aud ADD COLUMN laajuus int8 default 0;
