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
package fi.vm.sade.eperusteet.ylops.service.ops;

import fi.vm.sade.eperusteet.ylops.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.ylops.domain.Tila;
import fi.vm.sade.eperusteet.ylops.domain.Tyyppi;
import fi.vm.sade.eperusteet.ylops.domain.Vuosiluokka;
import fi.vm.sade.eperusteet.ylops.dto.Reference;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteOpetuksentavoiteDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteOppiaineDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteOppiaineenVuosiluokkakokonaisuusDto;
import fi.vm.sade.eperusteet.ylops.dto.koodisto.KoodistoDto;
import fi.vm.sade.eperusteet.ylops.dto.koodisto.OrganisaatioDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaLuontiDto;
import fi.vm.sade.eperusteet.ylops.repository.ops.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.ylops.service.external.EperusteetService;
import fi.vm.sade.eperusteet.ylops.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.ylops.service.mocks.EperusteetServiceMock;
import fi.vm.sade.eperusteet.ylops.service.util.SecurityUtil;
import fi.vm.sade.eperusteet.ylops.test.AbstractIntegrationTest;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import static fi.vm.sade.eperusteet.ylops.test.util.TestUtils.lt;
import static fi.vm.sade.eperusteet.ylops.test.util.TestUtils.uniikkiString;

/**
 * @author mikkom
 */
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class VuosiluokkaistusIT extends AbstractIntegrationTest {

    @Autowired
    OpetussuunnitelmaService opetussuunnitelmaService;

    @Autowired
    TekstiKappaleViiteService tekstiKappaleViiteService;

    @Autowired
    OpetussuunnitelmaRepository opetussuunnitelmaRepository;

    @Autowired
    OppiaineService oppiaineet;

    @Autowired
    private DtoMapper mapper;

    @Autowired
    private EperusteetService eperusteetService;

    private static Long opsId = null;

    @Before
    public void setUp() throws IOException {
//        try (InputStream json = getClass().getResourceAsStream()) {
//            eperusteetService.setPeruste(json);
//        }

        OpetussuunnitelmaLuontiDto ops = new OpetussuunnitelmaLuontiDto();
        ops.setPerusteenDiaarinumero(EperusteetServiceMock.DIAARINUMERO);
        ops.setNimi(lt(uniikkiString()));
        ops.setKuvaus(lt(uniikkiString()));
        ops.setTyyppi(Tyyppi.POHJA);
        ops.setKoulutustyyppi(KoulutusTyyppi.PERUSOPETUS);
        OpetussuunnitelmaDto luotu = opetussuunnitelmaService.addPohja(ops);
        opetussuunnitelmaService.updateTila(luotu.getId(), Tila.VALMIS);

        ops = new OpetussuunnitelmaLuontiDto();
        ops.setNimi(lt(uniikkiString()));
        ops.setKuvaus(lt(uniikkiString()));
        ops.setTila(Tila.LUONNOS);
        ops.setTyyppi(Tyyppi.OPS);
        ops.setKoulutustyyppi(KoulutusTyyppi.PERUSOPETUS);
        ops.setPohja(Reference.of(luotu.getId()));

        KoodistoDto kunta = new KoodistoDto();
        kunta.setKoodiUri("kunta_837");
        ops.setKunnat(new HashSet<>(Collections.singleton(kunta)));
        OrganisaatioDto kouluDto = new OrganisaatioDto();
        kouluDto.setNimi(lt("Etelä-Hervannan koulu"));
        kouluDto.setOid(SecurityUtil.OPH_OID);
        ops.setOrganisaatiot(new HashSet<>(Collections.singleton(kouluDto)));
        luotu = opetussuunnitelmaService.addOpetussuunnitelma(ops);
        opsId = luotu.getId();
    }

    @Test
    public void testVuosiluokkaistus() {
        for (int i = 0; i < 3; i++) {
            OpetussuunnitelmaDto opsDto = opetussuunnitelmaService.getOpetussuunnitelmaKaikki(opsId);
            PerusteDto peruste = opetussuunnitelmaService.getPeruste(opsId);
            opsDto.getOppiaineet().stream()
                    .flatMap(o -> Stream.of(o.getOppiaine()))
                    .forEach(oa -> {
                        PerusteOppiaineDto po = peruste.getPerusopetus().getOppiaine(oa.getTunniste()).get();
                        oa.getVuosiluokkakokonaisuudet().forEach(vk -> {
                            PerusteOppiaineenVuosiluokkakokonaisuusDto pvk =
                                    po.getVuosiluokkakokonaisuus(UUID.fromString(vk.getVuosiluokkakokonaisuus().toString())).get();
                            Map<Vuosiluokka, Set<UUID>> tavoitteet = new HashMap<>();
                            pvk.getVuosiluokkaKokonaisuus().getVuosiluokat().forEach(
                                    l -> tavoitteet.put(l, pvk.getTavoitteet().stream()
                                            .map(PerusteOpetuksentavoiteDto::getTunniste).collect(Collectors.toSet())));
                            oppiaineet.updateVuosiluokkienTavoitteet(opsId, oa.getId(), vk.getId(), tavoitteet);
                        });
                    });

            opsDto = opetussuunnitelmaService.getOpetussuunnitelmaKaikki(opsId);
            opsDto.getOppiaineet().stream()
                    .flatMap(o -> Stream.of(o.getOppiaine()))
                    .forEach(oa -> {
                        PerusteOppiaineDto po = peruste.getPerusopetus().getOppiaine(oa.getTunniste()).get();
                        oa.getVuosiluokkakokonaisuudet().forEach(vk -> {
                            PerusteOppiaineenVuosiluokkakokonaisuusDto pvk =
                                    po.getVuosiluokkakokonaisuus(UUID.fromString(vk.getVuosiluokkakokonaisuus().toString())).get();
                            Map<Vuosiluokka, Set<UUID>> tavoitteet = new HashMap<>();
                            pvk.getVuosiluokkaKokonaisuus().getVuosiluokat().forEach(
                                    l -> tavoitteet.put(l, pvk.getTavoitteet().stream()
                                            .map(PerusteOpetuksentavoiteDto::getTunniste).collect(Collectors.toSet())));
                            Assert.assertEquals(tavoitteet.values().stream().filter(s -> !s.isEmpty()).count(), vk.getVuosiluokat().size());
                            vk.getVuosiluokat().forEach(l -> {
                                Assert.assertEquals(pvk.getTavoitteet().size(), l.getTavoitteet().size());
                                Assert.assertEquals(pvk.getSisaltoalueet().size(), l.getSisaltoalueet().size());
                            });
                        });
                    });
        }

    }

}
