create table opetussuunnitelman_muokkaustieto (
    id int8 not null,
    luotu timestamp not null,
    muokkaaja varchar(255),
    nimi_id int8,
    tapahtuma varchar(255) not null,
    opetussuunnitelma_id int8 not null,
    kohde varchar(255) not null,
    kohde_id int8 not null,
    lisatieto varchar(255),
    primary key (id)
);

alter table opetussuunnitelman_muokkaustieto
    add constraint FK_t63msfkfpvpu7m9dhux9wejt9
    foreign key (nimi_id)
    references lokalisoituteksti;

alter table opetussuunnitelman_muokkaustieto
    add constraint FK_bkryny115e6rxgn7pbyo4emsj
    foreign key (opetussuunnitelma_id)
    references opetussuunnitelma;
