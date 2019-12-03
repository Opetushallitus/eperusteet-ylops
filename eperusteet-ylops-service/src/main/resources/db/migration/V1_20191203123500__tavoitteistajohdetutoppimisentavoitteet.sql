ALTER TABLE opetuksen_tavoite ADD COLUMN tavoitteistaJohdetutOppimisenTavoitteet_id int8;

ALTER TABLE opetuksen_tavoite_aud ADD COLUMN tavoitteistaJohdetutOppimisenTavoitteet_id int8;

ALTER TABLE opetuksen_tavoite
        ADD CONSTRAINT FK_obyxyto0fcw5ernsstgvp6nwq
        FOREIGN KEY (tavoitteistaJohdetutOppimisenTavoitteet_id)
        REFERENCES lokalisoituteksti;
