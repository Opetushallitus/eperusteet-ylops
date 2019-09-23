ALTER TABLE lops2019_poistettu_sisalto ADD COLUMN parent_id BIGINT REFERENCES lokalisoituteksti(id);
ALTER TABLE lops2019_poistettu_sisalto_aud ADD COLUMN parent_id BIGINT;
