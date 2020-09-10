package fi.vm.sade.eperusteet.ylops.service.ops;

import fi.vm.sade.eperusteet.ylops.domain.lops2019.PoistetunTyyppi;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.Lops2019OpintojaksoDto;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.Lops2019OpintojaksonOppiaineDto;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.Lops2019PaikallinenOppiaineDto;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.Lops2019PoistettuDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import fi.vm.sade.eperusteet.ylops.repository.lops2019.Lops2019OpintojaksoRepository;
import fi.vm.sade.eperusteet.ylops.repository.lops2019.Lops2019OppiaineRepository;
import fi.vm.sade.eperusteet.ylops.repository.ops.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.ylops.service.lops2019.Lops2019OpintojaksoService;
import fi.vm.sade.eperusteet.ylops.service.lops2019.Lops2019OppiaineService;
import fi.vm.sade.eperusteet.ylops.test.AbstractIntegrationTest;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class PoistoServiceIT extends AbstractIntegrationTest {

    @Autowired
    private Lops2019OppiaineService lops2019oppiaineService;

    @Autowired
    private Lops2019OppiaineRepository lops2019OppiaineRepository;

    @Autowired
    private Lops2019OpintojaksoService lops2019OpintojaksoService;

    @Autowired
    private Lops2019OpintojaksoRepository lops2019OpintojaksoRepository;

    @Autowired
    private PoistoService poistoService;

    @Autowired
    private OpetussuunnitelmaRepository opetussuunnitelmaRepository;

    @Test
    @Transactional
    public void testLops2019OppiaineRemoveRestore() {
        TestTransaction.end();
        TestTransaction.start();
        TestTransaction.flagForCommit();
        OpetussuunnitelmaDto ops = createLukioOpetussuunnitelma();
        final Lops2019PaikallinenOppiaineDto oppiaineDto = lops2019oppiaineService.addOppiaine(ops.getId(), Lops2019PaikallinenOppiaineDto.builder()
                .nimi(LokalisoituTekstiDto.of("Robotiikka"))
                .kuvaus(LokalisoituTekstiDto.of("Kuvaus"))
                .koodi("1234")
                .build());
        assertThat(oppiaineDto.getId()).isNotNull();
        TestTransaction.end();

        TestTransaction.start();
        TestTransaction.flagForCommit(); // jotta poisto ilmestyy audit tauluun
        Lops2019PoistettuDto poistettu = poistoService.remove(
                opetussuunnitelmaRepository.getOne(ops.getId()),
                lops2019OppiaineRepository.getOne(oppiaineDto.getId()));
        lops2019OppiaineRepository.delete(oppiaineDto.getId());
        TestTransaction.end();

        TestTransaction.start();
        assertThat(poistoService.getRemoved(ops.getId())).isNotEmpty();
        assertThat(poistettu.getPoistettuId()).isEqualTo(oppiaineDto.getId());
        assertThat(poistettu.getTyyppi()).isEqualTo(PoistetunTyyppi.LOPS2019OPPIAINE);

        poistoService.restore(ops.getId(), poistettu.getId());
        assertThat(poistoService.getRemoved(ops.getId())).isEmpty();
        TestTransaction.end();
    }

    @Test
    @Transactional
    public void testLops2019OpintojaksoRemoveRestore() {
        TestTransaction.end();
        TestTransaction.start();
        TestTransaction.flagForCommit();
        OpetussuunnitelmaDto ops = createLukioOpetussuunnitelma();

        Lops2019OpintojaksoDto opintojaksoDto = Lops2019OpintojaksoDto.builder()
                .oppiaineet(Collections.singleton(Lops2019OpintojaksonOppiaineDto.builder()
                        .koodi("oppiaineet_maa")
                        .build()))
                .build();

        opintojaksoDto = lops2019OpintojaksoService.addOpintojakso(ops.getId(), opintojaksoDto);
        List<Lops2019OpintojaksoDto> opintojaksot = lops2019OpintojaksoService.getAll(ops.getId());
        assertThat(opintojaksot.size()).isEqualTo(1);
        TestTransaction.end();

        TestTransaction.start();
        TestTransaction.flagForCommit(); // jotta poisto ilmestyy audit tauluun
        Lops2019PoistettuDto poistettu = poistoService.remove(
                opetussuunnitelmaRepository.getOne(ops.getId()),
                lops2019OpintojaksoRepository.getOne(opintojaksoDto.getId()));
//        lops2019OpintojaksoRepository.delete(opintojaksoDto.getId());
        opetussuunnitelmaRepository.getOne(ops.getId()).getLops2019().getOpintojaksot().remove(lops2019OpintojaksoRepository.getOne(opintojaksoDto.getId()));
        TestTransaction.end();

        TestTransaction.start();
        assertThat(poistoService.getRemoved(ops.getId())).isNotEmpty();
        assertThat(poistettu.getPoistettuId()).isEqualTo(opintojaksoDto.getId());
        assertThat(poistettu.getTyyppi()).isEqualTo(PoistetunTyyppi.OPINTOJAKSO);

        poistoService.restore(ops.getId(), poistettu.getId());
        assertThat(poistoService.getRemoved(ops.getId())).isEmpty();
        TestTransaction.end();
    }
}
