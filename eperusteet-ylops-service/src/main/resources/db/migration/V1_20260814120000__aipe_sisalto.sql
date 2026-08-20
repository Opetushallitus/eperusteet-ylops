create table aipe_sisalto (
    id int8 not null,
    luoja varchar(255),
    luotu timestamp,
    muokattu timestamp,
    muokkaaja varchar(255),
    opetussuunnitelma_id int8 not null,
    primary key (id)
);

create table aipe_sisalto_AUD (
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

create table aipe_vaihe (
    id int8 not null,
    luoja varchar(255),
    luotu timestamp,
    muokattu timestamp,
    muokkaaja varchar(255),
    perusteen_vaihe_id int8 not null,
    paikallinenTarkennus_id int8,
    piilotettu boolean not null default false,
    sisalto_id int8 not null,
    vaiheet_order int4,
    primary key (id)
);

create table aipe_vaihe_AUD (
    id int8 not null,
    REV int4 not null,
    REVTYPE int2,
    REVEND int4,
    luoja varchar(255),
    luotu timestamp,
    muokattu timestamp,
    muokkaaja varchar(255),
    perusteen_vaihe_id int8,
    paikallinenTarkennus_id int8,
    piilotettu boolean,
    sisalto_id int8,
    vaiheet_order int4,
    primary key (id, REV)
);

create table aipe_oppiaine (
    id int8 not null,
    luoja varchar(255),
    luotu timestamp,
    muokattu timestamp,
    muokkaaja varchar(255),
    perusteen_oppiaine_id int8 not null,
    paikallinenTarkennus_id int8,
    piilotettu boolean not null default false,
    vaihe_id int8,
    parent_id int8,
    oppiaineet_order int4,
    oppimaarat_order int4,
    primary key (id)
);

create table aipe_oppiaine_AUD (
    id int8 not null,
    REV int4 not null,
    REVTYPE int2,
    REVEND int4,
    luoja varchar(255),
    luotu timestamp,
    muokattu timestamp,
    muokkaaja varchar(255),
    perusteen_oppiaine_id int8,
    paikallinenTarkennus_id int8,
    piilotettu boolean,
    vaihe_id int8,
    parent_id int8,
    oppiaineet_order int4,
    oppimaarat_order int4,
    primary key (id, REV)
);

create table aipe_kurssi (
    id int8 not null,
    luoja varchar(255),
    luotu timestamp,
    muokattu timestamp,
    muokkaaja varchar(255),
    perusteen_kurssi_id int8 not null,
    paikallinenTarkennus_id int8,
    piilotettu boolean not null default false,
    oppiaine_id int8,
    kurssit_order int4,
    primary key (id)
);

create table aipe_kurssi_AUD (
    id int8 not null,
    REV int4 not null,
    REVTYPE int2,
    REVEND int4,
    luoja varchar(255),
    luotu timestamp,
    muokattu timestamp,
    muokkaaja varchar(255),
    perusteen_kurssi_id int8,
    paikallinenTarkennus_id int8,
    piilotettu boolean,
    oppiaine_id int8,
    kurssit_order int4,
    primary key (id, REV)
);

create table aipe_oppiaine_piilotettu_tavoite (
    oppiaine_id int8 not null,
    tavoite_id int8
);

create table aipe_oppiaine_piilotettu_tavoite_AUD (
    REV int4 not null,
    oppiaine_id int8 not null,
    tavoite_id int8,
    REVTYPE int2,
    REVEND int4,
    primary key (REV, oppiaine_id, tavoite_id)
);

alter table aipe_sisalto
    add constraint UK_aipe_sisalto_ops unique (opetussuunnitelma_id);

alter table aipe_sisalto
    add constraint FK_aipe_sisalto_ops foreign key (opetussuunnitelma_id) references opetussuunnitelma;

alter table aipe_vaihe
    add constraint UK_aipe_vaihe_sisalto_peruste unique (sisalto_id, perusteen_vaihe_id);

alter table aipe_vaihe
    add constraint FK_aipe_vaihe_sisalto foreign key (sisalto_id) references aipe_sisalto;

alter table aipe_vaihe
    add constraint FK_aipe_vaihe_tarkennus foreign key (paikallinenTarkennus_id) references lokalisoituteksti;

alter table aipe_oppiaine
    add constraint FK_aipe_oppiaine_vaihe foreign key (vaihe_id) references aipe_vaihe;

alter table aipe_oppiaine
    add constraint FK_aipe_oppiaine_parent foreign key (parent_id) references aipe_oppiaine;

alter table aipe_oppiaine
    add constraint FK_aipe_oppiaine_tarkennus foreign key (paikallinenTarkennus_id) references lokalisoituteksti;

alter table aipe_kurssi
    add constraint FK_aipe_kurssi_oppiaine foreign key (oppiaine_id) references aipe_oppiaine;

alter table aipe_kurssi
    add constraint FK_aipe_kurssi_tarkennus foreign key (paikallinenTarkennus_id) references lokalisoituteksti;

alter table aipe_oppiaine_piilotettu_tavoite
    add constraint FK_aipe_piilotettu_tavoite_oa foreign key (oppiaine_id) references aipe_oppiaine;
