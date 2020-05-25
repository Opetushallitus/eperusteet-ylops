alter table oppiaine
    add column valinnainen_tyyppi varchar(255),
    add column liittyva_oppiaine_id int8;
alter table oppiaine_aud
    add column valinnainen_tyyppi varchar(255),
    add column liittyva_oppiaine_id int8;

update oppiaine set valinnainen_tyyppi = 'EI_MAARITETTY';

alter table oppiaine alter column valinnainen_tyyppi set not null;

alter table oppiaine
    add constraint fk_oppiaine_liittyva_oppiaine foreign key (liittyva_oppiaine_id) references oppiaine;