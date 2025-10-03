drop materialized view if exists julkaistu_opetussuunnitelma_data_view;
drop trigger if exists tg_refresh_julkaistu_opetussuunnitelma_data_view on opetussuunnitelman_julkaisu;
drop trigger if exists tg_refresh_julkaistu_opetussuunnitelma_data_view on opetussuunnitelma;
drop trigger if exists tg_refresh_opetussuunnitelma_data_view_after_tila_update on opetussuunnitelma;
drop function if exists tg_refresh_julkaistu_opetussuunnitelma_data_view;

create materialized view julkaistu_opetussuunnitelma_data_view as
	SELECT DISTINCT ON (id)
		opsdata->>'id' as id,
      	opsdata->'nimi' as nimi,
      	opsdata->'peruste' as peruste,
      	opsdata->>'koulutustyyppi' as koulutustyyppi,
      	opsdata->'organisaatiot' as organisaatiot,
      	opsdata->'julkaisukielet' as julkaisukielet,
      	j.luotu as julkaisuaika
   	FROM opetussuunnitelman_julkaisu j
   	INNER JOIN opetussuunnitelman_julkaisu_data d on d.id = j.data_id
	INNER JOIN opetussuunnitelma o on o.id = j.ops_id
    	WHERE revision = (SELECT MAX(revision) FROM opetussuunnitelman_julkaisu j2 WHERE j.ops_id = j2.ops_id)
    	AND o.tila != 'POISTETTU'
    ORDER BY id;

CREATE UNIQUE INDEX ON julkaistu_opetussuunnitelma_data_view (id);

CREATE OR REPLACE FUNCTION tg_refresh_julkaistu_opetussuunnitelma_data_view()
RETURNS trigger AS
'
BEGIN
	REFRESH MATERIALIZED VIEW CONCURRENTLY julkaistu_opetussuunnitelma_data_view;
	RETURN null;
END
'
LANGUAGE plpgsql;

CREATE TRIGGER tg_refresh_julkaistu_opetussuunnitelma_data_view AFTER INSERT
ON opetussuunnitelman_julkaisu
FOR EACH STATEMENT EXECUTE PROCEDURE tg_refresh_julkaistu_opetussuunnitelma_data_view();

CREATE TRIGGER tg_refresh_opetussuunnitelma_data_view_after_tila_update
    AFTER UPDATE ON opetussuunnitelma
    FOR EACH ROW
    WHEN (NEW.tila = 'POISTETTU' OR OLD.tila = 'POISTETTU')
    EXECUTE PROCEDURE tg_refresh_julkaistu_opetussuunnitelma_data_view();