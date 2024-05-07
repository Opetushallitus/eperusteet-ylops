UPDATE opetussuunnitelma
SET tila = 'JULKAISTU'
    FROM opetussuunnitelman_julkaisu
WHERE opetussuunnitelma.tila = 'LUONNOS'
  AND opetussuunnitelman_julkaisu.ops_id = opetussuunnitelma.id;
