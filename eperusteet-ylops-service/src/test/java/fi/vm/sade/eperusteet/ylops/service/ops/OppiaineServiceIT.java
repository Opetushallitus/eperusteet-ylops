package fi.vm.sade.eperusteet.ylops.service.ops;

import fi.vm.sade.eperusteet.ylops.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.ylops.domain.Tila;
import fi.vm.sade.eperusteet.ylops.domain.Tyyppi;
import fi.vm.sade.eperusteet.ylops.domain.Vuosiluokka;
import fi.vm.sade.eperusteet.ylops.domain.Vuosiluokkakokonaisuusviite;
import fi.vm.sade.eperusteet.ylops.domain.lops2019.PoistetunTyyppi;
import fi.vm.sade.eperusteet.ylops.domain.oppiaine.OppiaineTyyppi;
import fi.vm.sade.eperusteet.ylops.domain.oppiaine.OppiaineValinnainenTyyppi;
import fi.vm.sade.eperusteet.ylops.domain.ops.Opetussuunnitelma;
import fi.vm.sade.eperusteet.ylops.domain.ops.OpsOppiaine;
import fi.vm.sade.eperusteet.ylops.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.ylops.dto.Reference;
import fi.vm.sade.eperusteet.ylops.dto.koodisto.KoodistoDto;
import fi.vm.sade.eperusteet.ylops.dto.koodisto.OrganisaatioDto;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.Lops2019PoistettuDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.KeskeinenSisaltoalueDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.KopioOppimaaraDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetuksenKeskeinensisaltoalueDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetuksenTavoiteDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaInfoDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaLuontiDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OppiaineDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OppiaineSuppeaDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OppiaineenVuosiluokkaDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OppiaineenVuosiluokkakokonaisuusDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpsOppiaineDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpsVuosiluokkakokonaisuusDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.PoistettuOppiaineDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.VuosiluokkakokonaisuusDto;
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
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static fi.vm.sade.eperusteet.ylops.test.util.TestUtils.lt;
import static fi.vm.sade.eperusteet.ylops.test.util.TestUtils.uniikkiString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

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
    private Reference vlkViiteRef3456;

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

        Vuosiluokkakokonaisuusviite viite12 = new Vuosiluokkakokonaisuusviite(UUID.randomUUID(), EnumSet.of(Vuosiluokka.VUOSILUOKKA_1, Vuosiluokka.VUOSILUOKKA_2));
        Vuosiluokkakokonaisuusviite viite3456 = new Vuosiluokkakokonaisuusviite(
                UUID.randomUUID(),
                EnumSet.of(Vuosiluokka.VUOSILUOKKA_3, Vuosiluokka.VUOSILUOKKA_4, Vuosiluokka.VUOSILUOKKA_5, Vuosiluokka.VUOSILUOKKA_6));
        this.vlkViiteRef = Reference.of(vlkViitteet.save(viite12));
        this.vlkViiteRef3456 = Reference.of(vlkViitteet.save(viite3456));
    }

    @Test
    @Ignore
    @Deprecated
    public void testPalautaYlempi() {
        OpetussuunnitelmaDto ylaOps = createOpsBasedOnPohja();

        oppiaineService.add(ylaOps.getId(), TestUtils.createOppiaine("oppiaine 1"));
        oppiaineService.add(ylaOps.getId(), TestUtils.createOppiaine("oppiaine 2"));
        oppiaineService.add(ylaOps.getId(), TestUtils.createOppiaine("oppiaine 3"));

        OpetussuunnitelmaLuontiDto alaOpsDto = createOpetussuunnitelmaLuonti(ylaOps);
        OpetussuunnitelmaDto alaOps = opetussuunnitelmaService.addOpetussuunnitelma(alaOpsDto);

        OppiaineDto oppiaine = oppiaineService.getAll(ylaOps.getId()).get(0);
        OpsOppiaineDto opsOppiaine = oppiaineService.kopioiMuokattavaksi(alaOps.getId(), oppiaine.getId(), false);
        assertNotEquals("Oppiaineet ovat samat", opsOppiaine.getOppiaine().getId(), oppiaine.getId());

        OpsOppiaineDto palautettuOpsOppiaine = oppiaineService.palautaYlempi(alaOps.getId(), opsOppiaine.getOppiaine().getId());
        assertEquals("Oppiaineet eivät ole samat", palautettuOpsOppiaine.getOppiaine().getId(), oppiaine.getId());

        opsOppiaine = oppiaineService.kopioiMuokattavaksi(alaOps.getId(), oppiaine.getId(), false);
        assertNotEquals("Oppiaineet ovat samat", opsOppiaine.getOppiaine().getId(), oppiaine.getId());

        oppiaineService.delete(ylaOps.getId(), oppiaine.getId());
        try {
            oppiaineService.palautaYlempi(alaOps.getId(), opsOppiaine.getOppiaine().getId());
            fail("Palauttamisen pitäisi epäonnistua");
        } catch (Exception e) {
            assertEquals(e.getClass(), BusinessRuleViolationException.class);
        }

    }

    /**
     * EP-3085: Pohjasta luodun opetussuunitelman oppimäärän poisto poisti oppimäärän myös pohjasta.
     * Testataan että tämä poisto estetään.
     */
    @Test
    public void testOppimaaraDeleteCantRemovePohjaOppimaara() {
        OpetussuunnitelmaDto ylaOps = createOpsBasedOnPohja();
        OppiaineDto vieraatKielet = addVieraatKieletOppiaineWithOppimaara(ylaOps);
        OpetussuunnitelmaDto alaOps = createOpsBasedOnOps(ylaOps, luontidto -> {
            luontidto.setLuontityyppi(OpetussuunnitelmaLuontiDto.Luontityyppi.LEGACY);
        });

        assertThatThrownBy(() -> oppiaineService.delete(alaOps.getId(), vieraatKielet.getOppimaarat().iterator().next().getId()))
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessage("Oppiaine tulee opetussuunnitelman pohjasta, joten sitä ei voi poistaa.");
    }

    private OpetussuunnitelmaDto createOpsBasedOnPohja() {
        OpetussuunnitelmaDto pohjaOps = opetussuunnitelmaService.getOpetussuunnitelmaKaikki(opsId);
        opetussuunnitelmaService.updateTila(pohjaOps.getId(), Tila.VALMIS);

        OpetussuunnitelmaLuontiDto newOps = createOpetussuunnitelmaLuonti(pohjaOps);
        newOps.setLuontityyppi(OpetussuunnitelmaLuontiDto.Luontityyppi.KOPIO);
        return opetussuunnitelmaService.addOpetussuunnitelma(newOps);
    }

    private OppiaineDto addVieraatKieletOppiaineWithOppimaara(OpetussuunnitelmaDto ops) {
        OppiaineDto vieraatKielet = TestUtils.createKoosteinenOppiaine("Vieraat kielet");
        vieraatKielet.setOppimaarat(Collections.singleton(TestUtils.createOppimaara("Ranska B2")));
        return oppiaineService.add(ops.getId(), vieraatKielet);
    }

    private OpetussuunnitelmaDto createOpsBasedOnOps(OpetussuunnitelmaDto ylaOps) {
        return createOpsBasedOnOps(ylaOps, null);
    }

    private OpetussuunnitelmaDto createOpsBasedOnOps(OpetussuunnitelmaDto ylaOps, Consumer<OpetussuunnitelmaLuontiDto> opsfn) {
        OpetussuunnitelmaLuontiDto alaOps = createOpetussuunnitelmaLuonti(ylaOps);
        alaOps.setLuontityyppi(OpetussuunnitelmaLuontiDto.Luontityyppi.VIITTEILLA);
        if (opsfn != null) {
            opsfn.accept(alaOps);
        }
        return opetussuunnitelmaService.addOpetussuunnitelma(alaOps);
    }

    private OpetussuunnitelmaLuontiDto createOpetussuunnitelmaLuonti(OpetussuunnitelmaDto pohjaOps) {
        return createOpetussuunnitelmaLuonti(pohjaOps, null);
    }

    private OpetussuunnitelmaLuontiDto createOpetussuunnitelmaLuonti(OpetussuunnitelmaDto pohjaOps, Consumer<OpetussuunnitelmaLuontiDto> opsfn) {
        OpetussuunnitelmaLuontiDto ops = new OpetussuunnitelmaLuontiDto();
        ops.setNimi(lt(uniikkiString()));
        ops.setKuvaus(lt(uniikkiString()));
        ops.setTila(Tila.LUONNOS);
        ops.setTyyppi(Tyyppi.OPS);
        ops.setKoulutustyyppi(KoulutusTyyppi.PERUSOPETUS);
        if (pohjaOps.getTyyppi().equals(Tyyppi.POHJA)) {
            ops.setLuontityyppi(OpetussuunnitelmaLuontiDto.Luontityyppi.KOPIO);
        }

        KoodistoDto kunta = new KoodistoDto();
        kunta.setKoodiUri("kunta_837");
        ops.setKunnat(new HashSet<>(Collections.singleton(kunta)));
        OrganisaatioDto kouluDto = new OrganisaatioDto();
        kouluDto.setNimi(lt("Etelä-Hervannan koulu"));
        kouluDto.setOid("1.2.246.562.10.00000000001");
        ops.setOrganisaatiot(new HashSet<>(Collections.singleton(kouluDto)));
        ops.setVuosiluokkakokonaisuudet(pohjaOps.getVuosiluokkakokonaisuudet().stream().map(pohjaVlk -> {
            OpsVuosiluokkakokonaisuusDto ovlk = new OpsVuosiluokkakokonaisuusDto();
            ovlk.setVuosiluokkakokonaisuus(new VuosiluokkakokonaisuusDto(pohjaVlk.getVuosiluokkakokonaisuus().getTunniste()));
            return ovlk;
        }).collect(Collectors.toSet()));

        ops.setPohja(Reference.of(pohjaOps.getId()));

        if (opsfn != null) {
            opsfn.accept(ops);
        }
        return ops;
    }

    /**
     * Jos alemman tason opsin oppiaine irroitetaan ylemmän tason opsista, ei oppimäärän poiston pitäisi enää
     * vaikuttaa yllemmän tason oppimääriin.
     */
    @Test
    public void testAbleToRemoveIrrotettuOppimaara() {
        OpetussuunnitelmaDto ylaOps = createOpsBasedOnPohja();
        OppiaineDto vieraatKielet = addVieraatKieletOppiaineWithOppimaara(ylaOps);

        OpetussuunnitelmaDto alaOps = createOpsBasedOnOps(ylaOps, luontiDto -> {
            luontiDto.setLuontityyppi(OpetussuunnitelmaLuontiDto.Luontityyppi.LEGACY);
        });

        OpsOppiaineDto vieraatkieletKopio = oppiaineService.kopioiMuokattavaksi(alaOps.getId(), vieraatKielet.getId(), false);
        oppiaineService.delete(alaOps.getId(), vieraatkieletKopio.getOppiaine().getOppimaarat().iterator().next().getId());

        OpetussuunnitelmaDto ylaOpsAfterDelete = opetussuunnitelmaService.getOpetussuunnitelmaKaikki(ylaOps.getId());
        Set<OppiaineSuppeaDto> ylaOpsinOppimaara = getOppimaarat(ylaOpsAfterDelete);
        assertThat(ylaOpsinOppimaara).isNotEmpty();

        OpetussuunnitelmaDto alaOpsAfterDelete = opetussuunnitelmaService.getOpetussuunnitelmaKaikki(alaOps.getId());
        Set<OppiaineSuppeaDto> alaOpsinOppimaara = getOppimaarat(alaOpsAfterDelete);
        assertThat(alaOpsinOppimaara).isEmpty();
    }

    private Set<OppiaineSuppeaDto> getOppimaarat(OpetussuunnitelmaDto ops) {
        return ops.getOppiaineet()
                .stream()
                .findFirst()
                .get()
                .getOppiaine()
                .getOppimaarat();
    }

    @Test
    @Transactional
    public void testCanAddOppimaara() {
        OpetussuunnitelmaDto ops = opetussuunnitelmaService.getOpetussuunnitelmaKaikki(opsId);
        OppiaineDto vieraatKielet = addVieraatKieletOppiaineWithOppimaara(ops);
        OpetussuunnitelmaDto ylaOps = createOpsBasedOnPohja();
        OpetussuunnitelmaDto alaOps = createOpsBasedOnOps(ylaOps);

        Opetussuunnitelma alinPohja = opetussuunnitelmaRepository.findOne(alaOps.getId()).getAlinPohja();
        OpsOppiaine alimmanPohjanOppiaine = alinPohja.getOppiaineet().stream().filter(oa -> !oa.getOppiaine().getOppimaarat().isEmpty()).findFirst().orElseThrow();
        UUID tunniste = alimmanPohjanOppiaine.getOppiaine().getOppimaarat().iterator().next().getTunniste();

        OppiaineDto alaOpsinOppiaine = oppiaineService.addCopyOppimaara(
                alaOps.getId(),
                alaOps.getOppiaineet().iterator().next().getOppiaine().getId(),
                KopioOppimaaraDto.builder()
                        .tunniste(tunniste)
                        .omaNimi(LokalisoituTekstiDto.of("uusi vieras kieli"))
                        .build());
        OpsOppiaineDto opsOppiaine = oppiaineService.get(alaOps.getId(), alaOpsinOppiaine.getId());
        assertThat(opsOppiaine.isOma()).isTrue();
    }

    @Test
    public void testMuokattavaksiKopioiminen() {
        OpetussuunnitelmaDto ylaOps = createOpsBasedOnPohja();

        OppiaineDto oppiainecreate = TestUtils.createOppiaine("oppiaine 1");
        OppiaineenVuosiluokkakokonaisuusDto ovk = new OppiaineenVuosiluokkakokonaisuusDto();
        ovk.setArviointi(getTekstiosa("Arviointi"));
        ovk.setTehtava(getTekstiosa("Tehtävä"));
        ovk.setTyotavat(getTekstiosa("Työtavat"));
        ovk.setOhjaus(getTekstiosa("Ohjaus"));
        ovk.setVuosiluokkakokonaisuus(vlkViiteRef);
        oppiainecreate.setVuosiluokkakokonaisuudet(Collections.singleton(ovk));
        OppiaineDto oppiaine = oppiaineService.add(ylaOps.getId(), oppiainecreate);

        OpetussuunnitelmaLuontiDto alaOpsDto = createOpetussuunnitelmaLuonti(ylaOps, luontidto -> {
            luontidto.setLuontityyppi(OpetussuunnitelmaLuontiDto.Luontityyppi.LEGACY);
        });
        OpetussuunnitelmaDto alaOps = opetussuunnitelmaService.addOpetussuunnitelma(alaOpsDto);

        OpsOppiaineDto opsOppiaine = oppiaineService.kopioiMuokattavaksi(alaOps.getId(), oppiaine.getId(), true);
        assertNotEquals("Oppiaineet ovat samat", opsOppiaine.getOppiaine().getId(), oppiaine.getId());
        assertNull(opsOppiaine.getOppiaine().getTehtava().getTeksti());
        assertEquals(opsOppiaine.getOppiaine().getTunniste(), oppiaine.getTunniste());
        opsOppiaine.getOppiaine().getVuosiluokkakokonaisuudet().forEach(vlk -> {
            assertNull(vlk.getArviointi().getTeksti());
            assertNull(vlk.getOhjaus().getTeksti());
            assertNull(vlk.getTavoitteistaJohdetutOppimisenTavoitteet().getTeksti());
            assertNull(vlk.getTehtava().getTeksti());
            assertNull(vlk.getTyotavat().getTeksti());
            assertNull(vlk.getYleistavoitteet().getTeksti());
        });
    }

    @Test
    public void testValinnainenAine() {
        OpetussuunnitelmaDto ops = opetussuunnitelmaService.getOpetussuunnitelmaKaikki(opsId);

        VuosiluokkakokonaisuusDto vlk = new VuosiluokkakokonaisuusDto(vlkViiteRef);
        OpsVuosiluokkakokonaisuusDto opsVlkDto = new OpsVuosiluokkakokonaisuusDto();
        vlk.setNimi(lt("ykköskakkoset"));
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
                .get()
                ;

        List<OpetuksenTavoiteDto> tavoitteet = new ArrayList<>();
        tavoitteet.add(OpetuksenTavoiteDto.builder()
                        .tavoite(TestUtils.lt("hello"))
                        .sisaltoalueet(Collections.singleton(OpetuksenKeskeinensisaltoalueDto.builder()
                                .sisaltoalueet(KeskeinenSisaltoalueDto.builder()
                                        .kuvaus(TestUtils.lt("world"))
                                        .build())
                                .build()))
                .build());
        oppiaineService.updateValinnaisenVuosiluokanSisalto(opsId, valinnainen.getId(), vuosiluokka.getId(), tavoitteet);

        OpsOppiaineDto valinanneOppiaine = oppiaineService.get(opsId, valinnainen.getId());
        testOppiaineenTavoitteenTekstit(valinanneOppiaine, 0, "hello", "world");
        UUID tavoite1Tunniste = vuosiluokanTavoitteet(valinanneOppiaine, vuosiluokka.getId()).get(0).getTunniste();

        tavoitteet = vuosiluokanTavoitteet(valinanneOppiaine, vuosiluokka.getId());
        tavoitteet.add(OpetuksenTavoiteDto.builder()
                    .tavoite(TestUtils.lt("foo"))
                    .sisaltoalueet(Collections.singleton(OpetuksenKeskeinensisaltoalueDto.builder()
                            .sisaltoalueet(KeskeinenSisaltoalueDto.builder()
                                    .kuvaus(TestUtils.lt("bar"))
                                    .build())
                            .build()))
                    .build());
        oppiaineService.updateValinnaisenVuosiluokanSisalto(opsId, valinnainen.getId(), vuosiluokka.getId(), tavoitteet);

        valinanneOppiaine = oppiaineService.get(opsId, valinnainen.getId());
        testOppiaineenTavoitteenTekstit(valinanneOppiaine, 0, "hello", "world");
        assertEquals(tavoite1Tunniste, vuosiluokanTavoitteet(valinanneOppiaine, vuosiluokka.getId()).get(0).getTunniste());
        testOppiaineenTavoitteenTekstit(valinanneOppiaine, 1, "foo", "bar");
    }

    private List<OpetuksenTavoiteDto> vuosiluokanTavoitteet(OpsOppiaineDto valinanneOppiaine, Long vuosiluokkaId) {
        return valinanneOppiaine.getOppiaine().getVuosiluokkakokonaisuudet().stream()
                .map(OppiaineenVuosiluokkakokonaisuusDto::getVuosiluokat)
                .flatMap(Collection::stream)
                .filter(vl -> vl.getId().equals(vuosiluokkaId))
                .findFirst()
                .map(OppiaineenVuosiluokkaDto::getTavoitteet)
                .get();
    }

    private void testOppiaineenTavoitteenTekstit(OpsOppiaineDto oppiaine, int rivi, String otsikko, String kuvaus) {
        OpetuksenTavoiteDto tavoite = oppiaine.getOppiaine().getVuosiluokkakokonaisuudet().iterator().next().getVuosiluokat().iterator().next().getTavoitteet().get(rivi);
        assertEquals(otsikko, tavoite.getTavoite().get(Kieli.FI));
        assertEquals(kuvaus, tavoite.getSisaltoalueet().iterator().next().getSisaltoalueet().getKuvaus().get(Kieli.FI));
    }

    @Test
    public void testCRUD() {
        OpetussuunnitelmaDto ops = opetussuunnitelmaService.getOpetussuunnitelmaKaikki(opsId);

        VuosiluokkakokonaisuusDto vlk = new VuosiluokkakokonaisuusDto(vlkViiteRef);
        vlk.setNimi(lt("ykköskakkoset"));
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
        assertEquals(TYOTAVAT_OTSIKKO, ovk.getTyotavat().getOtsikko().get(Kieli.FI));

        opsOppiaineDto = oppiaineService.get(opsId, oppiaineDto.getId());
        assertNotNull(opsOppiaineDto);
        oppiaineDto = opsOppiaineDto.getOppiaine();
        ovk = oppiaineDto.getVuosiluokkakokonaisuudet().stream().findFirst().get();
        assertEquals(TYOTAVAT_OTSIKKO, ovk.getTyotavat().getOtsikko().get(Kieli.FI));

        assertNotNull(oppiaineRepo.isOma(opsId, oppiaineDto.getId()));
        assertNull(oppiaineRepo.isOma(opsId, -1));
    }

    @Test
    public void testValinnainenOppiaine() {
        OpetussuunnitelmaDto ops = opetussuunnitelmaService.getOpetussuunnitelmaKaikki(opsId);

        VuosiluokkakokonaisuusDto vlk = new VuosiluokkakokonaisuusDto(vlkViiteRef);
        vlk.setNimi(lt("ykköskakkoset"));
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

    @Test
    public void perustopetusOpsVlkPoistoJaLisaysTest() {
        VuosiluokkakokonaisuusDto vlk = new VuosiluokkakokonaisuusDto(vlkViiteRef);
        vlk.setNimi(lt("ykköskakkoset"));
        vlk = vuosiluokkakokonaisuusService.add(opsId, vlk);

        VuosiluokkakokonaisuusDto vlk3456 = new VuosiluokkakokonaisuusDto(vlkViiteRef3456);
        vlk3456.setNimi(lt("kolmoskutoset"));
        vlk3456 = vuosiluokkakokonaisuusService.add(opsId, vlk3456);


        OpetussuunnitelmaDto pohjaOps = opetussuunnitelmaService.getOpetussuunnitelmaKaikki(opsId);
        OpetussuunnitelmaDto ops = createOpsBasedOnPohja();
        assertThat(ops.getVuosiluokkakokonaisuudet()).hasSize(2);
        assertThat(ops.getVuosiluokkakokonaisuudet().stream().map(opsvlk -> opsvlk.getVuosiluokkakokonaisuus().getTunniste().toString()).collect(Collectors.toList()))
                .containsExactlyInAnyOrder(vlkViiteRef.toString(), vlkViiteRef3456.toString());

        Map<String, Long> pohjanVlkTunnisteIdt = pohjaOps.getVuosiluokkakokonaisuudet().stream()
                .collect(Collectors.toMap(ovlk -> ovlk.getVuosiluokkakokonaisuus().getTunniste().toString(), ovlk -> ovlk.getVuosiluokkakokonaisuus().getId()));

        ops.setVuosiluokkakokonaisuudet(ops.getVuosiluokkakokonaisuudet().stream()
                .filter(ovlk -> ovlk.getVuosiluokkakokonaisuus().getTunniste().toString().equals(vlkViiteRef.toString()))
                .collect(Collectors.toSet()));

        ops = opetussuunnitelmaService.updateOpetussuunnitelma(ops);
        assertThat(ops.getVuosiluokkakokonaisuudet()).hasSize(1);
        assertThat(ops.getVuosiluokkakokonaisuudet().stream().map(opsvlk -> opsvlk.getVuosiluokkakokonaisuus().getTunniste().toString()).collect(Collectors.toList()))
                .containsExactlyInAnyOrder(vlkViiteRef.toString());

        ops.getVuosiluokkakokonaisuudet().addAll(pohjaOps.getVuosiluokkakokonaisuudet().stream()
                .filter(ovlk -> ovlk.getVuosiluokkakokonaisuus().getTunniste().toString().equals(vlkViiteRef3456.toString())).collect(Collectors.toSet()));

        //tarkistetaan että uusi vlk on luotu uudella id:llä
        ops = opetussuunnitelmaService.updateOpetussuunnitelma(ops);
        assertThat(ops.getVuosiluokkakokonaisuudet()).hasSize(2);
        assertThat(ops.getVuosiluokkakokonaisuudet().stream().map(opsvlk -> opsvlk.getVuosiluokkakokonaisuus().getTunniste().toString()).collect(Collectors.toList()))
                .containsExactlyInAnyOrder(vlkViiteRef.toString(), vlkViiteRef3456.toString());
        assertThat(pohjanVlkTunnisteIdt.get(vlkViiteRef.toString()))
                .isNotEqualTo(ops.getVuosiluokkakokonaisuudet().stream()
                        .filter(ovlk -> ovlk.getVuosiluokkakokonaisuus().getTunniste().toString().equals(vlkViiteRef.toString())).findFirst().get()
                        .getVuosiluokkakokonaisuus().getId());
        assertThat(pohjanVlkTunnisteIdt.get(vlkViiteRef3456.toString()))
                .isNotEqualTo(ops.getVuosiluokkakokonaisuudet().stream()
                        .filter(ovlk -> ovlk.getVuosiluokkakokonaisuus().getTunniste().toString().equals(vlkViiteRef3456.toString())).findFirst().get()
                        .getVuosiluokkakokonaisuus().getId());
        assertThat(ops.getVuosiluokkakokonaisuudet()).extracting("oma").containsExactlyInAnyOrder(true,true);
    }

    @Test
    public void testVlkOpsPohjana() {
        VuosiluokkakokonaisuusDto vlk = new VuosiluokkakokonaisuusDto(vlkViiteRef);
        vlk.setNimi(lt("ykköskakkoset"));
        vlk = vuosiluokkakokonaisuusService.add(opsId, vlk);

        VuosiluokkakokonaisuusDto vlk3456 = new VuosiluokkakokonaisuusDto(vlkViiteRef3456);
        vlk3456.setNimi(lt("kolmoskutoset"));
        vlk3456 = vuosiluokkakokonaisuusService.add(opsId, vlk3456);

        OpetussuunnitelmaDto ops = createOpsBasedOnPohja();
        OpetussuunnitelmaLuontiDto newOps = createOpetussuunnitelmaLuonti(ops);
        OpetussuunnitelmaDto ops2 = opetussuunnitelmaService.addOpetussuunnitelma(newOps);

        ops2.setVuosiluokkakokonaisuudet(ops2.getVuosiluokkakokonaisuudet().stream()
                .filter(ovlk -> ovlk.getVuosiluokkakokonaisuus().getTunniste().toString().equals(vlkViiteRef.toString()))
                .collect(Collectors.toSet()));

        // poistaminen ei poista pohjasta
        ops2 = opetussuunnitelmaService.updateOpetussuunnitelma(ops2);
        assertThat(ops2.getVuosiluokkakokonaisuudet()).hasSize(1);
        assertThat(ops2.getVuosiluokkakokonaisuudet().stream().map(opsvlk -> opsvlk.getVuosiluokkakokonaisuus().getTunniste().toString()).collect(Collectors.toList()))
                .containsExactlyInAnyOrder(vlkViiteRef.toString());

        ops = opetussuunnitelmaService.getOpetussuunnitelmaKaikki(ops.getId());
        assertThat(ops.getVuosiluokkakokonaisuudet()).hasSize(2);

        // lisääminen lisää suoran viitteen koska pohja ei ole tyypiltaan pohja
        ops2.getVuosiluokkakokonaisuudet().addAll(ops.getVuosiluokkakokonaisuudet().stream()
                .filter(ovlk -> ovlk.getVuosiluokkakokonaisuus().getTunniste().toString().equals(vlkViiteRef3456.toString()))
                .collect(Collectors.toSet()));

        ops2 = opetussuunnitelmaService.updateOpetussuunnitelma(ops2);
        assertThat(ops2.getVuosiluokkakokonaisuudet()).hasSize(2);
        assertThat(ops2.getVuosiluokkakokonaisuudet()).extracting("oma").containsExactlyInAnyOrder(true,true);
    }

    @Test
    public void testOppiaineenPoistoJaLisaysKunnanOpsiin() {
        OpetussuunnitelmaDto kunnanOps = createOpsBasedOnPohja();

        VuosiluokkakokonaisuusDto vlk = new VuosiluokkakokonaisuusDto(vlkViiteRef);
        vlk.setNimi(lt("ykköskakkoset"));
        vlk = vuosiluokkakokonaisuusService.add(kunnanOps.getId(), vlk);

        OppiaineDto oppiaineDto = createValinnainen(kunnanOps.getId(), vlk);

        OpetussuunnitelmaDto koulunOps = createOpetussuunnitelma(ops -> {
            ops.setPohja(Reference.of(kunnanOps.getId()));
            ops.setLuontityyppi(OpetussuunnitelmaLuontiDto.Luontityyppi.VIITTEILLA);
        });
        VuosiluokkakokonaisuusDto koulunVlk = new VuosiluokkakokonaisuusDto(vlkViiteRef);
        vlk.setNimi(lt("ykköskakkoset"));
        koulunVlk = vuosiluokkakokonaisuusService.add(koulunOps.getId(), koulunVlk);

        assertThat(oppiaineService.getAll(koulunOps.getId()).stream().anyMatch(oa -> oa.getNimi().get(Kieli.FI).equals(oppiaineDto.getNimi().get(Kieli.FI)))).isTrue();

        OppiaineDto oppiaineJalkikateenlisattyDto = createValinnainen(kunnanOps.getId(), vlk);
        assertThat(oppiaineService.getAll(koulunOps.getId()).stream().anyMatch(oa -> oa.getNimi().get(Kieli.FI).equals(oppiaineJalkikateenlisattyDto.getNimi().get(Kieli.FI)))).isTrue();

        oppiaineService.delete(kunnanOps.getId(), oppiaineDto.getId());
        assertThat(oppiaineService.getAll(kunnanOps.getId()).stream().anyMatch(oa -> oa.getNimi().get(Kieli.FI).equals(oppiaineDto.getNimi().get(Kieli.FI)))).isFalse();
        assertThat(oppiaineService.getAll(koulunOps.getId()).stream().anyMatch(oa -> oa.getNimi().get(Kieli.FI).equals(oppiaineDto.getNimi().get(Kieli.FI)))).isFalse();

        oppiaineService.delete(kunnanOps.getId(), oppiaineJalkikateenlisattyDto.getId());
        assertThat(oppiaineService.getAll(kunnanOps.getId()).stream().anyMatch(oa -> oa.getNimi().get(Kieli.FI).equals(oppiaineJalkikateenlisattyDto.getNimi().get(Kieli.FI)))).isFalse();
        assertThat(oppiaineService.getAll(koulunOps.getId()).stream().anyMatch(oa -> oa.getNimi().get(Kieli.FI).equals(oppiaineJalkikateenlisattyDto.getNimi().get(Kieli.FI)))).isFalse();

    }

    @Test
    public void testOppiainePoistettuJaPalautusOpsiin() {
        OpetussuunnitelmaDto kunnanOps = createOpsBasedOnPohja();

        VuosiluokkakokonaisuusDto vlk = new VuosiluokkakokonaisuusDto(vlkViiteRef);
        vlk.setNimi(lt("ykköskakkoset"));
        vlk = vuosiluokkakokonaisuusService.add(kunnanOps.getId(), vlk);

        OppiaineDto oppiaineDto = createValinnainen(kunnanOps.getId(), vlk);

        OpetussuunnitelmaDto koulunOps = createOpetussuunnitelma(ops -> {
            ops.setPohja(Reference.of(kunnanOps.getId()));
            ops.setLuontityyppi(OpetussuunnitelmaLuontiDto.Luontityyppi.VIITTEILLA);
        });

        assertThat(oppiaineService.getAll(kunnanOps.getId()).stream().anyMatch(oa -> oa.getNimi().get(Kieli.FI).equals(oppiaineDto.getNimi().get(Kieli.FI)))).isTrue();
        assertThat(oppiaineService.getAll(koulunOps.getId()).stream().anyMatch(oa -> oa.getNimi().get(Kieli.FI).equals(oppiaineDto.getNimi().get(Kieli.FI)))).isTrue();

        oppiaineService.delete(kunnanOps.getId(), oppiaineDto.getId());
        assertThat(oppiaineService.getAll(kunnanOps.getId()).stream().anyMatch(oa -> oa.getNimi().get(Kieli.FI).equals(oppiaineDto.getNimi().get(Kieli.FI)))).isFalse();
        assertThat(oppiaineService.getAll(koulunOps.getId()).stream().anyMatch(oa -> oa.getNimi().get(Kieli.FI).equals(oppiaineDto.getNimi().get(Kieli.FI)))).isFalse();

        poistoService.restoreOppiaine(kunnanOps.getId(), poistoService.getRemoved(kunnanOps.getId(), PoistetunTyyppi.OPPIAINE).get(0).getId());
        assertThat(oppiaineService.getAll(kunnanOps.getId()).stream().anyMatch(oa -> oa.getNimi().get(Kieli.FI).equals(oppiaineDto.getNimi().get(Kieli.FI)))).isTrue();
        assertThat(oppiaineService.getAll(koulunOps.getId()).stream().anyMatch(oa -> oa.getNimi().get(Kieli.FI).equals(oppiaineDto.getNimi().get(Kieli.FI)))).isTrue();
    }

    private static TekstiosaDto getTekstiosa(String suffiksi) {
        TekstiosaDto dto = new TekstiosaDto();
        dto.setOtsikko(lt("otsikko_" + suffiksi));
        dto.setTeksti(lt("teksti_" + suffiksi));
        return dto;
    }

    private OppiaineDto createValinnainen(Long opsId, VuosiluokkakokonaisuusDto vlk) {
        OppiaineDto oppiaineDto = new OppiaineDto();
        oppiaineDto.setTyyppi(OppiaineTyyppi.YHTEINEN);
        oppiaineDto.setValinnainenTyyppi(OppiaineValinnainenTyyppi.SOVELTAVA);
        oppiaineDto.setNimi(lt(uniikkiString()));
        oppiaineDto.setKoodiUri("koodikoodi");
        oppiaineDto.setTunniste(UUID.randomUUID());
        oppiaineDto.setKoosteinen(false);
        oppiaineDto.setVuosiluokkakokonaisuudet(Set.of(OppiaineenVuosiluokkakokonaisuusDto.builder().vuosiluokkakokonaisuus(Reference.of(vlk.getId())).build()));

        return  oppiaineService.addValinnainen(opsId, oppiaineDto, vlk.getId(), Set.of(Vuosiluokka.VUOSILUOKKA_1, Vuosiluokka.VUOSILUOKKA_2), Collections.emptyList(), null, null, false);
    }

}

