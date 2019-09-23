create table lops2019_opintojakson_keskeinensisalto (
    id int8 not null,
    luoja varchar(255),
    luotu timestamp,
    muokattu timestamp,
    muokkaaja varchar(255),
    kuvaus_id int8,
    primary key (id)
);

create table lops2019_opintojakson_keskeinensisalto_AUD (
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

create table lops2019_opintojakson_tavoite (
    id int8 not null,
    luoja varchar(255),
    luotu timestamp,
    muokattu timestamp,
    muokkaaja varchar(255),
    kuvaus_id int8,
    primary key (id)
);

create table lops2019_opintojakson_tavoite_AUD (
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

create table lops2019_opintojakso_sisalto (
    opintojakso_id int8 not null,
    keskeinen_sisalto_id int8 not null,
    keskeisetSisallot_ORDER int4 not null,
    primary key (opintojakso_id, keskeisetSisallot_ORDER)
);

create table lops2019_opintojakso_sisalto_AUD (
    REV int4 not null,
    opintojakso_id int8 not null,
    keskeinen_sisalto_id int8 not null,
    keskeisetSisallot_ORDER int4 not null,
    REVTYPE int2,
    REVEND int4,
    primary key (REV, opintojakso_id, keskeinen_sisalto_id, keskeisetSisallot_ORDER)
);


create table lops2019_opintojakso_tavoite (
    opintojakso_id int8 not null,
    tavoite_id int8 not null,
    tavoitteet_ORDER int4 not null,
    primary key (opintojakso_id, tavoitteet_ORDER)
);

create table lops2019_opintojakso_tavoite_AUD (
    REV int4 not null,
    opintojakso_id int8 not null,
    tavoite_id int8 not null,
    tavoitteet_ORDER int4 not null,
    REVTYPE int2,
    REVEND int4,
    primary key (REV, opintojakso_id, tavoite_id, tavoitteet_ORDER)
);

alter table lops2019_opintojakso_tavoite 
    add constraint UK_a3qrtlkla1h8w9ubqatwvrtwb  unique (tavoite_id);

alter table lops2019_opintojakso_tavoite 
    add constraint FK_a3qrtlkla1h8w9ubqatwvrtwb 
    foreign key (tavoite_id) 
    references lops2019_opintojakson_tavoite;

alter table lops2019_opintojakso_tavoite 
    add constraint FK_s0qp1fv806b2d2yn8glud60wx 
    foreign key (opintojakso_id) 
    references lops2019_opintojakso;

alter table lops2019_opintojakso_tavoite_AUD 
    add constraint FK_27tu0t80rweu4fxomgaihdk5f 
    foreign key (REV) 
    references revinfo;

alter table lops2019_opintojakso_tavoite_AUD 
    add constraint FK_3t23qkyq86ejpmpgyvlp5onx5 
    foreign key (REVEND) 
    references revinfo;

alter table lops2019_opintojakso_sisalto 
    add constraint UK_nfdhfx7xurtf95xf6gw00pk89  unique (keskeinen_sisalto_id);

alter table lops2019_opintojakso_sisalto 
    add constraint FK_nfdhfx7xurtf95xf6gw00pk89 
    foreign key (keskeinen_sisalto_id) 
    references lops2019_opintojakson_keskeinensisalto;

alter table lops2019_opintojakso_sisalto 
    add constraint FK_5o7b80hy0grw6nm052vlvbae7 
    foreign key (opintojakso_id) 
    references lops2019_opintojakso;

alter table lops2019_opintojakso_sisalto_AUD 
    add constraint FK_fd3or0tqx4pvigmlr1supljcd 
    foreign key (REV) 
    references revinfo;

alter table lops2019_opintojakso_sisalto_AUD 
    add constraint FK_e26ov8bmfpg695amg966fvsxn 
    foreign key (REVEND) 
    references revinfo;

alter table lops2019_opintojakson_keskeinensisalto 
    add constraint FK_8mc5i2w83idm6uavuer68fs6 
    foreign key (kuvaus_id) 
    references lokalisoituteksti;

alter table lops2019_opintojakson_keskeinensisalto_AUD 
    add constraint FK_bhld7r9jwoxnsyf0pjx2lcs49 
    foreign key (REV) 
    references revinfo;

alter table lops2019_opintojakson_keskeinensisalto_AUD 
    add constraint FK_8x44xpajxn18anfx7v2w370us 
    foreign key (REVEND) 
    references revinfo;

alter table lops2019_opintojakson_tavoite 
    add constraint FK_1qj76c9cisgx2pcopo9qbvl34 
    foreign key (kuvaus_id) 
    references lokalisoituteksti;

alter table lops2019_opintojakson_tavoite_AUD 
    add constraint FK_bilgt17eld19fddrh3kibq2pu 
    foreign key (REV) 
    references revinfo;

alter table lops2019_opintojakson_tavoite_AUD 
    add constraint FK_lcv8q9yikeh3qmycuy9y531uq 
    foreign key (REVEND) 
    references revinfo;
