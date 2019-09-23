ALTER TABLE lops2019_opintojakso ADD COLUMN arviointi_id BIGINT REFERENCES lokalisoituteksti(id);
ALTER TABLE lops2019_opintojakso_aud ADD COLUMN arviointi_id BIGINT;
