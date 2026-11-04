create table tpo_sisalto (
    id int8 not null,
    luoja varchar(255),
    luotu timestamp,
    muokattu timestamp,
    muokkaaja varchar(255),
    opetussuunnitelma_id int8 not null,
    primary key (id)
);

create table tpo_sisalto_AUD (
    id int8 not null,
    REV int4 not null,
    REVTYPE int2,
    REVEND int4,
    luoja varchar(255),
    luotu timestamp,
    muokattu timestamp,
    muokkaaja varchar(255),
    opetussuunnitelma_id int8,
    primary key (id, REV)
);

create table tpo_taiteenala (
    id int8 not null,
    luoja varchar(255),
    luotu timestamp,
    muokattu timestamp,
    muokkaaja varchar(255),
    perusteen_taiteenala_id int8 not null,
    paikallinenTarkennus_id int8,
    sisalto_id int8 not null,
    taiteenalat_order int4,
    primary key (id)
);

create table tpo_taiteenala_AUD (
    id int8 not null,
    REV int4 not null,
    REVTYPE int2,
    REVEND int4,
    luoja varchar(255),
    luotu timestamp,
    muokattu timestamp,
    muokkaaja varchar(255),
    perusteen_taiteenala_id int8,
    paikallinenTarkennus_id int8,
    sisalto_id int8,
    taiteenalat_order int4,
    primary key (id, REV)
);

create table tpo_taiteenala_vaihe (
    id int8 not null,
    luoja varchar(255),
    luotu timestamp,
    muokattu timestamp,
    muokkaaja varchar(255),
    perusteen_taiteenala_vaihe_id int8 not null,
    taiteenala_id int8 not null,
    vaiheet_order int4,
    primary key (id)
);

create table tpo_taiteenala_vaihe_AUD (
    id int8 not null,
    REV int4 not null,
    REVTYPE int2,
    REVEND int4,
    luoja varchar(255),
    luotu timestamp,
    muokattu timestamp,
    muokkaaja varchar(255),
    perusteen_taiteenala_vaihe_id int8,
    taiteenala_id int8,
    vaiheet_order int4,
    primary key (id, REV)
);

create table tpo_taiteenala_vaihe_vapaatekstit (
    vaihe_id int8 not null,
    vapaateksti_paikallinentarkennus_id int8 not null
);

create table tpo_taiteenala_vaihe_vapaatekstit_AUD (
    REV int4 not null,
    vaihe_id int8 not null,
    vapaateksti_paikallinentarkennus_id int8 not null,
    REVTYPE int2,
    REVEND int4,
    primary key (REV, vaihe_id, vapaateksti_paikallinentarkennus_id)
);

create table tpo_opintokokonaisuus (
    id int8 not null,
    luoja varchar(255),
    luotu timestamp,
    muokattu timestamp,
    muokkaaja varchar(255),
    nimi_id int8,
    laajuus int8,
    osaamisenArviointi_id int8,
    vaihe_id int8 not null,
    opintokokonaisuudet_order int4,
    primary key (id)
);

create table tpo_opintokokonaisuus_AUD (
    id int8 not null,
    REV int4 not null,
    REVTYPE int2,
    REVEND int4,
    luoja varchar(255),
    luotu timestamp,
    muokattu timestamp,
    muokkaaja varchar(255),
    nimi_id int8,
    laajuus int8,
    osaamisenArviointi_id int8,
    vaihe_id int8,
    opintokokonaisuudet_order int4,
    primary key (id, REV)
);

create table tpo_opintokokonaisuus_tavoitealue (
    opintokokonaisuus_id int8 not null,
    tavoitealue_id int8
);

create table tpo_opintokokonaisuus_tavoitealue_AUD (
    REV int4 not null,
    opintokokonaisuus_id int8 not null,
    tavoitealue_id int8,
    REVTYPE int2,
    REVEND int4,
    primary key (REV, opintokokonaisuus_id, tavoitealue_id)
);

alter table tpo_sisalto
    add constraint UK_tpo_sisalto_ops unique (opetussuunnitelma_id);

alter table tpo_sisalto
    add constraint FK_tpo_sisalto_ops foreign key (opetussuunnitelma_id) references opetussuunnitelma;

alter table tpo_taiteenala
    add constraint UK_tpo_taiteenala_sisalto_peruste unique (sisalto_id, perusteen_taiteenala_id);

alter table tpo_taiteenala
    add constraint FK_tpo_taiteenala_sisalto foreign key (sisalto_id) references tpo_sisalto;

alter table tpo_taiteenala
    add constraint FK_tpo_taiteenala_tarkennus foreign key (paikallinenTarkennus_id) references lokalisoituteksti;

alter table tpo_taiteenala_vaihe
    add constraint UK_tpo_taiteenala_vaihe_peruste unique (taiteenala_id, perusteen_taiteenala_vaihe_id);

alter table tpo_taiteenala_vaihe
    add constraint FK_tpo_taiteenala_vaihe_taiteenala foreign key (taiteenala_id) references tpo_taiteenala;

alter table tpo_taiteenala_vaihe_vapaatekstit
    add constraint FK_tpo_vaihe_vapaatekstit_tarkennus
        foreign key (vapaateksti_paikallinentarkennus_id) references vapaateksti_paikallinentarkennus;

alter table tpo_taiteenala_vaihe_vapaatekstit
    add constraint FK_tpo_vaihe_vapaatekstit_vaihe foreign key (vaihe_id) references tpo_taiteenala_vaihe;

alter table tpo_opintokokonaisuus
    add constraint FK_tpo_opintokokonaisuus_vaihe foreign key (vaihe_id) references tpo_taiteenala_vaihe;

alter table tpo_opintokokonaisuus
    add constraint FK_tpo_opintokokonaisuus_nimi foreign key (nimi_id) references lokalisoituteksti;

alter table tpo_opintokokonaisuus
    add constraint FK_tpo_opintokokonaisuus_arviointi foreign key (osaamisenArviointi_id) references lokalisoituteksti;

alter table tpo_opintokokonaisuus_tavoitealue
    add constraint FK_tpo_opintokokonaisuus_tavoitealue foreign key (opintokokonaisuus_id) references tpo_opintokokonaisuus;
