package fi.vm.sade.eperusteet.ylops.service.ops;

import fi.vm.sade.eperusteet.ylops.domain.KoulutustyyppiToteutus;
import fi.vm.sade.eperusteet.ylops.domain.Tila;
import fi.vm.sade.eperusteet.ylops.domain.Tyyppi;
import fi.vm.sade.eperusteet.ylops.domain.oppiaine.Opetuksentavoite;
import fi.vm.sade.eperusteet.ylops.dto.Reference;
import fi.vm.sade.eperusteet.ylops.dto.koodisto.OrganisaatioDto;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.Lops2019OpintojaksoDto;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.Lops2019OpintojaksonModuuliDto;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.Lops2019OpintojaksonOppiaineDto;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.Lops2019PaikallinenOppiaineDto;
import fi.vm.sade.eperusteet.ylops.dto.navigation.NavigationNodeDto;
import fi.vm.sade.eperusteet.ylops.dto.navigation.NavigationType;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaLuontiDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.TekstiKappaleViiteDto;
import fi.vm.sade.eperusteet.ylops.repository.ops.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.ylops.service.lops2019.Lops2019OpintojaksoService;
import fi.vm.sade.eperusteet.ylops.service.lops2019.Lops2019OppiaineService;
import fi.vm.sade.eperusteet.ylops.test.AbstractIntegrationTest;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

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

    private OpetussuunnitelmaDto createOpetussuunnitelma(KoulutustyyppiToteutus koulutustyyppiToteutus) {
        OpetussuunnitelmaLuontiDto pohjaLuontiDto = new OpetussuunnitelmaLuontiDto();
        pohjaLuontiDto.setTyyppi(Tyyppi.POHJA);
        pohjaLuontiDto.setPerusteenDiaarinumero("1/2/3");
        pohjaLuontiDto.setToteutus(koulutustyyppiToteutus);
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
        OpetussuunnitelmaDto ops = opetussuunnitelmaService.addOpetussuunnitelma(opsLuontiDto);

        TekstiKappaleViiteDto.Matala tk = tekstiKappaleViiteService.getTekstiKappaleViite(ops.getId(), ops.getTekstit().get().getLapset().get(1).getId());
        tk.setPiilotettu(true);
        tekstiKappaleViiteService.updateTekstiKappaleViite(ops.getId(), tk.getId(), tk);
        
        return ops;
    }

    @Test
    public void testNavigationBuilder() {
        OpetussuunnitelmaDto ops = createOpetussuunnitelma(KoulutustyyppiToteutus.LOPS2019);
        NavigationNodeDto navi = dispatcher.get(ops, NavigationBuilder.class).buildNavigation(ops.getId());
        assertThat(navi.getType()).isEqualTo(NavigationType.root);
        assertThat(navi.getChildren()).hasSize(7);
        assertThat(navi.getChildren().get(0).getType()).isEqualTo(NavigationType.viite);
    }

    @Test
    public void testNavigationBuilderPublic() {
        OpetussuunnitelmaDto ops = createOpetussuunnitelma(KoulutustyyppiToteutus.LOPS2019);

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
                    .moduulit(Collections.singleton(Lops2019OpintojaksonModuuliDto.builder().koodiUri("moduulit_bi2_1").build()))
                    .build();

            opintojaksoDto = opintojaksoService.addOpintojakso(ops.getId(), opintojaksoDto);
        }
        {
            Lops2019OpintojaksoDto opintojaksoDto = Lops2019OpintojaksoDto.builder()
                    .oppiaineet(Collections.singleton(Lops2019OpintojaksonOppiaineDto.builder().koodi("paikallinen2").build()))
                    .moduulit(Collections.singleton(Lops2019OpintojaksonModuuliDto.builder().koodiUri("moduulit_bi5").build()))
                    .build();

            opintojaksoDto = opintojaksoService.addOpintojakso(ops.getId(), opintojaksoDto);
        }

        NavigationNodeDto navi = dispatcher.get(ops, NavigationBuilderPublic.class).buildNavigation(ops.getId());
        assertThat(navi.getType()).isEqualTo(NavigationType.root);
        assertThat(navi.getChildren()).hasSize(6);

        List<NavigationNodeDto> oppiaineet = navi.getChildren().stream()
                .filter(child -> child.getType().equals(NavigationType.oppiaineet))
                .collect(Collectors.toList());
        assertThat(oppiaineet).hasSize(1);
        assertThat(oppiaineet).flatExtracting("children").hasSize(1);
        assertThat(oppiaineet.get(0).getChildren()).extracting("type")
                .containsExactly(NavigationType.oppiaine);

        assertThat(oppiaineet.get(0).getChildren().get(0).getChildren()).hasSize(3);
        assertThat(oppiaineet.get(0).getChildren().get(0).getChildren()).extracting("type")
                .containsExactly(NavigationType.oppimaarat, NavigationType.opintojaksot, NavigationType.moduulit);

        assertThat(oppiaineet.get(0).getChildren().get(0).getChildren().stream()
                .filter(child -> child.getType().equals(NavigationType.moduulit))
                .map(NavigationNodeDto::getChildren)
                .flatMap(x -> x.stream())
                .collect(Collectors.toList())).hasSize(2);
    }

    @Test
    public void testNavigationBuilder_yksinkertainen() {
        OpetussuunnitelmaDto ops = createOpetussuunnitelma(KoulutustyyppiToteutus.YKSINKERTAINEN);

        {
            NavigationNodeDto navi = dispatcher.get(ops, NavigationBuilder.class).buildNavigation(ops.getId());
            assertThat(navi.getChildren()).hasSize(6);
        }

        {
            NavigationNodeDto navi = dispatcher.get(ops, NavigationBuilderPublic.class).buildNavigation(ops.getId());
            assertThat(navi.getChildren()).hasSize(5);
        }
    }
}
