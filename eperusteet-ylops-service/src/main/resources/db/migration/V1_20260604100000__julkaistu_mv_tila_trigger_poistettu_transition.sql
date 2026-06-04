-- Faster MV definition (latest julkaisu per ops via DISTINCT ON, no correlated MAX).
-- Refresh only when tila crosses POISTETTU, not on every update while already POISTETTU.
drop trigger if exists tg_refresh_julkaistu_opetussuunnitelma_data_view on opetussuunnitelman_julkaisu;
drop trigger if exists tg_refresh_julkaistu_opetussuunnitelma_data_view on opetussuunnitelma;
drop trigger if exists tg_refresh_opetussuunnitelma_data_view_after_tila_update on opetussuunnitelma;

drop materialized view if exists julkaistu_opetussuunnitelma_data_view;

create materialized view julkaistu_opetussuunnitelma_data_view as
    SELECT DISTINCT ON (j.ops_id)
        d.opsdata->>'id' as id,
        d.opsdata->'nimi' as nimi,
        d.opsdata->'peruste' as peruste,
        d.opsdata->>'koulutustyyppi' as koulutustyyppi,
        d.opsdata->'organisaatiot' as organisaatiot,
        d.opsdata->'julkaisukielet' as julkaisukielet,
        j.luotu as julkaisuaika
    FROM opetussuunnitelman_julkaisu j
    INNER JOIN opetussuunnitelman_julkaisu_data d on d.id = j.data_id
    INNER JOIN opetussuunnitelma o on o.id = j.ops_id
    WHERE o.tila != 'POISTETTU'
    ORDER BY j.ops_id, j.revision DESC;

CREATE UNIQUE INDEX ON julkaistu_opetussuunnitelma_data_view (id);

CREATE TRIGGER tg_refresh_julkaistu_opetussuunnitelma_data_view AFTER INSERT
ON opetussuunnitelman_julkaisu
FOR EACH STATEMENT EXECUTE PROCEDURE tg_refresh_julkaistu_opetussuunnitelma_data_view();

CREATE TRIGGER tg_refresh_opetussuunnitelma_data_view_after_tila_update
    AFTER UPDATE ON opetussuunnitelma
    FOR EACH ROW
    WHEN (NEW.tila <> OLD.tila
        AND (OLD.tila = 'POISTETTU' OR NEW.tila = 'POISTETTU'))
    EXECUTE PROCEDURE tg_refresh_julkaistu_opetussuunnitelma_data_view();
