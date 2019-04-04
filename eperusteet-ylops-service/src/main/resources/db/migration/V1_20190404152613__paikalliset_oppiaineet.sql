create table lops2019_oppiaine (
    id int8 not null,
    luoja varchar(255),
    luotu timestamp,
    muokattu timestamp,
    muokkaaja varchar(255),
    koodi varchar(255),
    sisalto bytea not null,
    kuvaus_id int8,
    laajaAlainenOsaaminen_id int8,
    nimi_id int8,
    tavoitteet_id int8,
    tehtava_id int8,
    primary key (id)
);

create table lops2019_oppiaine_AUD (
    id int8 not null,
    REV int4 not null,
    REVTYPE int2,
    REVEND int4,
    luoja varchar(255),
    luotu timestamp,
    muokattu timestamp,
    muokkaaja varchar(255),
    koodi varchar(255),
    sisalto bytea,
    kuvaus_id int8,
    laajaAlainenOsaaminen_id int8,
    nimi_id int8,
    tavoitteet_id int8,
    tehtava_id int8,
    primary key (id, REV)
);

create table lops2019_oppiaine_laajaalainenosaaminen (
    id int8 not null,
    luoja varchar(255),
    luotu timestamp,
    muokattu timestamp,
    muokkaaja varchar(255),
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
    kuvaus_id int8,
    primary key (id, REV)
);

create table lops2019_oppiaine_tavoitteet (
    id int8 not null,
    luoja varchar(255),
    luotu timestamp,
    muokattu timestamp,
    muokkaaja varchar(255),
    kuvaus_id int8,
    primary key (id)
);

create table lops2019_oppiaine_tavoitteet_AUD (
    id int8 not null,
    REV int4 not null,
    REVTYPE int2,
    REVEND int4,
    luoja varchar(255),
    luotu timestamp,
    muokattu timestamp,
    muokkaaja varchar(255),
    kuvaus_id int8,
    primary key (id, REV)
);

create table lops2019_oppiaine_tehtava (
    id int8 not null,
    luoja varchar(255),
    luotu timestamp,
    muokattu timestamp,
    muokkaaja varchar(255),
    kuvaus_id int8,
    primary key (id)
);

create table lops2019_oppiaine_tehtava_AUD (
    id int8 not null,
    REV int4 not null,
    REVTYPE int2,
    REVEND int4,
    luoja varchar(255),
    luotu timestamp,
    muokattu timestamp,
    muokkaaja varchar(255),
    kuvaus_id int8,
    primary key (id, REV)
);

alter table lops2019_oppiaine 
    add constraint FK_diapowdmdpsv2s031yi42qddk 
    foreign key (kuvaus_id) 
    references lokalisoituteksti;

alter table lops2019_oppiaine 
    add constraint FK_17k2weuqtfbnpeaa6uk8m5r8b 
    foreign key (laajaAlainenOsaaminen_id) 
    references lops2019_oppiaine_laajaalainenosaaminen;

alter table lops2019_oppiaine 
    add constraint FK_q6ti4ax9fys8wo6hamlkhlhni 
    foreign key (nimi_id) 
    references lokalisoituteksti;

alter table lops2019_oppiaine 
    add constraint FK_ehf86hdk864w03xp70vx8ufim 
    foreign key (tavoitteet_id) 
    references lops2019_oppiaine_tavoitteet;

alter table lops2019_oppiaine 
    add constraint FK_m4ese3r90o39ixsxw3ysc401r 
    foreign key (tehtava_id) 
    references lops2019_oppiaine_tehtava;

alter table lops2019_oppiaine_AUD 
    add constraint FK_rllop6uvc1s4rel3fg6m0gwv2 
    foreign key (REV) 
    references revinfo;

alter table lops2019_oppiaine_AUD 
    add constraint FK_mi3wgahy9pnmil2ccnd15boiv 
    foreign key (REVEND) 
    references revinfo;

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

alter table lops2019_oppiaine_tavoitteet 
    add constraint FK_rjj5bhd7kqt59l6cvg5u807cq 
    foreign key (kuvaus_id) 
    references lokalisoituteksti;

alter table lops2019_oppiaine_tavoitteet_AUD 
    add constraint FK_2sry57xm44g15o39kq51mcd87 
    foreign key (REV) 
    references revinfo;

alter table lops2019_oppiaine_tavoitteet_AUD 
    add constraint FK_q51ao0t04o11fyew3940e9xbv 
    foreign key (REVEND) 
    references revinfo;

alter table lops2019_oppiaine_tehtava 
    add constraint FK_86kcwuvwutbrlobfcnmj6em6k 
    foreign key (kuvaus_id) 
    references lokalisoituteksti;

alter table lops2019_oppiaine_tehtava_AUD 
    add constraint FK_3lifysurfbdawc37lqef77ux1 
    foreign key (REV) 
    references revinfo;

alter table lops2019_oppiaine_tehtava_AUD 
    add constraint FK_1lwyg8d4lrx67ia7lr98cgivx 
    foreign key (REVEND) 
    references revinfo;

