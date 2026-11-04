package fi.vm.sade.eperusteet.ylops.service.tpo;

import fi.vm.sade.eperusteet.ylops.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.ylops.domain.KoulutustyyppiToteutus;
import fi.vm.sade.eperusteet.ylops.domain.Tila;
import fi.vm.sade.eperusteet.ylops.domain.Tyyppi;
import fi.vm.sade.eperusteet.ylops.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.ylops.domain.tpo.TaiteenOpintokokonaisuus;
import fi.vm.sade.eperusteet.ylops.domain.tpo.TavoitealueValinta;
import fi.vm.sade.eperusteet.ylops.dto.Reference;
import fi.vm.sade.eperusteet.ylops.dto.export.OpetussuunnitelmaExportTpoDto;
import fi.vm.sade.eperusteet.ylops.dto.koodisto.KoodistoDto;
import fi.vm.sade.eperusteet.ylops.dto.koodisto.OrganisaatioDto;
import fi.vm.sade.eperusteet.ylops.dto.navigation.NavigationNodeDto;
import fi.vm.sade.eperusteet.ylops.dto.navigation.NavigationType;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaLuontiDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.VapaatekstiPaikallinentarkennusDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.tpo.TpoPerusteenTaiteenalaDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.tpo.TpoPerusteenTaiteenalaVaiheDto;
import fi.vm.sade.eperusteet.ylops.dto.tpo.TaiteenOpintokokonaisuusDto;
import fi.vm.sade.eperusteet.ylops.dto.tpo.TavoitealueValintaDto;
import fi.vm.sade.eperusteet.ylops.dto.tpo.export.TaiteenOpintokokonaisuusExportDto;
import fi.vm.sade.eperusteet.ylops.dto.tpo.TaiteenalaDto;
import fi.vm.sade.eperusteet.ylops.dto.tpo.TaiteenalaKevytDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.tpo.PerusteTavoiteAlueDto;
import fi.vm.sade.eperusteet.ylops.dto.tpo.TaiteenalanVaiheDto;
import fi.vm.sade.eperusteet.ylops.dto.tpo.export.TaiteenalanVaiheExportDto;
import fi.vm.sade.eperusteet.ylops.dto.tpo.export.TpoSisaltoExportDto;
import fi.vm.sade.eperusteet.ylops.repository.tpo.TaiteenOpintokokonaisuusRepository;
import fi.vm.sade.eperusteet.ylops.service.exception.BusinessRuleViolationException;
import fi.vm.sade.eperusteet.ylops.service.ops.NavigationBuilder;
import fi.vm.sade.eperusteet.ylops.service.ops.OpetussuunnitelmaService;
import fi.vm.sade.eperusteet.ylops.service.ops.OpsDispatcher;
import fi.vm.sade.eperusteet.ylops.service.ops.OpsExport;
import fi.vm.sade.eperusteet.ylops.test.AbstractIntegrationTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static fi.vm.sade.eperusteet.ylops.test.util.TestUtils.lt;
import static fi.vm.sade.eperusteet.ylops.test.util.TestUtils.uniikkiString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
public class TaiteenperusopetusServiceIT extends AbstractIntegrationTest {

    private static final Long MUSIIKKI = 9101L;
    private static final Long TANSSI = 9201L;
    private static final Long MUSIIKKI_VAIHE = 9111L;
    private static final Long TAVOITEALUE_1 = 9121L;
    private static final Long TAVOITEALUE_2 = 9122L;
    private static final Long TANSSI_TAVOITEALUE = 9221L;
    private static final String OTSIKKO_1 = "tavoitealue_1";
    private static final String TAVOITE_1 = "tavoite_1";
    private static final String OTSIKKO_2 = "tavoitealue_2";
    private static final Long VAPAATEKSTI = 9131L;

    @Autowired
    private TaiteenperusopetusService taiteenperusopetusService;

    @Autowired
    private OpetussuunnitelmaService opetussuunnitelmaService;

    @Autowired
    private TaiteenOpintokokonaisuusRepository opintokokonaisuusRepository;

    @Autowired
    private OpsDispatcher dispatcher;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    public void testPerusteenTaiteenalat() {
        OpetussuunnitelmaDto ops = createTpoOps(Collections.emptyList());
        List<TpoPerusteenTaiteenalaDto> perusteTaiteenalat = taiteenperusopetusService.getPerusteTaiteenalat(ops.getId());
        assertThat(perusteTaiteenalat).extracting(TpoPerusteenTaiteenalaDto::getId).containsExactly(MUSIIKKI, TANSSI);
        assertThat(perusteTaiteenalat.get(0).getKasvatus().getTeksti().get(Kieli.FI)).isEqualTo("<p>Musiikin kasvatus</p>");
        assertThat(perusteTaiteenalat.get(0).getVaiheet()).hasSize(1);

        TpoPerusteenTaiteenalaVaiheDto vaihe = taiteenperusopetusService.getPerusteVaihe(ops.getId(), MUSIIKKI_VAIHE);
        assertThat(vaihe.getLaajuus()).isEqualTo(500);
        assertThat(vaihe.getTavoitealueet()).extracting("id").containsExactly(TAVOITEALUE_1, TAVOITEALUE_2);
        assertThat(vaihe.getVapaatTekstit()).hasSize(1);
    }

    @Test
    public void testLuontiValituillaTaiteenaloilla() {
        OpetussuunnitelmaDto ops = createTpoOps(Collections.singletonList(MUSIIKKI));
        assertThat(ops.getToteutus()).isEqualTo(KoulutustyyppiToteutus.TPO);

        List<TaiteenalaKevytDto> taiteenalat = taiteenperusopetusService.getTaiteenalat(ops.getId());
        assertThat(taiteenalat).hasSize(1);
        assertThat(taiteenalat.get(0).getPerusteenTaiteenalaId()).isEqualTo(MUSIIKKI);
        assertThat(taiteenalat.get(0).getNimi().get(Kieli.FI)).isEqualTo("Musiikki");
        assertThat(taiteenalat.get(0).getVaiheet()).hasSize(1);
        assertThat(taiteenalat.get(0).getVaiheet().get(0).getPerusteenTaiteenalaVaiheId()).isEqualTo(MUSIIKKI_VAIHE);

        TaiteenalanVaiheDto vaihe = taiteenperusopetusService.getVaihe(ops.getId(), taiteenalat.get(0).getVaiheet().get(0).getId());
        assertThat(vaihe.getVapaatTekstit()).extracting(VapaatekstiPaikallinentarkennusDto::getPerusteenVapaaTekstiId)
                .containsExactly(VAPAATEKSTI);
        assertThat(vaihe.getPerusteenVaihe().getTavoitealueet()).hasSize(2);
    }

    @Test
    public void testTaiteenalatEivatOleTekstipuussa() {
        OpetussuunnitelmaDto ops = createTpoOps(Collections.singletonList(MUSIIKKI));
        assertThat(ops.getTekstit().getLapset()).hasSize(1);
        assertThat(ops.getTekstit().getLapset().get(0).getTekstiKappale().getNimi().get(Kieli.FI)).isEqualTo("Teksti");
    }

    @Test
    public void testTaiteenalanLisaysJaPaivitys() {
        OpetussuunnitelmaDto ops = createTpoOps(Collections.singletonList(MUSIIKKI));

        assertThatThrownBy(() -> taiteenperusopetusService.addTaiteenalat(ops.getId(), Collections.singletonList(MUSIIKKI)))
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessage("taiteenala-on-jo-lisatty");

        List<TaiteenalaKevytDto> taiteenalat = taiteenperusopetusService.addTaiteenalat(ops.getId(), Collections.singletonList(TANSSI));
        assertThat(taiteenalat).extracting(TaiteenalaKevytDto::getPerusteenTaiteenalaId).containsExactly(MUSIIKKI, TANSSI);

        Long taiteenalaId = taiteenalat.get(0).getId();
        TaiteenalaDto taiteenala = taiteenperusopetusService.getTaiteenala(ops.getId(), taiteenalaId);
        assertThat(taiteenala.getPerusteenTaiteenala().getTeksti().get(Kieli.FI)).isEqualTo("<p>Musiikin kuvaus</p>");
        taiteenala.setPaikallinenTarkennus(lt("Paikallinen musiikki"));
        taiteenala = taiteenperusopetusService.updateTaiteenala(ops.getId(), taiteenalaId, taiteenala);
        assertThat(taiteenala.getPaikallinenTarkennus().get(Kieli.FI)).isEqualTo("Paikallinen musiikki");

        Long vaiheId = taiteenala.getVaiheet().get(0).getId();
        TaiteenalanVaiheDto vaihe = taiteenperusopetusService.getVaihe(ops.getId(), vaiheId);
        vaihe.getVapaatTekstit().get(0).setPaikallinenTarkennus(lt("Vapaan tekstin tarkennus"));
        vaihe = taiteenperusopetusService.updateVaihe(ops.getId(), vaiheId, vaihe);
        assertThat(vaihe.getVapaatTekstit().get(0).getPaikallinenTarkennus().get(Kieli.FI)).isEqualTo("Vapaan tekstin tarkennus");

        taiteenperusopetusService.removeTaiteenala(ops.getId(), taiteenalat.get(1).getId());
        assertThat(taiteenperusopetusService.getTaiteenalat(ops.getId())).hasSize(1);
    }

    @Test
    public void testOpintokokonaisuus() {
        OpetussuunnitelmaDto ops = createTpoOps(Collections.singletonList(MUSIIKKI));
        Long vaiheId = taiteenperusopetusService.getTaiteenalat(ops.getId()).get(0).getVaiheet().get(0).getId();

        TaiteenOpintokokonaisuusDto uusi = new TaiteenOpintokokonaisuusDto();
        uusi.setNimi(lt("Opintokokonaisuus"));
        uusi.setLaajuus(10L);
        uusi.setOsaamisenArviointi(lt("Arviointi"));
        uusi.setTavoitealueValinnat(Collections.singletonList(valinta(TANSSI_TAVOITEALUE, "tavoitealue_3")));
        assertThatThrownBy(() -> taiteenperusopetusService.addOpintokokonaisuus(ops.getId(), vaiheId, uusi))
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessage("tavoitealue-ei-kuulu-vaiheeseen");

        uusi.setTavoitealueValinnat(Collections.singletonList(valinta(TAVOITEALUE_1, "tuntematon")));
        assertThatThrownBy(() -> taiteenperusopetusService.addOpintokokonaisuus(ops.getId(), vaiheId, uusi))
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessage("tavoitealue-ei-kuulu-vaiheeseen");

        uusi.setTavoitealueValinnat(Arrays.asList(
                valinta(TAVOITEALUE_1, OTSIKKO_1),
                valinta(TAVOITEALUE_1, TAVOITE_1),
                valinta(TAVOITEALUE_2, OTSIKKO_2)));
        TaiteenOpintokokonaisuusDto ok = taiteenperusopetusService.addOpintokokonaisuus(ops.getId(), vaiheId, uusi);
        assertThat(ok.getId()).isNotNull();
        assertThat(ok.getTavoitealueValinnat()).containsExactlyInAnyOrder(
                valinta(TAVOITEALUE_1, OTSIKKO_1),
                valinta(TAVOITEALUE_1, TAVOITE_1),
                valinta(TAVOITEALUE_2, OTSIKKO_2));

        ok.setNimi(lt("Muokattu"));
        ok.setTavoitealueValinnat(Collections.singletonList(valinta(TAVOITEALUE_2, OTSIKKO_2)));
        ok = taiteenperusopetusService.updateOpintokokonaisuus(ops.getId(), ok.getId(), ok);
        assertThat(ok.getNimi().get(Kieli.FI)).isEqualTo("Muokattu");
        assertThat(ok.getTavoitealueValinnat()).containsExactly(valinta(TAVOITEALUE_2, OTSIKKO_2));

        TaiteenalanVaiheDto vaihe = taiteenperusopetusService.getVaihe(ops.getId(), vaiheId);
        assertThat(vaihe.getOpintokokonaisuudet()).hasSize(1);
        assertThat(vaihe.getOpintokokonaisuudet().get(0).getTavoitealueValinnat()).containsExactly(valinta(TAVOITEALUE_2, OTSIKKO_2));

        TpoSisaltoExportDto export = taiteenperusopetusService.getExportSisalto(ops.getId());
        assertThat(export.getTaiteenalat()).hasSize(1);
        assertThat(export.getTaiteenalat().get(0).getNimi().get(Kieli.FI)).isEqualTo("Musiikki");
        assertThat(export.getTaiteenalat().get(0).getPerusteenTaiteenala()).isNotNull();
        TaiteenalanVaiheExportDto exportVaihe = export.getTaiteenalat().get(0).getVaiheet().get(0);
        assertThat(exportVaihe.getPerusteenVaihe()).isNotNull();
        assertThat(exportVaihe.getVapaatTekstit()).hasSize(1);
        assertThat(exportVaihe.getOpintokokonaisuudet()).hasSize(1);
        assertThat(exportVaihe.getOpintokokonaisuudet().get(0).getOsaamisenArviointi().get(Kieli.FI)).isEqualTo("Arviointi");
        assertThat(exportVaihe.getOpintokokonaisuudet().get(0).getTavoitealueValinnat()).containsExactly(valinta(TAVOITEALUE_2, OTSIKKO_2));
        assertThat(exportVaihe.getOpintokokonaisuudet().get(0).getTavoitealueet())
                .extracting(PerusteTavoiteAlueDto::getId)
                .containsExactly(TAVOITEALUE_2);
        assertThat(exportVaihe.getOpintokokonaisuudet().get(0).getTavoitealueet().get(0).getTavoitteet()).isEmpty();

        taiteenperusopetusService.removeOpintokokonaisuus(ops.getId(), ok.getId());
        assertThat(taiteenperusopetusService.getVaihe(ops.getId(), vaiheId).getOpintokokonaisuudet()).isEmpty();
    }

    @Test
    public void testPerusteeltaPoistettuTavoitealue() {
        OpetussuunnitelmaDto ops = createTpoOps(Collections.singletonList(MUSIIKKI));
        Long vaiheId = taiteenperusopetusService.getTaiteenalat(ops.getId()).get(0).getVaiheet().get(0).getId();

        TaiteenOpintokokonaisuusDto uusi = new TaiteenOpintokokonaisuusDto();
        uusi.setNimi(lt("Opintokokonaisuus"));
        uusi.setTavoitealueValinnat(Collections.singletonList(valinta(TAVOITEALUE_1, OTSIKKO_1)));
        TaiteenOpintokokonaisuusDto ok = taiteenperusopetusService.addOpintokokonaisuus(ops.getId(), vaiheId, uusi);

        Long poistettu = 99999L;
        TaiteenOpintokokonaisuus entity = opintokokonaisuusRepository.findOne(ok.getId());
        entity.getTavoitealueValinnat().add(new TavoitealueValinta(poistettu, "poistettu"));
        entityManager.flush();

        ok = taiteenperusopetusService.getOpintokokonaisuus(ops.getId(), ok.getId());
        assertThat(ok.getTavoitealueValinnat()).containsExactlyInAnyOrder(
                valinta(TAVOITEALUE_1, OTSIKKO_1), valinta(poistettu, "poistettu"));

        ok.setNimi(lt("Tallennettu"));
        ok = taiteenperusopetusService.updateOpintokokonaisuus(ops.getId(), ok.getId(), ok);
        assertThat(ok.getTavoitealueValinnat()).containsExactlyInAnyOrder(
                valinta(TAVOITEALUE_1, OTSIKKO_1), valinta(poistettu, "poistettu"));

        ok.setTavoitealueValinnat(Collections.singletonList(valinta(TAVOITEALUE_1, OTSIKKO_1)));
        ok = taiteenperusopetusService.updateOpintokokonaisuus(ops.getId(), ok.getId(), ok);
        assertThat(ok.getTavoitealueValinnat()).containsExactly(valinta(TAVOITEALUE_1, OTSIKKO_1));

        TaiteenOpintokokonaisuusDto lisays = ok;
        lisays.setTavoitealueValinnat(Arrays.asList(valinta(TAVOITEALUE_1, OTSIKKO_1), valinta(poistettu, "poistettu")));
        assertThatThrownBy(() -> taiteenperusopetusService.updateOpintokokonaisuus(ops.getId(), lisays.getId(), lisays))
                .isInstanceOf(BusinessRuleViolationException.class);
    }

    @Test
    public void testNavigaatioJaExport() {
        OpetussuunnitelmaDto ops = createTpoOps(Collections.singletonList(MUSIIKKI));
        Long vaiheId = taiteenperusopetusService.getTaiteenalat(ops.getId()).get(0).getVaiheet().get(0).getId();
        TaiteenOpintokokonaisuusDto uusi = new TaiteenOpintokokonaisuusDto();
        uusi.setNimi(lt("Opintokokonaisuus"));
        uusi.setTavoitealueValinnat(Arrays.asList(valinta(TAVOITEALUE_1, OTSIKKO_1), valinta(TAVOITEALUE_1, TAVOITE_1)));
        TaiteenOpintokokonaisuusDto ok = taiteenperusopetusService.addOpintokokonaisuus(ops.getId(), vaiheId, uusi);

        NavigationNodeDto navi = dispatcher.get(ops.getToteutus(), NavigationBuilder.class).buildNavigation(ops.getId());
        List<NavigationNodeDto> taiteenalaNodet = navi.getChildren().stream()
                .filter(n -> n.getType() == NavigationType.taiteenala)
                .collect(Collectors.toList());
        assertThat(taiteenalaNodet).hasSize(1);
        assertThat(navi.getChildren()).extracting(NavigationNodeDto::getType).contains(NavigationType.uusi_taiteenala);

        NavigationNodeDto vaiheNode = taiteenalaNodet.get(0).getChildren().get(0);
        assertThat(vaiheNode.getType()).isEqualTo(NavigationType.taiteenalanvaihe);
        assertThat(vaiheNode.getId()).isEqualTo(vaiheId);
        assertThat(vaiheNode.getChildren()).extracting(NavigationNodeDto::getType)
                .containsExactly(NavigationType.taiteenopintokokonaisuus, NavigationType.uusi_opintokokonaisuus);
        assertThat(vaiheNode.getChildren().get(0).getId()).isEqualTo(ok.getId());
        assertThat(vaiheNode.getChildren().get(1).getMeta()).containsEntry("vaiheId", vaiheId);

        OpetussuunnitelmaExportTpoDto export = dispatcher.get(ops.getToteutus(), OpsExport.class)
                .export(ops.getId(), OpetussuunnitelmaExportTpoDto.class);
        assertThat(export.getTpo().getTaiteenalat()).hasSize(1);
        assertThat(export.getTpo().getTaiteenalat().get(0).getPerusteenTaiteenala().getId()).isEqualTo(MUSIIKKI);
        assertThat(export.getTpo().getTaiteenalat().get(0).getVaiheet().get(0).getOpintokokonaisuudet()).hasSize(1);
        assertThat(export.getTpo().getTaiteenalat().get(0).getVaiheet().get(0).getPerusteenVaihe().getTavoitealueet()).hasSize(2);
        TaiteenOpintokokonaisuusExportDto exportOk = export.getTpo().getTaiteenalat().get(0).getVaiheet().get(0).getOpintokokonaisuudet().get(0);
        assertThat(exportOk.getTavoitealueet()).extracting(PerusteTavoiteAlueDto::getId).containsExactly(TAVOITEALUE_1);
        assertThat(exportOk.getTavoitealueet().get(0).getTavoitteet()).extracting("uri").containsExactly(TAVOITE_1);
    }

    private TavoitealueValintaDto valinta(Long tavoitealueId, String koodiUri) {
        return new TavoitealueValintaDto(tavoitealueId, koodiUri);
    }

    private OpetussuunnitelmaDto createTpoOps(List<Long> taiteenalat) {
        OpetussuunnitelmaLuontiDto pohjaLuonti = new OpetussuunnitelmaLuontiDto();
        pohjaLuonti.setNimi(lt(uniikkiString()));
        pohjaLuonti.setKuvaus(lt(uniikkiString()));
        pohjaLuonti.setTila(Tila.LUONNOS);
        pohjaLuonti.setTyyppi(Tyyppi.POHJA);
        pohjaLuonti.setKoulutustyyppi(KoulutusTyyppi.TPO);
        pohjaLuonti.setPerusteenDiaarinumero("tpo-diaarinumero");

        KoodistoDto kunta = new KoodistoDto();
        kunta.setKoodiUri("kunta_837");
        pohjaLuonti.setKunnat(new HashSet<>(Collections.singleton(kunta)));
        OrganisaatioDto kouluDto = new OrganisaatioDto();
        kouluDto.setNimi(lt("Etelä-Hervannan koulu"));
        kouluDto.setOid("1.2.246.562.10.00000000001");
        pohjaLuonti.setOrganisaatiot(new HashSet<>(Collections.singleton(kouluDto)));

        OpetussuunnitelmaDto pohja = opetussuunnitelmaService.addPohja(pohjaLuonti);
        opetussuunnitelmaService.updateTila(pohja.getId(), Tila.VALMIS);

        OpetussuunnitelmaLuontiDto opsLuonti = new OpetussuunnitelmaLuontiDto();
        opsLuonti.setNimi(lt(uniikkiString()));
        opsLuonti.setKuvaus(lt(uniikkiString()));
        opsLuonti.setTila(Tila.LUONNOS);
        opsLuonti.setTyyppi(Tyyppi.OPS);
        opsLuonti.setKoulutustyyppi(KoulutusTyyppi.TPO);
        opsLuonti.setEsikatseltavissa(true);
        opsLuonti.setKunnat(new HashSet<>(Collections.singleton(kunta)));
        opsLuonti.setOrganisaatiot(new HashSet<>(Collections.singleton(kouluDto)));
        opsLuonti.setPohja(Reference.of(pohja.getId()));
        opsLuonti.setTaiteenalat(taiteenalat);
        return opetussuunnitelmaService.addOpetussuunnitelma(opsLuonti);
    }
}
