package fi.vm.sade.eperusteet.ylops.service.ops;

import fi.vm.sade.eperusteet.ylops.domain.AikatauluTapahtuma;
import fi.vm.sade.eperusteet.ylops.domain.Tila;
import fi.vm.sade.eperusteet.ylops.dto.Reference;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaLuontiDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmanAikatauluDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import fi.vm.sade.eperusteet.ylops.test.AbstractIntegrationTest;
import fi.vm.sade.eperusteet.ylops.test.util.TestUtils;
import java.util.Date;
import java.util.List;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
public class AikatauluServiceIT extends AbstractIntegrationTest {

    private final String TAPAHTUMA_TEKSTI = "tapahtuma tapahtui";
    private OpetussuunnitelmaDto opsDto;
    private Date TAPAHTUMAPAIVA = new DateTime(2018,1,1,1,1).toDate();
    private Date TAPAHTUMAPAIVA_UPDATE = new DateTime(2017,1,1,1,1).toDate();

    @Autowired
    OpetussuunnitelmanAikatauluService opetussuunnitelmanAikatauluService;

    @Autowired
    OpetussuunnitelmaService opetussuunnitelmaService;

    @Before
    public void setup() {
        OpetussuunnitelmaDto luotu = opetussuunnitelmaService.addPohja(TestUtils.createOpsPohja());
        opetussuunnitelmaService.updateTila(luotu.getId(), Tila.VALMIS);
        OpetussuunnitelmaLuontiDto ops = TestUtils.createOps();
        ops.setPohja(Reference.of(luotu.getId()));
        this.opsDto = opetussuunnitelmaService.addOpetussuunnitelma(TestUtils.createOps());
    }

    @Test
    public void addOpsAikatauluTest() {

        assertThat(opetussuunnitelmanAikatauluService.getAll(this.opsDto.getId())).isEmpty();

        OpetussuunnitelmanAikatauluDto dto = createDto();

        OpetussuunnitelmanAikatauluDto saved = opetussuunnitelmanAikatauluService.add(this.opsDto.getId(), dto);

        List<OpetussuunnitelmanAikatauluDto> dtos = opetussuunnitelmanAikatauluService.getAll(this.opsDto.getId());
        assertThat(dtos).hasSize(1);

        assertThat(dtos.get(0).getId()).isNotNull();
        assertThat(dtos.get(0))
                .extracting(
                        "tavoite.tekstit",
                        "tapahtuma",
                        "tapahtumapaiva",
                        "opetussuunnitelmaId")
                .containsExactly(
                        LokalisoituTekstiDto.of(TAPAHTUMA_TEKSTI).getTekstit(),
                        AikatauluTapahtuma.TAVOITE,
                        TAPAHTUMAPAIVA,
                        this.opsDto.getId()
                );
    }

    @Test
    public void testCrud() {
        assertThat(opetussuunnitelmanAikatauluService.getAll(this.opsDto.getId())).isEmpty();

        opetussuunnitelmanAikatauluService.add(this.opsDto.getId(), createDto());
        assertThat(opetussuunnitelmanAikatauluService.getAll(this.opsDto.getId())).hasSize(1);

        OpetussuunnitelmanAikatauluDto dto = opetussuunnitelmanAikatauluService.getAll(this.opsDto.getId()).get(0);
        assertThat(dto.getTapahtumapaiva()).isEqualTo(TAPAHTUMAPAIVA);
        dto.setTapahtumapaiva(TAPAHTUMAPAIVA_UPDATE);
        OpetussuunnitelmanAikatauluDto updatedDto = opetussuunnitelmanAikatauluService.update(this.opsDto.getId(), dto);
        assertThat(updatedDto.getTapahtumapaiva()).isEqualTo(TAPAHTUMAPAIVA_UPDATE);

        OpetussuunnitelmanAikatauluDto fetchedDto = opetussuunnitelmanAikatauluService.getAll(this.opsDto.getId()).get(0);
        assertThat(dto.getTapahtumapaiva()).isEqualTo(TAPAHTUMAPAIVA_UPDATE);

        opetussuunnitelmanAikatauluService.delete(this.opsDto.getId(), updatedDto);
        assertThat(opetussuunnitelmanAikatauluService.getAll(this.opsDto.getId())).isEmpty();
    }

    private OpetussuunnitelmanAikatauluDto createDto() {
        return OpetussuunnitelmanAikatauluDto.builder()
                .opetussuunnitelmaId(this.opsDto.getId())
                .tapahtuma(AikatauluTapahtuma.TAVOITE)
                .tapahtumapaiva(TAPAHTUMAPAIVA)
                .tavoite(LokalisoituTekstiDto.of(TAPAHTUMA_TEKSTI))
                .build();
    }
}
