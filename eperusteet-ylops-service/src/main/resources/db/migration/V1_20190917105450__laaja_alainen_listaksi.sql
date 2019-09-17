drop table if exists lops2019_oppiaine_laajaalainenosaaminen cascade;
drop table if exists lops2019_oppiaine_laajaalainenosaaminen_aud cascade;

create table lops2019_oppiaine_laajaalainenosaaminen (
    id int8 not null,
    luoja varchar(255),
    luotu timestamp,
    muokattu timestamp,
    muokkaaja varchar(255),
    koodi varchar(255),
    kuvaus_id int8,
    primary key (id)
);

create table lops2019_oppiaine_laajaalainenosaaminen_AUD (
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

create table lops2019_opintojakso_laajaalainen (
    opintojakso_id int8 not null,
    laaja_alainen_id int8 not null,
    laajaAlainenOsaaminen_ORDER int4 not null,
    primary key (opintojakso_id, laajaAlainenOsaaminen_ORDER)
);

create table lops2019_opintojakso_laajaalainen_AUD (
    REV int4 not null,
    opintojakso_id int8 not null,
    laaja_alainen_id int8 not null,
    laajaAlainenOsaaminen_ORDER int4 not null,
    REVTYPE int2,
    REVEND int4,
    primary key (REV, opintojakso_id, laaja_alainen_id, laajaAlainenOsaaminen_ORDER)
);

alter table lops2019_opintojakso_laajaalainen 
    add constraint UK_s128npy1jhcqcy8tin99vx2ey  unique (laaja_alainen_id);

alter table lops2019_opintojakso_laajaalainen 
    add constraint FK_s128npy1jhcqcy8tin99vx2ey 
    foreign key (laaja_alainen_id) 
    references lops2019_oppiaine_laajaalainenosaaminen;

alter table lops2019_opintojakso_laajaalainen 
    add constraint FK_mqnktbfdfy91ka0ehw05h406k 
    foreign key (opintojakso_id) 
    references lops2019_opintojakso;

alter table lops2019_opintojakso_laajaalainen_AUD 
    add constraint FK_o0yyab3tmgv92rk4mevewhya4 
    foreign key (REV) 
    references revinfo;

alter table lops2019_opintojakso_laajaalainen_AUD 
    add constraint FK_m2ygfm31wn70pcfkwfg8lc2ev 
    foreign key (REVEND) 
    references revinfo;

alter table lops2019_oppiaine 
    add constraint FK_17k2weuqtfbnpeaa6uk8m5r8b 
    foreign key (laajaAlainenOsaaminen_id) 
    references lops2019_oppiaine_laajaalainenosaaminen;

alter table lops2019_oppiaine_laajaalainenosaaminen 
    add constraint FK_575kvr0uq9jv8b3lxo75344gu 
    foreign key (kuvaus_id) 
    references lokalisoituteksti;

alter table lops2019_oppiaine_laajaalainenosaaminen_AUD 
    add constraint FK_e9j4red9cu3ku9xs99mrbua18 
    foreign key (REV) 
    references revinfo;

alter table lops2019_oppiaine_laajaalainenosaaminen_AUD 
    add constraint FK_e486w9t45lgftuglvsiaphub8 
    foreign key (REVEND) 
    references revinfo;

