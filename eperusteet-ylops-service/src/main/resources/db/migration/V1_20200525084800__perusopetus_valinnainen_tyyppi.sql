alter table oppiaine
    add column valinnainen_tyyppi varchar(255) not null default 'EI_MAARITELTY',
    add column liittyva_oppiaine_id int8;
alter table oppiaine_aud
    add column valinnainen_tyyppi varchar(255),
    add column liittyva_oppiaine_id int8;

alter table oppiaine
    add constraint fk_oppiaine_liittyva_oppiaine foreign key (liittyva_oppiaine_id) references oppiaine;