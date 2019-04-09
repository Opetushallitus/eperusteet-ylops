create table lops2019_oppiaine (
    id int8 not null,
    luoja varchar(255),
    luotu timestamp,
    muokattu timestamp,
    muokkaaja varchar(255),
    koodi varchar(255),
    kuvaus_id int8,
    arviointi_id int8,
    laajaAlainenOsaaminen_id int8,
    nimi_id int8,
    sisalto_id int8 not null,
    tehtava_id int8,
    primary key (id)
);

create table lops2019_oppiaine_arviointi (
    id int8 not null,
    luoja varchar(255),
    luotu timestamp,
    muokattu timestamp,
    muokkaaja varchar(255),
    kuvaus_id int8,
    primary key (id)
);

create table lops2019_oppiaine_arviointi_AUD (
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
    kuvaus_id int8,
    arviointi_id int8,
    laajaAlainenOsaaminen_id int8,
    nimi_id int8,
    sisalto_id int8,
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

create table lops2019_oppiaine_tavoitealue (
    id int8 not null,
    luoja varchar(255),
    luotu timestamp,
    muokattu timestamp,
    muokkaaja varchar(255),
    kohde_id int8,
    nimi_id int8,
    primary key (id)
);

create table lops2019_oppiaine_tavoitealue_AUD (
    id int8 not null,
    REV int4 not null,
    REVTYPE int2,
    REVEND int4,
    luoja varchar(255),
    luotu timestamp,
    muokattu timestamp,
    muokkaaja varchar(255),
    kohde_id int8,
    nimi_id int8,
    primary key (id, REV)
);

create table lops2019_oppiaine_tavoitteet (
    id int8 not null,
    luoja varchar(255),
    luotu timestamp,
    muokattu timestamp,
    muokkaaja varchar(255),
    tavoitteet_id int8,
    kuvaus_id int8,
    primary key (id)
);

create table lops2019_oppiaine_tavoitteet_AUD (
    id int8 not null,
    REV int4 not null,
    tavoitteet_id int8,
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

create table lops2019_tavoitealueen_tavoitteet (
    lops2019_oppiaine_tavoitealue_id int8 not null,
    tavoitteet_id int8 not null
);

create table lops2019_tavoitealueen_tavoitteet_AUD (
    REV int4 not null,
    lops2019_oppiaine_tavoitealue_id int8 not null,
    tavoitteet_id int8 not null,
    REVTYPE int2,
    REVEND int4,
    primary key (REV, lops2019_oppiaine_tavoitealue_id, tavoitteet_id)
);

create table lops2019_tavoitteiden_tavoitealueet (
    lops2019_oppiaine_tavoitteet_id int8 not null,
    tavoitealueet_id int8 not null,
    primary key (lops2019_oppiaine_tavoitteet_id, tavoitealueet_id)
);

create table lops2019_tavoitteiden_tavoitealueet_AUD (
    REV int4 not null,
    lops2019_oppiaine_tavoitteet_id int8 not null,
    tavoitealueet_id int8 not null,
    REVTYPE int2,
    REVEND int4,
    primary key (REV, lops2019_oppiaine_tavoitteet_id, tavoitealueet_id)
);

create table lops2019_tavoitealue_tavoite (
    id int8 not null,
    luoja varchar(255),
    luotu timestamp,
    muokattu timestamp,
    muokkaaja varchar(255),
    tavoite_id int8,
    primary key (id)
);

create table lops2019_tavoitealue_tavoite_AUD (
    id int8 not null,
    REV int4 not null,
    REVTYPE int2,
    REVEND int4,
    luoja varchar(255),
    luotu timestamp,
    muokattu timestamp,
    muokkaaja varchar(255),
    tavoite_id int8,
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
    add constraint FK_e9x5smk6yj4a71hgcwkk4i4dc 
    foreign key (arviointi_id) 
    references lops2019_oppiaine_arviointi;

alter table lops2019_oppiaine 
    add constraint FK_kag2d2imrik451a2qgq8r8vdm 
    foreign key (sisalto_id) 
    references lops2019_sisalto;

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

alter table lops2019_oppiaine_tavoitealue 
    add constraint FK_d220h3in0svjwpsrqbc16d2a6 
    foreign key (kohde_id) 
    references lokalisoituteksti;

alter table lops2019_oppiaine_tavoitteet 
    add constraint FK_cgyfj2u0dr3jwq2jgysqcxrt2 
    foreign key (tavoitteet_id) 
    references lops2019_oppiaine_tavoitteet;

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

alter table lops2019_tavoitealueen_tavoitteet 
    add constraint UK_lf6oen4b7r3l06kcykq05wys0  unique (tavoitteet_id);

alter table lops2019_tavoitteiden_tavoitealueet 
    add constraint UK_emma6qrtieo3hbf94x1yq5jtr  unique (tavoitealueet_id);

alter table lops2019_oppiaine_tavoitealue 
    add constraint FK_bwct7etqn2tx9t17r01u85j80 
    foreign key (nimi_id) 
    references lokalisoituteksti;

alter table lops2019_oppiaine_tavoitealue_AUD 
    add constraint FK_4wqfc5g1lf1udtjf0jrksht7r 
    foreign key (REV) 
    references revinfo;

alter table lops2019_oppiaine_tavoitealue_AUD 
    add constraint FK_2hr2tn42uncf7fxm685olxncb 
    foreign key (REVEND) 
    references revinfo;

alter table lops2019_tavoitealue_tavoite 
    add constraint FK_d33baj9eim0h87ghgtka7cm7y 
    foreign key (tavoite_id) 
    references lokalisoituteksti;

alter table lops2019_tavoitealue_tavoite_AUD 
    add constraint FK_bod3xn2w6p310stxpof5k00cu 
    foreign key (REV) 
    references revinfo;

alter table lops2019_tavoitealue_tavoite_AUD 
    add constraint FK_libniine6s04w191yv0tibtqx 
    foreign key (REVEND) 
    references revinfo;

alter table lops2019_tavoitealueen_tavoitteet 
    add constraint FK_lf6oen4b7r3l06kcykq05wys0 
    foreign key (tavoitteet_id) 
    references lops2019_tavoitealue_tavoite;

alter table lops2019_tavoitealueen_tavoitteet 
    add constraint FK_cpp5elkj721hfhwq0wdckit48 
    foreign key (lops2019_oppiaine_tavoitealue_id) 
    references lops2019_oppiaine_tavoitealue;

alter table lops2019_tavoitealueen_tavoitteet_AUD 
    add constraint FK_48djkormyyc6tgvx6wnoqf1g2 
    foreign key (REV) 
    references revinfo;

alter table lops2019_tavoitealueen_tavoitteet_AUD 
    add constraint FK_igilppytalihc9wmg3mbbd2vd 
    foreign key (REVEND) 
    references revinfo;

alter table lops2019_tavoitteiden_tavoitealueet 
    add constraint FK_emma6qrtieo3hbf94x1yq5jtr 
    foreign key (tavoitealueet_id) 
    references lops2019_oppiaine_tavoitealue;

alter table lops2019_tavoitteiden_tavoitealueet 
    add constraint FK_7ihl13vsf459dv477c1ymf4ph 
    foreign key (lops2019_oppiaine_tavoitteet_id) 
    references lops2019_oppiaine_tavoitteet;

alter table lops2019_tavoitteiden_tavoitealueet_AUD 
    add constraint FK_djkolgicsy1qrjuxwdf011xpi 
    foreign key (REV) 
    references revinfo;

alter table lops2019_tavoitteiden_tavoitealueet_AUD 
    add constraint FK_q46ggtnonve72yx1vr1cokiu9 
    foreign key (REVEND) 
    references revinfo;

alter table lops2019_oppiaine_arviointi 
    add constraint FK_b0hy2h07egydrvro6musfyuor 
    foreign key (kuvaus_id) 
    references lokalisoituteksti;

alter table lops2019_oppiaine_arviointi_AUD 
    add constraint FK_2iyv6hht0omg0sonas39km03m 
    foreign key (REV) 
    references revinfo;

alter table lops2019_oppiaine_arviointi_AUD 
    add constraint FK_9racq4sicclb32qvvrnp8k6sn 
    foreign key (REVEND) 
    references revinfo;

