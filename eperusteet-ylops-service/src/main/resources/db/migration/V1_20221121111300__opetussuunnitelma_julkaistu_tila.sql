create table julkaistu_opetussuunnitelma_tila (
    ops_id int8 not null,
    julkaisu_tila varchar(255) not null,
    muokattu timestamp not null,
    primary key (ops_id)
);