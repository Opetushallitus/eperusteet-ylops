package fi.vm.sade.eperusteet.ylops.service.teksti;

import fi.vm.sade.eperusteet.ylops.domain.Tyyppi;
import fi.vm.sade.eperusteet.ylops.domain.ops.Opetussuunnitelma;
import fi.vm.sade.eperusteet.ylops.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.ylops.domain.teksti.TekstiKappaleViite;
import fi.vm.sade.eperusteet.ylops.dto.Reference;
import fi.vm.sade.eperusteet.ylops.dto.koodisto.OrganisaatioDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaLuontiDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.TekstiKappaleDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.TekstiKappaleViiteDto;
import fi.vm.sade.eperusteet.ylops.repository.ops.OpetuksenkohdealueRepository;
import fi.vm.sade.eperusteet.ylops.repository.ops.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.ylops.service.ops.OpetussuunnitelmaService;
import fi.vm.sade.eperusteet.ylops.service.ops.TekstiKappaleViiteService;
import fi.vm.sade.eperusteet.ylops.test.AbstractIntegrationTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static fi.vm.sade.eperusteet.ylops.test.util.TestUtils.lt;
import static org.assertj.core.api.Assertions.assertThat;

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

    private OpetussuunnitelmaDto createLukioOpetussuunnitelma() {
        OpetussuunnitelmaLuontiDto pohjaLuontiDto = new OpetussuunnitelmaLuontiDto();
        pohjaLuontiDto.setTyyppi(Tyyppi.POHJA);
        pohjaLuontiDto.setPerusteenDiaarinumero("1/2/3");
        OpetussuunnitelmaDto pohjaDto = opetussuunnitelmaService.addPohja(pohjaLuontiDto);

        OpetussuunnitelmaLuontiDto opsLuontiDto = new OpetussuunnitelmaLuontiDto();
        opsLuontiDto.setTyyppi(Tyyppi.OPS);
        opsLuontiDto.setOrganisaatiot(Stream.of("1.2.246.562.10.83037752777")
                .map(oid -> {
                    OrganisaatioDto result = new OrganisaatioDto();
                    result.setOid(oid);
                    return result;
                })
                .collect(Collectors.toSet()));
        opsLuontiDto.setPohja(Reference.of(pohjaDto.getId()));

        return opetussuunnitelmaService.addOpetussuunnitelma(opsLuontiDto);
    }

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
}
