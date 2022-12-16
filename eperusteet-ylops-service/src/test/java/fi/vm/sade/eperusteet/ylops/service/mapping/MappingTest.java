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

import com.google.common.collect.ImmutableMap;
import fi.vm.sade.eperusteet.ylops.domain.Tila;
import fi.vm.sade.eperusteet.ylops.domain.ops.Opetussuunnitelma;
import fi.vm.sade.eperusteet.ylops.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.ylops.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.ylops.dto.koodisto.KoodistoDto;
import fi.vm.sade.eperusteet.ylops.dto.koodisto.OrganisaatioDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import fi.vm.sade.eperusteet.ylops.repository.teksti.LokalisoituTekstiRepository;
import fi.vm.sade.eperusteet.ylops.service.external.impl.perustedto.PerusteenLokalisoituTekstiDto;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static fi.vm.sade.eperusteet.ylops.test.util.TestUtils.lt;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author mikkom
 */
@RunWith(MockitoJUnitRunner.class)
public class MappingTest {

    @Mock
    LokalisoituTekstiRepository repository;

    @InjectMocks
    LokalisoituTekstiConverter lokalisoituTekstiConverter = new LokalisoituTekstiConverter();

    private MapperFacade mapper;

    @Before
    public void buildMapper() {
        DefaultMapperFactory factory = new DefaultMapperFactory.Builder().build();
        factory.getConverterFactory().registerConverter(lokalisoituTekstiConverter);
        factory.getConverterFactory().registerConverter(new PerusteenLokalisoituTekstiConverter());
        factory.getConverterFactory().registerConverter(new PerusteenLokalisoituTekstiToLokalisoituTekstiConverter());

        mapper =  factory.getMapperFacade();
    }

    @Getter
    @Setter
    public static class Luokka {
        public Set<String> domainString;
    }

    @Getter
    @Setter
    public static class LuokkaDto {
        public Set<OrganisaatioDto> domainString;
    }

    @Test
    public void testOrganisaatioMapping() {
        DefaultMapperFactory factory = new DefaultMapperFactory.Builder().build();
        factory.getConverterFactory().registerConverter(new OrganisaatioConverter());
        factory.getConverterFactory().registerConverter(new LokalisoituTekstiConverter());

        MapperFacade mapper = factory.getMapperFacade();

        String domainString = "Foobar";
        OrganisaatioDto organisaatioDto = mapper.map(domainString, OrganisaatioDto.class);
        Assert.assertNotNull(organisaatioDto);
        Assert.assertEquals(domainString, organisaatioDto.getOid());

        domainString = mapper.map(organisaatioDto, String.class);
        Assert.assertEquals(domainString, organisaatioDto.getOid());

        Luokka luokka = new Luokka();
        luokka.setDomainString(Collections.singleton("Foobar"));

        LuokkaDto dto = mapper.map(luokka, LuokkaDto.class);
        Assert.assertNotNull(dto);

        luokka.setDomainString(Collections.emptySet());

        mapper.map(dto, luokka);

        OpetussuunnitelmaDto opsDto = new OpetussuunnitelmaDto();
        opsDto.setNimi(lt("opsi"));

        KoodistoDto koodiDto = new KoodistoDto();
        koodiDto.setKoodiUri("kunta_007");
        opsDto.setKunnat(Collections.singleton(koodiDto));
        organisaatioDto = new OrganisaatioDto();
        organisaatioDto.setNimi(lt("Etelä-Hervannan koulu"));
        organisaatioDto.setOid("2.14.132352.26");
        opsDto.setOrganisaatiot(Collections.singleton(organisaatioDto));

        Opetussuunnitelma ops = mapper.map(opsDto, Opetussuunnitelma.class);
        Assert.assertNotNull(ops);
    }

    @Test
    public void testPerusteLokalisointiTekstiMapping() {

        PerusteenLokalisoituTekstiDto perusteenLokalisoituTekstiDto = new PerusteenLokalisoituTekstiDto(1l, UUID.randomUUID(), ImmutableMap.of(Kieli.FI, "teksti"));

        LokalisoituTekstiDto lokalisoituTekstiDto = mapper.map(perusteenLokalisoituTekstiDto, LokalisoituTekstiDto.class);

        assertThat(lokalisoituTekstiDto.getTekstit()).containsValues("teksti");
        assertThat(lokalisoituTekstiDto.getId()).isNull();
        assertThat(lokalisoituTekstiDto.getTunniste()).isNotNull();

        LokalisoituTeksti lokalisoituTeksti = mapper.map(lokalisoituTekstiDto, LokalisoituTeksti.class);
        assertThat(lokalisoituTeksti.getTeksti()).containsValues("teksti");
        assertThat(lokalisoituTeksti.getId()).isNull();
        assertThat(lokalisoituTeksti.getTunniste()).isNotNull();
        assertThat(lokalisoituTeksti.getTunniste()).isNotEqualTo(perusteenLokalisoituTekstiDto.getTunniste());
        assertThat(lokalisoituTeksti.getTunniste()).isNotEqualTo(lokalisoituTekstiDto.getTunniste());

        Mockito.verifyZeroInteractions(repository);
    }

    @Test
    public void testPerusteLokalisointiTekstiToLokalisoituTekstiMapping() {

        PerusteenLokalisoituTekstiDto perusteenLokalisoituTekstiDto = new PerusteenLokalisoituTekstiDto(1l, UUID.randomUUID(), ImmutableMap.of(Kieli.FI, "teksti"));

        LokalisoituTeksti lokalisoituTeksti = mapper.map(perusteenLokalisoituTekstiDto, LokalisoituTeksti.class);
        assertThat(lokalisoituTeksti.getTeksti()).containsValues("teksti");
        assertThat(lokalisoituTeksti.getId()).isNull();
        assertThat(lokalisoituTeksti.getTunniste()).isNotNull();
        assertThat(lokalisoituTeksti.getTunniste()).isNotEqualTo(perusteenLokalisoituTekstiDto.getTunniste());

        Mockito.verifyZeroInteractions(repository);
    }

    @Test
    public void testLokalisoituTekstiDtoMapping() {

        LokalisoituTekstiDto lokalisoituTekstiDto = new LokalisoituTekstiDto(1l, UUID.randomUUID(), ImmutableMap.of(Kieli.FI, "teksti"));

        LokalisoituTeksti lokalisoituTeksti = mapper.map(lokalisoituTekstiDto, LokalisoituTeksti.class);
        assertThat(lokalisoituTeksti.getTeksti()).containsValues("teksti");
        assertThat(lokalisoituTeksti.getId()).isNull();
        assertThat(lokalisoituTeksti.getTunniste()).isNotNull();
        assertThat(lokalisoituTeksti.getTunniste()).isNotEqualTo(lokalisoituTekstiDto.getTunniste());

        Mockito.verify(repository, Mockito.times(1)).findOne(lokalisoituTekstiDto.getId());
    }

    @Test
    public void testOptionalImmutableMapping() {
        DefaultMapperFactory factory = new DefaultMapperFactory.Builder()
                .build();

        OptionalSupport.register(factory);
        MapperFacade mapper = factory.getMapperFacade();
        B b = mapper.map(new A(), B.class);
        A a = mapper.map(b, A.class);
        assertEquals(new A(), a);
        a.setL(null);
        mapper.map(a, b);
        assertEquals(Long.valueOf(42L), b.getL());
        a.setL(Optional.empty());
        mapper.map(a, b);
        assertNull(b.getL());

        B b2 = mapper.map(new A(), B.class);
        assertEquals(Tila.LUONNOS, b2.tila);
    }

    @Getter
    @Setter
    @EqualsAndHashCode
    @ToString
    static public class A {
        Optional<Long> l = Optional.of(42L);
        Optional<String> t = Optional.of("Bar");
        Tila tila = Tila.LUONNOS;
    }

    @Getter
    @Setter
    static public class B {
        Long l;
        String t;
        Tila tila;
    }

}
