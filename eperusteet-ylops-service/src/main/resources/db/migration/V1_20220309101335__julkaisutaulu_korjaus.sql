drop materialized view if exists julkaistu_opetussuunnitelma_data_view;
drop trigger if exists tg_refresh_julkaistu_opetussuunnitelma_data_view on opetussuunnitelman_julkaisu;
drop trigger if exists tg_refresh_julkaistu_opetussuunnitelma_data_view on opetussuunnitelma;
drop function if exists tg_refresh_julkaistu_opetussuunnitelma_data_view;

create materialized view julkaistu_opetussuunnitelma_data_view as
	SELECT DISTINCT ON (id)
		opsdata->>'id' as id,
      	opsdata->'nimi' as nimi,
      	opsdata->'peruste' as peruste,
      	opsdata->>'koulutustyyppi' as koulutustyyppi,
      	opsdata->'organisaatiot' as organisaatiot
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

CREATE UNIQUE INDEX opetussuunnitelman_julkaisu_uniq_ops_rev ON opetussuunnitelman_julkaisu (ops_id, revision);
