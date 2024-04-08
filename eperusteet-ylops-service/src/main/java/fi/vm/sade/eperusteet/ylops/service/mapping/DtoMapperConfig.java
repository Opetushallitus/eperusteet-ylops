package fi.vm.sade.eperusteet.ylops.service.mapping;

import fi.vm.sade.eperusteet.ylops.domain.Tila;
import fi.vm.sade.eperusteet.ylops.domain.Tyyppi;
import fi.vm.sade.eperusteet.ylops.domain.dokumentti.DokumenttiKuva;
import fi.vm.sade.eperusteet.ylops.domain.dokumentti.DokumenttiKuva_;
import fi.vm.sade.eperusteet.ylops.domain.lukio.Lukiokurssi;
import fi.vm.sade.eperusteet.ylops.domain.oppiaine.Oppiaine;
import fi.vm.sade.eperusteet.ylops.domain.oppiaine.Oppiaine_;
import fi.vm.sade.eperusteet.ylops.domain.ops.Opetussuunnitelma;
import fi.vm.sade.eperusteet.ylops.domain.ops.Opetussuunnitelma_;
import fi.vm.sade.eperusteet.ylops.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.ylops.domain.teksti.TekstiKappale;
import fi.vm.sade.eperusteet.ylops.domain.teksti.TekstiKappale_;
import fi.vm.sade.eperusteet.ylops.dto.dokumentti.DokumenttiKuvaDto;
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
import fi.vm.sade.eperusteet.ylops.service.external.impl.perustedto.PerusteenLokalisoituTekstiDto;
import fi.vm.sade.eperusteet.ylops.service.external.impl.perustedto.TekstiKappaleDto;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.builtin.PassThroughConverter;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Instant;

@Configuration
public class DtoMapperConfig {
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

        factory.classMap(PoistettuOppiaineDto.class, Lops2019PoistettuDto.class)
                .fieldAToB("oppiaine", "poistettuId")
                .fieldBToA("poistettuId", "oppiaine")
                .byDefault()
                .register();

        factory.classMap(DokumenttiKuva.class, DokumenttiKuvaDto.class)
                .exclude(DokumenttiKuva_.kansikuva.getName())
                .exclude(DokumenttiKuva_.ylatunniste.getName())
                .exclude(DokumenttiKuva_.alatunniste.getName())
                .byDefault()
                .favorExtension(true)
                .customize(new CustomMapper<DokumenttiKuva, DokumenttiKuvaDto>() {
                    @Override
                    public void mapAtoB(DokumenttiKuva dokumenttiKuva, DokumenttiKuvaDto dokumenttiKuvaDto, MappingContext context) {
                        super.mapAtoB(dokumenttiKuva, dokumenttiKuvaDto, context);
                        dokumenttiKuvaDto.setKansikuva(dokumenttiKuva.getKansikuva() != null);
                        dokumenttiKuvaDto.setYlatunniste(dokumenttiKuva.getYlatunniste() != null);
                        dokumenttiKuvaDto.setAlatunniste(dokumenttiKuva.getAlatunniste() != null);
                    }
                })
                .register();

        factory.classMap(Opetussuunnitelma.class, OpetussuunnitelmaInfoDto.class)
                .byDefault()
                .favorExtension(true)
                .customize(new CustomMapper<>() {
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
