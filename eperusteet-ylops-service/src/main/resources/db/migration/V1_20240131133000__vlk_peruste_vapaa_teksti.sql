create table vlk_vapaatekstit (
                                  vlk_id int8 not null,
                                  vapaateksti_paikallinentarkennus_id int8 not null
);

create table vlk_vapaatekstit_AUD (
                                      REV int4 not null,
                                      vlk_id int8 not null,
                                      vapaateksti_paikallinentarkennus_id int8 not null,
                                      REVTYPE int2,
                                      REVEND int4,
                                      primary key (REV, vlk_id, vapaateksti_paikallinentarkennus_id)
);

alter table vlk_vapaatekstit
    add constraint FK_am44amfcix52k05pk8bkdr6jp
        foreign key (vapaateksti_paikallinentarkennus_id)
            references vapaateksti_paikallinentarkennus;

alter table vlk_vapaatekstit
    add constraint FK_pw99s59n99m2n62rtsjonmysb
        foreign key (vlk_id)
            references vlkokonaisuus;

alter table vlk_vapaatekstit_AUD
    add constraint FK_pwv0qj9pywro6b2c1dfmymoe7
        foreign key (REV)
            references revinfo;

alter table vlk_vapaatekstit_AUD
    add constraint FK_oulf29q8tldnp3d6wkd981c90
        foreign key (REVEND)
            references revinfo;
