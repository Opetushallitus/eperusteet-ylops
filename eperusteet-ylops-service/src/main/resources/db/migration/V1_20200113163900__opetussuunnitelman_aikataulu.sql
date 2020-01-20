create table opetussuunnitelman_aikataulu (
    id int8 not null,
    luoja varchar(255),
    luotu timestamp,
    muokattu timestamp,
    muokkaaja varchar(255),
    opetussuunnitelma_id int8 not null,
    tapahtuma varchar(255) not null,
    tapahtumapaiva timestamp not null,
    tavoite_id int8,
    primary key (id)
);

CREATE INDEX opetussuunnitelman_aikataulu_opetussuunnitelma_id_index ON opetussuunnitelman_aikataulu (opetussuunnitelma_id);

alter table opetussuunnitelman_aikataulu
    add constraint FK_1l4ixqpbvi8lakknru63hntsc
    foreign key (tavoite_id)
    references lokalisoituteksti;

create table opetussuunnitelman_aikataulu_AUD (
    id int8 not null,
    REV int4 not null,
    REVTYPE int2,
    REVEND int4,
    luoja varchar(255),
    luotu timestamp,
    muokattu timestamp,
    muokkaaja varchar(255),
    opetussuunnitelma_id int8,
    tapahtuma varchar(255),
    tapahtumapaiva timestamp,
    tavoite_id int8,
    primary key (id, REV)
);

alter table opetussuunnitelman_aikataulu_AUD
    add constraint FK_pkdsegqfa1so2ovu6enq72jmm
    foreign key (REV)
    references revinfo;

alter table opetussuunnitelman_aikataulu_AUD
    add constraint FK_s2pebfc88loctfbe8qupfenqy
    foreign key (REVEND)
    references revinfo;
