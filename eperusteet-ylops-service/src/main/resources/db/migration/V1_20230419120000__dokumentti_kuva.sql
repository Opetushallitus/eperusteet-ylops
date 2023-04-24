create table dokumentti_kuva (
                          id int8 not null,
                          ops_id int8 not null,
                          kieli varchar(255) not null,
                          kansikuva OID,
                          ylatunniste OID,
                          alatunniste OID,
                          primary key (id)
);

alter table dokumentti_kuva
    add constraint FK_dokumentti_kuva_opetussuunnitelma
        foreign key (ops_id)
            references opetussuunnitelma;

INSERT INTO dokumentti_kuva (id, ops_id, kieli, kansikuva, ylatunniste, alatunniste)
SELECT NEXTVAL('hibernate_sequence'), ops_id, kieli, kansikuva, ylatunniste, alatunniste
FROM dokumentti WHERE kansikuva IS NOT NULL OR ylatunniste IS NOT NULL OR alatunniste IS NOT NULL;

ALTER TABLE dokumentti DROP COLUMN IF EXISTS kansikuva;
ALTER TABLE dokumentti DROP COLUMN IF EXISTS ylatunniste;
ALTER TABLE dokumentti DROP COLUMN IF EXISTS alatunniste;
