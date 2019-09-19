ALTER TABLE lops2019_oppiaine DROP CONSTRAINT fk_17k2weuqtfbnpeaa6uk8m5r8b;
ALTER TABLE lops2019_oppiaine ADD CONSTRAINT FK_17k2weuqtfbnpeaa6uk8m5r8b
    FOREIGN KEY (laajaAlainenOsaaminen_id)
    REFERENCES lokalisoituteksti;