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

import fi.vm.sade.eperusteet.ylops.domain.*;
import fi.vm.sade.eperusteet.ylops.domain.lops2019.PoistetunTyyppi;
import fi.vm.sade.eperusteet.ylops.domain.oppiaine.OppiaineTyyppi;
import fi.vm.sade.eperusteet.ylops.domain.oppiaine.OppiaineValinnainenTyyppi;
import fi.vm.sade.eperusteet.ylops.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.ylops.dto.Reference;
import fi.vm.sade.eperusteet.ylops.dto.koodisto.KoodistoDto;
import fi.vm.sade.eperusteet.ylops.dto.koodisto.OrganisaatioDto;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.Lops2019PoistettuDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.*;
import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.TekstiosaDto;
import fi.vm.sade.eperusteet.ylops.repository.ops.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.ylops.repository.ops.OppiaineRepository;
import fi.vm.sade.eperusteet.ylops.repository.ops.VuosiluokkakokonaisuusviiteRepository;
import fi.vm.sade.eperusteet.ylops.service.exception.BusinessRuleViolationException;
import fi.vm.sade.eperusteet.ylops.service.mocks.EperusteetServiceMock;
import fi.vm.sade.eperusteet.ylops.test.AbstractIntegrationTest;
import fi.vm.sade.eperusteet.ylops.test.util.TestUtils;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.*;
import org.springframework.test.annotation.Rollback;

import static fi.vm.sade.eperusteet.ylops.test.util.TestUtils.lt;
import static fi.vm.sade.eperusteet.ylops.test.util.TestUtils.uniikkiString;
import static org.junit.Assert.*;

/**
 * @author mikkom
 */
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class OppiaineServiceIT extends AbstractIntegrationTest {
    @Autowired
    OpetussuunnitelmaService opetussuunnitelmaService;

    @Autowired
    OppiaineService oppiaineService;

    @Autowired
    OpetussuunnitelmaRepository opetussuunnitelmaRepository;

    @Autowired
    OppiaineRepository oppiaineRepo;

    @Autowired
    VuosiluokkakokonaisuusService vuosiluokkakokonaisuusService;

    @Autowired
    private VuosiluokkakokonaisuusviiteRepository vlkViitteet;

    @Autowired
    private PoistoService poistoService;

    private Long opsId;
    private Reference vlkViiteRef;

    @Before
    public void setUp() {
        OpetussuunnitelmaLuontiDto ops = new OpetussuunnitelmaLuontiDto();
        ops.setNimi(lt(uniikkiString()));
        ops.setKuvaus(lt(uniikkiString()));
        ops.setPerusteenDiaarinumero(EperusteetServiceMock.DIAARINUMERO);
        ops.setTila(Tila.LUONNOS);
        ops.setTyyppi(Tyyppi.POHJA);

        KoodistoDto kunta = new KoodistoDto();
        kunta.setKoodiUri("kunta_837");
        ops.setKunnat(new HashSet<>(Collections.singleton(kunta)));
        OrganisaatioDto kouluDto = new OrganisaatioDto();
        kouluDto.setNimi(lt("Etelä-Hervannan koulu"));
        kouluDto.setOid("1.2.15252345624572462");
        ops.setOrganisaatiot(new HashSet<>(Collections.singleton(kouluDto)));
        opetussuunnitelmaService.addPohja(ops);

        List<OpetussuunnitelmaInfoDto> opsit = opetussuunnitelmaService.getAll(Tyyppi.POHJA);

        this.opsId = opsit.get(0).getId();
        assertNotNull(this.opsId);

        Vuosiluokkakokonaisuusviite viite = new Vuosiluokkakokonaisuusviite(UUID.randomUUID(), EnumSet.of(Vuosiluokka.VUOSILUOKKA_1, Vuosiluokka.VUOSILUOKKA_2));
        this.vlkViiteRef = Reference.of(vlkViitteet.save(viite));
    }


    @Test
    public void testPalautaYlempi() {
        OpetussuunnitelmaDto ylaOps = createOpsBasedOnPohja();

        oppiaineService.add(ylaOps.getId(), TestUtils.createOppiaine("oppiaine 1"));
        oppiaineService.add(ylaOps.getId(), TestUtils.createOppiaine("oppiaine 2"));
        oppiaineService.add(ylaOps.getId(), TestUtils.createOppiaine("oppiaine 3"));

        OpetussuunnitelmaLuontiDto alaOpsDto = createOpetussuunnitelmaLuonti(ylaOps);
        OpetussuunnitelmaDto alaOps = opetussuunnitelmaService.addOpetussuunnitelma(alaOpsDto);

        OppiaineDto oppiaine = oppiaineService.getAll(ylaOps.getId()).get(0);
        OpsOppiaineDto opsOppiaine = oppiaineService.kopioiMuokattavaksi(alaOps.getId(), oppiaine.getId());
        assertNotEquals("Oppiaineet ovat samat", opsOppiaine.getOppiaine().getId(), oppiaine.getId());

        OpsOppiaineDto palautettuOpsOppiaine = oppiaineService.palautaYlempi(alaOps.getId(), opsOppiaine.getOppiaine().getId());
        assertEquals("Oppiaineet eivät ole samat", palautettuOpsOppiaine.getOppiaine().getId(), oppiaine.getId());

        opsOppiaine = oppiaineService.kopioiMuokattavaksi(alaOps.getId(), oppiaine.getId());
        assertNotEquals("Oppiaineet ovat samat", opsOppiaine.getOppiaine().getId(), oppiaine.getId());

        oppiaineService.delete(ylaOps.getId(), oppiaine.getId());
        try {
            oppiaineService.palautaYlempi(alaOps.getId(), opsOppiaine.getOppiaine().getId());
            fail("Palauttamisen pitäisi epäonnistua");
        } catch (Exception e) {
            assertEquals(e.getClass(), BusinessRuleViolationException.class);
        }

    }

    /*
     * EP-3085: Pohjasta luodun opetussuunitelman oppimäärän poisto poisti oppimäärän myös pohjasta.
     * Testataan että tämä poisto estetään.
     */
    @Test
    @Ignore
    public void testOppimaaraRemoveCantRemovePohjaOppimaara() {
        OpetussuunnitelmaDto uusiOps = createOpsBasedOnPohja();

        OppiaineDto vieraatKielet = oppiaineService.add(uusiOps.getId(), TestUtils.createOppiaine("vieraat kielet"));

        KopioOppimaaraDto ranskaB1 = new KopioOppimaaraDto();
        ranskaB1.setOmaNimi(LokalisoituTekstiDto.of("Ranska, B1-oppimäärä"));
        oppiaineService.addCopyOppimaara(uusiOps.getId(), vieraatKielet.getId(), ranskaB1);

    }

    private OpetussuunnitelmaDto createOpsBasedOnPohja() {
        OpetussuunnitelmaDto pohjaOps = opetussuunnitelmaService.getOpetussuunnitelmaKaikki(opsId);
        opetussuunnitelmaService.updateTila(pohjaOps.getId(), Tila.VALMIS);

        OpetussuunnitelmaLuontiDto newOps = createOpetussuunnitelmaLuonti(pohjaOps);
        return opetussuunnitelmaService.addOpetussuunnitelma(newOps);
    }

    private OpetussuunnitelmaLuontiDto createOpetussuunnitelmaLuonti(OpetussuunnitelmaDto pohjaOps) {
        OpetussuunnitelmaLuontiDto ops = new OpetussuunnitelmaLuontiDto();
        ops.setNimi(lt(uniikkiString()));
        ops.setKuvaus(lt(uniikkiString()));
        ops.setTila(Tila.LUONNOS);
        ops.setTyyppi(Tyyppi.OPS);
        ops.setKoulutustyyppi(KoulutusTyyppi.PERUSOPETUS);

        KoodistoDto kunta = new KoodistoDto();
        kunta.setKoodiUri("kunta_837");
        ops.setKunnat(new HashSet<>(Collections.singleton(kunta)));
        OrganisaatioDto kouluDto = new OrganisaatioDto();
        kouluDto.setNimi(lt("Etelä-Hervannan koulu"));
        kouluDto.setOid("1.2.246.562.10.00000000001");
        ops.setOrganisaatiot(new HashSet<>(Collections.singleton(kouluDto)));

        ops.setPohja(Reference.of(pohjaOps.getId()));
        return ops;
    }

    @Test
    public void testMuokattavaksiKopioiminen() {
        OpetussuunnitelmaDto ylaOps = createOpsBasedOnPohja();

        OppiaineDto oppiaine = oppiaineService.add(ylaOps.getId(), TestUtils.createOppiaine("oppiaine 1"));

        OpetussuunnitelmaLuontiDto alaOpsDto = createOpetussuunnitelmaLuonti(ylaOps);
        OpetussuunnitelmaDto alaOps = opetussuunnitelmaService.addOpetussuunnitelma(alaOpsDto);

        OpsOppiaineDto opsOppiaine = oppiaineService.kopioiMuokattavaksi(alaOps.getId(), oppiaine.getId());
        assertNotEquals("Oppiaineet ovat samat", opsOppiaine.getOppiaine().getId(), oppiaine.getId());
    }

    @Test
    public void testValinnainenAine() {
        OpetussuunnitelmaDto ops = opetussuunnitelmaService.getOpetussuunnitelmaKaikki(opsId);

        VuosiluokkakokonaisuusDto vlk = new VuosiluokkakokonaisuusDto(vlkViiteRef);
        OpsVuosiluokkakokonaisuusDto opsVlkDto = new OpsVuosiluokkakokonaisuusDto();
        vlk.setNimi(Optional.of(lt("ykköskakkoset")));
        vlk = vuosiluokkakokonaisuusService.add(ops.getId(), vlk);
        opsVlkDto.setVuosiluokkakokonaisuus(vlk);
        ops.setVuosiluokkakokonaisuudet(Collections.singleton(opsVlkDto));

        OppiaineDto valinnainen = TestUtils.createOppiaine("Valinnainen");
        valinnainen.setTyyppi(OppiaineTyyppi.MUU_VALINNAINEN);
        OppiaineenVuosiluokkakokonaisuusDto ovk = new OppiaineenVuosiluokkakokonaisuusDto();
        ovk.setVuosiluokkakokonaisuus(vlkViiteRef);

        OppiaineenVuosiluokkaDto ovlDto = new OppiaineenVuosiluokkaDto();
        ovk.setVuosiluokat(Collections.singleton(ovlDto));

        valinnainen.setVuosiluokkakokonaisuudet(Collections.singleton(ovk));

        valinnainen = oppiaineService.add(opsId, valinnainen);
        assertNotNull(valinnainen);

        OppiaineenVuosiluokkaDto vuosiluokka = valinnainen.getVuosiluokkakokonaisuudet().stream()
                .findAny()
                .get()
                .getVuosiluokat().stream()
                .findFirst()
                .get();

        List<TekstiosaDto> tavoitteet = new ArrayList<>();
        tavoitteet.add(TestUtils.createTekstiosa("hello", "world"));
        oppiaineService.updateValinnaisenVuosiluokanSisalto(opsId, valinnainen.getId(), vuosiluokka.getId(), tavoitteet);
        tavoitteet.add(TestUtils.createTekstiosa("foo", "bar"));
        OpsOppiaineDto get = oppiaineService.get(opsId, valinnainen.getId());
        oppiaineService.updateValinnaisenVuosiluokanSisalto(opsId, valinnainen.getId(), vuosiluokka.getId(), tavoitteet);

        { // Valinnaisten oppiaineiden tavoitteet (tavoitteet + sisältöalueet)
            OppiaineenVuosiluokkaDto ovlk = get.getOppiaine().getVuosiluokkakokonaisuudet().iterator().next()
                    .getVuosiluokat().iterator().next();
            assertEquals(ovlk.getTavoitteet().size(), 1);
            assertEquals(ovlk.getSisaltoalueet().size(), 1);
        }
    }

    @Test
    public void testCRUD() {
        OpetussuunnitelmaDto ops = opetussuunnitelmaService.getOpetussuunnitelmaKaikki(opsId);

        VuosiluokkakokonaisuusDto vlk = new VuosiluokkakokonaisuusDto(vlkViiteRef);
        vlk.setNimi(Optional.of(lt("ykköskakkoset")));
        vlk = vuosiluokkakokonaisuusService.add(ops.getId(), vlk);
        assertNotNull(vlk);

        OppiaineDto oppiaineDto = new OppiaineDto();
        oppiaineDto.setTyyppi(OppiaineTyyppi.YHTEINEN);
        oppiaineDto.setNimi(lt("Uskonto"));
        oppiaineDto.setKoodiUri("koodikoodi");
        oppiaineDto.setTunniste(UUID.randomUUID());
        oppiaineDto.setKoosteinen(false);

        oppiaineDto = oppiaineService.add(opsId, oppiaineDto);
        assertNotNull(oppiaineDto);

        oppiaineDto = new OppiaineDto();
        oppiaineDto.setTyyppi(OppiaineTyyppi.YHTEINEN);
        oppiaineDto.setNimi(lt("Äidinkieli"));
        oppiaineDto.setKoodiUri("koodi_123");
        oppiaineDto.setTunniste(UUID.randomUUID());

        OppiaineSuppeaDto oppimaaraDto = new OppiaineSuppeaDto();
        oppimaaraDto.setTyyppi(OppiaineTyyppi.YHTEINEN);
        oppimaaraDto.setNimi(lt("Suomen kieli ja kirjallisuus"));
        oppimaaraDto.setKoosteinen(false);
        oppimaaraDto.setTunniste(UUID.randomUUID());

        oppiaineDto.setOppimaarat(Collections.singleton(oppimaaraDto));
        oppiaineDto.setKoosteinen(true);

        oppiaineDto = oppiaineService.add(opsId, oppiaineDto);
        assertNotNull(oppiaineDto);
        assertNotNull(oppiaineDto.getOppimaarat());
        assertEquals(1, oppiaineDto.getOppimaarat().size());

        oppiaineDto = new OppiaineDto();
        oppiaineDto.setTyyppi(OppiaineTyyppi.YHTEINEN);
        oppiaineDto.setNimi(lt("Matematiikka"));
        oppiaineDto.setKoodiUri("jaa-a");
        oppiaineDto.setKoosteinen(false);
        oppiaineDto.setTunniste(UUID.randomUUID());

        OppiaineenVuosiluokkakokonaisuusDto ovk = new OppiaineenVuosiluokkakokonaisuusDto();
        ovk.setArviointi(getTekstiosa("Arviointi"));
        ovk.setTehtava(getTekstiosa("Tehtävä"));
        ovk.setTyotavat(getTekstiosa("Työtavat"));
        ovk.setOhjaus(getTekstiosa("Ohjaus"));
        ovk.setVuosiluokkakokonaisuus(vlkViiteRef);
        oppiaineDto.setVuosiluokkakokonaisuudet(Collections.singleton(ovk));

        oppiaineDto = oppiaineService.add(opsId, oppiaineDto);
        assertNotNull(oppiaineDto);

        List<OppiaineDto> oppiaineet = oppiaineService.getAll(opsId);
        assertNotNull(oppiaineet);
        assertEquals(3, oppiaineet.size());

        oppiaineet = oppiaineService.getAll(opsId, true);
        assertNotNull(oppiaineet);
        assertEquals(0, oppiaineet.size());

        oppiaineet = oppiaineService.getAll(opsId, false);
        assertNotNull(oppiaineet);
        assertEquals(3, oppiaineet.size());

        OpsOppiaineDto opsOppiaineDto = oppiaineService.get(opsId, oppiaineDto.getId());
        assertNotNull(opsOppiaineDto);
        oppiaineDto = opsOppiaineDto.getOppiaine();
        assertNotNull(oppiaineDto);
        assertEquals("Matematiikka", oppiaineDto.getNimi().get(Kieli.FI));

        oppiaineDto.setNimi(lt("Biologia"));
        oppiaineDto = oppiaineService.update(opsId, oppiaineDto).getOppiaine();

        opsOppiaineDto = oppiaineService.get(opsId, oppiaineDto.getId());
        assertNotNull(opsOppiaineDto);
        oppiaineDto = opsOppiaineDto.getOppiaine();
        assertNotNull(oppiaineDto);
        assertEquals("Biologia", oppiaineDto.getNimi().get(Kieli.FI));

        assertEquals(1, oppiaineDto.getVuosiluokkakokonaisuudet().size());
        ovk = oppiaineDto.getVuosiluokkakokonaisuudet().stream().findFirst().get();
        final String TYOTAVAT = "Uudet työtavat";
        ovk.setTyotavat(getTekstiosa(TYOTAVAT));
        ovk = oppiaineService.updateVuosiluokkakokonaisuudenSisalto(opsId, oppiaineDto.getId(), ovk);
        assertNotNull(ovk);
        final String TYOTAVAT_OTSIKKO = "otsikko_" + TYOTAVAT;
        assertEquals(TYOTAVAT_OTSIKKO, ovk.getTyotavat().getOtsikko().get().get(Kieli.FI));

        opsOppiaineDto = oppiaineService.get(opsId, oppiaineDto.getId());
        assertNotNull(opsOppiaineDto);
        oppiaineDto = opsOppiaineDto.getOppiaine();
        ovk = oppiaineDto.getVuosiluokkakokonaisuudet().stream().findFirst().get();
        assertEquals(TYOTAVAT_OTSIKKO, ovk.getTyotavat().getOtsikko().get().get(Kieli.FI));

        assertNotNull(oppiaineRepo.isOma(opsId, oppiaineDto.getId()));
        assertNull(oppiaineRepo.isOma(opsId, -1));
    }

    @Test
    public void testValinnainenOppiaine() {
        OpetussuunnitelmaDto ops = opetussuunnitelmaService.getOpetussuunnitelmaKaikki(opsId);

        VuosiluokkakokonaisuusDto vlk = new VuosiluokkakokonaisuusDto(vlkViiteRef);
        vlk.setNimi(Optional.of(lt("ykköskakkoset")));
        vlk = vuosiluokkakokonaisuusService.add(ops.getId(), vlk);
        assertNotNull(vlk);

        OppiaineDto oppiaineDto = new OppiaineDto();
        oppiaineDto.setTyyppi(OppiaineTyyppi.YHTEINEN);
        oppiaineDto.setValinnainenTyyppi(OppiaineValinnainenTyyppi.SOVELTAVA);
        oppiaineDto.setNimi(lt("Valinnainen"));
        oppiaineDto.setKoodiUri("koodikoodi");
        oppiaineDto.setTunniste(UUID.randomUUID());
        oppiaineDto.setKoosteinen(false);

        oppiaineDto = oppiaineService.add(opsId, oppiaineDto);
        assertNotNull(oppiaineDto);
        assertEquals(oppiaineDto.getValinnainenTyyppi(), OppiaineValinnainenTyyppi.SOVELTAVA);
        assertNull(oppiaineDto.getLiittyvaOppiaine());

        oppiaineDto.setLiittyvaOppiaine(Reference.of(oppiaineDto));

        oppiaineDto = oppiaineService.update(opsId, oppiaineDto).getOppiaine();
        assertNotNull(oppiaineDto.getLiittyvaOppiaine());
        assertEquals(oppiaineDto.getLiittyvaOppiaine().getId(), oppiaineDto.getId().toString());

        oppiaineService.delete(opsId, oppiaineDto.getId());
    }

    @Test
    @Rollback
    public void testOppiaineDeleteRestore() {
        OpetussuunnitelmaDto ops = createLukioOpetussuunnitelma();

        OppiaineDto oppiaineDto = new OppiaineDto();
        oppiaineDto.setTyyppi(OppiaineTyyppi.YHTEINEN);
        oppiaineDto.setNimi(lt("Äidinkieli"));
        oppiaineDto.setKoodiUri("koodi_123");
        oppiaineDto.setTunniste(UUID.randomUUID());

        oppiaineDto = oppiaineService.add(ops.getId(), oppiaineDto);
        assertNotNull(oppiaineDto);

        Assertions.assertThat(poistoService.getRemoved(ops.getId(), PoistetunTyyppi.OPPIAINE)).isEmpty();
        PoistettuOppiaineDto poistettuDto = oppiaineService.delete(ops.getId(), oppiaineDto.getId());
        Assertions.assertThat(poistettuDto.getOppiaine()).isEqualTo(oppiaineDto.getId());
        List<OppiaineDto> oppiaineet = oppiaineService.getAll(ops.getId());
        Assertions.assertThat(oppiaineet).isEmpty();

        List<Lops2019PoistettuDto> poistetut = poistoService.getRemoved(ops.getId(), PoistetunTyyppi.OPPIAINE);
        Assertions.assertThat(poistetut).isNotEmpty();
        Assertions.assertThat(poistetut.get(0).getPoistettuId()).isEqualTo(oppiaineDto.getId());

        poistoService.restoreOppiaine(ops.getId(), poistetut.get(0).getId());
        Assertions.assertThat(oppiaineService.getAll(ops.getId())).isNotEmpty();
        Assertions.assertThat(poistoService.getRemoved(ops.getId(), PoistetunTyyppi.OPPIAINE)).isEmpty();
    }

    @Test
    @Rollback
    public void testOppimaaraDeleteRestore() {
        OpetussuunnitelmaDto ops = createLukioOpetussuunnitelma();

        OppiaineDto oppiaineDto = new OppiaineDto();
        oppiaineDto.setTyyppi(OppiaineTyyppi.YHTEINEN);
        oppiaineDto.setNimi(lt("Äidinkieli"));
        oppiaineDto.setKoodiUri("koodi_123");
        oppiaineDto.setTunniste(UUID.randomUUID());

        UUID oppimaaraTunniste = UUID.randomUUID();
        OppiaineSuppeaDto oppimaaraDto = new OppiaineSuppeaDto();
        oppimaaraDto.setTyyppi(OppiaineTyyppi.YHTEINEN);
        oppimaaraDto.setNimi(lt("Suomen kieli ja kirjallisuus"));
        oppimaaraDto.setKoosteinen(false);
        oppimaaraDto.setTunniste(oppimaaraTunniste);

        oppiaineDto.setOppimaarat(Collections.singleton(oppimaaraDto));
        oppiaineDto.setKoosteinen(true);

        oppiaineDto = oppiaineService.add(ops.getId(), oppiaineDto);
        oppimaaraDto = oppiaineDto.getOppimaarat().iterator().next();
        assertNotNull(oppiaineDto);
        assertNotNull(oppiaineDto.getOppimaarat());
        assertEquals(1, oppiaineDto.getOppimaarat().size());

        Assertions.assertThat(poistoService.getRemoved(ops.getId(), PoistetunTyyppi.OPPIAINE)).isEmpty();
        oppiaineService.delete(ops.getId(), oppimaaraDto.getId());
        List<OppiaineDto> oppiaineet = oppiaineService.getAll(ops.getId());
        Assertions.assertThat(oppiaineet).hasSize(1);
        Assertions.assertThat(oppiaineet.get(0).getId()).isEqualTo(oppiaineDto.getId());
        Assertions.assertThat(oppiaineet.get(0).getOppimaarat()).isEmpty();

        List<Lops2019PoistettuDto> poistetut = poistoService.getRemoved(ops.getId(), PoistetunTyyppi.OPPIAINE);
        Assertions.assertThat(poistetut).hasSize(1);
        Assertions.assertThat(poistetut.get(0).getPoistettuId()).isEqualTo(oppimaaraDto.getId());

        poistoService.restoreOppiaine(ops.getId(), poistetut.get(0).getId());
        oppiaineet = oppiaineService.getAll(ops.getId());
        Assertions.assertThat(oppiaineet).hasSize(1);
        Assertions.assertThat(oppiaineet.get(0).getId()).isEqualTo(oppiaineDto.getId());
        Assertions.assertThat(oppiaineet.get(0).getOppimaarat()).isNotEmpty();
        Assertions.assertThat(oppiaineet.get(0).getOppimaarat().iterator().next().getTunniste()).isEqualTo(oppimaaraTunniste);
        Assertions.assertThat(poistoService.getRemoved(ops.getId(), PoistetunTyyppi.OPPIAINE)).isEmpty();
    }

    private static TekstiosaDto getTekstiosa(String suffiksi) {
        TekstiosaDto dto = new TekstiosaDto();
        dto.setOtsikko(Optional.of(lt("otsikko_" + suffiksi)));
        dto.setTeksti(Optional.of(lt("teksti_" + suffiksi)));
        return dto;
    }

}

