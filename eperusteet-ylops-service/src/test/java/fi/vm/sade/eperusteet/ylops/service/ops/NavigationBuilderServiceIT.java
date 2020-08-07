package fi.vm.sade.eperusteet.ylops.service.ops;

import com.google.common.collect.Sets;
import fi.vm.sade.eperusteet.ylops.domain.KoulutustyyppiToteutus;
import fi.vm.sade.eperusteet.ylops.domain.Tila;
import fi.vm.sade.eperusteet.ylops.domain.Tyyppi;
import fi.vm.sade.eperusteet.ylops.dto.Reference;
import fi.vm.sade.eperusteet.ylops.dto.koodisto.OrganisaatioDto;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.Lops2019OpintojaksoDto;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.Lops2019OpintojaksonOppiaineDto;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.Lops2019PaikallinenOppiaineDto;
import fi.vm.sade.eperusteet.ylops.dto.navigation.NavigationNodeDto;
import fi.vm.sade.eperusteet.ylops.dto.navigation.NavigationType;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaLuontiDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import fi.vm.sade.eperusteet.ylops.repository.ops.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.ylops.service.lops2019.Lops2019OpintojaksoService;
import fi.vm.sade.eperusteet.ylops.service.lops2019.Lops2019OppiaineService;
import fi.vm.sade.eperusteet.ylops.test.AbstractIntegrationTest;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
public class NavigationBuilderServiceIT extends AbstractIntegrationTest {
    @Autowired
    private OpsDispatcher dispatcher;

    @Autowired
    private TekstiKappaleViiteService tekstiKappaleViiteService;

    @Autowired
    private OpetussuunnitelmaService opetussuunnitelmaService;

    @Autowired
    private Lops2019OppiaineService oppiaineService;

    @Autowired
    private Lops2019OpintojaksoService opintojaksoService;

    @Autowired
    private OpetussuunnitelmaRepository opetussuunnitelmaRepository;

    private OpetussuunnitelmaDto createOpetussuunnitelma() {
        OpetussuunnitelmaLuontiDto pohjaLuontiDto = new OpetussuunnitelmaLuontiDto();
        pohjaLuontiDto.setTyyppi(Tyyppi.POHJA);
        pohjaLuontiDto.setPerusteenDiaarinumero("1/2/3");
        pohjaLuontiDto.setToteutus(KoulutustyyppiToteutus.LOPS2019);
        OpetussuunnitelmaDto pohjaDto = opetussuunnitelmaService.addPohja(pohjaLuontiDto);
        opetussuunnitelmaService.updateTila(pohjaDto.getId(), Tila.VALMIS);

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
    public void testNavigationBuilder() {
        OpetussuunnitelmaDto ops = createOpetussuunnitelma();
        NavigationNodeDto navi = dispatcher.get(ops, NavigationBuilder.class).buildNavigation(ops.getId());
        assertThat(navi.getType()).isEqualTo(NavigationType.root);
        assertThat(navi.getChildren()).hasSize(7);
        assertThat(navi.getChildren().get(0).getType()).isEqualTo(NavigationType.viite);
    }

    @Test
    public void testNavigationBuilderPublic() {
        OpetussuunnitelmaDto ops = createOpetussuunnitelma();

        Lops2019PaikallinenOppiaineDto oppiaineDto = Lops2019PaikallinenOppiaineDto.builder()
                .nimi(LokalisoituTekstiDto.of("Biologia"))
                .kuvaus(LokalisoituTekstiDto.of("Kuvaus"))
                .koodi("paikallinen2")
                .perusteenOppiaineUri("oppiaineet_bi")
                .build();
        oppiaineDto = oppiaineService.addOppiaine(ops.getId(), oppiaineDto);

        {
            Lops2019OpintojaksoDto opintojaksoDto = Lops2019OpintojaksoDto.builder()
                    .oppiaineet(Collections.singleton(Lops2019OpintojaksonOppiaineDto.builder().koodi("oppiaineet_bi").build()))
                    .build();

            opintojaksoDto = opintojaksoService.addOpintojakso(ops.getId(), opintojaksoDto);
        }
        {
            Lops2019OpintojaksoDto opintojaksoDto = Lops2019OpintojaksoDto.builder()
                    .oppiaineet(Collections.singleton(Lops2019OpintojaksonOppiaineDto.builder().koodi("paikallinen2").build()))
                    .build();

            opintojaksoDto = opintojaksoService.addOpintojakso(ops.getId(), opintojaksoDto);
        }

        NavigationNodeDto navi = dispatcher.get(ops, NavigationBuilderPublic.class).buildNavigation(ops.getId());
        assertThat(navi.getType()).isEqualTo(NavigationType.root);
        assertThat(navi.getChildren()).hasSize(7);

        List<NavigationNodeDto> oppiaineet = navi.getChildren().stream().filter(child -> child.getType().equals(NavigationType.oppiaineet)).collect(Collectors.toList());
        assertThat(oppiaineet).hasSize(1);
        assertThat(oppiaineet).flatExtracting("children").hasSize(2);
        assertThat(oppiaineet.get(0).getChildren()).extracting("type").containsExactly(NavigationType.oppiaine, NavigationType.poppiaine);

        assertThat(oppiaineet.get(0).getChildren().get(0).getChildren()).hasSize(2);
        assertThat(oppiaineet.get(0).getChildren().get(0).getChildren()).extracting("type").containsExactly(NavigationType.opintojaksot, NavigationType.moduulit);

        assertThat(oppiaineet.get(0).getChildren().get(1).getChildren()).hasSize(1);
        assertThat(oppiaineet.get(0).getChildren().get(1).getChildren()).extracting("type").containsExactlyInAnyOrder(NavigationType.opintojaksot);
    }
}
