package fi.vm.sade.eperusteet.ylops.service.tpo;

import fi.vm.sade.eperusteet.ylops.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.ylops.domain.Tila;
import fi.vm.sade.eperusteet.ylops.domain.Tyyppi;
import fi.vm.sade.eperusteet.ylops.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.ylops.dto.OpetussuunnitelmaExportDto;
import fi.vm.sade.eperusteet.ylops.dto.Reference;
import fi.vm.sade.eperusteet.ylops.dto.koodisto.KoodistoDto;
import fi.vm.sade.eperusteet.ylops.dto.koodisto.OrganisaatioDto;
import fi.vm.sade.eperusteet.ylops.dto.navigation.NavigationNodeDto;
import fi.vm.sade.eperusteet.ylops.dto.navigation.NavigationType;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaLuontiDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.TPOOpetuksenSisaltoDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.tpo.PerusteTaiteenosaDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.tpo.TpoPerusteenTaiteenalaDto;
import fi.vm.sade.eperusteet.ylops.dto.tpo.TaiteenalaDto;
import fi.vm.sade.eperusteet.ylops.dto.tpo.TaiteenosaDto;
import fi.vm.sade.eperusteet.ylops.dto.tpo.export.OpetussuunnitelmaExportTpoDto;
import fi.vm.sade.eperusteet.ylops.service.exception.BusinessRuleViolationException;
import fi.vm.sade.eperusteet.ylops.service.ops.OpsDispatcher;
import fi.vm.sade.eperusteet.ylops.service.ops.OpsExport;
import fi.vm.sade.eperusteet.ylops.test.AbstractIntegrationTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import static fi.vm.sade.eperusteet.ylops.test.util.TestUtils.lt;
import static fi.vm.sade.eperusteet.ylops.test.util.TestUtils.uniikkiString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

@Transactional
public class TaiteenperusopetusServiceIT extends AbstractIntegrationTest {

    private static final String DIAARINUMERO = "tpo-diaarinumero";
    private static final String MUSIIKKI = "taiteenalat_musiikki";
    private static final String TANSSI = "taiteenalat_tanssi";

    @Autowired
    private TaiteenperusopetusService taiteenperusopetusService;

    @Autowired
    private OpsDispatcher dispatcher;

    private OpetussuunnitelmaDto ops;

    @Before
    public void setUp() {
        ops = createOps();
    }

    @Test
    public void testPerusteenTaiteenalatLuetaanPerusteelta() {
        TPOOpetuksenSisaltoDto perusteenSisalto = taiteenperusopetusService.getPerusteSisalto(ops.getId());
        assertThat(perusteenSisalto.getTaiteenalat())
                .extracting(taiteenala -> taiteenala.getKoodi().getUri())
                .containsExactly(MUSIIKKI, TANSSI);

        TpoPerusteenTaiteenalaDto musiikki = perusteenSisalto.getTaiteenalat().get(0);
        assertThat(musiikki.getNimi().get(Kieli.FI)).isEqualTo("Musiikki");
        assertThat(musiikki.getTaiteenOsat())
                .extracting(osa -> osa.getNimi().get(Kieli.FI))
                .containsExactly("Yhteismusisointi", "Musiikin hahmotus");
    }

    @Test
    public void testPerusteenTaiteenalaHaetaanKoodilla() {
        TpoPerusteenTaiteenalaDto musiikki = taiteenperusopetusService.getPerusteenTaiteenala(ops.getId(), MUSIIKKI);
        assertThat(musiikki.getNimi().get(Kieli.FI)).isEqualTo("Musiikki");
        assertThat(musiikki.getTaiteenOsat()).hasSize(2);

        assertThatThrownBy(() -> taiteenperusopetusService.getPerusteenTaiteenala(ops.getId(), "taiteenalat_sirkus"))
                .isInstanceOf(BusinessRuleViolationException.class);
    }

    @Test
    public void testPerusteenTaiteenosaHaetaanIdlla() {
        PerusteTaiteenosaDto yhteismusisointi = taiteenperusopetusService.getPerusteenTaiteenosa(ops.getId(), 5290L);
        assertThat(yhteismusisointi.getNimi().get(Kieli.FI)).isEqualTo("Yhteismusisointi");

        assertThatThrownBy(() -> taiteenperusopetusService.getPerusteenTaiteenosa(ops.getId(), 1L))
                .isInstanceOf(BusinessRuleViolationException.class);
    }

    @Test
    public void testTaiteenalatEivatPaadyTekstikappaleiksi() {
        assertThat(ops.getTekstit().getLapset()).hasSize(1);
        assertThat(ops.getTekstit().getLapset().get(0).getTekstiKappale().getNimi().get(Kieli.FI)).isEqualTo("Teksti");
    }

    @Test
    public void testTaiteenalanElinkaari() {
        assertThat(taiteenperusopetusService.getTaiteenalat(ops.getId())).isEmpty();

        TaiteenalaDto lisatty = taiteenperusopetusService.addTaiteenala(ops.getId(), taiteenala(MUSIIKKI));
        assertThat(lisatty.getId()).isNotNull();
        assertThat(lisatty.getKoodi()).isEqualTo(MUSIIKKI);
        assertThat(lisatty.getNimi().get(Kieli.FI)).isEqualTo("Musiikki");
        assertThat(taiteenperusopetusService.getTaiteenalat(ops.getId())).hasSize(1);

        TaiteenalaDto muokattu = lisatty;
        muokattu.setPaikallinenTarkennus(lt("Paikallinen tarkennus"));
        muokattu = taiteenperusopetusService.updateTaiteenala(ops.getId(), lisatty.getId(), muokattu);

        assertThat(muokattu.getPaikallinenTarkennus().get(Kieli.FI)).isEqualTo("Paikallinen tarkennus");
        assertThat(muokattu.getKoodi()).isEqualTo(MUSIIKKI);

        taiteenperusopetusService.removeTaiteenala(ops.getId(), lisatty.getId());
        assertThat(taiteenperusopetusService.getTaiteenalat(ops.getId())).isEmpty();
    }

    @Test
    public void testTaiteenosatLuodaanPerusteenTaiteenosista() {
        TaiteenalaDto lisatty = taiteenperusopetusService.addTaiteenala(ops.getId(), taiteenala(MUSIIKKI));
        assertThat(lisatty.getTaiteenosat())
                .extracting(TaiteenosaDto::getPerusteenTaiteenosanId)
                .containsExactly(5290L, 5292L);
        assertThat(lisatty.getTaiteenosat()).extracting(TaiteenosaDto::getId).doesNotContainNull();

        assertThat(taiteenperusopetusService.getTaiteenala(ops.getId(), lisatty.getId()).getTaiteenosat())
                .hasSize(2);
    }

    @Test
    public void testTaiteenosaHaetaanIdlla() {
        TaiteenalaDto taiteenala = taiteenperusopetusService.addTaiteenala(ops.getId(), taiteenala(MUSIIKKI));

        TaiteenosaDto haettu = taiteenperusopetusService.getTaiteenosa(
                ops.getId(), taiteenosa(taiteenala, 5290L).getId());
        assertThat(haettu.getPerusteenTaiteenosanId()).isEqualTo(5290L);

        assertThatThrownBy(() -> taiteenperusopetusService.getTaiteenosa(ops.getId(), 1L))
                .isInstanceOf(BusinessRuleViolationException.class);
    }

    @Test
    public void testTaiteenosaaTarkennetaanOmanaSisaltona() {
        TaiteenalaDto taiteenala = taiteenperusopetusService.addTaiteenala(ops.getId(), taiteenala(MUSIIKKI));
        TaiteenosaDto yhteismusisointi = taiteenosa(taiteenala, 5290L);

        yhteismusisointi.setPaikallinenTarkennus(lt("Yhteismusisoinnin tarkennus"));
        TaiteenosaDto muokattu = taiteenperusopetusService.updateTaiteenosa(
                ops.getId(), yhteismusisointi.getId(), yhteismusisointi);

        assertThat(muokattu.getId()).isEqualTo(yhteismusisointi.getId());
        assertThat(muokattu.getPerusteenTaiteenosanId()).isEqualTo(5290L);
        assertThat(muokattu.getPaikallinenTarkennus().get(Kieli.FI)).isEqualTo("Yhteismusisoinnin tarkennus");

        // Taiteenalan tallennus ei saa hukata taiteenosia eikä niiden tarkennuksia
        TaiteenalaDto paivitetty = taiteenperusopetusService.updateTaiteenala(
                ops.getId(), taiteenala.getId(), taiteenala);
        assertThat(paivitetty.getTaiteenosat())
                .extracting(TaiteenosaDto::getId, TaiteenosaDto::getPerusteenTaiteenosanId)
                .containsExactly(
                        tuple(yhteismusisointi.getId(), 5290L),
                        tuple(taiteenosa(taiteenala, 5292L).getId(), 5292L));
        assertThat(taiteenperusopetusService.getTaiteenosa(ops.getId(), yhteismusisointi.getId())
                .getPaikallinenTarkennus().get(Kieli.FI)).isEqualTo("Yhteismusisoinnin tarkennus");
    }

    @Test
    public void testTaiteenalaaEiVoiLisataKahdesti() {
        taiteenperusopetusService.addTaiteenala(ops.getId(), taiteenala(MUSIIKKI));
        assertThatThrownBy(() -> taiteenperusopetusService.addTaiteenala(ops.getId(), taiteenala(MUSIIKKI)))
                .isInstanceOf(BusinessRuleViolationException.class);
    }

    @Test
    public void testTuntematontaTaiteenalaaEiVoiLisata() {
        assertThatThrownBy(() -> taiteenperusopetusService.addTaiteenala(ops.getId(), taiteenala("taiteenalat_sirkus")))
                .isInstanceOf(BusinessRuleViolationException.class);
    }

    @Test
    public void testNavigaatio() {
        TaiteenalaDto taiteenala = taiteenperusopetusService.addTaiteenala(ops.getId(), taiteenala(MUSIIKKI));

        List<NavigationNodeDto> juuri = opetussuunnitelmaService.buildNavigation(ops.getId(), "fi").getChildren();
        assertThat(juuri).extracting(NavigationNodeDto::getType)
                .containsSubsequence(NavigationType.viite, NavigationType.taiteenala, NavigationType.uusi_taiteenala);
        assertThat(juuri.get(juuri.size() - 1).getType()).isEqualTo(NavigationType.uusi_tekstikappale);

        NavigationNodeDto musiikki = juuri.stream()
                .filter(node -> node.getType() == NavigationType.taiteenala)
                .findFirst()
                .orElseThrow(() -> new AssertionError("Taiteenala puuttuu navigaatiosta"));

        assertThat(musiikki.getId()).isEqualTo(taiteenala.getId());
        assertThat(musiikki.getLabel().get(Kieli.FI)).isEqualTo("Musiikki");

        assertThat(musiikki.getChildren()).extracting(NavigationNodeDto::getType)
                .containsExactly(NavigationType.taiteenosa, NavigationType.taiteenosa);
        assertThat(musiikki.getChildren())
                .extracting(node -> node.getLabel().get(Kieli.FI))
                .containsExactly("Yhteismusisointi", "Musiikin hahmotus");

        NavigationNodeDto yhteismusisointi = musiikki.getChildren().get(0);
        assertThat(yhteismusisointi.getId()).isEqualTo(taiteenosa(taiteenala, 5290L).getId());
        assertThat(yhteismusisointi.getMeta().get("taiteenalaId")).isEqualTo(taiteenala.getId());
    }

    @Test
    public void testTaiteenalatLisataanOpetussuunnitelmanLuonnissa() {
        OpetussuunnitelmaDto luotu = createOps(taiteenala(MUSIIKKI), taiteenala(TANSSI));

        assertThat(taiteenperusopetusService.getTaiteenalat(luotu.getId()))
                .extracting(TaiteenalaDto::getKoodi)
                .containsExactly(MUSIIKKI, TANSSI);
        assertThat(taiteenperusopetusService.getTaiteenalat(luotu.getId()))
                .extracting(taiteenala -> taiteenala.getTaiteenosat().size())
                .containsExactly(2, 1);
    }

    @Test
    public void testExportSisaltaaTaiteenalat() {
        TaiteenalaDto taiteenala = taiteenperusopetusService.addTaiteenala(ops.getId(), taiteenala(MUSIIKKI));

        OpetussuunnitelmaExportDto export = dispatcher.get(ops.getId(), OpsExport.class).export(ops.getId());
        assertThat(export).isInstanceOf(OpetussuunnitelmaExportTpoDto.class);
        assertThat(((OpetussuunnitelmaExportTpoDto) export).getTaiteenalat())
                .extracting(TaiteenalaDto::getId, TaiteenalaDto::getKoodi)
                .containsExactly(tuple(taiteenala.getId(), MUSIIKKI));
    }

    private TaiteenalaDto taiteenala(String koodi) {
        TaiteenalaDto taiteenala = new TaiteenalaDto();
        taiteenala.setKoodi(koodi);
        return taiteenala;
    }

    private TaiteenosaDto taiteenosa(TaiteenalaDto taiteenala, Long perusteenTaiteenosanId) {
        return taiteenala.getTaiteenosat().stream()
                .filter(taiteenosa -> Objects.equals(taiteenosa.getPerusteenTaiteenosanId(), perusteenTaiteenosanId))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Taiteenosa " + perusteenTaiteenosanId + " puuttuu taiteenalalta"));
    }

    private OpetussuunnitelmaDto createOps(TaiteenalaDto... taiteenalat) {
        OpetussuunnitelmaLuontiDto pohja = new OpetussuunnitelmaLuontiDto();
        pohja.setNimi(lt(uniikkiString()));
        pohja.setTila(Tila.LUONNOS);
        pohja.setTyyppi(Tyyppi.POHJA);
        pohja.setKoulutustyyppi(KoulutusTyyppi.TPO);
        pohja.setPerusteenDiaarinumero(DIAARINUMERO);
        asetaOrganisaatiot(pohja);
        OpetussuunnitelmaDto pohjaDto = opetussuunnitelmaService.addPohja(pohja);
        opetussuunnitelmaService.updateTila(pohjaDto.getId(), Tila.VALMIS);

        OpetussuunnitelmaLuontiDto luonti = new OpetussuunnitelmaLuontiDto();
        luonti.setNimi(lt(uniikkiString()));
        luonti.setTila(Tila.LUONNOS);
        luonti.setTyyppi(Tyyppi.OPS);
        luonti.setKoulutustyyppi(KoulutusTyyppi.TPO);
        luonti.setEsikatseltavissa(true);
        luonti.setPohja(Reference.of(pohjaDto.getId()));
        if (taiteenalat.length > 0) {
            luonti.setTaiteenalat(List.of(taiteenalat));
        }
        asetaOrganisaatiot(luonti);
        return opetussuunnitelmaService.addOpetussuunnitelma(luonti);
    }

    private void asetaOrganisaatiot(OpetussuunnitelmaLuontiDto ops) {
        KoodistoDto kunta = new KoodistoDto();
        kunta.setKoodiUri("kunta_837");
        ops.setKunnat(new HashSet<>(Collections.singleton(kunta)));

        OrganisaatioDto koulu = new OrganisaatioDto();
        koulu.setNimi(lt("Etelä-Hervannan koulu"));
        koulu.setOid("1.2.246.562.10.00000000001");
        ops.setOrganisaatiot(new HashSet<>(Collections.singleton(koulu)));
    }
}
