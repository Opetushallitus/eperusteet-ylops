ALTER TABLE tekstikappaleviite ADD COLUMN original_id BIGINT REFERENCES tekstikappaleviite(id);
ALTER TABLE tekstikappaleviite_aud ADD COLUMN original_id BIGINT;

ALTER TABLE tekstikappaleviite ADD COLUMN nayta_pohjan_teksti BOOLEAN NOT NULL DEFAULT true;
ALTER TABLE tekstikappaleviite_aud ADD COLUMN nayta_pohjan_teksti BOOLEAN NOT NULL DEFAULT true;