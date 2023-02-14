alter table ops_vuosiluokkakokonaisuus add column lisatieto_id int8;
alter table ops_vuosiluokkakokonaisuus_aud add column lisatieto_id int8;

create table ops_vuosiluokkakokonaisuus_lisatieto (
    id int8 not null,
    primary key (id)
);

create table ops_vuosiluokkakokonaisuus_lisatieto_piilotetut_oppiaineet (
    OpsVuosiluokkakokonaisuusLisatieto_id int8 not null,
    piilotettu_oppiaine_id int8
);

alter table ops_vuosiluokkakokonaisuus
    add constraint FK_m1kxlgt5mt8ji7qig58sly2vk
    foreign key (lisatieto_id)
    references ops_vuosiluokkakokonaisuus_lisatieto;

alter table ops_vuosiluokkakokonaisuus_lisatieto_piilotetut_oppiaineet
    add constraint FK_4p2a3ytob10tbimw43xf7llug
    foreign key (OpsVuosiluokkakokonaisuusLisatieto_id)
    references ops_vuosiluokkakokonaisuus_lisatieto;
