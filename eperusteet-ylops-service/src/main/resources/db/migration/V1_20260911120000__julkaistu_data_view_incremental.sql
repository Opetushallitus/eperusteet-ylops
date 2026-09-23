-- Replace materialized view with a regular table maintained incrementally.
-- REFRESH MATERIALIZED VIEW CONCURRENTLY rebuilds the whole view on every julkaisu
-- insert (JSONB extract from every opsdata row) and runs inside the same transaction,
-- which makes publication very slow.

drop trigger if exists tg_refresh_julkaistu_opetussuunnitelma_data_view on opetussuunnitelman_julkaisu;
drop trigger if exists tg_refresh_opetussuunnitelma_data_view_after_tila_update on opetussuunnitelma;

create table julkaistu_opetussuunnitelma_data_view_new as
select * from julkaistu_opetussuunnitelma_data_view;

alter table julkaistu_opetussuunnitelma_data_view_new
    add constraint julkaistu_opetussuunnitelma_data_view_pkey primary key (id);

drop materialized view if exists julkaistu_opetussuunnitelma_data_view;
drop function if exists tg_refresh_julkaistu_opetussuunnitelma_data_view();

alter table julkaistu_opetussuunnitelma_data_view_new rename to julkaistu_opetussuunnitelma_data_view;

create or replace function tg_upsert_julkaistu_opetussuunnitelma_data_view()
returns trigger as $$
begin
    insert into julkaistu_opetussuunnitelma_data_view (id, nimi, peruste, koulutustyyppi, organisaatiot, julkaisukielet, julkaisuaika)
    select
        d.opsdata->>'id',
        d.opsdata->'nimi',
        d.opsdata->'peruste',
        d.opsdata->>'koulutustyyppi',
        d.opsdata->'organisaatiot',
        d.opsdata->'julkaisukielet',
        new.luotu
    from opetussuunnitelman_julkaisu_data d
    inner join opetussuunnitelma o on o.id = new.ops_id
    where d.id = new.data_id
      and o.tila != 'POISTETTU'
    on conflict (id) do update set
        nimi = excluded.nimi,
        peruste = excluded.peruste,
        koulutustyyppi = excluded.koulutustyyppi,
        organisaatiot = excluded.organisaatiot,
        julkaisukielet = excluded.julkaisukielet,
        julkaisuaika = excluded.julkaisuaika;
    return null;
end;
$$ language plpgsql;

create trigger tg_refresh_julkaistu_opetussuunnitelma_data_view
    after insert on opetussuunnitelman_julkaisu
    for each row
    execute procedure tg_upsert_julkaistu_opetussuunnitelma_data_view();

create or replace function tg_sync_julkaistu_opetussuunnitelma_data_view_tila()
returns trigger as $$
begin
    if new.tila = 'POISTETTU' then
        delete from julkaistu_opetussuunnitelma_data_view
        where id = new.id::text;
    else
        insert into julkaistu_opetussuunnitelma_data_view (id, nimi, peruste, koulutustyyppi, organisaatiot, julkaisukielet, julkaisuaika)
        select
            d.opsdata->>'id',
            d.opsdata->'nimi',
            d.opsdata->'peruste',
            d.opsdata->>'koulutustyyppi',
            d.opsdata->'organisaatiot',
            d.opsdata->'julkaisukielet',
            j.luotu
        from opetussuunnitelman_julkaisu j
        inner join opetussuunnitelman_julkaisu_data d on d.id = j.data_id
        where j.ops_id = new.id
        order by j.revision desc
        limit 1
        on conflict (id) do update set
            nimi = excluded.nimi,
            peruste = excluded.peruste,
            koulutustyyppi = excluded.koulutustyyppi,
            organisaatiot = excluded.organisaatiot,
            julkaisukielet = excluded.julkaisukielet,
            julkaisuaika = excluded.julkaisuaika;
    end if;
    return null;
end;
$$ language plpgsql;

create trigger tg_refresh_opetussuunnitelma_data_view_after_tila_update
    after update on opetussuunnitelma
    for each row
    when (new.tila <> old.tila
        and (old.tila = 'POISTETTU' or new.tila = 'POISTETTU'))
    execute procedure tg_sync_julkaistu_opetussuunnitelma_data_view_tila();
