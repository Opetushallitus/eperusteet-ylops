ALTER TABLE opetussuunnitelma ADD COLUMN toteutus CHARACTER VARYING(255);
ALTER TABLE opetussuunnitelma_aud ADD COLUMN toteutus CHARACTER VARYING(255);

CREATE TABLE lops2019_sisalto (
  id BIGINT NOT NULL PRIMARY KEY,
  opetussuunnitelma_id BIGINT NOT NULL,
  luoja varchar(255),
  luotu timestamp,
  muokattu timestamp,
  muokkaaja varchar(255)
);


CREATE TABLE lops2019_sisalto_aud (
  id BIGINT,
  opetussuunnitelma_id BIGINT,
  luoja varchar(255),
  luotu timestamp,
  muokattu timestamp,
  muokkaaja varchar(255),
  REV int4 not null,
  REVTYPE int2 not null,
  REVEND int4
);

ALTER TABLE opetussuunnitelma ADD COLUMN lops_2019_sisalto BIGINT REFERENCES lops2019_sisalto(id);
ALTER TABLE opetussuunnitelma_aud ADD COLUMN lops_2019_sisalto BIGINT;
