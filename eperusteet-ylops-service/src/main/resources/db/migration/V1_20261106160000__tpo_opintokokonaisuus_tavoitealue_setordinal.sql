alter table tpo_opintokokonaisuus_tavoitealue_aud
    add column SETORDINAL int4 not null default 0;

alter table tpo_opintokokonaisuus_tavoitealue_aud
    alter column SETORDINAL drop default;

alter table tpo_opintokokonaisuus_tavoitealue_aud
    alter column revtype set not null;

alter table tpo_opintokokonaisuus_tavoitealue_aud
    drop constraint tpo_opintokokonaisuus_tavoitealue_aud_pkey;

alter table tpo_opintokokonaisuus_tavoitealue_aud
    add primary key (REV, REVTYPE, opintokokonaisuus_id, SETORDINAL);
