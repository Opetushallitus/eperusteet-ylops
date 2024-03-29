package fi.vm.sade.eperusteet.ylops.service.lops2019;

import com.google.common.collect.Sets;
import fi.vm.sade.eperusteet.ylops.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.ylops.domain.KoulutustyyppiToteutus;
import fi.vm.sade.eperusteet.ylops.domain.Tyyppi;
import fi.vm.sade.eperusteet.ylops.domain.lops2019.Lops2019Opintojakso;
import fi.vm.sade.eperusteet.ylops.domain.lops2019.Lops2019OpintojaksonOppiaine;
import fi.vm.sade.eperusteet.ylops.domain.lops2019.Lops2019Sisalto;
import fi.vm.sade.eperusteet.ylops.domain.lops2019.Poistettu;
import fi.vm.sade.eperusteet.ylops.domain.lops2019.PoistetunTyyppi;
import fi.vm.sade.eperusteet.ylops.domain.ops.Opetussuunnitelma;
import fi.vm.sade.eperusteet.ylops.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.ylops.dto.OppiaineOpintojaksoDto;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.Lops2019OpintojaksoDto;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.Lops2019OpintojaksonModuuliDto;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.Lops2019OpintojaksonOppiaineDto;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.Lops2019PaikallinenOppiaineDto;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.Lops2019PoistettuDto;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.export.OpetussuunnitelmaExportLops2019Dto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaLuontiDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaNimiDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteInfoDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.lops2019.oppiaineet.Lops2019OppiaineKaikkiDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.lops2019.oppiaineet.moduuli.Lops2019ModuuliDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.TekstiKappaleViiteDto;
import fi.vm.sade.eperusteet.ylops.repository.lops2019.Lops2019OpintojaksoRepository;
import fi.vm.sade.eperusteet.ylops.repository.lops2019.Lops2019OpintojaksonOppiaineRepository;
import fi.vm.sade.eperusteet.ylops.repository.lops2019.Lops2019SisaltoRepository;
import fi.vm.sade.eperusteet.ylops.repository.lops2019.PoistetutRepository;
import fi.vm.sade.eperusteet.ylops.repository.ops.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.ylops.service.exception.BusinessRuleViolationException;
import fi.vm.sade.eperusteet.ylops.service.external.EperusteetService;
import fi.vm.sade.eperusteet.ylops.service.external.impl.perustedto.TekstiKappaleDto;
import fi.vm.sade.eperusteet.ylops.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.ylops.service.ops.Kommentti2019Service;
import fi.vm.sade.eperusteet.ylops.service.ops.OpetussuunnitelmaService;
import fi.vm.sade.eperusteet.ylops.service.ops.PoistoService;
import fi.vm.sade.eperusteet.ylops.service.ops.ValidointiService;
import fi.vm.sade.eperusteet.ylops.service.util.UpdateWrapperDto;
import fi.vm.sade.eperusteet.ylops.test.AbstractIntegrationTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private PoistetutRepository poistetutRepository;

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

    @Autowired
    private Lops2019OpintojaksonOppiaineRepository opintojaksonOppiaineRepository;

    @Autowired
    private Lops2019SisaltoRepository lops2019SisaltoRepository;

    @Autowired
    private PoistoService poistoService;

    @Autowired
    private ValidointiService validointiService;

    @Test
    public void convertTestJsonToDto() {
        List<PerusteInfoDto> perusteet = eperusteetService.findPerusteet();
        assertThat(perusteet.size()).isGreaterThan(0);
        PerusteDto peruste = eperusteetService.getPeruste("1/2/3");
        assertThat(peruste.getLops2019()).isNotNull();
        List<Lops2019ModuuliDto> moduulit = peruste.getLops2019().getOppiaineet().get(1).getOppimaarat().get(0).getModuulit();
        assertThat(moduulit.size()).isNotEqualTo(0);
        TekstiKappaleDto tekstiKappale = peruste.getLops2019().getSisalto().getLapset().get(1).getTekstiKappale();
        assertThat(tekstiKappale.getNimi().get(Kieli.FI)).isEqualTo("Arvoperusta");
        assertThat(tekstiKappale.getTeksti().get(Kieli.FI)).isNotBlank();
        assertThat(tekstiKappale.getOsanTyyppi()).isEqualTo("tekstikappale");
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

        fi.vm.sade.eperusteet.ylops.service.external.impl.perustedto.TekstiKappaleViiteDto tekstikappaleet = lopsService.getPerusteTekstikappaleet(opsDto.getId());
        assertThat(tekstikappaleet).isNotNull();

        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(opsDto.getId());
        assertThat(ops.getLops2019()).isNotNull();
        assertThat(ops.getLops2019().getOpetussuunnitelma()).isNotNull();

    }

    // Relates EP-2396
    @Test
    public void testOpintojaksollisenOppiaineenPoisto() {
        OpetussuunnitelmaDto ops = createLukioOpetussuunnitelma();

        // Oppiaineen lisäys
        Lops2019PaikallinenOppiaineDto oppiaineDto = Lops2019PaikallinenOppiaineDto.builder()
                .nimi(LokalisoituTekstiDto.of("A"))
                .kuvaus(LokalisoituTekstiDto.of("A"))
                .koodi("poa1")
                .build();
        oppiaineDto = oppiaineService.addOppiaine(ops.getId(), oppiaineDto);

        // Paikalliselle opintojakso
        Lops2019OpintojaksoDto opintojaksoDto = Lops2019OpintojaksoDto.builder()
                .kuvaus(LokalisoituTekstiDto.of("X"))
                .oppiaineet(Collections.singleton(Lops2019OpintojaksonOppiaineDto.builder().koodi("poa1").build()))
                .build();

        opintojaksoDto.setNimi(LokalisoituTekstiDto.of("Oj nimi"));
        opintojaksoDto.setKoodi("oj1");
        opintojaksoDto = opintojaksoService.addOpintojakso(ops.getId(), opintojaksoDto);
        Long oppiaineId = oppiaineDto.getId();

        assertThatThrownBy(() -> {
            oppiaineService.removeOne(ops.getId(), oppiaineId);
        }).hasMessage("oppaine-sisaltaa-opintojaksoja");
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
        List<Lops2019OpintojaksoDto> opintojaksot = opintojaksoService.getAll(ops.getId(), Lops2019OpintojaksoDto.class);
        assertThat(opintojaksot.size()).isEqualTo(1);
        assertThat(opintojaksot.get(0).getId()).isEqualTo(opintojaksoDto.getId());
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

            opintojaksoDto.setKoodi("virheellinen!2");
            assertThatThrownBy(() -> opintojaksoService.addOpintojakso(ops.getId(), opintojaksoDto))
                    .isInstanceOf(BusinessRuleViolationException.class)
                    .hasMessage("koodi-virheellinen");
        }
        
        {
            opintojaksoDto.setKoodi("vali_di.12-3");
            assertThat(opintojaksoService.addOpintojakso(ops.getId(), opintojaksoDto)).isNotNull();
        }
    }

    @Test
    @Rollback
    public void testOppiaineKoodiMuutos() {

        OpetussuunnitelmaDto ops1 = createLukioOpetussuunnitelma();
        OpetussuunnitelmaDto ops2 = createLukioOpetussuunnitelma();
        OpetussuunnitelmaDto ops3 = createLukioOpetussuunnitelma();

        {
            Lops2019PaikallinenOppiaineDto oppiaineDto = Lops2019PaikallinenOppiaineDto.builder()
                    .nimi(LokalisoituTekstiDto.of("Biologia"))
                    .kuvaus(LokalisoituTekstiDto.of("Kuvaus"))
                    .koodi("paikallinen")
                    .perusteenOppiaineUri("oppiaineet_bi")
                    .build();
            oppiaineDto = oppiaineService.addOppiaine(ops1.getId(), oppiaineDto);
            Lops2019OpintojaksoDto opintojaksoDto = Lops2019OpintojaksoDto.builder()
                    .build();
            opintojaksoDto.setKoodi("1234");

            opintojaksoDto.setOppiaineet(Sets.newHashSet(
                    Lops2019OpintojaksonOppiaineDto.builder().koodi(oppiaineDto.getKoodi()).build(),
                    Lops2019OpintojaksonOppiaineDto.builder().koodi("oppiaineet_maa").laajuus(1l).build()));

            assertThat(opintojaksoService.addOpintojakso(ops1.getId(), opintojaksoDto)).isNotNull();
        }

        {
            Lops2019PaikallinenOppiaineDto oppiaineDto = Lops2019PaikallinenOppiaineDto.builder()
                    .nimi(LokalisoituTekstiDto.of("Biologia"))
                    .kuvaus(LokalisoituTekstiDto.of("Kuvaus"))
                    .koodi("paikallinen")
                    .perusteenOppiaineUri("oppiaineet_bi")
                    .build();
            oppiaineDto = oppiaineService.addOppiaine(ops2.getId(), oppiaineDto);
            Lops2019OpintojaksoDto opintojaksoDto = Lops2019OpintojaksoDto.builder()
                    .build();
            opintojaksoDto.setKoodi("1234");

            opintojaksoDto.setOppiaineet(Sets.newHashSet(
                    Lops2019OpintojaksonOppiaineDto.builder().koodi(oppiaineDto.getKoodi()).build(),
                    Lops2019OpintojaksonOppiaineDto.builder().koodi("oppiaineet_maa").laajuus(1l).build()));

            assertThat(opintojaksoService.addOpintojakso(ops2.getId(), opintojaksoDto)).isNotNull();

            oppiaineDto.setKoodi("muutettuKoodi");
            UpdateWrapperDto<Lops2019PaikallinenOppiaineDto> wrapperDto = new UpdateWrapperDto<>();
            wrapperDto.setData(oppiaineDto);

            oppiaineService.updateOppiaine(ops2.getId(), oppiaineDto.getId(), wrapperDto);
        }

        {
            Lops2019PaikallinenOppiaineDto oppiaineDto = Lops2019PaikallinenOppiaineDto.builder()
                    .nimi(LokalisoituTekstiDto.of("Biologia"))
                    .kuvaus(LokalisoituTekstiDto.of("Kuvaus"))
                    .koodi("paikallinen")
                    .perusteenOppiaineUri("oppiaineet_bi")
                    .build();
            oppiaineDto = oppiaineService.addOppiaine(ops3.getId(), oppiaineDto);

            Lops2019OpintojaksoDto paikallinenOpintojaksoDto = Lops2019OpintojaksoDto.builder()
                    .build();
            paikallinenOpintojaksoDto.setKoodi("pp1234");
            paikallinenOpintojaksoDto.setOppiaineet(Sets.newHashSet(
                    Lops2019OpintojaksonOppiaineDto.builder().koodi(oppiaineDto.getKoodi()).build()));

            paikallinenOpintojaksoDto = opintojaksoService.addOpintojakso(ops3.getId(), paikallinenOpintojaksoDto);

            Lops2019OpintojaksoDto opintojaksoDto = Lops2019OpintojaksoDto.builder()
                    .build();
            opintojaksoDto.setKoodi("1234");
            opintojaksoDto.setOppiaineet(Sets.newHashSet(
                    Lops2019OpintojaksonOppiaineDto.builder().koodi(oppiaineDto.getKoodi()).build()));
            opintojaksoDto.setPaikallisetOpintojaksot(Arrays.asList(paikallinenOpintojaksoDto));

            assertThat(opintojaksoService.addOpintojakso(ops3.getId(), opintojaksoDto)).isNotNull();

            oppiaineDto.setKoodi("muutettuKoodi2");
            UpdateWrapperDto<Lops2019PaikallinenOppiaineDto> wrapperDto = new UpdateWrapperDto<>();
            wrapperDto.setData(oppiaineDto);

            oppiaineService.updateOppiaine(ops3.getId(), oppiaineDto.getId(), wrapperDto);

            oppiaineDto.setKoodi("koodi!");
            Long oppiaineId = oppiaineDto.getId();
            UpdateWrapperDto<Lops2019PaikallinenOppiaineDto> wrapperDto2 = new UpdateWrapperDto<>();
            wrapperDto2.setData(oppiaineDto);

            assertThatThrownBy(() -> oppiaineService.updateOppiaine(ops3.getId(), oppiaineId, wrapperDto2));
        }

        {
            Lops2019PaikallinenOppiaineDto oppiaineDto = Lops2019PaikallinenOppiaineDto.builder()
                    .nimi(LokalisoituTekstiDto.of("Biologia"))
                    .kuvaus(LokalisoituTekstiDto.of("Kuvaus"))
                    .koodi("virhe!1")
                    .perusteenOppiaineUri("oppiaineet_bi")
                    .build();
            assertThatThrownBy(() -> oppiaineService.addOppiaine(ops3.getId(), oppiaineDto));
        }

        List<Lops2019OpintojaksonOppiaine> opintojaksonOppiaineet = opintojaksonOppiaineRepository.findAll();
        assertThat(opintojaksonOppiaineet).hasSize(6);
        assertThat(opintojaksonOppiaineet).extracting("koodi").containsExactlyInAnyOrder("paikallinen", "muutettuKoodi", "muutettuKoodi2", "muutettuKoodi2", "oppiaineet_maa", "oppiaineet_maa");

        {
            assertThat(lops2019SisaltoRepository.findOpintojaksonOppiaineetByOpetussuunnitelma(ops1.getId())).hasSize(2);
            assertThat(lops2019SisaltoRepository.findOpintojaksonOppiaineetByOpetussuunnitelma(ops1.getId(), "paikallinen")).hasSize(1);
            assertThat(lops2019SisaltoRepository.findOpintojaksonOppiaineetByOpetussuunnitelma(ops1.getId(), "muutettuKoodi")).hasSize(0);
            assertThat(lops2019SisaltoRepository.findOpintojaksonOppiaineetByOpetussuunnitelma(ops1.getId(), "oppiaineet_maa")).hasSize(1);
        }

        {
            assertThat(lops2019SisaltoRepository.findOpintojaksonOppiaineetByOpetussuunnitelma(ops2.getId())).hasSize(2);
            assertThat(lops2019SisaltoRepository.findOpintojaksonOppiaineetByOpetussuunnitelma(ops2.getId(), "paikallinen")).hasSize(0);
            assertThat(lops2019SisaltoRepository.findOpintojaksonOppiaineetByOpetussuunnitelma(ops2.getId(), "muutettuKoodi")).hasSize(1);
            assertThat(lops2019SisaltoRepository.findOpintojaksonOppiaineetByOpetussuunnitelma(ops2.getId(), "oppiaineet_maa")).hasSize(1);
        }

        {
            assertThat(lops2019SisaltoRepository.findOpintojaksonOppiaineetByOpetussuunnitelma(ops3.getId())).hasSize(2);
            assertThat(lops2019SisaltoRepository.findOpintojaksonOppiaineetByOpetussuunnitelma(ops3.getId(), "paikallinen")).hasSize(0);
            assertThat(lops2019SisaltoRepository.findOpintojaksonOppiaineetByOpetussuunnitelma(ops3.getId(), "muutettuKoodi2")).hasSize(2);
        }
    }

    @Test
    @Rollback
    @Transactional
    public void testOppiaineetJarjestamien() {
        OpetussuunnitelmaDto opsDto = createLukioOpetussuunnitelma();
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(opsDto.getId());

        Lops2019Sisalto lops2019 = ops.getLops2019();
        assertThat(lops2019.getOppiaineJarjestykset()).hasSize(0);

        List<OppiaineOpintojaksoDto> jarjestykset = new ArrayList<>();
        opetussuunnitelmaService.updateOppiaineJaOpintojaksojarjestys(opsDto.getId(), jarjestykset);
        assertThat(lops2019.getOppiaineJarjestykset()).hasSize(0);

        List<Lops2019OppiaineKaikkiDto> oppiaineet = lopsService.getPerusteOppiaineet(opsDto.getId());
        assertThat(oppiaineet).hasSize(2);

        OppiaineOpintojaksoDto biologia = new OppiaineOpintojaksoDto();
        biologia.setId(oppiaineet.get(0).getId());
        jarjestykset.add(biologia);

        OppiaineOpintojaksoDto matematiikka = new OppiaineOpintojaksoDto();
        matematiikka.setId(oppiaineet.get(1).getId());
        jarjestykset.add(matematiikka);

        opetussuunnitelmaService.updateOppiaineJaOpintojaksojarjestys(opsDto.getId(), jarjestykset);
        assertThat(lops2019.getOppiaineJarjestykset()).hasSize(2);
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

        assertThat(validointiService.tarkistaOpintojaksot(ops.getId())).isEmpty();

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

        assertThat(validointiService.tarkistaOpintojaksot(ops.getId())).isEmpty();

        opintojaksoDto2.setPaikallisetOpintojaksot(Arrays.asList(opintojaksoDto));

        Lops2019Opintojakso opintojakso = opintojaksoRepository.save(mapper.map(opintojaksoDto2, Lops2019Opintojakso.class));

        assertThat(validointiService.tarkistaOpintojaksot(ops.getId())).isNotEmpty();

    }

    @Test
    public void testOpetussuunitelma_addTuotuOpintojakso() {

        Set<Long> ids = new HashSet<>();

        OpetussuunnitelmaDto ops = createLukioOpetussuunnitelma();
        OpetussuunnitelmaNimiDto pohja = ops.getPohja();
        {
            Lops2019OpintojaksoDto opintojaksoDto = Lops2019OpintojaksoDto.builder()
                    .oppiaineet(Collections.singleton(Lops2019OpintojaksonOppiaineDto.builder()
                            .koodi("oppiaineet_maa")
                            .build()))
                    .build();
            opintojaksoDto.setNimi(LokalisoituTekstiDto.of("Geometriat"));
            opintojaksoDto.setKoodi("1234");
            ids.add(opintojaksoService.addOpintojakso(pohja.getId(), opintojaksoDto).getId());
        }

        {
            Lops2019OpintojaksoDto opintojaksoDto = Lops2019OpintojaksoDto.builder()
                    .oppiaineet(Collections.singleton(Lops2019OpintojaksonOppiaineDto.builder()
                            .koodi("oppiaineet_mab")
                            .build()))
                    .build();
            opintojaksoDto.setNimi(LokalisoituTekstiDto.of("Geometriat"));
            opintojaksoDto.setKoodi("12345");
            ids.add(opintojaksoService.addOpintojakso(ops.getId(), opintojaksoDto).getId());
        }

        List<Lops2019OpintojaksoDto> ops1Opintojaksot = opintojaksoService.getAll(pohja.getId(), Lops2019OpintojaksoDto.class);

        assertThat(opintojaksoService.getAll(ops.getId(), Lops2019OpintojaksoDto.class)).hasSize(1);
        assertThat(opintojaksoService.getTuodut(ops.getId(), Lops2019OpintojaksoDto.class)).hasSize(1);
        assertThat(opintojaksoService.getAllTuodut(ops.getId(), Lops2019OpintojaksoDto.class)).hasSize(2);
        assertThat(opintojaksoService.getAllTuodut(ops.getId(), Lops2019OpintojaksoDto.class)).extracting("id").containsExactlyInAnyOrderElementsOf(ids);

        {
            Poistettu poistettu = new Poistettu();
            poistettu.setOpetussuunnitelma(opetussuunnitelmaRepository.getOne(ops.getId()));
            poistettu.setPoistettuId(opintojaksoService.getTuodut(ops.getId(), Lops2019OpintojaksoDto.class).get(0).getId());
            poistettu.setTyyppi(PoistetunTyyppi.TUOTU_OPINTOJAKSO);
            poistetutRepository.save(poistettu);
        }
        assertThat(opintojaksoService.getTuodut(ops.getId(), Lops2019OpintojaksoDto.class)).hasSize(0);
    }

    @Test
    public void testOpetussuunitelma_tuodutOppimaarat() {
        OpetussuunnitelmaDto ops = createLukioOpetussuunnitelma();
        ops.setTuoPohjanOppimaarat(true);
        opetussuunnitelmaService.updateOpetussuunnitelma(ops);

        OpetussuunnitelmaNimiDto pohja = ops.getPohja();
        Lops2019PaikallinenOppiaineDto pohjanOppiaineDto1 = oppiaineService.addOppiaine(pohja.getId(), Lops2019PaikallinenOppiaineDto.builder()
                .nimi(LokalisoituTekstiDto.of("A1"))
                .kuvaus(LokalisoituTekstiDto.of("A1"))
                .koodi("poa1")
                .build());
        Lops2019PaikallinenOppiaineDto pohjanOppiaineDto2 = oppiaineService.addOppiaine(pohja.getId(), Lops2019PaikallinenOppiaineDto.builder()
                .nimi(LokalisoituTekstiDto.of("A2"))
                .kuvaus(LokalisoituTekstiDto.of("A2"))
                .koodi("poa2")
                .build());

        Lops2019PaikallinenOppiaineDto oppiaineDto1 = oppiaineService.addOppiaine(ops.getId(), Lops2019PaikallinenOppiaineDto.builder()
                .nimi(LokalisoituTekstiDto.of("B1"))
                .kuvaus(LokalisoituTekstiDto.of("B1"))
                .koodi("pob1")
                .build());
        Lops2019PaikallinenOppiaineDto oppiaineDto2 = oppiaineService.addOppiaine(ops.getId(), Lops2019PaikallinenOppiaineDto.builder()
                .nimi(LokalisoituTekstiDto.of("B2"))
                .kuvaus(LokalisoituTekstiDto.of("B2"))
                .koodi("pob2")
                .build());

        assertThat(oppiaineService.getAll(ops.getId(), Lops2019PaikallinenOppiaineDto.class)).hasSize(4);
        assertThat(oppiaineService.getAll(ops.getId(), Lops2019PaikallinenOppiaineDto.class)).extracting("id").containsExactlyInAnyOrder(pohjanOppiaineDto1.getId(), pohjanOppiaineDto2.getId(), oppiaineDto1.getId(), oppiaineDto2.getId());

        oppiaineService.removeOne(ops.getId(), pohjanOppiaineDto1.getId());
        assertThat(oppiaineService.getAll(ops.getId(), Lops2019PaikallinenOppiaineDto.class)).hasSize(3);
        assertThat(oppiaineService.getAll(ops.getId(), Lops2019PaikallinenOppiaineDto.class)).extracting("id").containsExactlyInAnyOrder(pohjanOppiaineDto2.getId(), oppiaineDto1.getId(), oppiaineDto2.getId());

        oppiaineService.removeOne(ops.getId(), pohjanOppiaineDto2.getId());
        assertThat(oppiaineService.getAll(ops.getId(), Lops2019PaikallinenOppiaineDto.class)).hasSize(2);
        assertThat(oppiaineService.getAll(ops.getId(), Lops2019PaikallinenOppiaineDto.class)).extracting("id").containsExactlyInAnyOrder(oppiaineDto1.getId(), oppiaineDto2.getId());

        oppiaineService.removeOne(ops.getId(), oppiaineDto1.getId());
        assertThat(oppiaineService.getAll(ops.getId(), Lops2019PaikallinenOppiaineDto.class)).hasSize(1);
        assertThat(oppiaineService.getAll(ops.getId(), Lops2019PaikallinenOppiaineDto.class)).extracting("id").containsExactlyInAnyOrder(oppiaineDto2.getId());

        oppiaineService.removeOne(ops.getId(), oppiaineDto2.getId());
        assertThat(oppiaineService.getAll(ops.getId(), Lops2019PaikallinenOppiaineDto.class)).hasSize(0);

        assertThat(oppiaineService.getAll(ops.getPohja().getId(), Lops2019PaikallinenOppiaineDto.class)).hasSize(2);
        assertThat(oppiaineService.getAll(ops.getPohja().getId(), Lops2019PaikallinenOppiaineDto.class)).extracting("id").containsExactlyInAnyOrder(pohjanOppiaineDto1.getId(), pohjanOppiaineDto2.getId());

        List<Lops2019PoistettuDto> poistetut = poistoService.getRemoved(ops.getId());

        poistetut.stream().forEach(poistettu -> poistoService.restore(ops.getId(), poistettu.getId()));
        assertThat(oppiaineService.getAll(ops.getId(), Lops2019PaikallinenOppiaineDto.class)).hasSize(4);
        assertThat(oppiaineService.getAll(ops.getId(), Lops2019PaikallinenOppiaineDto.class)).extracting("id").contains(pohjanOppiaineDto1.getId(), pohjanOppiaineDto2.getId());
    }

    @Test
    public void testExport() {
        OpetussuunnitelmaDto ops = createLukioOpetussuunnitelma();

        Lops2019PaikallinenOppiaineDto oppiaineDto = oppiaineService.addOppiaine(ops.getId(), Lops2019PaikallinenOppiaineDto.builder()
                .nimi(LokalisoituTekstiDto.of("A"))
                .kuvaus(LokalisoituTekstiDto.of("A"))
                .koodi("pao")
                .build());

        Lops2019OpintojaksoDto opintojaksoDto = Lops2019OpintojaksoDto.builder()
                .kuvaus(LokalisoituTekstiDto.of(""))
                .oppiaineet(Collections.singleton(Lops2019OpintojaksonOppiaineDto.builder().koodi("pao").build()))
                .build();
        opintojaksoDto.setNimi(LokalisoituTekstiDto.of("oj1"));
        opintojaksoDto.setKoodi("oj1");
        opintojaksoDto = opintojaksoService.addOpintojakso(ops.getId(), opintojaksoDto);

        Lops2019OpintojaksoDto opintojakso2Dto = Lops2019OpintojaksoDto.builder()
                .kuvaus(LokalisoituTekstiDto.of(""))
                .oppiaineet(Collections.singleton(Lops2019OpintojaksonOppiaineDto.builder().koodi("oppiaineet_bi").build()))
                .moduuli(Lops2019OpintojaksonModuuliDto.builder()
                        .koodiUri("moduulit_bi1")
                        .kuvaus(LokalisoituTekstiDto.of("X"))
                        .build())
                .build();
        opintojakso2Dto.setNimi(LokalisoituTekstiDto.of("oj1"));
        opintojakso2Dto.setKoodi("bi1");
        opintojakso2Dto = opintojaksoService.addOpintojakso(ops.getId(), opintojakso2Dto);

        OpetussuunnitelmaExportLops2019Dto exported = (OpetussuunnitelmaExportLops2019Dto) opetussuunnitelmaService.getExportedOpetussuunnitelma(ops.getId());
        assertThat(exported.getPeruste()).isNotNull();
        assertThat(exported.getPohja()).isNotNull();
        assertThat(exported.getTekstit()).isNotNull();
        assertThat(exported.getValtakunnallisetOppiaineet()).hasSize(2);
        assertThat(exported.getPaikallisetOppiaineet()).hasSize(1);
        assertThat(exported.getOpintojaksot()).hasSize(2);
        assertThat(exported.getPaikallisetOppiaineet().get(0).getOpintojaksot()).hasSize(1);
        assertThat(exported.getValtakunnallisetOppiaineet().get(0).getOpintojaksot()).hasSize(1);
        assertThat(exported.getPaikallisetOppiaineet().get(0).getOpintojaksot().get(0).getOpintojaksoId())
                .isEqualTo(opintojaksoDto.getId());
        assertThat(exported.getValtakunnallisetOppiaineet().get(0).getOpintojaksot().get(0).getOpintojaksoId())
                .isEqualTo(opintojakso2Dto.getId());
    }

}
