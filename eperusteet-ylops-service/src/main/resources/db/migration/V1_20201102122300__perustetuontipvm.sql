ALTER TABLE opetussuunnitelma ADD COLUMN peruste_data_tuonti_pvm timestamp DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE opetussuunnitelma_aud ADD COLUMN peruste_data_tuonti_pvm timestamp;

UPDATE opetussuunnitelma set peruste_data_tuonti_pvm = null WHERE koulutustyyppi in ('PERUSOPETUS', 'ESIOPETUS', 'VARHAISKASVATUS', 'TPO');
