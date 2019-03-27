alter table lops2019_opintojakso drop column oppiaineUri;
alter table lops2019_opintojakso_aud drop column oppiaineUri;

create table Lops2019Opintojakso_oppiaineet (
    Lops2019Opintojakso_id int8 not null,
    oppiaineet varchar(255)
);

create table Lops2019Opintojakso_oppiaineet_AUD (
    REV int4 not null,
    Lops2019Opintojakso_id int8 not null,
    oppiaineet varchar(255) not null,
    REVTYPE int2,
    REVEND int4,
    primary key (REV, Lops2019Opintojakso_id, oppiaineet)
);

alter table Lops2019Opintojakso_oppiaineet 
    add constraint FK_7v3hn0l4wdlcvjsrru7o37vhv 
    foreign key (Lops2019Opintojakso_id) 
    references lops2019_opintojakso;

alter table Lops2019Opintojakso_oppiaineet_AUD 
    add constraint FK_hg9ht22p1d1e7swc5f49qksuh 
    foreign key (REV) 
    references revinfo;

alter table Lops2019Opintojakso_oppiaineet_AUD 
    add constraint FK_ftxi1lxbc1d8r1omjoufs28i5 
    foreign key (REVEND) 
    references revinfo;

