UPDATE oppiaineen_vlkok SET piilotettu = false WHERE piilotettu IS NULL;
ALTER TABLE oppiaineen_vlkok ALTER COLUMN piilotettu SET DEFAULT false; 