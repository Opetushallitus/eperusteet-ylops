create table lops2019_sisalto_oppiaine_jarjestys (
    sisalto_id int8 not null,
    oppiaine_jarjestys_id int8 not null,
    primary key (sisalto_id, oppiaine_jarjestys_id)
);

create table lops2019_sisalto_oppiaine_jarjestys_AUD (
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
    jarjestys int4,
    koodi varchar(255) not null,
    primary key (id)
);

create table lops2019_oppiaine_jarjestys_AUD (
    id int8 not null,
    REV int4 not null,
    REVTYPE int2,
    REVEND int4,
    luoja varchar(255),
    luotu timestamp,
    muokattu timestamp,
    muokkaaja varchar(255),
    jarjestys int4,
    koodi varchar(255),
    primary key (id, REV)
);

alter table lops2019_sisalto_oppiaine_jarjestys
    add constraint UK_e3hadebfb1phc3fwdf0v41v0o  unique (oppiaine_jarjestys_id);

alter table lops2019_sisalto_oppiaine_jarjestys
    add constraint FK_iragko6nucrsswhbky1fm2voc
    foreign key (sisalto_id)
    references lops2019_sisalto;

alter table lops2019_sisalto_oppiaine_jarjestys_AUD
    add constraint FK_eo3u3hv7233p5pqi77l3jcx4h
    foreign key (REV)
    references revinfo;

alter table lops2019_sisalto_oppiaine_jarjestys_AUD
    add constraint FK_1ctk3mb5qpi1y537vos5yicsk
    foreign key (REVEND)
    references revinfo;

alter table lops2019_sisalto_oppiaine_jarjestys
    add constraint FK_e3hadebfb1phc3fwdf0v41v0o
    foreign key (oppiaine_jarjestys_id)
    references lops2019_oppiaine_jarjestys;

alter table lops2019_oppiaine_jarjestys_AUD
    add constraint FK_rqx9rjrb9uasvu6cps4gixqsa
    foreign key (REV)
    references revinfo;

alter table lops2019_oppiaine_jarjestys_AUD
    add constraint FK_1lcegmvp9v5ogo166d3l9bt4p
    foreign key (REVEND)
    references revinfo;