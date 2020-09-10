package fi.vm.sade.eperusteet.ylops.service.teksti;

import fi.vm.sade.eperusteet.ylops.domain.ops.Opetussuunnitelma;
import fi.vm.sade.eperusteet.ylops.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.TekstiKappaleDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.TekstiKappaleViiteDto;
import fi.vm.sade.eperusteet.ylops.repository.ops.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.ylops.service.exception.BusinessRuleViolationException;
import fi.vm.sade.eperusteet.ylops.service.ops.OpetussuunnitelmaService;
import fi.vm.sade.eperusteet.ylops.service.ops.TekstiKappaleViiteService;
import fi.vm.sade.eperusteet.ylops.test.AbstractIntegrationTest;
import javax.persistence.EntityManager;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

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

            assertThatThrownBy(() -> tekstiKappaleViiteService.reorderSubTree(opsDto.getId(), tekstit.getId(), tekstit))
                    .isInstanceOf(BusinessRuleViolationException.class)
                    .hasMessage("paatasolle-ei-sallita-muutoksia");
        }

        {
            final TekstiKappaleViiteDto.Puu tekstit = opetussuunnitelmaService.getTekstit(opsDto.getId(), TekstiKappaleViiteDto.Puu.class);
            assertThat(tekstit.getLapset()).hasSize(6);
            tekstiKappaleViiteService.reorderSubTree(opsDto.getId(), tekstit.getId(), tekstit);

            tekstit.getLapset().remove(0);

            assertThatThrownBy(() -> tekstiKappaleViiteService.reorderSubTree(opsDto.getId(), tekstit.getId(), tekstit))
                    .isInstanceOf(BusinessRuleViolationException.class)
                    .hasMessage("paatasolle-ei-sallita-muutoksia");
        }

    }
}
