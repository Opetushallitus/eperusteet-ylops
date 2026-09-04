package fi.vm.sade.eperusteet.ylops.service.aipe;

import fi.vm.sade.eperusteet.ylops.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.ylops.domain.Tila;
import fi.vm.sade.eperusteet.ylops.domain.Tyyppi;
import fi.vm.sade.eperusteet.ylops.domain.aipe.AIPEKurssi;
import fi.vm.sade.eperusteet.ylops.domain.aipe.AIPEOppiaine;
import fi.vm.sade.eperusteet.ylops.domain.aipe.AIPEVaihe;
import fi.vm.sade.eperusteet.ylops.dto.Reference;
import fi.vm.sade.eperusteet.ylops.dto.aipe.AIPEKurssiDto;
import fi.vm.sade.eperusteet.ylops.dto.aipe.AIPEKurssiKevytDto;
import fi.vm.sade.eperusteet.ylops.dto.aipe.AIPEOppiaineDto;
import fi.vm.sade.eperusteet.ylops.dto.aipe.AIPEOppiaineKevytDto;
import fi.vm.sade.eperusteet.ylops.dto.aipe.AIPEPerusteVaiheKevytDto;
import fi.vm.sade.eperusteet.ylops.dto.aipe.AIPESisaltoDto;
import fi.vm.sade.eperusteet.ylops.dto.aipe.AIPEVaiheDto;
import fi.vm.sade.eperusteet.ylops.dto.aipe.AIPEVaiheKevytDto;
import fi.vm.sade.eperusteet.ylops.dto.aipe.export.AIPESisaltoExportDto;
import fi.vm.sade.eperusteet.ylops.dto.aipe.export.AIPEVaiheExportDto;
import fi.vm.sade.eperusteet.ylops.dto.koodisto.KoodistoDto;
import fi.vm.sade.eperusteet.ylops.dto.koodisto.OrganisaatioDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaLuontiDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import fi.vm.sade.eperusteet.ylops.repository.aipe.AIPEVaiheRepository;
import fi.vm.sade.eperusteet.ylops.service.exception.BusinessRuleViolationException;
import fi.vm.sade.eperusteet.ylops.service.ops.OpetussuunnitelmaService;
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
import java.util.Set;

import static fi.vm.sade.eperusteet.ylops.test.util.TestUtils.lt;
import static fi.vm.sade.eperusteet.ylops.test.util.TestUtils.uniikkiString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
public class AIPEServiceIT extends AbstractIntegrationTest {

    @Autowired
    private AIPEService aipeService;

    @Autowired
    private OpetussuunnitelmaService opetussuunnitelmaService;

    @Autowired
    private AIPEVaiheRepository aipeVaiheRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    public void testAddVaiheMergeHideAndDuplicate() {
        OpetussuunnitelmaDto ops = createAipeOps();

        List<AIPEPerusteVaiheKevytDto> perusteVaiheet = aipeService.getPerusteVaiheet(ops.getId());
        assertThat(perusteVaiheet).hasSize(2);

        AIPEVaiheDto vaihe = aipeService.addVaihe(ops.getId(), 17101L);

        assertThat(vaihe.getId()).isNotNull();
        assertThat(vaihe.getPerusteenVaiheId()).isEqualTo(17101L);
        assertThat(vaihe.isPiilotettu()).isFalse();
        assertThat(vaihe.getPerusteSisalto()).isNotNull();
        assertThat(vaihe.getPerusteSisalto().getTehtava()).isNotNull();
        assertThat(vaihe.getOppiaineet()).hasSize(1);
        assertThat(vaihe.getOppiaineet().get(0).getKurssit()).hasSize(1);

        AIPESisaltoExportDto export = aipeService.getExportSisalto(ops.getId());
        assertThat(export.getVaiheet()).hasSize(1);
        AIPEVaiheExportDto exportVaihe = export.getVaiheet().get(0);
        assertThat(exportVaihe.getPerusteSisalto()).isNotNull();
        assertThat(exportVaihe.getPerusteSisalto().getTehtava()).isNotNull();
        assertThat(exportVaihe.getOppiaineet()).hasSize(1);
        assertThat(exportVaihe.getOppiaineet().get(0).getPerusteSisalto()).isNotNull();
        assertThat(exportVaihe.getOppiaineet().get(0).getPerusteSisalto().getTavoitteet()).hasSize(1);
        assertThat(exportVaihe.getOppiaineet().get(0).getPerusteSisalto().getTavoitteet().get(0).getLaajattavoitteet())
                .extracting(l -> l.getId())
                .containsExactlyInAnyOrder(2500112L, 2500111L);
        assertThat(exportVaihe.getOppiaineet().get(0).getPerusteSisalto().getTavoitteet().get(0).getKohdealueet())
                .extracting(k -> k.getId())
                .containsExactly(2530070L);
        assertThat(exportVaihe.getOppiaineet().get(0).getKurssit()).hasSize(1);
        assertThat(exportVaihe.getOppiaineet().get(0).getKurssit().get(0).getPerusteSisalto()).isNotNull();
        assertThat(exportVaihe.getOppiaineet().get(0).getKurssit().get(0).getPerusteSisalto().getKuvaus()).isNotNull();
        assertThat(exportVaihe.getOppiaineet().get(0).getKurssit().get(0).getPerusteSisalto().getTavoitteet())
                .extracting(t -> t.getId())
                .containsExactly(17401L);
        assertThat(exportVaihe.getOppiaineet().get(0).getKurssit().get(0).getPerusteSisalto().getTavoitteet())
                .allMatch(t -> t.getTavoite() != null);

        AIPEOppiaineDto oppiaineEnnenPiilotusta = aipeService.getOppiaine(ops.getId(), vaihe.getOppiaineet().get(0).getId());
        oppiaineEnnenPiilotusta.setPiilotetutTavoitteet(Collections.singletonList(17401L));
        aipeService.updateOppiaine(ops.getId(), oppiaineEnnenPiilotusta.getId(), oppiaineEnnenPiilotusta);
        AIPESisaltoExportDto exportIlmanTavoitetta = aipeService.getExportSisalto(ops.getId());
        assertThat(exportIlmanTavoitetta.getVaiheet().get(0).getOppiaineet().get(0).getPerusteSisalto().getTavoitteet()).isEmpty();
        assertThat(exportIlmanTavoitetta.getVaiheet().get(0).getOppiaineet().get(0).getKurssit().get(0).getPerusteSisalto().getTavoitteet()).isEmpty();
        AIPEKurssiDto kurssiIlmanTavoitetta = aipeService.getKurssi(ops.getId(), vaihe.getOppiaineet().get(0).getKurssit().get(0).getId());
        assertThat(kurssiIlmanTavoitetta.getPerusteSisalto().getTavoitteet()).isEmpty();
        oppiaineEnnenPiilotusta.setPiilotetutTavoitteet(Collections.emptyList());
        aipeService.updateOppiaine(ops.getId(), oppiaineEnnenPiilotusta.getId(), oppiaineEnnenPiilotusta);

        assertThat(aipeService.getPerusteVaiheet(ops.getId())).hasSize(2);
        assertThat(aipeService.getVaiheet(ops.getId())).hasSize(1);

        assertThatThrownBy(() -> aipeService.addVaihe(ops.getId(), 17101L))
                .isInstanceOf(BusinessRuleViolationException.class);

        AIPEOppiaineDto oppiaine = aipeService.getOppiaine(ops.getId(), vaihe.getOppiaineet().get(0).getId());
        assertThat(oppiaine.getPerusteSisalto()).isNotNull();
        assertThat(oppiaine.getPerusteSisalto().getTavoitteet()).hasSize(1);
        assertThat(oppiaine.getPerusteSisalto().getTavoitteet().get(0).getLaajattavoitteet())
                .extracting(l -> l.getId())
                .containsExactlyInAnyOrder(2500112L, 2500111L);
        assertThat(oppiaine.getPerusteSisalto().getTavoitteet().get(0).getLaajattavoitteet())
                .allMatch(l -> l.getNimi() != null);
        assertThat(oppiaine.getPerusteSisalto().getTavoitteet().get(0).getKohdealueet())
                .extracting(k -> k.getId())
                .containsExactly(2530070L);
        assertThat(oppiaine.getPerusteSisalto().getTavoitteet().get(0).getKohdealueet())
                .allMatch(k -> k.getNimi() != null);

        AIPEKurssiDto kurssi = aipeService.getKurssi(ops.getId(), vaihe.getOppiaineet().get(0).getKurssit().get(0).getId());
        assertThat(kurssi.getPerusteSisalto()).isNotNull();
        assertThat(kurssi.getPerusteSisalto().getKuvaus()).isNotNull();
        assertThat(kurssi.getPerusteSisalto().getTavoitteet())
                .extracting(t -> t.getId())
                .containsExactly(17401L);
        assertThat(kurssi.getPerusteSisalto().getTavoitteet())
                .allMatch(t -> t.getTavoite() != null);

        vaihe.setPiilotettu(true);
        vaihe.setPaikallinenTarkennus(LokalisoituTekstiDto.of("Paikallinen tarkennus"));
        AIPEVaiheDto paivitetty = aipeService.updateVaihe(ops.getId(), vaihe.getId(), vaihe);
        assertThat(paivitetty.isPiilotettu()).isTrue();
        assertThat(paivitetty.getPaikallinenTarkennus()).isNotNull();

        oppiaine.setPiilotettu(true);
        oppiaine.setPiilotetutTavoitteet(Collections.singletonList(17401L));
        AIPEOppiaineDto paivitettyOppiaine = aipeService.updateOppiaine(ops.getId(), oppiaine.getId(), oppiaine);
        assertThat(paivitettyOppiaine.isPiilotettu()).isTrue();
        assertThat(paivitettyOppiaine.getPiilotetutTavoitteet()).containsExactly(17401L);

        kurssi.setPiilotettu(true);
        AIPEKurssiDto paivitettyKurssi = aipeService.updateKurssi(ops.getId(), kurssi.getId(), kurssi);
        assertThat(paivitettyKurssi.isPiilotettu()).isTrue();

        aipeService.removeVaihe(ops.getId(), vaihe.getId());
        assertThat(aipeService.getVaiheet(ops.getId())).isEmpty();
        assertThat(aipeService.getPerusteVaiheet(ops.getId())).hasSize(2);
    }

    @Test
    public void testAddVaiheWithOppimaarat() {
        OpetussuunnitelmaDto ops = createAipeOps();
        AIPEVaiheDto vaihe = aipeService.addVaihe(ops.getId(), 17102L);

        assertThat(vaihe.getOppiaineet()).hasSize(1);
        assertThat(vaihe.getOppiaineet().get(0).getOppimaarat()).hasSize(1);
        assertThat(vaihe.getOppiaineet().get(0).getOppimaarat().get(0).getKurssit()).hasSize(1);
    }

    @Test
    public void testGetSisaltoEiLuoUuttaRiviaKunSisaltoOnJoOlemassa() {
        OpetussuunnitelmaDto ops = createAipeOps();
        Long sisaltoId = aipeService.getSisalto(ops.getId()).getId();
        assertThat(sisaltoId).isNotNull();

        entityManager.flush();
        entityManager.clear();

        opetussuunnitelmaService.buildNavigation(ops.getId(), "fi");
        opetussuunnitelmaService.buildNavigation(ops.getId(), "fi");

        assertThat(aipeService.getSisalto(ops.getId()).getId()).isEqualTo(sisaltoId);
    }

    private OpetussuunnitelmaDto createAipeOps() {
        return createAipeOps(null);
    }

    private OpetussuunnitelmaDto createAipeOps(Set<Long> perusteenVaiheIdt) {
        OpetussuunnitelmaLuontiDto pohjaLuonti = new OpetussuunnitelmaLuontiDto();
        pohjaLuonti.setNimi(lt(uniikkiString()));
        pohjaLuonti.setKuvaus(lt(uniikkiString()));
        pohjaLuonti.setTila(Tila.LUONNOS);
        pohjaLuonti.setTyyppi(Tyyppi.POHJA);
        pohjaLuonti.setKoulutustyyppi(KoulutusTyyppi.AIKUISTENPERUSOPETUS);
        pohjaLuonti.setPerusteenDiaarinumero("OPH-AIPE-TEST");

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
        opsLuonti.setKoulutustyyppi(KoulutusTyyppi.AIKUISTENPERUSOPETUS);
        opsLuonti.setEsikatseltavissa(true);
        opsLuonti.setKunnat(new HashSet<>(Collections.singleton(kunta)));
        opsLuonti.setOrganisaatiot(new HashSet<>(Collections.singleton(kouluDto)));
        opsLuonti.setPohja(Reference.of(pohja.getId()));
        opsLuonti.setPerusteenVaiheIdt(perusteenVaiheIdt);
        return opetussuunnitelmaService.addOpetussuunnitelma(opsLuonti);
    }

    @Test
    public void testUpdateVaiheJarjestys() {
        OpetussuunnitelmaDto ops = createAipeOps();
        AIPEVaiheDto vaihe1 = aipeService.addVaihe(ops.getId(), 17101L);
        AIPEVaiheDto vaihe2 = aipeService.addVaihe(ops.getId(), 17102L);

        assertThat(aipeService.getVaiheet(ops.getId()))
                .extracting(AIPEVaiheKevytDto::getId)
                .containsExactly(vaihe1.getId(), vaihe2.getId());

        List<AIPEVaiheKevytDto> jarjestetty = aipeService.updateVaiheJarjestys(
                ops.getId(), Arrays.asList(vaihe2.getId(), vaihe1.getId()));

        assertThat(jarjestetty)
                .extracting(AIPEVaiheKevytDto::getId)
                .containsExactly(vaihe2.getId(), vaihe1.getId());
        assertThat(aipeService.getVaiheet(ops.getId()))
                .extracting(AIPEVaiheKevytDto::getId)
                .containsExactly(vaihe2.getId(), vaihe1.getId());

        assertThatThrownBy(() -> aipeService.updateVaiheJarjestys(ops.getId(), Collections.singletonList(vaihe1.getId())))
                .isInstanceOf(BusinessRuleViolationException.class);
        assertThatThrownBy(() -> aipeService.updateVaiheJarjestys(ops.getId(), Arrays.asList(vaihe1.getId(), 999L)))
                .isInstanceOf(BusinessRuleViolationException.class);
    }

    @Test
    public void testLuoOpsValituillaVaiheilla() {
        OpetussuunnitelmaDto ops = createAipeOps(Collections.singleton(17101L));

        List<AIPEPerusteVaiheKevytDto> perusteVaiheet = aipeService.getPerusteVaiheet(ops.getId());
        assertThat(perusteVaiheet).hasSize(2);
        assertThat(aipeService.getVaiheet(ops.getId())).hasSize(1);
        assertThat(aipeService.getVaiheet(ops.getId()).get(0).getPerusteenVaiheId()).isEqualTo(17101L);
    }

    @Test
    public void testOnkoPuuttuviaSisaltojaFalseAfterAddVaihe() {
        OpetussuunnitelmaDto ops = createAipeOps();
        aipeService.addVaihe(ops.getId(), 17101L);

        assertThat(aipeService.onkoPuuttuviaSisaltoja(ops.getId())).isFalse();
    }

    @Test
    public void testLisaaPuuttuvaOppiaine() {
        OpetussuunnitelmaDto ops = createAipeOps();
        AIPEVaiheDto vaiheDto = aipeService.addVaihe(ops.getId(), 17101L);
        assertThat(aipeService.onkoPuuttuviaSisaltoja(ops.getId())).isFalse();

        AIPEVaihe vaihe = aipeVaiheRepository.findOne(vaiheDto.getId());
        vaihe.getOppiaineet().clear();
        aipeVaiheRepository.flush();

        assertThat(aipeService.onkoPuuttuviaSisaltoja(ops.getId())).isTrue();
        aipeService.lisaaPuuttuvatSisallot(ops.getId());

        assertThat(aipeService.onkoPuuttuviaSisaltoja(ops.getId())).isFalse();
        AIPEVaiheDto synkronoitu = aipeService.getVaihe(ops.getId(), vaiheDto.getId());
        assertThat(synkronoitu.getOppiaineet()).hasSize(1);
        assertThat(synkronoitu.getOppiaineet().get(0).getPerusteenOppiaineId()).isEqualTo(17201L);
        assertThat(synkronoitu.getOppiaineet().get(0).getKurssit()).hasSize(1);
    }

    @Test
    public void testLisaaPuuttuvaKurssi() {
        OpetussuunnitelmaDto ops = createAipeOps();
        AIPEVaiheDto vaiheDto = aipeService.addVaihe(ops.getId(), 17101L);

        AIPEVaihe vaihe = aipeVaiheRepository.findOne(vaiheDto.getId());
        vaihe.getOppiaineet().get(0).getKurssit().clear();
        aipeVaiheRepository.flush();

        assertThat(aipeService.onkoPuuttuviaSisaltoja(ops.getId())).isTrue();
        aipeService.lisaaPuuttuvatSisallot(ops.getId());

        assertThat(aipeService.onkoPuuttuviaSisaltoja(ops.getId())).isFalse();
        AIPEVaiheDto synkronoitu = aipeService.getVaihe(ops.getId(), vaiheDto.getId());
        assertThat(synkronoitu.getOppiaineet()).hasSize(1);
        assertThat(synkronoitu.getOppiaineet().get(0).getKurssit()).hasSize(1);
        assertThat(synkronoitu.getOppiaineet().get(0).getKurssit().get(0).getPerusteenKurssiId()).isEqualTo(17301L);
    }

    @Test
    public void testLisaaPuuttuvaOppimaara() {
        OpetussuunnitelmaDto ops = createAipeOps();
        AIPEVaiheDto vaiheDto = aipeService.addVaihe(ops.getId(), 17102L);

        AIPEVaihe vaihe = aipeVaiheRepository.findOne(vaiheDto.getId());
        AIPEOppiaine oppiaine = vaihe.getOppiaineet().get(0);
        oppiaine.getOppimaarat().clear();
        aipeVaiheRepository.flush();

        assertThat(aipeService.onkoPuuttuviaSisaltoja(ops.getId())).isTrue();
        aipeService.lisaaPuuttuvatSisallot(ops.getId());

        assertThat(aipeService.onkoPuuttuviaSisaltoja(ops.getId())).isFalse();
        AIPEVaiheDto synkronoitu = aipeService.getVaihe(ops.getId(), vaiheDto.getId());
        assertThat(synkronoitu.getOppiaineet()).hasSize(1);
        assertThat(synkronoitu.getOppiaineet().get(0).getOppimaarat()).hasSize(1);
        assertThat(synkronoitu.getOppiaineet().get(0).getOppimaarat().get(0).getPerusteenOppiaineId()).isEqualTo(17203L);
        assertThat(synkronoitu.getOppiaineet().get(0).getOppimaarat().get(0).getKurssit()).hasSize(1);
    }

    @Test
    public void testLisaaPuuttuvatIdempotentti() {
        OpetussuunnitelmaDto ops = createAipeOps();
        AIPEVaiheDto vaihe = aipeService.addVaihe(ops.getId(), 17101L);
        Long oppiaineId = vaihe.getOppiaineet().get(0).getId();
        Long kurssiId = vaihe.getOppiaineet().get(0).getKurssit().get(0).getId();

        aipeService.lisaaPuuttuvatSisallot(ops.getId());

        assertThat(aipeService.onkoPuuttuviaSisaltoja(ops.getId())).isFalse();
        AIPEVaiheDto synkronoitu = aipeService.getVaihe(ops.getId(), vaihe.getId());
        assertThat(synkronoitu.getOppiaineet()).hasSize(1);
        assertThat(synkronoitu.getOppiaineet().get(0).getId()).isEqualTo(oppiaineId);
        assertThat(synkronoitu.getOppiaineet().get(0).getKurssit()).hasSize(1);
        assertThat(synkronoitu.getOppiaineet().get(0).getKurssit().get(0).getId()).isEqualTo(kurssiId);
    }

    @Test
    public void testLisaaPuuttuvatEiLisaaVaiheita() {
        OpetussuunnitelmaDto ops = createAipeOps();
        aipeService.addVaihe(ops.getId(), 17101L);

        assertThat(aipeService.getPerusteVaiheet(ops.getId())).hasSize(2);
        assertThat(aipeService.onkoPuuttuviaSisaltoja(ops.getId())).isFalse();

        aipeService.lisaaPuuttuvatSisallot(ops.getId());

        assertThat(aipeService.getVaiheet(ops.getId())).hasSize(1);
        assertThat(aipeService.getVaiheet(ops.getId()).get(0).getPerusteenVaiheId()).isEqualTo(17101L);
    }

    @Test
    public void testLisaaPuuttuvatSailyttääPerusteenJarjestyksen() {
        OpetussuunnitelmaDto ops = createAipeOps();
        AIPEVaiheDto vaiheDto = aipeService.addVaihe(ops.getId(), 17101L);

        AIPEVaihe vaihe = aipeVaiheRepository.findOne(vaiheDto.getId());
        AIPEOppiaine oppiaine = vaihe.getOppiaineet().get(0);
        AIPEKurssi perusteKurssi = oppiaine.getKurssit().get(0);

        AIPEKurssi extraKurssi = new AIPEKurssi();
        extraKurssi.setPerusteenKurssiId(88888L);
        oppiaine.addKurssi(extraKurssi, 0);
        oppiaine.getKurssit().remove(perusteKurssi);
        aipeVaiheRepository.flush();

        aipeService.lisaaPuuttuvatSisallot(ops.getId());
        AIPEVaiheDto kurssiJarjestys = aipeService.getVaihe(ops.getId(), vaiheDto.getId());
        assertThat(kurssiJarjestys.getOppiaineet().get(0).getKurssit())
                .extracting(AIPEKurssiKevytDto::getPerusteenKurssiId)
                .containsExactly(17301L, 88888L);

        vaihe = aipeVaiheRepository.findOne(vaiheDto.getId());
        AIPEOppiaine perusteOppiaine = vaihe.getOppiaineet().get(0);
        AIPEOppiaine extraOppiaine = new AIPEOppiaine();
        extraOppiaine.setPerusteenOppiaineId(99999L);
        vaihe.addOppiaine(extraOppiaine, 0);
        vaihe.getOppiaineet().remove(perusteOppiaine);
        aipeVaiheRepository.flush();

        aipeService.lisaaPuuttuvatSisallot(ops.getId());
        AIPEVaiheDto oppiaineJarjestys = aipeService.getVaihe(ops.getId(), vaiheDto.getId());
        assertThat(oppiaineJarjestys.getOppiaineet())
                .extracting(AIPEOppiaineKevytDto::getPerusteenOppiaineId)
                .containsExactly(17201L, 99999L);

        AIPEVaiheDto oppimaaraVaiheDto = aipeService.addVaihe(ops.getId(), 17102L);
        AIPEVaihe oppimaaraVaihe = aipeVaiheRepository.findOne(oppimaaraVaiheDto.getId());
        AIPEOppiaine oppimaaranParent = oppimaaraVaihe.getOppiaineet().get(0);
        AIPEOppiaine perusteOppimaara = oppimaaranParent.getOppimaarat().get(0);
        AIPEOppiaine extraOppimaara = new AIPEOppiaine();
        extraOppimaara.setPerusteenOppiaineId(77777L);
        oppimaaranParent.addOppimaara(extraOppimaara, 0);
        oppimaaranParent.getOppimaarat().remove(perusteOppimaara);
        aipeVaiheRepository.flush();

        aipeService.lisaaPuuttuvatSisallot(ops.getId());
        AIPEVaiheDto oppimaaraJarjestys = aipeService.getVaihe(ops.getId(), oppimaaraVaiheDto.getId());
        assertThat(oppimaaraJarjestys.getOppiaineet().get(0).getOppimaarat())
                .extracting(AIPEOppiaineKevytDto::getPerusteenOppiaineId)
                .containsExactly(17203L, 77777L);
    }
}
