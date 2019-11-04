ALTER TABLE lops2019_oppiaine DROP COLUMN laajaalainenosaaminen_id;
ALTER TABLE lops2019_oppiaine_aud DROP COLUMN laajaalainenosaaminen_id;

create table lops2019_oppiaine_paikallinen_laaja_alainen_osaaminen (
    lops2019_oppiaine_id int8 not null,
    laajaAlainenOsaaminen_id int8 not null,
    laajaAlainenOsaaminen_ORDER int4 not null,
    primary key (lops2019_oppiaine_id, laajaAlainenOsaaminen_ORDER)
);

create table lops2019_oppiaine_paikallinen_laaja_alainen_osaaminen_AUD (
    REV int4 not null,
    lops2019_oppiaine_id int8 not null,
    laajaAlainenOsaaminen_id int8 not null,
    laajaAlainenOsaaminen_ORDER int4 not null,
    REVTYPE int2,
    REVEND int4,
    primary key (REV, lops2019_oppiaine_id, laajaAlainenOsaaminen_id, laajaAlainenOsaaminen_ORDER)
);

create table paikallinen_laaja_alainen_osaaminen (
    id int8 not null,
    luoja varchar(255),
    luotu timestamp,
    muokattu timestamp,
    muokkaaja varchar(255),
    koodi varchar(255) not null,
    kuvaus_id int8,
    primary key (id)
);

create table paikallinen_laaja_alainen_osaaminen_AUD (
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
    primary key (id, REV)
);

alter table paikallinen_laaja_alainen_osaaminen
    add constraint FK_fnhfct7ffbx77ndeeem7xkrja
    foreign key (kuvaus_id)
    references lokalisoituteksti;

alter table paikallinen_laaja_alainen_osaaminen_AUD
    add constraint FK_inpn7t09423t5wqndly8g7yqp
    foreign key (REV)
    references revinfo;

alter table paikallinen_laaja_alainen_osaaminen_AUD
    add constraint FK_3elj65q3wj4vgbpjnmun5f0fa
    foreign key (REVEND)
    references revinfo;

alter table lops2019_oppiaine_paikallinen_laaja_alainen_osaaminen
    add constraint UK_bf1c6vhmf82kd8c9883vmsyr0  unique (laajaAlainenOsaaminen_id);

alter table lops2019_oppiaine_paikallinen_laaja_alainen_osaaminen
    add constraint FK_bf1c6vhmf82kd8c9883vmsyr0
    foreign key (laajaAlainenOsaaminen_id)
    references paikallinen_laaja_alainen_osaaminen;

alter table lops2019_oppiaine_paikallinen_laaja_alainen_osaaminen
    add constraint FK_ox6v4vqnpnjjg0j2msqy7jsww
    foreign key (lops2019_oppiaine_id)
    references lops2019_oppiaine;

alter table lops2019_oppiaine_paikallinen_laaja_alainen_osaaminen_AUD
    add constraint FK_ie3k78j22nrha6ydyu52c6im5
    foreign key (REV)
    references revinfo;

alter table lops2019_oppiaine_paikallinen_laaja_alainen_osaaminen_AUD
    add constraint FK_9rk93mmw4mrq34v7kymtd00ux
    foreign key (REVEND)
    references revinfo;