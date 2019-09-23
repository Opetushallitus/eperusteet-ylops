drop table if exists kysymys cascade;
drop table if exists kysymys_aud cascade;
drop table if exists kysymys_organisaatiot cascade;
drop table if exists kysymys_organisaatiot_aud cascade;

create table kysymys (
    id int8 not null,
    luoja varchar(255),
    luotu timestamp,
    muokattu timestamp,
    muokkaaja varchar(255),
    kysymys_id int8,
    vastaus_id int8,
    primary key (id)
);

create table kysymys_aud (
    id int8 not null,
    rev int4 not null,
    revtype int2,
    revend int4,
    luoja varchar(255),
    luotu timestamp,
    muokattu timestamp,
    muokkaaja varchar(255),
    kysymys_id int8,
    vastaus_id int8,
    primary key (id, rev)
);

create table kysymys_organisaatiot (
    kysymys_id int8 not null,
    organisaatiot varchar(255)
);

create table kysymys_organisaatiot_aud (
    rev int4 not null,
    kysymys_id int8 not null,
    organisaatiot varchar(255) not null,
    revtype int2,
    revend int4,
    primary key (rev, kysymys_id, organisaatiot)
);