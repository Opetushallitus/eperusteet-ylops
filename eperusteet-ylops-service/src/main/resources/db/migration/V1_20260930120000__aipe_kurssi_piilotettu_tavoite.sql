create table aipe_kurssi_piilotettu_tavoite (
    kurssi_id int8 not null,
    tavoite_id int8
);

create table aipe_kurssi_piilotettu_tavoite_AUD (
    REV int4 not null,
    kurssi_id int8 not null,
    tavoite_id int8,
    REVTYPE int2,
    REVEND int4,
    primary key (REV, kurssi_id, tavoite_id)
);

alter table aipe_kurssi_piilotettu_tavoite
    add constraint FK_aipe_piilotettu_tavoite_kurssi foreign key (kurssi_id) references aipe_kurssi;

drop table aipe_oppiaine_piilotettu_tavoite_AUD;
drop table aipe_oppiaine_piilotettu_tavoite;
