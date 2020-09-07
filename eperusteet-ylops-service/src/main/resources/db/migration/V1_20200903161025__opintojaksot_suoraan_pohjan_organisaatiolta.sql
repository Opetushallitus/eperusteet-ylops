DROP TABLE IF EXISTS lops2019_sisalto_tuotu_opintojakso;
DROP TABLE IF EXISTS lops2019_sisalto_tuotu_opintojakso_aud;

ALTER TABLE opetussuunnitelma ADD COLUMN tuoPohjanOpintojaksot BOOLEAN DEFAULT FALSE;
ALTER TABLE opetussuunnitelma_aud ADD COLUMN tuoPohjanOpintojaksot BOOLEAN DEFAULT FALSE;