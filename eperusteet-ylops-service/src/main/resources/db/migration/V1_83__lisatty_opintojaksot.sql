create table lops2019_opintojakso (
    id int8 not null,
    luoja varchar(255),
    luotu timestamp,
    muokattu timestamp,
    muokkaaja varchar(255),
    koodi varchar(255),
    kuvaus_id int8,
    nimi_id int8,
    primary key (id)
);

create table lops2019_opintojakso_AUD (
    id int8 not null,
    REV int4 not null,
    REVTYPE int2,
    REVEND int4,
    luoja varchar(255),
    luotu timestamp,
    muokattu timestamp,
    muokkaaja varchar(255),
    koodi varchar(255),
    kuvaus_id int8,
    nimi_id int8,
    primary key (id, REV)
);

create table lops2019_opintojakso_lops2019_opintojakson_moduuli (
    lops2019_opintojakso_id int8 not null,
    moduulit_id int8 not null,
    primary key (lops2019_opintojakso_id, moduulit_id)
);

create table lops2019_opintojakso_lops2019_opintojakson_moduuli_AUD (
    REV int4 not null,
    lops2019_opintojakso_id int8 not null,
    moduulit_id int8 not null,
    REVTYPE int2,
    REVEND int4,
    primary key (REV, lops2019_opintojakso_id, moduulit_id)
);

create table lops2019_opintojakson_moduuli (
    id int8 not null,
    luoja varchar(255),
    luotu timestamp,
    muokattu timestamp,
    muokkaaja varchar(255),
    koodiUri varchar(255),
    kuvaus_id int8,
    primary key (id)
);

create table lops2019_opintojakson_moduuli_AUD (
    id int8 not null,
    REV int4 not null,
    REVTYPE int2,
    REVEND int4,
    luoja varchar(255),
    luotu timestamp,
    muokattu timestamp,
    muokkaaja varchar(255),
    koodiUri varchar(255),
    kuvaus_id int8,
    primary key (id, REV)
);

alter table lops2019_opintojakso_lops2019_opintojakson_moduuli 
    add constraint UK_e99ydwt3onvbq0spp5n40kmvy  unique (moduulit_id);

alter table lops2019_opintojakso 
    add constraint FK_jea9ej8wtdoqpavevns3ugjh3 
    foreign key (kuvaus_id) 
    references lokalisoituteksti;

alter table lops2019_opintojakso 
    add constraint FK_ejci0tllfnwd1m4pkh07tx4r4 
    foreign key (nimi_id) 
    references lokalisoituteksti;

alter table lops2019_opintojakso_AUD 
    add constraint FK_999fpuses2khcpfm4hca6r9yr 
    foreign key (REV) 
    references revinfo;

alter table lops2019_opintojakso_AUD 
    add constraint FK_fknoui90dpulcsjcptvl6ismg 
    foreign key (REVEND) 
    references revinfo;

alter table lops2019_opintojakso_lops2019_opintojakson_moduuli 
    add constraint FK_e99ydwt3onvbq0spp5n40kmvy 
    foreign key (moduulit_id) 
    references lops2019_opintojakson_moduuli;

alter table lops2019_opintojakso_lops2019_opintojakson_moduuli 
    add constraint FK_i3ei4y7oh47ho9l8hs2fimrv1 
    foreign key (lops2019_opintojakso_id) 
    references lops2019_opintojakso;

alter table lops2019_opintojakso_lops2019_opintojakson_moduuli_AUD 
    add constraint FK_98qewgpynbaosduwmot9nxcxf 
    foreign key (REV) 
    references revinfo;

alter table lops2019_opintojakso_lops2019_opintojakson_moduuli_AUD 
    add constraint FK_thpitp120r6itqrij118dc2y4 
    foreign key (REVEND) 
    references revinfo;

alter table lops2019_opintojakson_moduuli 
    add constraint FK_k3gb9v97boi289q2jic2wynbw 
    foreign key (kuvaus_id) 
    references lokalisoituteksti;

alter table lops2019_opintojakson_moduuli_AUD 
    add constraint FK_bfrwo6m1q7151encvxwlrcnoh 
    foreign key (REV) 
    references revinfo;

alter table lops2019_opintojakson_moduuli_AUD 
    add constraint FK_8v8upo8467oabaff6s8f4myfi 
    foreign key (REVEND) 
    references revinfo;
