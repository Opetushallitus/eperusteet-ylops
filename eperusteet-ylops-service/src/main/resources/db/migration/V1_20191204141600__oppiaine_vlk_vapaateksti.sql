ALTER TABLE oppiaineenvuosiluokka ADD COLUMN vapaaTeksti_id int8;

ALTER TABLE oppiaineenvuosiluokka_AUD ADD COLUMN vapaaTeksti_id int8;

ALTER TABLE oppiaineenvuosiluokka
        ADD CONSTRAINT FK_arnblm9jx6ocda99fbr5fvtqx
        FOREIGN KEY (vapaaTeksti_id)
        REFERENCES lokalisoituteksti;
