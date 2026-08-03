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
    koodi varchar(255),
    paikallinen_tarkennus_id int8,
    primary key (id)
);

create table tpo_taiteenala_AUD (
    id int8 not null,
    REV int4 not null,
    REVTYPE int2,
    luoja varchar(255),
    luotu timestamp,
    muokattu timestamp,
    muokkaaja varchar(255),
    koodi varchar(255),
    paikallinen_tarkennus_id int8,
    primary key (id, REV)
);

create table tpo_taiteenosa (
    id int8 not null,
    luoja varchar(255),
    luotu timestamp,
    muokattu timestamp,
    muokkaaja varchar(255),
    perusteen_taiteenosan_id int8,
    paikallinen_tarkennus_id int8,
    primary key (id)
);

create table tpo_taiteenosa_AUD (
    id int8 not null,
    REV int4 not null,
    REVTYPE int2,
    luoja varchar(255),
    luotu timestamp,
    muokattu timestamp,
    muokkaaja varchar(255),
    perusteen_taiteenosan_id int8,
    paikallinen_tarkennus_id int8,
    primary key (id, REV)
);

create table tpo_sisalto_taiteenala (
    sisalto_id int8 not null,
    taiteenala_id int8 not null,
    taiteenala_jarjestys int4 not null,
    primary key (sisalto_id, taiteenala_jarjestys)
);

create table tpo_sisalto_taiteenala_AUD (
    REV int4 not null,
    sisalto_id int8 not null,
    taiteenala_id int8 not null,
    taiteenala_jarjestys int4 not null,
    REVTYPE int2,
    primary key (REV, sisalto_id, taiteenala_id, taiteenala_jarjestys)
);

create table tpo_taiteenala_taiteenosa (
    taiteenala_id int8 not null,
    taiteenosa_id int8 not null,
    taiteenosa_jarjestys int4 not null,
    primary key (taiteenala_id, taiteenosa_jarjestys)
);

create table tpo_taiteenala_taiteenosa_AUD (
    REV int4 not null,
    taiteenala_id int8 not null,
    taiteenosa_id int8 not null,
    taiteenosa_jarjestys int4 not null,
    REVTYPE int2,
    primary key (REV, taiteenala_id, taiteenosa_id, taiteenosa_jarjestys)
);

alter table tpo_sisalto
    add constraint UK_tpo_sisalto_opetussuunnitelma unique (opetussuunnitelma_id);

alter table tpo_sisalto
    add constraint FK_tpo_sisalto_opetussuunnitelma
    foreign key (opetussuunnitelma_id)
    references opetussuunnitelma;

alter table tpo_sisalto_AUD
    add constraint FK_tpo_sisalto_AUD_rev
    foreign key (REV)
    references revinfo;

alter table tpo_taiteenala
    add constraint FK_tpo_taiteenala_paikallinen_tarkennus
    foreign key (paikallinen_tarkennus_id)
    references lokalisoituteksti;

alter table tpo_taiteenala_AUD
    add constraint FK_tpo_taiteenala_AUD_rev
    foreign key (REV)
    references revinfo;

alter table tpo_taiteenosa
    add constraint FK_tpo_taiteenosa_paikallinen_tarkennus
    foreign key (paikallinen_tarkennus_id)
    references lokalisoituteksti;

alter table tpo_taiteenosa_AUD
    add constraint FK_tpo_taiteenosa_AUD_rev
    foreign key (REV)
    references revinfo;

alter table tpo_sisalto_taiteenala
    add constraint UK_tpo_sisalto_taiteenala unique (taiteenala_id);

alter table tpo_sisalto_taiteenala
    add constraint FK_tpo_sisalto_taiteenala_sisalto
    foreign key (sisalto_id)
    references tpo_sisalto;

alter table tpo_sisalto_taiteenala
    add constraint FK_tpo_sisalto_taiteenala_taiteenala
    foreign key (taiteenala_id)
    references tpo_taiteenala;

alter table tpo_sisalto_taiteenala_AUD
    add constraint FK_tpo_sisalto_taiteenala_AUD_rev
    foreign key (REV)
    references revinfo;

alter table tpo_taiteenala_taiteenosa
    add constraint UK_tpo_taiteenala_taiteenosa unique (taiteenosa_id);

alter table tpo_taiteenala_taiteenosa
    add constraint FK_tpo_taiteenala_taiteenosa_taiteenala
    foreign key (taiteenala_id)
    references tpo_taiteenala;

alter table tpo_taiteenala_taiteenosa
    add constraint FK_tpo_taiteenala_taiteenosa_taiteenosa
    foreign key (taiteenosa_id)
    references tpo_taiteenosa;

alter table tpo_taiteenala_taiteenosa_AUD
    add constraint FK_tpo_taiteenala_taiteenosa_AUD_rev
    foreign key (REV)
    references revinfo;
