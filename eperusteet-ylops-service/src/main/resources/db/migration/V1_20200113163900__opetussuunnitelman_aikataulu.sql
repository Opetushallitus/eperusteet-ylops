create table opetussuunnitelman_aikataulu (
    id int8 not null,
    opetussuunnitelma_id int8 not null,
    tapahtuma varchar(255) not null,
    tapahtumapaiva timestamp not null,
    tavoite_id int8,
    luoja varchar(255),
    luotu timestamp,
    primary key (id)
);

alter table opetussuunnitelman_aikataulu
    add constraint FK_1l4ixqpbvi8lakknru63hntsc
    foreign key (tavoite_id)
    references lokalisoituteksti;
