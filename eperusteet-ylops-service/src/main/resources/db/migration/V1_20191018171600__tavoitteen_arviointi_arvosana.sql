CREATE TABLE tavoitteen_arviointi_temp AS
  TABLE tavoitteen_arviointi;

ALTER TABLE tavoitteen_arviointi ADD COLUMN arvosana int4 default 8;
ALTER TABLE tavoitteen_arviointi RENAME COLUMN hyvanosaamisenkuvaus_id TO osaamisenKuvaus_id;

ALTER TABLE tavoitteen_arviointi_AUD ADD COLUMN arvosana int4 default 8;
ALTER TABLE tavoitteen_arviointi_AUD RENAME COLUMN hyvanosaamisenkuvaus_id TO osaamisenKuvaus_id;

ALTER TABLE opetuksen_tavoite ADD COLUMN vapaaTeksti_id int8;
ALTER TABLE opetuksen_tavoite ADD COLUMN arvioinninKuvaus_id int8;

ALTER TABLE opetuksen_tavoite_AUD ADD COLUMN vapaaTeksti_id int8;
ALTER TABLE opetuksen_tavoite_AUD ADD COLUMN arvioinninKuvaus_id int8;

alter table opetuksen_tavoite
        add constraint FK_1vie3n80bxovcn44n6ns00ym8
        foreign key (arvioinninKuvaus_id)
        references lokalisoituteksti;

alter table opetuksen_tavoite
        add constraint FK_ekqnqsqo26rbif8boxlro647l
        foreign key (vapaaTeksti_id)
        references lokalisoituteksti;
