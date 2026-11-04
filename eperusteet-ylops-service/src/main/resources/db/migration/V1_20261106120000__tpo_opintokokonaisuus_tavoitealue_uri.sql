delete from tpo_opintokokonaisuus_tavoitealue;
delete from tpo_opintokokonaisuus_tavoitealue_aud;

alter table tpo_opintokokonaisuus_tavoitealue
    add column koodi_uri varchar(255) not null;

alter table tpo_opintokokonaisuus_tavoitealue_aud drop constraint if exists tpo_opintokokonaisuus_tavoitealue_aud_pkey;

alter table tpo_opintokokonaisuus_tavoitealue_aud
    add column koodi_uri varchar(255) not null;

alter table tpo_opintokokonaisuus_tavoitealue_aud
    add primary key (rev, opintokokonaisuus_id, tavoitealue_id, koodi_uri);
