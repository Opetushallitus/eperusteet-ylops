package fi.vm.sade.eperusteet.ylops.test.docker;

import fi.vm.sade.eperusteet.ylops.dto.OpetussuunnitelmaExportDto;
import fi.vm.sade.eperusteet.ylops.dto.dokumentti.DokumenttiDto;
import fi.vm.sade.eperusteet.ylops.service.dokumentti.ExternalPdfService;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Primary
@Profile("docker")
public class ExternalPdfServiceDockerMock implements ExternalPdfService {

    @Override
    public void generatePdf(DokumenttiDto dto) {
    }

    @Override
    public void generatePdf(DokumenttiDto dto, OpetussuunnitelmaExportDto opsDto) {
    }
}
