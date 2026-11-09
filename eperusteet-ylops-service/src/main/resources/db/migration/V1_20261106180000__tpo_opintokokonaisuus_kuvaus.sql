alter table tpo_opintokokonaisuus
    add column kuvaus_id int8;

alter table tpo_opintokokonaisuus
    add constraint fk_tpo_opintokokonaisuus_kuvaus foreign key (kuvaus_id) references lokalisoituteksti;

alter table tpo_opintokokonaisuus_aud
    add column kuvaus_id int8;
