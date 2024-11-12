UPDATE opetussuunnitelman_muokkaustieto
SET kohde_id = pc.peruste_id,
    kohde    = 'peruste'
FROM opetussuunnitelma o
         inner join peruste_cache pc ON o.cached_peruste = pc.id
WHERE o.id = opetussuunnitelman_muokkaustieto.opetussuunnitelma_id
  AND lisatieto = 'tapahtuma-opetussuunnitelma-peruste-paivitys';