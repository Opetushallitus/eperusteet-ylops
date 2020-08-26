create table opetussuunnitelman_muokkaustieto_lisaparametrit (
    OpetussuunnitelmanMuokkaustieto_id int8 not null,
    kohde varchar(255),
    kohde_id int8
);

alter table opetussuunnitelman_muokkaustieto_lisaparametrit
    add constraint FK_pxnn3cikb2bfumxcc3oujhwdi
    foreign key (OpetussuunnitelmanMuokkaustieto_id)
    references opetussuunnitelman_muokkaustieto;


