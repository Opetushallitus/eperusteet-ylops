package fi.vm.sade.eperusteet.ylops.service.lops2019;

import com.google.common.collect.Sets;
import fi.vm.sade.eperusteet.ylops.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.ylops.domain.KoulutustyyppiToteutus;
import fi.vm.sade.eperusteet.ylops.domain.Tila;
import fi.vm.sade.eperusteet.ylops.domain.Tyyppi;
import fi.vm.sade.eperusteet.ylops.domain.lops2019.Lops2019Opintojakso;
import fi.vm.sade.eperusteet.ylops.domain.ops.Opetussuunnitelma;
import fi.vm.sade.eperusteet.ylops.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.ylops.dto.Reference;
import fi.vm.sade.eperusteet.ylops.dto.koodisto.OrganisaatioDto;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.Lops2019OpintojaksoDto;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.Lops2019OpintojaksonModuuliDto;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.Lops2019OpintojaksonOppiaineDto;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.Lops2019PaikallinenOppiaineDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.Kommentti2019Dto;
import fi.vm.sade.eperusteet.ylops.dto.ops.Kommentti2019LuontiDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaLuontiDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteInfoDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteTekstiKappaleDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteTekstiKappaleViiteDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.lops2019.oppiaineet.Lops2019OppiaineKaikkiDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.lops2019.oppiaineet.moduuli.Lops2019ModuuliDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.TekstiKappaleViiteDto;
import fi.vm.sade.eperusteet.ylops.repository.lops2019.Lops2019OpintojaksoRepository;
import fi.vm.sade.eperusteet.ylops.repository.ops.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.ylops.service.exception.BusinessRuleViolationException;
import fi.vm.sade.eperusteet.ylops.service.external.EperusteetService;
import fi.vm.sade.eperusteet.ylops.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.ylops.service.ops.Kommentti2019Service;
import fi.vm.sade.eperusteet.ylops.service.ops.OpetussuunnitelmaService;
import fi.vm.sade.eperusteet.ylops.test.AbstractIntegrationTest;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class Lops2019ServiceIT extends AbstractIntegrationTest {

    @Autowired
    private DtoMapper mapper;

    @Autowired
    private EperusteetService eperusteetService;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private Lops2019OpintojaksoService opintojaksoService;

    @Autowired
    private Lops2019Service lopsService;

    @Autowired
    private Lops2019OppiaineService oppiaineService;

    @Autowired
    private OpetussuunnitelmaService opetussuunnitelmaService;

    @Autowired
    private OpetussuunnitelmaRepository opetussuunnitelmaRepository;

    @Autowired
    private Lops2019OpintojaksoRepository opintojaksoRepository;

    @Autowired
    private Kommentti2019Service kommenttiService;

    private OpetussuunnitelmaDto createLukioOpetussuunnitelma() {
        OpetussuunnitelmaLuontiDto pohjaLuontiDto = new OpetussuunnitelmaLuontiDto();
        pohjaLuontiDto.setTyyppi(Tyyppi.POHJA);
        pohjaLuontiDto.setPerusteenDiaarinumero("1/2/3");
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
    public void convertTestJsonToDto() {
        List<PerusteInfoDto> perusteet = eperusteetService.findPerusteet();
        assertThat(perusteet.size()).isGreaterThan(0);
        PerusteDto peruste = eperusteetService.getPeruste("1/2/3");
        assertThat(peruste.getLops2019()).isNotNull();
        List<Lops2019ModuuliDto> moduulit = peruste.getLops2019().getOppiaineet().get(1).getOppimaarat().get(0).getModuulit();
        assertThat(moduulit.size()).isNotEqualTo(0);
        PerusteTekstiKappaleDto viite = peruste.getLops2019().getSisalto().getLapset().get(1).getPerusteenOsa();
        assertThat(viite.getNimi().get(Kieli.FI)).isEqualTo("Arvoperusta");
        assertThat(viite.getTeksti().get(Kieli.FI)).isNotBlank();
        assertThat(viite.getOsanTyyppi()).isEqualTo("tekstikappale");
    }

    @Test
    public void testUudenLukiopohjanLuonti() {
        OpetussuunnitelmaLuontiDto pohjaLuontiDto = new OpetussuunnitelmaLuontiDto();
        pohjaLuontiDto.setTyyppi(Tyyppi.POHJA);
        pohjaLuontiDto.setPerusteenDiaarinumero("1/2/3");
        OpetussuunnitelmaDto pohjaDto = opetussuunnitelmaService.addPohja(pohjaLuontiDto);
        assertThat(pohjaDto)
                .extracting("toteutus", "koulutustyyppi", "perusteenDiaarinumero", "tyyppi")
                .containsExactly(KoulutustyyppiToteutus.LOPS2019, KoulutusTyyppi.LUKIOKOULUTUS, "1/2/3", Tyyppi.POHJA);

        TekstiKappaleViiteDto.Puu tekstit = opetussuunnitelmaService.getTekstit(pohjaDto.getId(), TekstiKappaleViiteDto.Puu.class);
        assertThat(tekstit.getLapset().size()).isEqualTo(6);
    }

    @Test
    public void testUudenLukionOpsinLuonti() {
        OpetussuunnitelmaDto opsDto = createLukioOpetussuunnitelma();
        assertThat(opsDto)
                .extracting("toteutus", "koulutustyyppi", "perusteenDiaarinumero", "tyyppi")
                .containsExactly(KoulutustyyppiToteutus.LOPS2019, KoulutusTyyppi.LUKIOKOULUTUS, "1/2/3", Tyyppi.OPS);
        assertThat(opsDto.getPohja()).isNotNull();

        List<Lops2019OppiaineKaikkiDto> oppiaineet = lopsService.getPerusteOppiaineet(opsDto.getId());
        assertThat(oppiaineet).isNotNull();
        assertThat(oppiaineet.size()).isNotEqualTo(0);

        PerusteTekstiKappaleViiteDto tekstikappaleet = lopsService.getPerusteTekstikappaleet(opsDto.getId());
        assertThat(tekstikappaleet).isNotNull();

        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(opsDto.getId());
        assertThat(ops.getLops2019()).isNotNull();
        assertThat(ops.getLops2019().getOpetussuunnitelma()).isNotNull();

    }

    @Test
    public void testOpintojaksojenHallinta() {
        OpetussuunnitelmaDto ops = createLukioOpetussuunnitelma();

        Lops2019OpintojaksoDto opintojaksoDto = Lops2019OpintojaksoDto.builder()
                .kuvaus(LokalisoituTekstiDto.of("Geometriaan liittyvät moduulit toteutetaan yhtenä opintojaksona"))
                .oppiaineet(Collections.singleton(Lops2019OpintojaksonOppiaineDto.builder().koodi("oppiaineet_maa").build()))
                .moduuli(Lops2019OpintojaksonModuuliDto.builder()
                        .koodiUri("moduulit_maa3")
                        .kuvaus(LokalisoituTekstiDto.of("X"))
                        .build())
                .moduuli(Lops2019OpintojaksonModuuliDto.builder()
                        .koodiUri("moduulit_maa4")
                        .kuvaus(LokalisoituTekstiDto.of("Y"))
                        .build())
                .build();

        opintojaksoDto.setNimi(LokalisoituTekstiDto.of("Geometriat"));
        opintojaksoDto.setKoodi("1234");

        opintojaksoDto = opintojaksoService.addOpintojakso(ops.getId(), opintojaksoDto);
        List<Lops2019OpintojaksoDto> opintojaksot = opintojaksoService.getAll(ops.getId());
        assertThat(opintojaksot.size()).isEqualTo(1);
        assertThat(opintojaksot.get(0).getId()).isEqualTo(opintojaksoDto.getId());
    }

    @Test
    public void testKommentointi() {
        OpetussuunnitelmaDto ops = createLukioOpetussuunnitelma();

        Kommentti2019Dto kommentti = new Kommentti2019Dto();
        kommentti.setSisalto("kommentti");
        Kommentti2019Dto lisatty = kommenttiService.add(ops.getId(), kommentti);
        assertThat(lisatty)
                .extracting(Kommentti2019LuontiDto::getParent, Kommentti2019Dto::getOpsId, Kommentti2019Dto::getLuoja, Kommentti2019LuontiDto::getSisalto)
                .containsExactly(null, ops.getId(), SecurityContextHolder.getContext().getAuthentication().getName(), "kommentti");
        assertThat(lisatty.getUuid()).isNotNull();
        assertThat(lisatty.getMuokattu()).isNotNull();
        assertThat(lisatty.getLuotu()).isNotNull();

        Kommentti2019Dto paivitys = new Kommentti2019Dto();
        paivitys.setSisalto("päivitetty");
        Kommentti2019Dto paivitetty = kommenttiService.update(ops.getId(), lisatty.getUuid(), paivitys);
        assertThat(lisatty.getUuid()).isEqualTo(paivitetty.getUuid());
        assertThat(paivitetty.getSisalto()).isEqualTo("päivitetty");

        Kommentti2019Dto kommenttiDto = kommenttiService.get(ops.getId(), lisatty.getUuid());
        assertThat(kommenttiDto.getSisalto()).isEqualTo("päivitetty");

        kommenttiService.remove(ops.getId(), lisatty.getUuid());

        assertThatThrownBy(() -> {
            kommenttiService.get(ops.getId(), lisatty.getUuid());
        }).isInstanceOf(BusinessRuleViolationException.class);
    }

    @Test
    @Rollback
    public void testVirheellisetKoodit() {
        OpetussuunnitelmaDto ops = createLukioOpetussuunnitelma();
        Lops2019OpintojaksoDto opintojaksoDto = Lops2019OpintojaksoDto.builder()
                .build();
        opintojaksoDto.setKoodi("1234");

        assertThatThrownBy(() -> opintojaksoService.addOpintojakso(ops.getId(), opintojaksoDto))
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessage("perusteen-oppiainetta-ei-olemassa");

        opintojaksoDto.setOppiaineet(Collections.singleton(Lops2019OpintojaksonOppiaineDto.builder()
                .koodi("oppiaineet_ma")
                .build()));

        assertThatThrownBy(() -> opintojaksoService.addOpintojakso(ops.getId(), opintojaksoDto))
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessage("opintojaksoon-ei-voi-liittaa-abstraktia-oppiainetta");

        opintojaksoDto.setOppiaineet(Collections.singleton(Lops2019OpintojaksonOppiaineDto.builder()
                .koodi("xyz")
                .build()));

        assertThatThrownBy(() -> opintojaksoService.addOpintojakso(ops.getId(), opintojaksoDto))
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessage("oppiainetta-ei-ole");

        opintojaksoDto.setOppiaineet(Collections.singleton(Lops2019OpintojaksonOppiaineDto.builder()
                .koodi("oppiaineet_bi")
                .build()));

        opintojaksoDto.setModuulit(Collections.singletonList(Lops2019OpintojaksonModuuliDto.builder()
                .koodiUri("moduulit_xyz")
                .build()));

        opintojaksoDto.setOppiaineet(new HashSet<>());
        assertThatThrownBy(() -> opintojaksoService.addOpintojakso(ops.getId(), opintojaksoDto))
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessage("perusteen-moduulia-ei-olemassa");

        opintojaksoDto.setOppiaineet(Collections.singleton(Lops2019OpintojaksonOppiaineDto.builder()
                .koodi("oppiaineet_bi")
                .build()));

        opintojaksoDto.setModuulit(Collections.singletonList(Lops2019OpintojaksonModuuliDto.builder()
                .koodiUri("moduulit_maa2")
                .build()));

        assertThatThrownBy(() -> opintojaksoService.addOpintojakso(ops.getId(), opintojaksoDto))
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessage("liitetyt-moduulit-tulee-loytya-opintojakson-oppiaineilta");

        {
            Lops2019PaikallinenOppiaineDto oppiaineDto = Lops2019PaikallinenOppiaineDto.builder()
                    .nimi(LokalisoituTekstiDto.of("Biologia"))
                    .kuvaus(LokalisoituTekstiDto.of("Kuvaus"))
                    .koodi("paikallinen1")
                    .build();
            oppiaineDto = oppiaineService.addOppiaine(ops.getId(), oppiaineDto);

            opintojaksoDto.setOppiaineet(Sets.newHashSet(
                    Lops2019OpintojaksonOppiaineDto.builder().koodi(oppiaineDto.getKoodi()).build(),
                    Lops2019OpintojaksonOppiaineDto.builder().koodi("oppiaineet_maa").build()));

            opintojaksoDto.setModuulit(Arrays.asList(
                    Lops2019OpintojaksonModuuliDto.builder().koodiUri("moduulit_bi1").build(),
                    Lops2019OpintojaksonModuuliDto.builder().koodiUri("moduulit_maa2").build()));

            assertThatThrownBy(() -> opintojaksoService.addOpintojakso(ops.getId(), opintojaksoDto))
                    .isInstanceOf(BusinessRuleViolationException.class)
                    .hasMessage("liitetyt-moduulit-tulee-loytya-opintojakson-oppiaineilta");
        }

        {
            opintojaksoDto.setOppiaineet(Sets.newHashSet(
                    Lops2019OpintojaksonOppiaineDto.builder()
                            .koodi("oppiaineet_maa")
                            .laajuus(-1l)
                            .build()));

            assertThatThrownBy(() -> opintojaksoService.addOpintojakso(ops.getId(), opintojaksoDto))
                    .isInstanceOf(BusinessRuleViolationException.class)
                    .hasMessage("opintojakson-oppiaineen-laajuus-virheellinen");
        }

        {
            Lops2019PaikallinenOppiaineDto oppiaineDto = Lops2019PaikallinenOppiaineDto.builder()
                    .nimi(LokalisoituTekstiDto.of("Biologia"))
                    .kuvaus(LokalisoituTekstiDto.of("Kuvaus"))
                    .koodi("paikallinen2")
                    .perusteenOppiaineUri("oppiaineet_bi")
                    .build();
            oppiaineDto = oppiaineService.addOppiaine(ops.getId(), oppiaineDto);

            opintojaksoDto.setOppiaineet(Sets.newHashSet(
                    Lops2019OpintojaksonOppiaineDto.builder().koodi(oppiaineDto.getKoodi()).build(),
                    Lops2019OpintojaksonOppiaineDto.builder().koodi("oppiaineet_maa").laajuus(1l).build()));

            opintojaksoDto.setModuulit(Arrays.asList(
                    Lops2019OpintojaksonModuuliDto.builder().koodiUri("moduulit_bi1").build(),
                    Lops2019OpintojaksonModuuliDto.builder().koodiUri("moduulit_maa2").build()));

            assertThat(opintojaksoService.addOpintojakso(ops.getId(), opintojaksoDto)).isNotNull();
        }
    }

    @Test
    public void testOpintojaksojenLaajuus() {
        OpetussuunnitelmaDto ops = createLukioOpetussuunnitelma();

        Lops2019OpintojaksoDto opintojaksoDto = Lops2019OpintojaksoDto.builder()
                .oppiaineet(Collections.singleton(Lops2019OpintojaksonOppiaineDto.builder()
                        .koodi("oppiaineet_maa")
                        .build()))
                .moduuli(Lops2019OpintojaksonModuuliDto.builder()
                        .koodiUri("moduulit_maa3")
                        .kuvaus(LokalisoituTekstiDto.of("X"))
                        .build())
                .moduuli(Lops2019OpintojaksonModuuliDto.builder()
                        .koodiUri("moduulit_maa4")
                        .kuvaus(LokalisoituTekstiDto.of("Y"))
                        .build())
                .build();

        opintojaksoDto.setNimi(LokalisoituTekstiDto.of("Geometriat"));
        opintojaksoDto.setKoodi("1234");

        opintojaksoDto = opintojaksoService.addOpintojakso(ops.getId(), opintojaksoDto);
        assertThat(opintojaksoDto.getLaajuus()).isEqualTo(6L);
    }

    @Test
    public void testOppiaineidenLisays() {
        OpetussuunnitelmaDto ops = createLukioOpetussuunnitelma();

        Lops2019PaikallinenOppiaineDto oppiaineDto = Lops2019PaikallinenOppiaineDto.builder()
                .nimi(LokalisoituTekstiDto.of("Robotiikka"))
                .kuvaus(LokalisoituTekstiDto.of("Kuvaus"))
                .koodi("1234")
                .build();

        oppiaineDto = oppiaineService.addOppiaine(ops.getId(), oppiaineDto);
        assertThat(oppiaineDto.getId()).isNotNull();
    }

    @Test
    @Transactional
    public void testOpintojaksosValid() {

        OpetussuunnitelmaDto ops = createLukioOpetussuunnitelma();

        Lops2019OpintojaksoDto opintojaksoDtoPaikallinen1 = Lops2019OpintojaksoDto.builder()
                .oppiaineet(Collections.singleton(Lops2019OpintojaksonOppiaineDto.builder()
                        .koodi("oppiaineet_maa")
                        .build()))
                .build();
        opintojaksoDtoPaikallinen1.setNimi(LokalisoituTekstiDto.of("Paikallinen1"));
        opintojaksoDtoPaikallinen1.setKoodi("Paikallinen1");
        opintojaksoDtoPaikallinen1 = opintojaksoService.addOpintojakso(ops.getId(), opintojaksoDtoPaikallinen1);

        Lops2019OpintojaksoDto opintojaksoDtoPaikallinen2 = Lops2019OpintojaksoDto.builder()
                .oppiaineet(Collections.singleton(Lops2019OpintojaksonOppiaineDto.builder()
                        .koodi("oppiaineet_maa")
                        .build()))
                .build();
        opintojaksoDtoPaikallinen2.setNimi(LokalisoituTekstiDto.of("Paikallinen1"));
        opintojaksoDtoPaikallinen2.setKoodi("Paikallinen1");
        opintojaksoDtoPaikallinen2 = opintojaksoService.addOpintojakso(ops.getId(), opintojaksoDtoPaikallinen2);

        Lops2019OpintojaksoDto opintojaksoDto = Lops2019OpintojaksoDto.builder()
                .oppiaineet(Collections.singleton(Lops2019OpintojaksonOppiaineDto.builder()
                        .koodi("oppiaineet_maa")
                        .build()))
                .build();
        opintojaksoDto.setNimi(LokalisoituTekstiDto.of("Geometriat"));
        opintojaksoDto.setKoodi("1234");

        opintojaksoDto.setPaikallisetOpintojaksot(Arrays.asList(opintojaksoDtoPaikallinen1, opintojaksoDtoPaikallinen2));
        opintojaksoDto = opintojaksoService.addOpintojakso(ops.getId(), opintojaksoDto);

        assertThat(opintojaksoService.tarkistaOpintojaksot(ops.getId())).isTrue();

    }

    @Test
    @Transactional
    public void testAddOpintojaksoPaikallinenInvalid() {

        OpetussuunnitelmaDto ops = createLukioOpetussuunnitelma();

        Lops2019OpintojaksoDto opintojaksoDtoPaikallinen1 = Lops2019OpintojaksoDto.builder()
                .oppiaineet(Collections.singleton(Lops2019OpintojaksonOppiaineDto.builder()
                        .koodi("oppiaineet_maa")
                        .build()))
                .moduulit(Collections.singleton(Lops2019OpintojaksonModuuliDto.builder()
                        .koodiUri("moduulit_maa3")
                        .build()))
                .build();
        opintojaksoDtoPaikallinen1.setNimi(LokalisoituTekstiDto.of("Paikallinen1"));
        opintojaksoDtoPaikallinen1.setKoodi("Paikallinen1");
        opintojaksoDtoPaikallinen1 = opintojaksoService.addOpintojakso(ops.getId(), opintojaksoDtoPaikallinen1);

        {
            Lops2019OpintojaksoDto opintojaksoDto = Lops2019OpintojaksoDto.builder()
                    .oppiaineet(Collections.singleton(Lops2019OpintojaksonOppiaineDto.builder()
                            .koodi("oppiaineet_maa2")
                            .build()))
                    .build();
            opintojaksoDto.setNimi(LokalisoituTekstiDto.of("Geometriat"));
            opintojaksoDto.setKoodi("1234");
            opintojaksoDto.setPaikallisetOpintojaksot(Arrays.asList(opintojaksoDtoPaikallinen1));
            assertThatThrownBy(() -> opintojaksoService.addOpintojakso(ops.getId(), opintojaksoDto)).hasMessage("opintojaksoon-lisatty-paikallinen-opintojakso-vaaralla-oppiaineella");
        }

        {
            Lops2019OpintojaksoDto opintojaksoDto = Lops2019OpintojaksoDto.builder()
                    .oppiaineet(Collections.singleton(Lops2019OpintojaksonOppiaineDto.builder()
                            .koodi("oppiaineet_maa")
                            .build()))
                    .moduulit(Collections.singleton(Lops2019OpintojaksonModuuliDto.builder()
                            .koodiUri("moduulit_maa3")
                            .build()))
                    .build();
            opintojaksoDto.setNimi(LokalisoituTekstiDto.of("Geometriat"));
            opintojaksoDto.setKoodi("1234");
            opintojaksoDto.setPaikallisetOpintojaksot(Arrays.asList(opintojaksoDtoPaikallinen1));
            assertThatThrownBy(() -> opintojaksoService.addOpintojakso(ops.getId(), opintojaksoDto)).hasMessage("opintojaksoon-lisatty-paikallisen-opintojakson-moduuleita");
        }

        Lops2019OpintojaksoDto opintojaksoDto = Lops2019OpintojaksoDto.builder()
                .oppiaineet(Collections.singleton(Lops2019OpintojaksonOppiaineDto.builder()
                        .koodi("oppiaineet_maa")
                        .build()))
                .build();
        opintojaksoDto.setNimi(LokalisoituTekstiDto.of("Geometriat"));
        opintojaksoDto.setKoodi("1234");
        opintojaksoDto.setPaikallisetOpintojaksot(Arrays.asList(opintojaksoDtoPaikallinen1));
        opintojaksoDto = opintojaksoService.addOpintojakso(ops.getId(), opintojaksoDto);


        {
            Lops2019OpintojaksoDto opintojaksoDto2 = Lops2019OpintojaksoDto.builder()
                    .oppiaineet(Collections.singleton(Lops2019OpintojaksonOppiaineDto.builder()
                            .koodi("oppiaineet_maa")
                            .build()))
                    .build();
            opintojaksoDto2.setNimi(LokalisoituTekstiDto.of("Geometriat 2"));
            opintojaksoDto2.setKoodi("2234");
            opintojaksoDto2.setPaikallisetOpintojaksot(Arrays.asList(opintojaksoDto));

            assertThatThrownBy(() -> opintojaksoService.addOpintojakso(ops.getId(), opintojaksoDto2)).hasMessage("paikalliseen-opintojaksoon-on-jo-lisatty-opintojaksoja");
        }
    }

    @Test
    @Transactional
    public void testOpetussuunnitelmanOpintojaksot_tarkistus() {

        OpetussuunnitelmaDto ops = createLukioOpetussuunnitelma();

        Lops2019OpintojaksoDto opintojaksoDtoPaikallinen1 = Lops2019OpintojaksoDto.builder()
                .oppiaineet(Collections.singleton(Lops2019OpintojaksonOppiaineDto.builder()
                        .koodi("oppiaineet_maa")
                        .build()))
                .build();
        opintojaksoDtoPaikallinen1.setNimi(LokalisoituTekstiDto.of("Paikallinen1"));
        opintojaksoDtoPaikallinen1.setKoodi("Paikallinen1");
        opintojaksoDtoPaikallinen1 = opintojaksoService.addOpintojakso(ops.getId(), opintojaksoDtoPaikallinen1);

        Lops2019OpintojaksoDto opintojaksoDto = Lops2019OpintojaksoDto.builder()
                .oppiaineet(Collections.singleton(Lops2019OpintojaksonOppiaineDto.builder()
                        .koodi("oppiaineet_maa")
                        .build()))
                .build();
        opintojaksoDto.setNimi(LokalisoituTekstiDto.of("Geometriat"));
        opintojaksoDto.setKoodi("1234");
        opintojaksoDto.setPaikallisetOpintojaksot(Arrays.asList(opintojaksoDtoPaikallinen1));
        opintojaksoDto = opintojaksoService.addOpintojakso(ops.getId(), opintojaksoDto);

        Lops2019OpintojaksoDto opintojaksoDto2 = Lops2019OpintojaksoDto.builder()
                .oppiaineet(Collections.singleton(Lops2019OpintojaksonOppiaineDto.builder()
                        .koodi("oppiaineet_maa")
                        .build()))
                .build();
        opintojaksoDto2.setNimi(LokalisoituTekstiDto.of("Geometriat"));
        opintojaksoDto2.setKoodi("2234");
        opintojaksoDto2.setPaikallisetOpintojaksot(Arrays.asList(opintojaksoDtoPaikallinen1));
        opintojaksoDto2 = opintojaksoService.addOpintojakso(ops.getId(), opintojaksoDto2);

        assertThat(opintojaksoService.tarkistaOpintojaksot(ops.getId())).isTrue();

        opintojaksoDto2.setPaikallisetOpintojaksot(Arrays.asList(opintojaksoDto));

        Lops2019Opintojakso opintojakso = opintojaksoRepository.save(mapper.map(opintojaksoDto2, Lops2019Opintojakso.class));

        assertThat(opintojaksoService.tarkistaOpintojaksot(ops.getId())).isFalse();

    }

    @Test
    public void testOpetussuunitelma_addTuotuOpintojakso() {

        Set<Long> ids = new HashSet<>();

        OpetussuunnitelmaDto ops1 = createLukioOpetussuunnitelma();
        {
            Lops2019OpintojaksoDto opintojaksoDto = Lops2019OpintojaksoDto.builder()
                    .oppiaineet(Collections.singleton(Lops2019OpintojaksonOppiaineDto.builder()
                            .koodi("oppiaineet_maa")
                            .build()))
                    .build();
            opintojaksoDto.setNimi(LokalisoituTekstiDto.of("Geometriat"));
            opintojaksoDto.setKoodi("1234");
            ids.add(opintojaksoService.addOpintojakso(ops1.getId(), opintojaksoDto).getId());
        }

        OpetussuunnitelmaDto ops2 = createLukioOpetussuunnitelma();
        {
            Lops2019OpintojaksoDto opintojaksoDto = Lops2019OpintojaksoDto.builder()
                    .oppiaineet(Collections.singleton(Lops2019OpintojaksonOppiaineDto.builder()
                            .koodi("oppiaineet_maa")
                            .build()))
                    .build();
            opintojaksoDto.setNimi(LokalisoituTekstiDto.of("Geometriat"));
            opintojaksoDto.setKoodi("1234");
            ids.add(opintojaksoService.addOpintojakso(ops2.getId(), opintojaksoDto).getId());
        }

        List<Lops2019OpintojaksoDto> ops1Opintojaksot = opintojaksoService.getAll(ops1.getId());
        opintojaksoService.addTuodutOpintojaksot(ops2.getId(), ops1Opintojaksot);

        assertThat(opintojaksoService.getAll(ops2.getId())).hasSize(1);
        assertThat(opintojaksoService.getTuodut(ops2.getId())).hasSize(1);
        assertThat(opintojaksoService.getAllTuodut(ops2.getId())).hasSize(2);
        assertThat(opintojaksoService.getAllTuodut(ops2.getId())).extracting("id").containsExactlyInAnyOrderElementsOf(ids);
    }
}
