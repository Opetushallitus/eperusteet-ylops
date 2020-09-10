INSERT INTO lops2019_poistettu_sisalto (id, luoja,luotu,muokattu,muokkaaja,tyyppi,poistettu_id, opetussuunnitelma_id, nimi_id)
SELECT NEXTVAL('hibernate_sequence'), op.luoja, op.luotu, op.muokattu, op.muokkaaja, 'OPPIAINE', op.oppiaine_id, op.opetussuunnitelma_id, (SELECT nimi_id FROM oppiaine_aud aud WHERE aud.id = op.oppiaine_id AND nimi_id IS NOT NULL ORDER BY luotu DESC LIMIT 1)
FROM poistettu_oppiaine op
WHERE palautettu = FALSE
