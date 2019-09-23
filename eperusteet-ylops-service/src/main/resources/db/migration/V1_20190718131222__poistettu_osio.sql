create table lops2019_poistettu_sisalto (
    id int8 not null,
    luoja varchar(255),
    luotu timestamp,
    muokattu timestamp,
    muokkaaja varchar(255),
    tyyppi varchar(255),
    poistettu_id int8 not null,
    palautettu boolean,
    nimi_id int8 references lokalisoituteksti(id),
    opetussuunnitelma_id int8 not null references opetussuunnitelma(id),
    primary key (id)
);


create table lops2019_poistettu_sisalto_AUD (
    id int8 not null,
    REV int4 not null references revinfo(REV),
    REVTYPE int2,
    REVEND int4,
    luoja varchar(255),
    luotu timestamp,
    muokattu timestamp,
    muokkaaja varchar(255),
    tyyppi varchar(255),
    poistettu_id int8,
    palautettu boolean,
    nimi_id int8,
    opetussuunnitelma_id int8 not null,
    primary key (id, REV)
);