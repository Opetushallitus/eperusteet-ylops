drop table opetussuunnitelmanjulkaisu_dokumentit;
drop table opetussuunnitelman_julkaisu;

create table OpetussuunnitelmanJulkaisu_dokumentit (
    OpetussuunnitelmanJulkaisu_id int8 not null,
    dokumentit int8
);

create table opetussuunnitelman_julkaisu (
    id int8 not null,
    luoja varchar(255) not null,
    luotu timestamp,
    revision int4 not null,
    data_id int8 not null,
    ops_id int8 not null,
    tiedote_id int8 not null,
    primary key (id)
);

create table opetussuunnitelman_julkaisu_data (
    id int8 not null,
    hash int4 not null,
    opsData text not null,
    primary key (id)
);


alter table OpetussuunnitelmanJulkaisu_dokumentit 
    add constraint FK_42fxqq7amvujah537e154kysy 
    foreign key (OpetussuunnitelmanJulkaisu_id) 
    references opetussuunnitelman_julkaisu;

alter table opetussuunnitelman_julkaisu 
    add constraint FK_hwmlysqrn8x3h9yo4e4lll9l6 
    foreign key (data_id) 
    references opetussuunnitelman_julkaisu_data;

alter table opetussuunnitelman_julkaisu 
    add constraint FK_i9k93te4grfgq6v2whyxu1axe 
    foreign key (ops_id) 
    references opetussuunnitelma;

alter table opetussuunnitelman_julkaisu 
    add constraint FK_d3kmpekojj3wa08bms0dt5cgr 
    foreign key (tiedote_id) 
    references lokalisoituteksti;

