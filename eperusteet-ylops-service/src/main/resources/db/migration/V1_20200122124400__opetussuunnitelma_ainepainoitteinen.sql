ALTER TABLE opetussuunnitelma ADD COLUMN ainepainoitteinen boolean NOT NULL DEFAULT false;

ALTER TABLE opetussuunnitelma_aud ADD COLUMN ainepainoitteinen boolean DEFAULT false;
