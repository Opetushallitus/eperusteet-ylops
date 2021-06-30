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
import fi.vm.sade.eperusteet.ylops.domain.KoulutustyyppiToteutus;
import fi.vm.sade.eperusteet.ylops.domain.Tila;
import fi.vm.sade.eperusteet.ylops.domain.Tyyppi;
import fi.vm.sade.eperusteet.ylops.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.ylops.domain.teksti.Omistussuhde;
import fi.vm.sade.eperusteet.ylops.domain.utils.KoodistoUtils;
import fi.vm.sade.eperusteet.ylops.dto.Reference;
import fi.vm.sade.eperusteet.ylops.dto.koodisto.KoodistoDto;
import fi.vm.sade.eperusteet.ylops.dto.koodisto.OrganisaatioDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaInfoDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaKevytDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaLaajaDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaLuontiDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.TekstiKappaleDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.TekstiKappaleViiteDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.TekstiKappaleViiteKevytDto;
import fi.vm.sade.eperusteet.ylops.repository.ops.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.ylops.service.exception.BusinessRuleViolationException;
import fi.vm.sade.eperusteet.ylops.service.mocks.EperusteetServiceMock;
import fi.vm.sade.eperusteet.ylops.test.AbstractIntegrationTest;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.test.annotation.DirtiesContext;

import static fi.vm.sade.eperusteet.ylops.test.util.TestUtils.lt;
import static fi.vm.sade.eperusteet.ylops.test.util.TestUtils.uniikkiString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author mikkom
 */
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class OpetussuunnitelmaServiceIT extends AbstractIntegrationTest {

    @Autowired
    OpetussuunnitelmaService opetussuunnitelmaService;

    @Autowired
    TekstiKappaleViiteService tekstiKappaleViiteService;

    @Autowired
    OpetussuunnitelmaRepository opetussuunnitelmaRepository;

    private Long opsId;

    @Before
    public void setUp() {
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
        kouluDto.setOid("1.2.246.562.10.00000000001");
        ops.setOrganisaatiot(new HashSet<>(Collections.singleton(kouluDto)));
        OpetussuunnitelmaDto createdOps = opetussuunnitelmaService.addOpetussuunnitelma(ops);
        this.opsId = createdOps.getId();
    }

    @Test
    public void testiHierarkia() {
//        OpetussuunnitelmaKevytDto pohja = opetussuunnitelmaRepository.findOne(opsId);
//        OpetussuunnitelmaLuontiDto ylaopsLuonti = TestUtils.createOps();
//        ylaopsLuonti.setPohja(pohja);
//        OpetussuunnitelmaDto ylaops = opetussuunnitelmaService.addOpetussuunnitelma(ylaopsLuonti);
//        TekstiKappaleViiteDto.Matala tkv = TestUtils.createTekstiKappaleViite();
//        ylaops

//        ylaopsLuonti.setVuosiluokkakokonaisuudet(vuosiluokkakokonaisuudet);

//        OpetussuunnitelmaLuontiDto aliops = TestUtils.createOps();
//        aliops.setPohja(pohja);
    }

    @Test
    public void testGetAll() {
        List<OpetussuunnitelmaInfoDto> opsit = opetussuunnitelmaService.getAll(Tyyppi.OPS);
        assertEquals(1, opsit.size());
    }

    @Test
    public void testGetById() {
        List<OpetussuunnitelmaInfoDto> opsit = opetussuunnitelmaService.getAll(Tyyppi.OPS);
        assertEquals(1, opsit.size());

        Long id = opsit.get(0).getId();
        assertNotNull(id);
        OpetussuunnitelmaKevytDto ops = opetussuunnitelmaService.getOpetussuunnitelma(id);
        assertNotNull(ops);
        assertEquals(id, ops.getId());
        assertEquals(EperusteetServiceMock.DIAARINUMERO, ops.getPerusteenDiaarinumero());
    }

    @Test
    public void testUpdate() {
        List<OpetussuunnitelmaInfoDto> opsit = opetussuunnitelmaService.getAll(Tyyppi.OPS);
        assertEquals(1, opsit.size());

        Long id = opsit.get(0).getId();
        OpetussuunnitelmaDto ops = opetussuunnitelmaService.getOpetussuunnitelmaKaikki(id);
        Tila vanhaTila = ops.getTila();
        String kuvaus = uniikkiString();
        ops.setKuvaus(lt(kuvaus));
        ops.setTila(Tila.POISTETTU);
        Date pvm = new GregorianCalendar(Calendar.getInstance().get(Calendar.YEAR) - 1, Calendar.MARCH, 12).getTime();
        ops.setPaatospaivamaara(pvm);
        opetussuunnitelmaService.updateOpetussuunnitelma(ops);

        ops = opetussuunnitelmaService.getOpetussuunnitelmaKaikki(id);
        assertEquals(kuvaus, ops.getKuvaus().get(Kieli.FI));
        assertEquals(vanhaTila, ops.getTila());
        assertEquals(pvm, ops.getPaatospaivamaara());
    }

    @Test
    public void testUpdateOrganisationUpdate() {
        List<OpetussuunnitelmaInfoDto> opsit = opetussuunnitelmaService.getAll(Tyyppi.OPS);
        assertEquals(1, opsit.size());

        Long id = opsit.get(0).getId();
        {
            OpetussuunnitelmaDto ops = opetussuunnitelmaService.getOpetussuunnitelmaKaikki(id);
            ops.setKunnat(Collections.emptySet());
            assertThatThrownBy(() -> opetussuunnitelmaService.updateOpetussuunnitelma(ops)).hasMessage("Kuntia ei voi poistaa");

        }

        {
            OpetussuunnitelmaDto ops = opetussuunnitelmaService.getOpetussuunnitelmaKaikki(id);
            ops.setOrganisaatiot(Collections.emptySet());
            assertThatThrownBy(() -> opetussuunnitelmaService.updateOpetussuunnitelma(ops)).hasMessage("Organisaatioita ei voi poistaa");

        }
    }

    @Test(expected = BusinessRuleViolationException.class)
    public void testOpsPohja() {
        OpetussuunnitelmaLuontiDto ops = new OpetussuunnitelmaLuontiDto();
        ops.setNimi(lt(uniikkiString()));
        ops.setKuvaus(lt(uniikkiString()));
        ops.setTila(Tila.LUONNOS);
        ops.setTyyppi(Tyyppi.OPS);
        ops.setKoulutustyyppi(KoulutusTyyppi.LUKIOKOULUTUS);

        KoodistoDto kunta = new KoodistoDto();
        kunta.setKoodiUri("kunta_837");
        ops.setKunnat(new HashSet<>(Collections.singleton(kunta)));
        OrganisaatioDto kouluDto = new OrganisaatioDto();
        kouluDto.setNimi(lt("Etelä-Hervannan koulu"));
        kouluDto.setOid("1.2.246.562.10.00000000001");
        ops.setOrganisaatiot(new HashSet<>(Collections.singleton(kouluDto)));

        opetussuunnitelmaService.addOpetussuunnitelma(ops);
    }

    @Test
    public void testOpsPohja2() {
        OpetussuunnitelmaLuontiDto ops = new OpetussuunnitelmaLuontiDto();
        ops.setNimi(lt(uniikkiString()));
        ops.setKuvaus(lt(uniikkiString()));
        ops.setTila(Tila.LUONNOS);
        ops.setTyyppi(Tyyppi.POHJA);
        ops.setKoulutustyyppi(KoulutusTyyppi.PERUSOPETUS);

        KoodistoDto kunta = new KoodistoDto();
        kunta.setKoodiUri("kunta_837");
        ops.setKunnat(new HashSet<>(Collections.singleton(kunta)));
        OrganisaatioDto kouluDto = new OrganisaatioDto();
        kouluDto.setNimi(lt("Etelä-Hervannan koulu"));
        kouluDto.setOid("1.2.246.562.10.00000000001");
        ops.setOrganisaatiot(new HashSet<>(Collections.singleton(kouluDto)));

        OpetussuunnitelmaDto dto = opetussuunnitelmaService.addOpetussuunnitelma(ops);
        assertNotNull(dto);
    }

    @Test
    public void testOpsLuontiPohjanTIloilla() {
        assertNotNull(addOpsPohjanTilalla(Tila.VALMIS));
        assertNotNull(addOpsPohjanTilalla(Tila.JULKAISTU));

        assertThatThrownBy(() -> {
            addOpsPohjanTilalla(Tila.LUONNOS);
        }).isInstanceOf(BusinessRuleViolationException.class).hasMessageContaining("pohjan-pitaa-olla-julkaistu");
    }

    private OpetussuunnitelmaDto addOpsPohjanTilalla(Tila paivitettavaTila){
        OpetussuunnitelmaLuontiDto pohja = new OpetussuunnitelmaLuontiDto();
        pohja.setPerusteenDiaarinumero(EperusteetServiceMock.DIAARINUMERO);
        pohja.setNimi(lt(uniikkiString()));
        pohja.setKuvaus(lt(uniikkiString()));
        pohja.setTyyppi(Tyyppi.POHJA);
        pohja.setTila(Tila.LUONNOS);
        OpetussuunnitelmaDto pohjaDto = opetussuunnitelmaService.addPohja(pohja);
        opetussuunnitelmaService.updateTila(pohjaDto.getId(), paivitettavaTila);

        OpetussuunnitelmaLuontiDto ops = new OpetussuunnitelmaLuontiDto();
        ops.setNimi(lt(uniikkiString()));
        ops.setKuvaus(lt(uniikkiString()));
        ops.setTila(Tila.LUONNOS);
        ops.setPohja(new Reference(pohjaDto.getId().toString()));
        ops.setTyyppi(Tyyppi.OPS);
        ops.setKoulutustyyppi(KoulutusTyyppi.PERUSOPETUS);

        KoodistoDto kunta = new KoodistoDto();
        kunta.setKoodiUri("kunta_837");
        ops.setKunnat(new HashSet<>(Collections.singleton(kunta)));
        OrganisaatioDto kouluDto = new OrganisaatioDto();
        kouluDto.setNimi(lt("Etelä-Hervannan koulu"));
        kouluDto.setOid("1.2.246.562.10.00000000001");
        ops.setOrganisaatiot(new HashSet<>(Collections.singleton(kouluDto)));

        return opetussuunnitelmaService.addOpetussuunnitelma(ops);
    }

    @Test
    public void testOpsPohja3() {
        List<OpetussuunnitelmaInfoDto> opsit = opetussuunnitelmaService.getAll(Tyyppi.OPS);
        assertEquals(1, opsit.size());
        Long id = opsit.get(0).getId();
        OpetussuunnitelmaDto ops = opetussuunnitelmaService.getOpetussuunnitelmaKaikki(id);
        ops.setPohja(null);
        ops = opetussuunnitelmaService.updateOpetussuunnitelma(ops);
        assertNotNull(ops.getPohja());
    }

    @Test
    public void testUpdateTila() {
        List<OpetussuunnitelmaInfoDto> opsit = opetussuunnitelmaService.getAll(Tyyppi.POHJA);
        assertEquals(1, opsit.size());

        Long id = opsit.get(0).getId();
        {
            OpetussuunnitelmaDto ops = opetussuunnitelmaService.getOpetussuunnitelmaKaikki(id);
            assertEquals(Tila.VALMIS, ops.getTila());

            ops = opetussuunnitelmaService.updateTila(id, Tila.LUONNOS);
            assertEquals(Tila.LUONNOS, ops.getTila());

            ops = opetussuunnitelmaService.updateTila(id, Tila.VALMIS);
            assertEquals(Tila.VALMIS, ops.getTila());
        }

        // Vanha valmis pohja merkitään poistetuksi kun uusi pohja merkitään valmiiksi
        OpetussuunnitelmaLuontiDto ops = new OpetussuunnitelmaLuontiDto();
        ops.setPerusteenDiaarinumero(EperusteetServiceMock.DIAARINUMERO);
        ops.setNimi(lt(uniikkiString()));
        ops.setKuvaus(lt(uniikkiString()));
        ops.setTyyppi(Tyyppi.POHJA);
        OpetussuunnitelmaDto luotu = opetussuunnitelmaService.addPohja(ops);
        luotu = opetussuunnitelmaService.updateTila(luotu.getId(), Tila.VALMIS);
        assertEquals(Tila.VALMIS, luotu.getTila());

        opsit = opetussuunnitelmaService.getAll(Tyyppi.POHJA);
        assertEquals(2, opsit.size());

        luotu = opetussuunnitelmaService.getOpetussuunnitelmaKaikki(luotu.getId());
        assertEquals(Tila.VALMIS, luotu.getTila());

        luotu = opetussuunnitelmaService.getOpetussuunnitelmaKaikki(id);
        assertEquals(Tila.VALMIS, luotu.getTila());
    }

    @Test
    public void testUpdateOpsinTila() {
        List<OpetussuunnitelmaInfoDto> opsit = opetussuunnitelmaService.getAll(Tyyppi.OPS);
        assertEquals(1, opsit.size());

        Long id = opsit.get(0).getId();
        OpetussuunnitelmaDto ops = opetussuunnitelmaService.getOpetussuunnitelmaKaikki(id);
        assertEquals(Tila.LUONNOS, ops.getTila());

        // Opsin voi palauttaa valmiista luonnokseksi, muuten normaali tilan eteneminen
        ops = opetussuunnitelmaService.updateTila(id, Tila.VALMIS);
        assertEquals(Tila.VALMIS, ops.getTila());

        ops = opetussuunnitelmaService.updateTila(id, Tila.LUONNOS);
        assertEquals(Tila.LUONNOS, ops.getTila());

        ops = opetussuunnitelmaService.updateTila(id, Tila.VALMIS);
        assertEquals(Tila.VALMIS, ops.getTila());

        opetussuunnitelmaService.updateTila(id, Tila.JULKAISTU);
        assertEquals(Tila.VALMIS, ops.getTila());
    }

    @Test
    public void testAddTekstiKappale() {
        List<OpetussuunnitelmaInfoDto> opsit = opetussuunnitelmaService.getAll(Tyyppi.OPS);
        assertEquals(1, opsit.size());

        Long opsId = opsit.get(0).getId();

        TekstiKappaleDto tekstiKappale = new TekstiKappaleDto();
        tekstiKappale.setNimi(lt("Otsake"));
        tekstiKappale.setTeksti(lt("Leipää ja tekstiä"));

        TekstiKappaleViiteDto.Matala viiteDto = new TekstiKappaleViiteDto.Matala();
        viiteDto.setPakollinen(true);
        viiteDto.setTekstiKappale(tekstiKappale);

        TekstiKappaleViiteDto.Puu tekstit = opetussuunnitelmaService.getTekstit(opsId, TekstiKappaleViiteDto.Puu.class);
        final int lastenMaara = tekstit.getLapset() != null ? tekstit.getLapset().size() : 0;

        viiteDto = opetussuunnitelmaService.addTekstiKappale(opsId, viiteDto);

        tekstit = opetussuunnitelmaService.getTekstit(opsId, TekstiKappaleViiteDto.Puu.class);
        assertNotNull(tekstit);
        assertEquals(lastenMaara + 1, tekstit.getLapset().size());

        TekstiKappaleViiteDto.Matala dto = tekstiKappaleViiteService.getTekstiKappaleViite(opsId, viiteDto.getId());
        assertNotNull(dto);
        assertTrue(dto.isPakollinen());

        dto.setPakollinen(false);
        TekstiKappaleViiteDto updatedDto =
                tekstiKappaleViiteService.updateTekstiKappaleViite(opsId, viiteDto.getId(), dto);
        assertFalse(updatedDto.isPakollinen());
        dto = tekstiKappaleViiteService.getTekstiKappaleViite(opsId, viiteDto.getId());
        assertNotNull(dto);
        assertFalse(dto.isPakollinen());

        tekstiKappale = new TekstiKappaleDto();
        tekstiKappale.setNimi(lt("Aliotsake"));
        tekstiKappale.setTeksti(lt("Sirkushuveja"));
        viiteDto = new TekstiKappaleViiteDto.Matala();
        viiteDto.setTekstiKappale(tekstiKappale);

        opetussuunnitelmaService.addTekstiKappaleLapsi(opsId, dto.getId(), viiteDto);

        tekstit = opetussuunnitelmaService.getTekstit(opsId, TekstiKappaleViiteDto.Puu.class);
        TekstiKappaleViiteKevytDto otsikot = opetussuunnitelmaService.getTekstit(opsId, TekstiKappaleViiteKevytDto.class);
        assertNotNull(tekstit);
        assertNotNull(otsikot);
        assertEquals(1, tekstit.getLapset().get(lastenMaara).getLapset().size());
        assertEquals(
                otsikot.getLapset().get(lastenMaara).getLapset().size(),
                tekstit.getLapset().get(lastenMaara).getLapset().size());
    }

    @Test
    public void testKielitarjontakoodit() {
        assertEquals(KoodistoUtils.getVieraskielikoodi("lukiokielitarjonta_ea", Kieli.FI), "EA");
        assertEquals(KoodistoUtils.getVieraskielikoodi("lukiokielitarjonta_ea", Kieli.SV), "SP");
        assertEquals(KoodistoUtils.getVieraskielikoodi("lukiokielitarjonta_ve", Kieli.FI), "VE");
        assertEquals(KoodistoUtils.getVieraskielikoodi("abc", Kieli.FI), "KX");
    }

    @Test
    public void testImportPerusteTekstit() {
        {
            OpetussuunnitelmaDto ops = createOpetussuunnitelmaLuonti(createOpetussuunnitelma(KoulutusTyyppi.PERUSOPETUS, "perusopetus-diaarinumero"), KoulutusTyyppi.PERUSOPETUS);

            TekstiKappaleViiteDto.Matala tk = tekstiKappaleViiteService.getTekstiKappaleViite(ops.getId(), ops.getTekstit().get().getLapset().get(1).getId());
            tk.setPakollinen(true);
            tk.setOmistussuhde(Omistussuhde.LAINATTU);
            tekstiKappaleViiteService.updateTekstiKappaleViite(ops.getId(), tk.getId(), tk);
            tekstiKappaleViiteService.addTekstiKappaleViite(ops.getId(), tk.getId(), tk);

            ops = opetussuunnitelmaService.getOpetussuunnitelmaKaikki(ops.getId());

            Date paivitetty = ops.getPerusteDataTuontiPvm();
            assertThat(ops.getTekstit()).isPresent();
            assertThat(ops.getTekstit().get().getLapset()).hasSize(2);
            assertThat(ops.getTekstit().get().getLapset().stream().filter(t -> t.getTekstiKappale().getNimi().get(Kieli.FI).contains("(vanha)")).count()).isEqualTo(0);
            assertThat(ops.getTekstit().get().getLapset().get(1).isPakollinen()).isTrue();
            assertThat(ops.getTekstit().get().getLapset().get(1).getLapset().get(0).isPakollinen()).isTrue();
            assertThat(ops.getPerusteDataTuontiPvm()).isNotNull();

            ops = opetussuunnitelmaService.importPerusteTekstit(ops.getId());
            assertThat(paivitetty).isNotEqualTo(ops.getPerusteDataTuontiPvm());
            assertThat(ops.getTekstit().get().getLapset()).hasSize(3);
            assertThat(ops.getTekstit().get().getLapset().stream().filter(t -> t.getTekstiKappale().getNimi().get(Kieli.FI).contains("(vanha)")).count()).isEqualTo(2);
            assertThat(ops.getTekstit().get().getLapset().get(0).getTekstiKappale().getNimi().get(Kieli.FI)).doesNotContain("(vanha)");
            assertThat(ops.getTekstit().get().getLapset().get(2).getTekstiKappale().getNimi().get(Kieli.FI)).contains("(vanha)");
            assertThat(ops.getTekstit().get().getLapset().get(2).isPakollinen()).isFalse();
            assertThat(ops.getTekstit().get().getLapset().get(2)).extracting("omistussuhde").containsExactly(Omistussuhde.OMA);
            assertThat(ops.getTekstit().get().getLapset().get(2).getLapset().get(0).isPakollinen()).isFalse();
            assertThat(ops.getTekstit().get().getLapset().get(2).getLapset().get(0)).extracting("omistussuhde").containsExactly(Omistussuhde.OMA);
        }

        {
            OpetussuunnitelmaDto ops = createOpetussuunnitelmaLuonti(createOpetussuunnitelma(KoulutusTyyppi.VARHAISKASVATUS, "OPH-2791-2018"), KoulutusTyyppi.VARHAISKASVATUS);
            assertThat(ops.getTekstit()).isPresent();
            assertThat(ops.getTekstit().get().getLapset()).hasSize(2);
            assertThat(ops.getTekstit().get().getLapset().stream().filter(t -> t.getTekstiKappale().getNimi().get(Kieli.FI).contains("(vanha)")).count()).isEqualTo(0);

            ops = opetussuunnitelmaService.importPerusteTekstit(ops.getId());
            assertThat(ops.getTekstit().get().getLapset()).hasSize(9);
            assertThat(ops.getTekstit().get().getLapset().stream().filter(t -> t.getTekstiKappale().getNimi().get(Kieli.FI).contains("(vanha)")).count()).isEqualTo(2);
        }

        {
            OpetussuunnitelmaDto ops = createOpetussuunnitelmaLuonti(createOpetussuunnitelma(KoulutusTyyppi.ESIOPETUS, "OPH-2791-2018"), KoulutusTyyppi.ESIOPETUS);
            assertThat(ops.getTekstit()).isPresent();
            assertThat(ops.getTekstit().get().getLapset()).hasSize(2);
            assertThat(ops.getTekstit().get().getLapset().stream().filter(t -> t.getTekstiKappale().getNimi().get(Kieli.FI).contains("(vanha)")).count()).isEqualTo(0);

            ops = opetussuunnitelmaService.importPerusteTekstit(ops.getId());
            assertThat(ops.getTekstit().get().getLapset()).hasSize(9);
            assertThat(ops.getTekstit().get().getLapset().stream().filter(t -> t.getTekstiKappale().getNimi().get(Kieli.FI).contains("(vanha)")).count()).isEqualTo(2);
        }

        {
            OpetussuunnitelmaDto ops = createOpetussuunnitelmaLuonti(createOpetussuunnitelma(KoulutusTyyppi.TPO, "tpo-diaarinumero"), KoulutusTyyppi.TPO);
            assertThat(ops.getTekstit()).isPresent();
            assertThat(ops.getTekstit().get().getLapset()).hasSize(2);
            assertThat(ops.getTekstit().get().getLapset().stream().filter(t -> t.getTekstiKappale().getNimi().get(Kieli.FI).contains("(vanha)")).count()).isEqualTo(0);

            ops = opetussuunnitelmaService.importPerusteTekstit(ops.getId());
            assertThat(ops.getTekstit().get().getLapset()).hasSize(3);
            assertThat(ops.getTekstit().get().getLapset().stream().filter(t -> t.getTekstiKappale().getNimi().get(Kieli.FI).contains("(vanha)")).count()).isEqualTo(2);
        }

        {
            OpetussuunnitelmaDto ops = createOpetussuunnitelmaLuonti(createOpetussuunnitelma(KoulutusTyyppi.TPO, "tpo-diaarinumero"), KoulutusTyyppi.TPO);
            assertThat(ops.getTekstit().get().getLapset()).hasSize(2);

            ops = opetussuunnitelmaService.importPerusteTekstit(ops.getId(), true);
            assertThat(ops.getTekstit().get().getLapset()).hasSize(2);
        }
    }

    private OpetussuunnitelmaDto createOpetussuunnitelma(KoulutusTyyppi koulutustyyppi, String diaarinumero) {
        OpetussuunnitelmaLuontiDto ops = new OpetussuunnitelmaLuontiDto();
        ops.setNimi(lt(uniikkiString()));
        ops.setKuvaus(lt(uniikkiString()));
        ops.setTila(Tila.LUONNOS);
        ops.setTyyppi(Tyyppi.POHJA);
        ops.setKoulutustyyppi(koulutustyyppi);
        ops.setPerusteenDiaarinumero(diaarinumero);

        KoodistoDto kunta = new KoodistoDto();
        kunta.setKoodiUri("kunta_837");
        ops.setKunnat(new HashSet<>(Collections.singleton(kunta)));
        OrganisaatioDto kouluDto = new OrganisaatioDto();
        kouluDto.setNimi(lt("Etelä-Hervannan koulu"));
        kouluDto.setOid("1.2.246.562.10.00000000001");
        ops.setOrganisaatiot(new HashSet<>(Collections.singleton(kouluDto)));

        OpetussuunnitelmaDto luotu = opetussuunnitelmaService.addPohja(ops);
        return opetussuunnitelmaService.updateTila(luotu.getId(), Tila.VALMIS);
    }

    private OpetussuunnitelmaDto createOpetussuunnitelmaLuonti(OpetussuunnitelmaDto pohjaOps, KoulutusTyyppi koulutustyyppi) {
        OpetussuunnitelmaLuontiDto ops = new OpetussuunnitelmaLuontiDto();
        ops.setNimi(lt(uniikkiString()));
        ops.setKuvaus(lt(uniikkiString()));
        ops.setTila(Tila.LUONNOS);
        ops.setTyyppi(Tyyppi.OPS);
        ops.setKoulutustyyppi(koulutustyyppi);

        KoodistoDto kunta = new KoodistoDto();
        kunta.setKoodiUri("kunta_837");
        ops.setKunnat(new HashSet<>(Collections.singleton(kunta)));
        OrganisaatioDto kouluDto = new OrganisaatioDto();
        kouluDto.setNimi(lt("Etelä-Hervannan koulu"));
        kouluDto.setOid("1.2.246.562.10.00000000001");
        ops.setOrganisaatiot(new HashSet<>(Collections.singleton(kouluDto)));

        ops.setPohja(Reference.of(pohjaOps.getId()));
        return opetussuunnitelmaService.addOpetussuunnitelma(ops);
    }

    @Test
    public void testExportYksinkertainen() {
        OpetussuunnitelmaLuontiDto pohjaLuontiDto = new OpetussuunnitelmaLuontiDto();
        pohjaLuontiDto.setTuoPohjanOpintojaksot(false);
        pohjaLuontiDto.setToteutus(KoulutustyyppiToteutus.YKSINKERTAINEN);
        pohjaLuontiDto.setTyyppi(Tyyppi.POHJA);
        pohjaLuontiDto.setPerusteenDiaarinumero("OPH-2791-2018");
        OpetussuunnitelmaDto pohjaDto = opetussuunnitelmaService.addPohja(pohjaLuontiDto);
        opetussuunnitelmaService.updateTila(pohjaDto.getId(), Tila.VALMIS);

        OpetussuunnitelmaLuontiDto opsLuontiDto = new OpetussuunnitelmaLuontiDto();
        opsLuontiDto.setTuoPohjanOpintojaksot(true);
        pohjaLuontiDto.setToteutus(KoulutustyyppiToteutus.YKSINKERTAINEN);
        opsLuontiDto.setTyyppi(Tyyppi.OPS);
        opsLuontiDto.setOrganisaatiot(Stream.of("1.2.246.562.10.83037752777")
                .map(oid -> {
                    OrganisaatioDto result = new OrganisaatioDto();
                    result.setOid(oid);
                    return result;
                })
                .collect(Collectors.toSet()));
        opsLuontiDto.setPohja(Reference.of(pohjaDto.getId()));
        OpetussuunnitelmaDto ops = opetussuunnitelmaService.addOpetussuunnitelma(opsLuontiDto);

        OpetussuunnitelmaLaajaDto exported = (OpetussuunnitelmaLaajaDto) opetussuunnitelmaService.getExportedOpetussuunnitelma(ops.getId());
        assertThat(exported.getPeruste()).isNotNull();
        assertThat(exported.getPohja()).isNotNull();
        assertThat(exported.getTekstit()).isNotNull();
    }

    @Test
    public void testExportPerusopetus() {
        OpetussuunnitelmaLuontiDto pohjaLuontiDto = new OpetussuunnitelmaLuontiDto();
        pohjaLuontiDto.setTuoPohjanOpintojaksot(false);
        pohjaLuontiDto.setToteutus(KoulutustyyppiToteutus.PERUSOPETUS);
        pohjaLuontiDto.setTyyppi(Tyyppi.POHJA);
        pohjaLuontiDto.setPerusteenDiaarinumero("perusopetus-diaarinumero");
        OpetussuunnitelmaDto pohjaDto = opetussuunnitelmaService.addPohja(pohjaLuontiDto);
        opetussuunnitelmaService.updateTila(pohjaDto.getId(), Tila.VALMIS);

        OpetussuunnitelmaLuontiDto opsLuontiDto = new OpetussuunnitelmaLuontiDto();
        opsLuontiDto.setTuoPohjanOpintojaksot(true);
        pohjaLuontiDto.setToteutus(KoulutustyyppiToteutus.PERUSOPETUS);
        opsLuontiDto.setTyyppi(Tyyppi.OPS);
        opsLuontiDto.setOrganisaatiot(Stream.of("1.2.246.562.10.83037752777")
                .map(oid -> {
                    OrganisaatioDto result = new OrganisaatioDto();
                    result.setOid(oid);
                    return result;
                })
                .collect(Collectors.toSet()));
        opsLuontiDto.setPohja(Reference.of(pohjaDto.getId()));
        OpetussuunnitelmaDto ops = opetussuunnitelmaService.addOpetussuunnitelma(opsLuontiDto);

        OpetussuunnitelmaLaajaDto exported = (OpetussuunnitelmaLaajaDto) opetussuunnitelmaService.getExportedOpetussuunnitelma(ops.getId());
        assertThat(exported.getPeruste()).isNotNull();
        assertThat(exported.getPohja()).isNotNull();
        assertThat(exported.getTekstit()).isNotNull();
        assertThat(exported.getOppiaineet()).isNotNull();
    }

    @Test
    public void testYksinkertainenPohjanTekstitPerusteelta() {
        OpetussuunnitelmaLuontiDto pohjaLuontiDto = new OpetussuunnitelmaLuontiDto();
        pohjaLuontiDto.setTuoPohjanOpintojaksot(false);
        pohjaLuontiDto.setToteutus(KoulutustyyppiToteutus.YKSINKERTAINEN);
        pohjaLuontiDto.setTyyppi(Tyyppi.POHJA);
        pohjaLuontiDto.setPerusteenDiaarinumero("OPH-2791-2018");

        {
            OpetussuunnitelmaDto pohjaDto = opetussuunnitelmaService.addPohja(pohjaLuontiDto);
            assertThat(pohjaDto.getTekstit().isPresent()).isTrue();
            assertThat(pohjaDto.getTekstit().get().getLapset()).hasSize(2);
            assertThat(pohjaDto.getTekstit().get().getLapset()).extracting("perusteTekstikappaleId").containsNull();
        }

        {
            pohjaLuontiDto.setRakennePohjasta(true);
            OpetussuunnitelmaDto pohjaDto = opetussuunnitelmaService.addPohja(pohjaLuontiDto);
            assertThat(pohjaDto.getTekstit().isPresent()).isTrue();
            assertThat(pohjaDto.getTekstit().get().getLapset()).hasSize(7);
            assertThat(pohjaDto.getTekstit().get().getLapset()).extracting("perusteTekstikappaleId").doesNotContainNull();
        }
    }

    @Test
    public void testGetTop10ByLuomisaika() {
        OpetussuunnitelmaLuontiDto perusopetusPohja = new OpetussuunnitelmaLuontiDto();
        perusopetusPohja.setTuoPohjanOpintojaksot(false);
        perusopetusPohja.setToteutus(KoulutustyyppiToteutus.PERUSOPETUS);
        perusopetusPohja.setTyyppi(Tyyppi.POHJA);
        perusopetusPohja.setPerusteenDiaarinumero("perusopetus-diaarinumero");
        OpetussuunnitelmaDto perusopetusPohjaDto = opetussuunnitelmaService.addPohja(perusopetusPohja);
        opetussuunnitelmaService.updateTila(perusopetusPohjaDto.getId(), Tila.VALMIS);

        OpetussuunnitelmaLuontiDto lukioPohja = new OpetussuunnitelmaLuontiDto();
        lukioPohja.setToteutus(KoulutustyyppiToteutus.LOPS2019);
        lukioPohja.setTyyppi(Tyyppi.POHJA);
        lukioPohja.setPerusteenDiaarinumero("1/2/3");
        OpetussuunnitelmaDto lukioPohjaDto = opetussuunnitelmaService.addPohja(lukioPohja);
        opetussuunnitelmaService.updateTila(lukioPohjaDto.getId(), Tila.VALMIS);

        setUser("test8");

        IntStream.range(1, 11).forEach(lkm -> {
            createOpetussuunnitelma((ops) -> {
                ops.setKoulutustyyppi(KoulutusTyyppi.PERUSOPETUS);
                ops.setPohja(Reference.of(perusopetusPohjaDto.getId()));
                ops.setNimi(LokalisoituTekstiDto.of("nimi" + lkm));
            });
        });

        IntStream.range(11, 26).forEach(lkm -> {
            createOpetussuunnitelma((ops) -> {
                ops.setKoulutustyyppi(KoulutusTyyppi.LUKIOKOULUTUS);
                ops.setPohja(Reference.of(lukioPohjaDto.getId()));
                ops.setNimi(LokalisoituTekstiDto.of("nimi" + lkm));
            });
        });

        Page<OpetussuunnitelmaInfoDto> opsit = opetussuunnitelmaService.getSivutettu(Tyyppi.OPS, Tila.LUONNOS, null, null, 0, 10);
        assertThat(opsit.getContent()).hasSize(10);
        assertThat(opsit.getTotalElements()).isEqualTo(25);
        assertThat(opsit.getTotalPages()).isEqualTo(3);

        opsit = opetussuunnitelmaService.getSivutettu(Tyyppi.OPS, Tila.LUONNOS, null, null, 1, 10);
        assertThat(opsit.getContent()).hasSize(10);

        opsit = opetussuunnitelmaService.getSivutettu(Tyyppi.OPS, Tila.LUONNOS, null, null, 2, 10);
        assertThat(opsit.getContent()).hasSize(5);

        opsit = opetussuunnitelmaService.getSivutettu(Tyyppi.OPS, Tila.LUONNOS, KoulutusTyyppi.LUKIOKOULUTUS, null, 0, 10);
        assertThat(opsit.getContent()).hasSize(10);
        assertThat(opsit.getTotalElements()).isEqualTo(15);
        assertThat(opsit.getTotalPages()).isEqualTo(2);

        opsit = opetussuunnitelmaService.getSivutettu(Tyyppi.OPS, Tila.LUONNOS, KoulutusTyyppi.LUKIOKOULUTUS, null, 1, 10);
        assertThat(opsit.getContent()).hasSize(5);

        opsit = opetussuunnitelmaService.getSivutettu(Tyyppi.OPS, Tila.LUONNOS, KoulutusTyyppi.LUKIOKOULUTUS, "nimi11", 0, 10);
        assertThat(opsit.getContent()).hasSize(1);
        assertThat(opsit.getTotalElements()).isEqualTo(1);
        assertThat(opsit.getTotalPages()).isEqualTo(1);

        setUser("testAdmin");
        opsit = opetussuunnitelmaService.getSivutettu(Tyyppi.OPS, Tila.LUONNOS, null, null, 0, 10);
        assertThat(opsit.getContent()).hasSize(10);
        assertThat(opsit.getTotalElements()).isEqualTo(26);
        assertThat(opsit.getTotalPages()).isEqualTo(3);

        opsit = opetussuunnitelmaService.getSivutettu(Tyyppi.OPS, Tila.LUONNOS, null, null, 1, 10);
        assertThat(opsit.getContent()).hasSize(10);

        opsit = opetussuunnitelmaService.getSivutettu(Tyyppi.OPS, Tila.LUONNOS, null, null, 2, 10);
        assertThat(opsit.getContent()).hasSize(6);
    }
}
