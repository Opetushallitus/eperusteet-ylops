/*
 * Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
 *
 * This program is free software: Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://ec.europa.eu/idabc/eupl
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * European Union Public Licence for more details.
 */
package fi.vm.sade.eperusteet.ylops.service.mapping;

import fi.vm.sade.eperusteet.ylops.domain.Tila;
import fi.vm.sade.eperusteet.ylops.domain.Tyyppi;
import fi.vm.sade.eperusteet.ylops.domain.dokumentti.Dokumentti;
import fi.vm.sade.eperusteet.ylops.domain.dokumentti.Dokumentti_;
import fi.vm.sade.eperusteet.ylops.domain.lukio.Lukiokurssi;
import fi.vm.sade.eperusteet.ylops.domain.oppiaine.Oppiaine;
import fi.vm.sade.eperusteet.ylops.domain.oppiaine.Oppiaine_;
import fi.vm.sade.eperusteet.ylops.domain.ops.Opetussuunnitelma;
import fi.vm.sade.eperusteet.ylops.domain.ops.Opetussuunnitelma_;
import fi.vm.sade.eperusteet.ylops.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.ylops.domain.teksti.TekstiKappale;
import fi.vm.sade.eperusteet.ylops.domain.teksti.TekstiKappale_;
import fi.vm.sade.eperusteet.ylops.dto.dokumentti.DokumenttiDto;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.Lops2019PoistettuDto;
import fi.vm.sade.eperusteet.ylops.dto.lukio.LukioOppiaineRakenneListausDto;
import fi.vm.sade.eperusteet.ylops.dto.lukio.LukioOppiaineSaveDto;
import fi.vm.sade.eperusteet.ylops.dto.lukio.LukioOppiaineTiedotDto;
import fi.vm.sade.eperusteet.ylops.dto.lukio.LukiokurssiSaveDto;
import fi.vm.sade.eperusteet.ylops.dto.lukio.LukiokurssiUpdateDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaBaseDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaInfoDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OppiaineDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OppiaineLaajaDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.PoistettuOppiaineDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.lukio.LukioPerusteOppiaineDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import fi.vm.sade.eperusteet.ylops.repository.teksti.LokalisoituTekstiRepository;
import fi.vm.sade.eperusteet.ylops.service.external.impl.perustedto.PerusteenLokalisoituTekstiDto;
import fi.vm.sade.eperusteet.ylops.service.external.impl.perustedto.TekstiKappaleDto;
import java.time.Instant;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.builtin.PassThroughConverter;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author jhyoty
 */
@Configuration
public class DtoMapperConfig {
    @Autowired
    private LokalisoituTekstiRepository lokalisoituTekstiRepository;
    
    @Bean
    public DtoMapper dtoMapper(
            ReferenceableEntityConverter referenceableEntityConverter,
            LokalisoituTekstiConverter lokalisoituTekstiConverter,
            PerusteenLokalisoituTekstiConverter perusteenLokalisoituTekstiConverter,
            PerusteenLokalisoituTekstiToLokalisoituTekstiConverter perusteenLokalisoituTekstiToLokalisoituTekstiConverter,
            KoodistoKoodiConverter koodistoKoodiConverter) {
        DefaultMapperFactory factory = new DefaultMapperFactory.Builder()
                .build();

        factory.getConverterFactory().registerConverter(referenceableEntityConverter);
        factory.getConverterFactory().registerConverter(lokalisoituTekstiConverter);
        factory.getConverterFactory().registerConverter(perusteenLokalisoituTekstiConverter);
        factory.getConverterFactory().registerConverter(perusteenLokalisoituTekstiToLokalisoituTekstiConverter);
        factory.getConverterFactory().registerConverter(koodistoKoodiConverter);
        factory.getConverterFactory().registerConverter(new LaajaalainenosaaminenViiteConverter());
        factory.getConverterFactory().registerConverter(new PassThroughConverter(LokalisoituTeksti.class));
        factory.getConverterFactory().registerConverter(new PassThroughConverter(LokalisoituTekstiDto.class));
        factory.getConverterFactory().registerConverter(new PassThroughConverter(PerusteenLokalisoituTekstiDto.class));
        factory.getConverterFactory().registerConverter(new PassThroughConverter(Instant.class));
        factory.getConverterFactory().registerConverter(new OrganisaatioConverter());
        OptionalSupport.register(factory);
        factory.registerMapper(new ReferenceableCollectionMergeMapper());

        // Yksisuuntainen mappaus
        factory.classMap(OpetussuunnitelmaDto.class, Opetussuunnitelma.class)
                .fieldBToA(Opetussuunnitelma_.tekstit.getName(), Opetussuunnitelma_.tekstit.getName())
                .fieldBToA(Opetussuunnitelma_.oppiaineet.getName(), Opetussuunnitelma_.oppiaineet.getName())
                .byDefault()
                .register();

        factory.classMap(Opetussuunnitelma.class, OpetussuunnitelmaBaseDto.class)
                .byDefault()
                .favorExtension(true)
                .fieldAToB("cachedPeruste.perusteId", "perusteenId")
                .register();

        factory.classMap(OppiaineDto.class, Oppiaine.class)
                .fieldBToA(Oppiaine_.vuosiluokkakokonaisuudet.getName(), Oppiaine_.vuosiluokkakokonaisuudet.getName())
                .fieldBToA(Oppiaine_.oppimaarat.getName(), Oppiaine_.oppimaarat.getName()).byDefault()
                .byDefault()
                .register();

        factory.classMap(OppiaineLaajaDto.class, Oppiaine.class)
                .fieldBToA(Oppiaine_.vuosiluokkakokonaisuudet.getName(), Oppiaine_.vuosiluokkakokonaisuudet.getName())
                .fieldBToA(Oppiaine_.oppimaarat.getName(), Oppiaine_.oppimaarat.getName()).byDefault()
                .register();

        factory.classMap(Oppiaine.class, LukioOppiaineRakenneListausDto.class)
                .exclude(Oppiaine_.oppimaarat.getName())
                .byDefault()
                .register();

        factory.classMap(Oppiaine.class, LukioOppiaineTiedotDto.class)
                .exclude(Oppiaine_.oppimaarat.getName())
                .exclude("kurssiTyyppiKuvaukset") // does not handle Optional correctly
                .byDefault()
                .register();

        factory.classMap(TekstiKappale.class, TekstiKappaleDto.class)
                .exclude(TekstiKappale_.tila.getName())
                .byDefault()
                .register();

        factory.classMap(LukioOppiaineSaveDto.class, Oppiaine.class)
                .exclude("kurssiTyyppiKuvaukset") // does not handle Optional correctly
                .byDefault()
                .register();

        factory.classMap(LukioPerusteOppiaineDto.class, LukioPerusteOppiaineDto.class)
                .exclude("kurssiTyyppiKuvaukset") // does not handle Optional correctly
                .byDefault()
                .register();

        factory.classMap(LukiokurssiSaveDto.class, Lukiokurssi.class)
                .exclude("tyyppi")
                .byDefault()
                .register();

        factory.classMap(LukiokurssiUpdateDto.class, Lukiokurssi.class)
                .exclude("tyyppi")
                .byDefault()
                .register();

//        factory.classMap(KommenttiKahvaDto.class, KommenttiKahva.class)
//                .byDefault()
//                .register();

        factory.classMap(PoistettuOppiaineDto.class, Lops2019PoistettuDto.class)
                .fieldAToB("oppiaine", "poistettuId")
                .fieldBToA("poistettuId", "oppiaine")
                .byDefault()
                .register();

        factory.classMap(Dokumentti.class, DokumenttiDto.class)
                .exclude(Dokumentti_.kansikuva.getName())
                .exclude(Dokumentti_.ylatunniste.getName())
                .exclude(Dokumentti_.alatunniste.getName())
                .byDefault()
                .favorExtension(true)
                .customize(new CustomMapper<Dokumentti, DokumenttiDto>() {
                    @Override
                    public void mapAtoB(Dokumentti dokumentti, DokumenttiDto dokumenttiDto, MappingContext context) {
                        super.mapAtoB(dokumentti, dokumenttiDto, context);
                        dokumenttiDto.setKansikuva(dokumentti.getKansikuva() != null);
                        dokumenttiDto.setYlatunniste(dokumentti.getYlatunniste() != null);
                        dokumenttiDto.setAlatunniste(dokumentti.getAlatunniste() != null);
                    }
                })
                .register();

        factory.classMap(Opetussuunnitelma.class, OpetussuunnitelmaInfoDto.class)
                .byDefault()
                .favorExtension(true)
                .customize(new CustomMapper<Opetussuunnitelma, OpetussuunnitelmaInfoDto>() {
                    @Override
                    public void mapAtoB(Opetussuunnitelma opetussuunnitelma, OpetussuunnitelmaInfoDto opetussuunnitelmaInfoDto, MappingContext context) {
                        super.mapAtoB(opetussuunnitelma, opetussuunnitelmaInfoDto, context);
                        if (!Tyyppi.POHJA.equals(opetussuunnitelma.getTyyppi()) && !Tila.POISTETTU.equals(opetussuunnitelma.getTila()) && CollectionUtils.isNotEmpty(opetussuunnitelma.getJulkaisut())) {
                            opetussuunnitelmaInfoDto.setTila(Tila.JULKAISTU);
                        }
                    }
                })
                .register();

        return new DtoMapperImpl(factory.getMapperFacade());
    }

}
