create table oppiaine_vapaatekstit (
                                       oppiaine_id int8 not null,
                                       vapaateksti_paikallinentarkennus_id int8 not null
);

create table oppiaine_vapaatekstit_AUD (
                                           REV int4 not null,
                                           oppiaine_id int8 not null,
                                           vapaateksti_paikallinentarkennus_id int8 not null,
                                           REVTYPE int2,
                                           REVEND int4,
                                           primary key (REV, oppiaine_id, vapaateksti_paikallinentarkennus_id)
);

create table vapaateksti_paikallinentarkennus (
                                                  id int8 not null,
                                                  perusteenVapaaTekstiId int8 not null,
                                                  paikallinenTarkennus_id int8,
                                                  primary key (id)
);

create table vapaateksti_paikallinentarkennus_AUD (
                                                      id int8 not null,
                                                      REV int4 not null,
                                                      REVTYPE int2,
                                                      REVEND int4,
                                                      perusteenVapaaTekstiId int8,
                                                      paikallinenTarkennus_id int8,
                                                      primary key (id, REV)
);

alter table oppiaine_vapaatekstit
    add constraint FK_1gqumd9dr3wm6esk3uubh3d82
        foreign key (vapaateksti_paikallinentarkennus_id)
            references vapaateksti_paikallinentarkennus;

alter table oppiaine_vapaatekstit
    add constraint FK_3o4p2t4gevcav17a8cuviwiwy
        foreign key (oppiaine_id)
            references oppiaine;

alter table oppiaine_vapaatekstit_AUD
    add constraint FK_6f6ej65dpkxgngfvleh8i7qb9
        foreign key (REV)
            references revinfo;

alter table oppiaine_vapaatekstit_AUD
    add constraint FK_tnoxwebpphvxn4q9bve4v0t8s
        foreign key (REVEND)
            references revinfo;
