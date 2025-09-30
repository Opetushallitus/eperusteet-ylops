alter table skeduloitu_ajo add column kaytossa boolean default true not null;

update skeduloitu_ajo set kaytossa = false where nimi = 'OpetussuunnitelmaCacheTask';