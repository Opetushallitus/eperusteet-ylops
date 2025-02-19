package fi.vm.sade.eperusteet.ylops.service.ops;

import com.fasterxml.jackson.databind.ObjectMapper;
import fi.vm.sade.eperusteet.ylops.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.ylops.domain.KoulutustyyppiToteutus;
import fi.vm.sade.eperusteet.ylops.domain.Tila;
import fi.vm.sade.eperusteet.ylops.domain.Tyyppi;
import fi.vm.sade.eperusteet.ylops.dto.OpetussuunnitelmaExportDto;
import fi.vm.sade.eperusteet.ylops.dto.Reference;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.export.OpetussuunnitelmaExportLops2019Dto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaLaajaDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaLuontiDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import fi.vm.sade.eperusteet.ylops.repository.ops.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.ylops.resource.config.InitJacksonConverter;
import fi.vm.sade.eperusteet.ylops.test.AbstractIntegrationTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.annotation.DirtiesContext;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class OpetussuunnitelmaExportIT extends AbstractIntegrationTest {

    @Autowired
    private OpetussuunnitelmaService opetussuunnitelmaService;

    @Autowired
    private OpetussuunnitelmaRepository opetussuunnitelmaRepository;

    @Autowired
    private OpsDispatcher dispatcher;

    private final ObjectMapper objectMapper = InitJacksonConverter.createMapper();

    @Test
    public void testPerusopetusExport() throws IOException {
        OpetussuunnitelmaDto persops = createOpetussuunnitelma((ops) -> {
            ops.setKoulutustyyppi(KoulutusTyyppi.PERUSOPETUS);
            ops.setPohja(Reference.of(createPohja(KoulutustyyppiToteutus.PERUSOPETUS, "perusopetus-diaarinumero").getId()));
            ops.setNimi(LokalisoituTekstiDto.of("persopetus"));
        });

        Resource resource = new ClassPathResource("ops/perusopetus.json");
        OpetussuunnitelmaExportDto exportDto = objectMapper.readValue(resource.getFile(), dispatcher.get(persops.getId(), OpsExport.class).getExportClass());
        assertThat(exportDto).isNotNull();
        assertThat(exportDto instanceof OpetussuunnitelmaLaajaDto).isTrue();
        OpetussuunnitelmaLaajaDto laajaDto = (OpetussuunnitelmaLaajaDto) exportDto;
        assertThat(((OpetussuunnitelmaLaajaDto) exportDto).getOppiaineet()).isNotEmpty();
        assertThat(((OpetussuunnitelmaLaajaDto) exportDto).getVuosiluokkakokonaisuudet()).isNotEmpty();
    }

    @Test
    public void testLukio2019Export() throws IOException {
        OpetussuunnitelmaDto lukioOps = createOpetussuunnitelma((ops) -> {
            ops.setKoulutustyyppi(KoulutusTyyppi.LUKIOKOULUTUS);
            ops.setPohja(Reference.of(createPohja(KoulutustyyppiToteutus.LOPS2019, "1/2/3").getId()));
            ops.setNimi(LokalisoituTekstiDto.of("lukioopetus"));
        });

        Resource resource = new ClassPathResource("ops/lukio2019.json");
        OpetussuunnitelmaExportDto exportDto = objectMapper.readValue(resource.getFile(), dispatcher.get(lukioOps.getId(), OpsExport.class).getExportClass());
        assertThat(exportDto).isNotNull();
        assertThat(exportDto instanceof OpetussuunnitelmaExportLops2019Dto).isTrue();
        assertThat(((OpetussuunnitelmaExportLops2019Dto) exportDto).getLaajaAlaisetOsaamiset()).isNotEmpty();
        assertThat(((OpetussuunnitelmaExportLops2019Dto) exportDto).getOpintojaksot()).isNotEmpty();
        assertThat(((OpetussuunnitelmaExportLops2019Dto) exportDto).getValtakunnallisetOppiaineet()).isNotEmpty();
        assertThat(((OpetussuunnitelmaExportLops2019Dto) exportDto).getPaikallisetOppiaineet()).isNotEmpty();
    }

    private OpetussuunnitelmaDto createPohja(KoulutustyyppiToteutus toteutus, String diaarinumero) {
        OpetussuunnitelmaLuontiDto pohja = new OpetussuunnitelmaLuontiDto();
        pohja.setToteutus(toteutus);
        pohja.setTyyppi(Tyyppi.POHJA);
        pohja.setPerusteenDiaarinumero(diaarinumero);
        OpetussuunnitelmaDto perusopetusPohjaDto = opetussuunnitelmaService.addPohja(pohja);
        return opetussuunnitelmaService.updateTila(perusopetusPohjaDto.getId(), Tila.VALMIS);
    }

}
