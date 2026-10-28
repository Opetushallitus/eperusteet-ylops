package fi.vm.sade.eperusteet.ylops.resource;

import com.google.common.collect.Sets;
import fi.vm.sade.eperusteet.ylops.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.ylops.domain.Tila;
import fi.vm.sade.eperusteet.ylops.domain.Tyyppi;
import fi.vm.sade.eperusteet.ylops.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.ylops.dto.OpetussuunnitelmaExportDto;
import fi.vm.sade.eperusteet.ylops.dto.Reference;
import fi.vm.sade.eperusteet.ylops.dto.koodisto.KoodistoDto;
import fi.vm.sade.eperusteet.ylops.dto.koodisto.OrganisaatioDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaLuontiDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmanJulkaisuDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.UusiJulkaisuDto;
import fi.vm.sade.eperusteet.ylops.repository.ops.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.ylops.resource.julkinen.ExternalController;
import fi.vm.sade.eperusteet.ylops.service.mocks.EperusteetServiceMock;
import fi.vm.sade.eperusteet.ylops.service.ops.OpetussuunnitelmaService;
import fi.vm.sade.eperusteet.ylops.service.util.JulkaisuService;
import fi.vm.sade.eperusteet.ylops.test.docker.AbstractDockerIntegrationTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;

import static fi.vm.sade.eperusteet.ylops.test.util.TestUtils.lt;
import static fi.vm.sade.eperusteet.ylops.test.util.TestUtils.uniikkiString;
import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext
@ActiveProfiles(profiles = {"docker"})
@Transactional
@SpringBootTest
@ContextConfiguration(classes = ExternalController.class)
public class ExternalControllerIT extends AbstractDockerIntegrationTest {

    @Autowired
    private ExternalController externalController;

    @Autowired
    private OpetussuunnitelmaService opetussuunnitelmaService;

    @Autowired
    private JulkaisuService julkaisuService;

    @Autowired
    private OpetussuunnitelmaRepository opetussuunnitelmaRepository;

    private OpetussuunnitelmaDto pohja;

    @Before
    public void setUp() {
        pohja = luoPohja();
    }

    @After
    public void clean() {
        arkistoiOpetussuunnitelmat();
    }

    @Test
    public void testOpetussuunnitelmaViimeisinRevisio() {
        OpetussuunnitelmaDto ops = luoJaJulkaise("ops-viimeisin", 2);

        OpetussuunnitelmaExportDto julkaistu = externalController.getExternalOpetussuunnitelma(ops.getId()).getBody();
        assertThat(julkaistu).isNotNull();
        assertThat(julkaistu.getId()).isEqualTo(ops.getId());
        assertThat(julkaistu.getJulkaisuRevisio()).isEqualTo(viimeisinRevisio(ops.getId()));
    }

    @Test
    public void testOpetussuunnitelmaRevisioLoytyy() {
        OpetussuunnitelmaDto ops = luoJaJulkaise("ops-revisio", 2);

        julkaisuService.getJulkaisut(ops.getId()).forEach(julkaisu -> {
            ResponseEntity<OpetussuunnitelmaExportDto> response = externalController.getExternalOpetussuunnitelmaJulkaisuRevisio(ops.getId(), julkaisu.getRevision());
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getId()).isEqualTo(ops.getId());
            assertThat(response.getBody().getJulkaisuRevisio()).isEqualTo(julkaisu.getRevision());
        });
    }

    @Test
    public void testOpetussuunnitelmaRevisioEiLoydy() {
        OpetussuunnitelmaDto ops = luoJaJulkaise("ops-ei-loydy", 1);

        startNewTransaction();
        opetussuunnitelmaRepository.findOne(ops.getId()).setEsikatseltavissa(true);
        endTransaction();

        ResponseEntity<OpetussuunnitelmaExportDto> response = externalController.getExternalOpetussuunnitelmaJulkaisuRevisio(ops.getId(), 999);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();

        ResponseEntity<OpetussuunnitelmaExportDto> esikatselu = externalController.getExternalOpetussuunnitelmaJulkaisuRevisio(ops.getId(), 0);
        assertThat(esikatselu.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(esikatselu.getBody()).isNull();
    }

    @Test
    public void testOpetussuunnitelmaRevisioReititys() throws Exception {
        OpetussuunnitelmaDto ops = luoJaJulkaise("ops-reititys", 1);
        int revisio = viimeisinRevisio(ops.getId());
        MockMvc mvc = MockMvcBuilders.standaloneSetup(externalController).build();

        mvc.perform(MockMvcRequestBuilders.get("/api/external/opetussuunnitelma/" + ops.getId() + "/v" + revisio))
                .andExpect(MockMvcResultMatchers.handler().methodName("getExternalOpetussuunnitelmaJulkaisuRevisio"));

        mvc.perform(MockMvcRequestBuilders.get("/api/external/opetussuunnitelma/" + ops.getId() + "/v999"))
                .andExpect(MockMvcResultMatchers.handler().methodName("getExternalOpetussuunnitelmaJulkaisuRevisio"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        mvc.perform(MockMvcRequestBuilders.get("/api/external/opetussuunnitelma/" + ops.getId() + "/id"))
                .andExpect(MockMvcResultMatchers.handler().methodName("getOpetussuunnitelmaDynamicQuery"));

        mvc.perform(MockMvcRequestBuilders.get("/api/external/opetussuunnitelma/" + ops.getId() + "/nimi/fi"))
                .andExpect(MockMvcResultMatchers.handler().methodName("getOpetussuunnitelmaDynamicQueryHidden"));
    }

    private OpetussuunnitelmaDto luoJaJulkaise(String nimi, int julkaisuja) {
        startNewTransaction();
        OpetussuunnitelmaDto ops = luoOpetussuunnitelma(pohja.getId(), nimi);
        endTransaction();

        for (int i = 0; i < julkaisuja; i++) {
            julkaisuService.addJulkaisu(ops.getId(), julkaisuDto());
        }
        return ops;
    }

    private int viimeisinRevisio(Long opsId) {
        return julkaisuService.getJulkaisut(opsId).stream()
                .mapToInt(OpetussuunnitelmanJulkaisuDto::getRevision)
                .max()
                .orElseThrow();
    }

    private OpetussuunnitelmaDto luoPohja() {
        startNewTransaction();

        OpetussuunnitelmaLuontiDto pohjaLuonti = new OpetussuunnitelmaLuontiDto();
        pohjaLuonti.setPerusteenDiaarinumero(EperusteetServiceMock.PERUSOPETUS_DIAARINUMERO);
        pohjaLuonti.setNimi(lt(uniikkiString()));
        pohjaLuonti.setKuvaus(lt(uniikkiString()));
        pohjaLuonti.setTyyppi(Tyyppi.POHJA);
        pohjaLuonti.setKoulutustyyppi(KoulutusTyyppi.PERUSOPETUS);
        OpetussuunnitelmaDto luotu = opetussuunnitelmaService.addPohja(pohjaLuonti);
        OpetussuunnitelmaDto valmis = opetussuunnitelmaService.updateTila(luotu.getId(), Tila.VALMIS);

        endTransaction();
        return valmis;
    }

    private OpetussuunnitelmaDto luoOpetussuunnitelma(Long pohjaId, String nimi) {
        OpetussuunnitelmaLuontiDto ops = new OpetussuunnitelmaLuontiDto();
        ops.setNimi(lt(nimi));
        ops.setKuvaus(lt(uniikkiString()));
        ops.setTila(Tila.LUONNOS);
        ops.setTyyppi(Tyyppi.OPS);
        ops.setKoulutustyyppi(KoulutusTyyppi.PERUSOPETUS);
        ops.setPohja(Reference.of(pohjaId));

        KoodistoDto kunta = new KoodistoDto();
        kunta.setKoodiUri("kunta_837");
        ops.setKunnat(new HashSet<>(Collections.singleton(kunta)));

        OrganisaatioDto kouluDto = new OrganisaatioDto();
        kouluDto.setNimi(lt("Etelä-Hervannan koulu"));
        kouluDto.setOid("1.2.246.562.10.00000000001");
        ops.setOrganisaatiot(new HashSet<>(Collections.singleton(kouluDto)));
        ops.setJulkaisukielet(Sets.newHashSet(Kieli.FI));
        ops.setPaatospaivamaara(new Date());
        ops.setHyvaksyjataho("hyvaksyja");
        return opetussuunnitelmaService.addOpetussuunnitelma(ops);
    }

    private UusiJulkaisuDto julkaisuDto() {
        return UusiJulkaisuDto.builder()
                .julkaisutiedote(lt("julkaisutiedote"))
                .build();
    }

    private void arkistoiOpetussuunnitelmat() {
        opetussuunnitelmaRepository.findAll().forEach(ops -> {
            if (ops.getTila() != Tila.POISTETTU) {
                opetussuunnitelmaService.updateTila(ops.getId(), Tila.POISTETTU);
            }
        });
    }
}
