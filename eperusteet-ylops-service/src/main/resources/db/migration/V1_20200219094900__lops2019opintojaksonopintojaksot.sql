create table lops2019_opintojakso_opintojakso (
    opintojakso_id int8 not null,
    oj_opintojakso_id int8 not null,
    primary key (opintojakso_id, oj_opintojakso_id)
);

create table lops2019_opintojakso_opintojakso_AUD (
    REV int4 not null,
    opintojakso_id int8 not null,
    oj_opintojakso_id int8 not null,
    REVTYPE int2,
    REVEND int4,
    primary key (REV, opintojakso_id, oj_opintojakso_id)
);

alter table lops2019_opintojakso_opintojakso
    add constraint FK_dj87896wcet95jwkuci5l54g6
    foreign key (oj_opintojakso_id)
    references lops2019_opintojakso;

alter table lops2019_opintojakso_opintojakso
    add constraint FK_rs67pssg0w25vjb6mcchd1j0
    foreign key (opintojakso_id)
    references lops2019_opintojakso;

alter table lops2019_opintojakso_opintojakso_AUD
    add constraint FK_d7nuudiy90hcxgwy94bj6vdsw
    foreign key (REV)
    references revinfo;

alter table lops2019_opintojakso_opintojakso_AUD
    add constraint FK_1twi9odt3s0j5oam2n9fadowo
    foreign key (REVEND)
    references revinfo;
