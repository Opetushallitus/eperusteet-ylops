ALTER TABLE oppiaine ADD COLUMN pohjan_oppiaine_id int8;

ALTER TABLE oppiaine_aud ADD COLUMN pohjan_oppiaine_id int8;

alter table oppiaine
    add constraint FK_as3n3js2n6rb5cun9toxqljtq
    foreign key (pohjan_oppiaine_id)
    references oppiaine;
