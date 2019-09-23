create table poistettu_opintojakso (
    id int8 not null,
    luoja varchar(255),
    luotu timestamp,
    muokattu timestamp,
    muokkaaja varchar(255),
    opintojakso_id int8,
    palautettu boolean,
    nimi_id int8,
    opetussuunnitelma_id int8 not null,
    primary key (id)
);


create table poistettu_opintojakso_AUD (
    id int8 not null,
    REV int4 not null,
    REVTYPE int2,
    REVEND int4,
    luoja varchar(255),
    luotu timestamp,
    muokattu timestamp,
    muokkaaja varchar(255),
    opintojakso_id int8,
    palautettu boolean,
    nimi_id int8,
    opetussuunnitelma_id int8,
    primary key (id, REV)
);

alter table poistettu_opintojakso 
    add constraint FK_lvdp4l46cpi9a11161n0bqxfs 
    foreign key (nimi_id) 
    references lokalisoituteksti;

alter table poistettu_opintojakso 
    add constraint FK_fosujycl6eomtbepy095oh2cn 
    foreign key (opetussuunnitelma_id) 
    references opetussuunnitelma;

alter table poistettu_opintojakso_AUD 
    add constraint FK_d1of5ruxn0pi38d9878ivkepw 
    foreign key (REV) 
    references revinfo;

alter table poistettu_opintojakso_AUD 
    add constraint FK_f8euou8psqnc3sg9uia4ks3h1 
    foreign key (REVEND) 
    references revinfo;
