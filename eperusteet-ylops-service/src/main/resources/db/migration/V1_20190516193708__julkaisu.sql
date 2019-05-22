create table opetussuunnitelman_julkaisu (
    id int8 not null,
    peruste_json text not null,
    ops_id int8 not null,
    tiedote_id int8 not null,
    primary key (id)
);

create table OpetussuunnitelmanJulkaisu_dokumentit (
    OpetussuunnitelmanJulkaisu_id int8 not null,
    dokumentit int8
);

alter table opetussuunnitelman_julkaisu 
    add constraint FK_i9k93te4grfgq6v2whyxu1axe 
    foreign key (ops_id) 
    references opetussuunnitelma;

alter table opetussuunnitelman_julkaisu 
    add constraint FK_d3kmpekojj3wa08bms0dt5cgr 
    foreign key (tiedote_id) 
    references lokalisoituteksti;

alter table OpetussuunnitelmanJulkaisu_dokumentit 
    add constraint FK_42fxqq7amvujah537e154kysy 
    foreign key (OpetussuunnitelmanJulkaisu_id) 
    references opetussuunnitelman_julkaisu;

