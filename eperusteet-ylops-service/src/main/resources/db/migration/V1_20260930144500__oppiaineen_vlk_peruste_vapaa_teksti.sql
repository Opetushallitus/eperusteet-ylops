create table oppiaineen_vlkok_vapaatekstit (
                                               oppiaineen_vlkok_id int8 not null,
                                               vapaateksti_paikallinentarkennus_id int8 not null
);

create table oppiaineen_vlkok_vapaatekstit_AUD (
                                                   REV int4 not null,
                                                   oppiaineen_vlkok_id int8 not null,
                                                   vapaateksti_paikallinentarkennus_id int8 not null,
                                                   REVTYPE int2,
                                                   REVEND int4,
                                                   primary key (REV, oppiaineen_vlkok_id, vapaateksti_paikallinentarkennus_id)
);

alter table oppiaineen_vlkok_vapaatekstit
    add constraint FK_oavlk_vapaatekstit_tarkennus
        foreign key (vapaateksti_paikallinentarkennus_id)
            references vapaateksti_paikallinentarkennus;

alter table oppiaineen_vlkok_vapaatekstit
    add constraint FK_oavlk_vapaatekstit_oavlk
        foreign key (oppiaineen_vlkok_id)
            references oppiaineen_vlkok;

alter table oppiaineen_vlkok_vapaatekstit_AUD
    add constraint FK_oavlk_vapaatekstit_aud_rev
        foreign key (REV)
            references revinfo;

alter table oppiaineen_vlkok_vapaatekstit_AUD
    add constraint FK_oavlk_vapaatekstit_aud_revend
        foreign key (REVEND)
            references revinfo;
