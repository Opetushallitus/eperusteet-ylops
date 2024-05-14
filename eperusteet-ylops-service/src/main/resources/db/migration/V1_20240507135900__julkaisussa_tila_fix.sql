ALTER TABLE opetussuunnitelma DISABLE TRIGGER tg_refresh_opetussuunnitelma_data_view_after_tila_update;

UPDATE opetussuunnitelma
SET tila = 'JULKAISTU'
    FROM opetussuunnitelman_julkaisu
WHERE opetussuunnitelma.tila = 'LUONNOS'
  AND opetussuunnitelman_julkaisu.ops_id = opetussuunnitelma.id;

ALTER TABLE opetussuunnitelma ENABLE TRIGGER tg_refresh_opetussuunnitelma_data_view_after_tila_update;