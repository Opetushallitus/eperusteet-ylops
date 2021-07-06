update opetussuunnitelma set cached_peruste = peruste.id
from
(
   SELECT
   id,
   diaarinumero
   from
   (
      select
      id,
      diaarinumero,
      aikaleima,
      row_number() over(partition by peruste_id order by aikaleima desc) AS rivi
      from peruste_cache
   ) pc
   WHERE pc.rivi = 1
) peruste
WHERE opetussuunnitelma.cached_peruste IS NULL
AND opetussuunnitelma.perusteendiaarinumero = peruste.diaarinumero
