create table lops2019_sisalto_piilotettu_opintojakso (
    sisalto_id int8 not null,
    opintojakso_id int8 not null,
    primary key (sisalto_id, opintojakso_id)
);

create table lops2019_sisalto_piilotettu_opintojakso_AUD (
    REV int4 not null,
    sisalto_id int8 not null,
    opintojakso_id int8 not null,
    REVTYPE int2,
    REVEND int4,
    primary key (REV, sisalto_id, opintojakso_id)
);

create table lops2019_sisalto_tuotu_opintojakso (
    sisalto_id int8 not null,
    opintojakso_id int8 not null,
    primary key (sisalto_id, opintojakso_id)
);

create table lops2019_sisalto_tuotu_opintojakso_AUD (
    REV int4 not null,
    sisalto_id int8 not null,
    opintojakso_id int8 not null,
    REVTYPE int2,
    REVEND int4,
    primary key (REV, sisalto_id, opintojakso_id)
);

alter table lops2019_sisalto_piilotettu_opintojakso
    add constraint FK_qncl3fehcx69u0lt3pcc7uyms
    foreign key (opintojakso_id)
    references lops2019_opintojakso;

alter table lops2019_sisalto_piilotettu_opintojakso
    add constraint FK_39s8lroaafw7dg3eefey5xj4a
    foreign key (sisalto_id)
    references lops2019_sisalto;

alter table lops2019_sisalto_piilotettu_opintojakso_AUD
    add constraint FK_1jcbtv52xhs3o1f27tk1gqrv2
    foreign key (REV)
    references revinfo;

alter table lops2019_sisalto_piilotettu_opintojakso_AUD
    add constraint FK_poks9s0up59mytp836kp43i7d
    foreign key (REVEND)
    references revinfo;

alter table lops2019_sisalto_tuotu_opintojakso
    add constraint FK_2pm9e6oeoqkcfphk37he7jrs
    foreign key (opintojakso_id)
    references lops2019_opintojakso;

alter table lops2019_sisalto_tuotu_opintojakso
    add constraint FK_21bk42vt754ivao51ca9qbecw
    foreign key (sisalto_id)
    references lops2019_sisalto;

alter table lops2019_sisalto_tuotu_opintojakso_AUD
    add constraint FK_obayoslhmmrspkga8f7t3w9ak
    foreign key (REV)
    references revinfo;

alter table lops2019_sisalto_tuotu_opintojakso_AUD
    add constraint FK_6f0dru360wces4q2yn1ojbc29
    foreign key (REVEND)
    references revinfo;
