create table lops2019_sisalto_oppiaine_jarjestys (
    sisalto_id int8 not null,
    oppiaine_jarjestys_id int8 not null,
    primary key (sisalto_id, oppiaine_jarjestys_id)
);

create table lops2019_sisalto_oppiaine_jarjestys_aud (
    REV int4 not null,
    sisalto_id int8 not null,
    oppiaine_jarjestys_id int8 not null,
    REVTYPE int2,
    REVEND int4,
    primary key (REV, sisalto_id, oppiaine_jarjestys_id)
);

create table lops2019_oppiaine_jarjestys (
    id int8 not null,
    luoja varchar(255),
    luotu timestamp,
    muokattu timestamp,
    muokkaaja varchar(255),
    koodi varchar(255),
    jarjestys int,
    primary key (id)
);

create table lops2019_oppiaine_jarjestys_aud (
    id int8 not null,
    REV int4 not null,
    REVTYPE int2,
    REVEND int4,
    luoja varchar(255),
    luotu timestamp,
    muokattu timestamp,
    muokkaaja varchar(255),
    koodi varchar(255),
    jarjestys int,
    primary key (id, REV)
);