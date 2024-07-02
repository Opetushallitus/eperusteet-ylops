package fi.vm.sade.eperusteet.ylops.service.teksti;

import fi.vm.sade.eperusteet.ylops.domain.ops.Opetussuunnitelma;
import fi.vm.sade.eperusteet.ylops.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.ylops.domain.teksti.PoistettuTekstiKappale;
import fi.vm.sade.eperusteet.ylops.domain.teksti.TekstiKappaleViite;
import fi.vm.sade.eperusteet.ylops.dto.Reference;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.TekstiKappaleDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.TekstiKappaleViiteDto;
import fi.vm.sade.eperusteet.ylops.repository.ops.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.ylops.repository.teksti.PoistettuTekstiKappaleRepository;
import fi.vm.sade.eperusteet.ylops.service.exception.BusinessRuleViolationException;
import fi.vm.sade.eperusteet.ylops.service.ops.OpetussuunnitelmaService;
import fi.vm.sade.eperusteet.ylops.service.ops.TekstiKappaleViiteService;
import fi.vm.sade.eperusteet.ylops.service.util.CollectionUtil;
import fi.vm.sade.eperusteet.ylops.test.AbstractIntegrationTest;
import fi.vm.sade.eperusteet.ylops.test.util.TestUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static fi.vm.sade.eperusteet.ylops.test.util.TestUtils.lt;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TekstiKappaleViiteServiceIT extends AbstractIntegrationTest {

    @Autowired
    private TekstiKappaleViiteService tekstiKappaleViiteService;

    @Autowired
    private OpetussuunnitelmaService opetussuunnitelmaService;

    @Autowired
    private OpetussuunnitelmaRepository opetussuunnitelmaRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private PoistettuTekstiKappaleRepository poistettuTekstiKappaleRepository;

    @Test
    public void testOpintojaksojenHallinta() {
        OpetussuunnitelmaDto opsDto = createLukioOpetussuunnitelma();
        Opetussuunnitelma ops = opetussuunnitelmaRepository.getOne(opsDto.getId());

        TekstiKappaleDto tekstiKappaleDto = new TekstiKappaleDto();
        tekstiKappaleDto.setNimi(lt("A"));
        tekstiKappaleDto.setTeksti(lt("B"));

        TekstiKappaleViiteDto.Matala viiteDto = new TekstiKappaleViiteDto.Matala();
        viiteDto.setPakollinen(true);
        viiteDto.setTekstiKappale(tekstiKappaleDto);

        TekstiKappaleViiteDto.Matala uusi = opetussuunnitelmaService.addTekstiKappale(ops.getId(), viiteDto);
        assertThat(uusi.isNaytaPerusteenTeksti()).isTrue();
        assertThat(uusi.isNaytaPerusteenTeksti()).isTrue();
        assertThat(uusi.getTekstiKappale().getTeksti().get(Kieli.FI)).isEqualTo("B");

        uusi.setNaytaPerusteenTeksti(false);
        uusi.setNaytaPohjanTeksti(false);
        uusi.getTekstiKappale().setTeksti(lt("teksti"));
        TekstiKappaleViiteDto updated = tekstiKappaleViiteService.updateTekstiKappaleViite(opsDto.getId(), uusi.getId(), uusi);

        assertThat(updated.isNaytaPerusteenTeksti()).isFalse();
        assertThat(updated.isNaytaPerusteenTeksti()).isFalse();
        assertThat(updated.getTekstiKappale().getTeksti().get(Kieli.FI)).isNotBlank();
        assertThat(updated.getTekstiKappale().getTeksti().get(Kieli.FI)).isEqualTo("teksti");
    }

    @Test
    @Ignore
    public void testTekstikappalePuuReorder() {
        OpetussuunnitelmaDto opsDto = createLukioOpetussuunnitelma();
        Opetussuunnitelma ops = opetussuunnitelmaRepository.getOne(opsDto.getId());

        {
            final TekstiKappaleViiteDto.Puu tekstit = opetussuunnitelmaService.getTekstit(opsDto.getId(), TekstiKappaleViiteDto.Puu.class);
            assertThat(tekstit.getLapset()).hasSize(6);
            tekstiKappaleViiteService.reorderSubTree(opsDto.getId(), tekstit.getId(), tekstit);

            TekstiKappaleDto tekstiKappaleDto = new TekstiKappaleDto();
            tekstiKappaleDto.setNimi(lt("A"));
            tekstiKappaleDto.setTeksti(lt("B"));

            TekstiKappaleViiteDto.Puu viiteDto = new TekstiKappaleViiteDto.Puu();
            viiteDto.setPakollinen(true);
            viiteDto.setTekstiKappale(tekstiKappaleDto);

            tekstit.getLapset().add(viiteDto);
            tekstiKappaleViiteService.reorderSubTree(opsDto.getId(), tekstit.getId(), tekstit);

            tekstit.setLapset(
                    Stream.concat(
                        tekstit.getLapset().subList(5, 7).stream(),
                        tekstit.getLapset().subList(0, 5).stream()
                    ).collect(Collectors.toList()));
            assertThatThrownBy(() -> tekstiKappaleViiteService.reorderSubTree(opsDto.getId(), tekstit.getId(), tekstit))
                    .isInstanceOf(BusinessRuleViolationException.class)
                    .hasMessage("paatasolle-ei-sallita-muutoksia");
        }

        {
            final TekstiKappaleViiteDto.Puu tekstit = opetussuunnitelmaService.getTekstit(opsDto.getId(), TekstiKappaleViiteDto.Puu.class);
            assertThat(tekstit.getLapset()).hasSize(7);
            tekstiKappaleViiteService.reorderSubTree(opsDto.getId(), tekstit.getId(), tekstit);

            tekstit.getLapset().remove(0);

            assertThatThrownBy(() -> tekstiKappaleViiteService.reorderSubTree(opsDto.getId(), tekstit.getId(), tekstit))
                    .isInstanceOf(BusinessRuleViolationException.class)
                    .hasMessage("paatasolle-ei-sallita-muutoksia");
        }

    }

    @Test
    public void testPerusteenTekstinPoisto() {
        OpetussuunnitelmaDto ops = createLukioOpetussuunnitelma();
        TekstiKappaleViiteDto.Matala perusteenTekstiDto = tekstiKappaleViiteService.getTekstiKappaleViite(ops.getId(), findTkNimi(ops.getId(), "Uudistuva lukiokoulutus").getId());
        assertThatThrownBy(() ->tekstiKappaleViiteService.removeTekstiKappaleViite(ops.getId(), perusteenTekstiDto.getId()))
                .hasMessage("pakollista-tekstikappaletta-ei-voi-poistaa");
    }

    @Test
    public void testTekstikappaleenPoistoJaLisaysKunnanOpsiin() {
        OpetussuunnitelmaDto kunnanOps = createLukioOpetussuunnitelma();
        TekstiKappaleViiteDto.Matala kunnanTeksti = tekstiKappaleViiteService.addTekstiKappaleViite(kunnanOps.getId(), kunnanOps.getTekstit().getId(), TestUtils.createTekstiKappaleViite());
        opetussuunnitelmaService.addTekstiKappale(kunnanOps.getId(), TestUtils.createTekstiKappaleViite());

        OpetussuunnitelmaDto koulunOps = createOpetussuunnitelma(ops -> {
            ops.setPohja(Reference.of(kunnanOps.getId()));
        });
        assertThat(findTkNimi(koulunOps.getId(), kunnanTeksti.getTekstiKappale().getNimi().getTekstit().get(Kieli.FI))).isNotNull();

        tekstiKappaleViiteService.removeTekstiKappaleViite(kunnanOps.getId(), kunnanTeksti.getId());
        assertThat(findTkNimi(kunnanOps.getId(), kunnanTeksti.getTekstiKappale().getNimi().getTekstit().get(Kieli.FI))).isNull();
        assertThat(findTkNimi(koulunOps.getId(), kunnanTeksti.getTekstiKappale().getNimi().getTekstit().get(Kieli.FI))).isNull();

        TekstiKappaleViiteDto.Matala kunnanTekstiLisatty = opetussuunnitelmaService.addTekstiKappaleLapsi(kunnanOps.getId(), kunnanOps.getTekstit().getId(), TestUtils.createTekstiKappaleViite());
        TekstiKappaleViiteDto.Matala kunnanTekstiLisatty2 = opetussuunnitelmaService.addTekstiKappaleLapsi(kunnanOps.getId(), kunnanTekstiLisatty.getId(), TestUtils.createTekstiKappaleViite());
        assertThat(findTkNimi(koulunOps.getId(), kunnanTekstiLisatty2.getTekstiKappale().getNimi().getTekstit().get(Kieli.FI))).isNotNull();
    }

    @Test
    public void testTekstikappalePoistettuJaPalautusOpsiin() {
        OpetussuunnitelmaDto kunnanOps = createLukioOpetussuunnitelma();
        TekstiKappaleViiteDto.Matala kunnanTeksti = tekstiKappaleViiteService.addTekstiKappaleViite(kunnanOps.getId(), kunnanOps.getTekstit().getId(), TestUtils.createTekstiKappaleViite());
        opetussuunnitelmaService.addTekstiKappale(kunnanOps.getId(), TestUtils.createTekstiKappaleViite());

        OpetussuunnitelmaDto koulunOps = createOpetussuunnitelma(ops -> {
            ops.setPohja(Reference.of(kunnanOps.getId()));
        });
        assertThat(findTkNimi(kunnanOps.getId(), kunnanTeksti.getTekstiKappale().getNimi().getTekstit().get(Kieli.FI))).isNotNull();
        assertThat(findTkNimi(koulunOps.getId(), kunnanTeksti.getTekstiKappale().getNimi().getTekstit().get(Kieli.FI))).isNotNull();

        tekstiKappaleViiteService.removeTekstiKappaleViite(kunnanOps.getId(), kunnanTeksti.getId());
        assertThat(findTkNimi(kunnanOps.getId(), kunnanTeksti.getTekstiKappale().getNimi().getTekstit().get(Kieli.FI))).isNull();
        assertThat(findTkNimi(koulunOps.getId(), kunnanTeksti.getTekstiKappale().getNimi().getTekstit().get(Kieli.FI))).isNull();

        List<PoistettuTekstiKappale> poistettuTekstiKappales = poistettuTekstiKappaleRepository.findPoistetutByOpsId(kunnanOps.getId());
        tekstiKappaleViiteService.returnRemovedTekstikappale(kunnanOps.getId(), poistettuTekstiKappales.get(0).getId());
        assertThat(findTkNimi(kunnanOps.getId(), kunnanTeksti.getTekstiKappale().getNimi().getTekstit().get(Kieli.FI))).isNotNull();
        assertThat(findTkNimi(koulunOps.getId(), kunnanTeksti.getTekstiKappale().getNimi().getTekstit().get(Kieli.FI))).isNotNull();
    }

    private TekstiKappaleViite findTkNimi(Long opsId, String nimi) {
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(opsId);
        return CollectionUtil.treeToStream(ops.getTekstit(), TekstiKappaleViite::getLapset)
                .filter(viite -> viite.getVanhempi() != null)
                .filter(viite -> viite.getTekstiKappale().getNimi().getTeksti().get(Kieli.FI).equals(nimi))
                .findFirst()
                .orElse(null);
    }
}
