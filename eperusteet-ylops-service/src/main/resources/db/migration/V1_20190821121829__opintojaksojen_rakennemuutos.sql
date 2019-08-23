create table lops2019_opintojakso_lokalisoituteksti (
    Lops2019Opintojakso_id int8 not null,
    tavoitteet_id int8 not null,
    tavoitteet_ORDER int4 not null,
    keskeisetSisallot_id int8 not null,
    keskeisetSisallot_ORDER int4 not null,
    primary key (Lops2019Opintojakso_id, keskeisetSisallot_ORDER)
);

create table lops2019_opintojakso_lokalisoituteksti_AUD (
    REV int4 not null,
    Lops2019Opintojakso_id int8 not null,
    keskeisetSisallot_id int8 not null,
    keskeisetSisallot_ORDER int4 not null,
    REVTYPE int2,
    REVEND int4,
    tavoitteet_id int8 not null,
    tavoitteet_ORDER int4 not null,
    primary key (REV, Lops2019Opintojakso_id, tavoitteet_id, tavoitteet_ORDER)
);

alter table lops2019_opintojakso_lokalisoituteksti 
    add constraint UK_1ai9pe9dgypk1ws418bowtrmj  unique (tavoitteet_id);

alter table lops2019_opintojakso_lokalisoituteksti 
    add constraint UK_4yw1h4usjijwv2mrfye61b5y0  unique (keskeisetSisallot_id);

alter table lops2019_opintojakso_lokalisoituteksti 
    add constraint FK_1ai9pe9dgypk1ws418bowtrmj 
    foreign key (tavoitteet_id) 
    references lokalisoituteksti;

alter table lops2019_opintojakso_lokalisoituteksti 
    add constraint FK_7ferg2ms00no60kim0rovxush 
    foreign key (Lops2019Opintojakso_id) 
    references lops2019_opintojakso;

alter table lops2019_opintojakso_lokalisoituteksti 
    add constraint FK_4yw1h4usjijwv2mrfye61b5y0 
    foreign key (keskeisetSisallot_id) 
    references lokalisoituteksti;

alter table lops2019_opintojakso_lokalisoituteksti_AUD 
    add constraint FK_73b5lfamnrc96sdwjya0r94q0 
    foreign key (REV) 
    references revinfo;

alter table lops2019_opintojakso_lokalisoituteksti_AUD 
    add constraint FK_e6ugyjgtbjdw6hkij5205xpan 
    foreign key (REVEND) 
    references revinfo;

