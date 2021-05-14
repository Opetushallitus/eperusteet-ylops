alter table lops2019_oppiaine add column opiskeluymparistoTyotavat_id int8;

alter table lops2019_oppiaine_aud add column opiskeluymparistoTyotavat_id int8;

alter table lops2019_opintojakso add column opiskeluymparistoTyotavat_id int8;

alter table lops2019_opintojakso_aud add column opiskeluymparistoTyotavat_id int8;

create table lops2019_oppiaine_opiskeluymparisto_tyotavat (
    id int8 not null,
    luoja varchar(255),
    luotu timestamp,
    muokattu timestamp,
    muokkaaja varchar(255),
    kuvaus_id int8,
    primary key (id)
);

create table lops2019_oppiaine_opiskeluymparisto_tyotavat_AUD (
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
    add constraint FK_hp47m881288b263slda6dpihe
    foreign key (opiskeluymparistoTyotavat_id)
    references lops2019_oppiaine_opiskeluymparisto_tyotavat;

alter table lops2019_oppiaine_opiskeluymparisto_tyotavat
    add constraint FK_mvtnjvks4ja483mntvpe9obf9
    foreign key (kuvaus_id)
    references lokalisoituteksti;

alter table lops2019_oppiaine_opiskeluymparisto_tyotavat_AUD
    add constraint FK_s2jev8tuyd0mxe92jy3t9x4f6
    foreign key (REV)
    references revinfo;

alter table lops2019_oppiaine_opiskeluymparisto_tyotavat_AUD
    add constraint FK_47l3108caqca5tv4v1uv3ibwh
    foreign key (REVEND)
    references revinfo;

alter table lops2019_opintojakso
    add constraint FK_89vd7dvhy0ci0d0tb3c9y5e1r
    foreign key (opiskeluymparistoTyotavat_id)
    references lokalisoituteksti;
