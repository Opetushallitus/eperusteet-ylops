package fi.vm.sade.eperusteet.ylops.test.docker;

import fi.vm.sade.eperusteet.ylops.domain.dokumentti.DokumenttiTila;
import fi.vm.sade.eperusteet.ylops.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.ylops.dto.OpetussuunnitelmaExportDto;
import fi.vm.sade.eperusteet.ylops.dto.dokumentti.DokumenttiDto;
import fi.vm.sade.eperusteet.ylops.service.dokumentti.DokumenttiService;
import fi.vm.sade.eperusteet.ylops.service.exception.DokumenttiException;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;

@Service
@Primary
@Profile("docker")
public class DokumenttiServiceDockerMock implements DokumenttiService {

    private final AtomicLong ids = new AtomicLong(1);

    @Override
    public DokumenttiDto createDtoFor(Long id, Kieli kieli) {
        DokumenttiDto dto = new DokumenttiDto();
        dto.setId(ids.getAndIncrement());
        dto.setOpsId(id);
        dto.setKieli(kieli);
        dto.setTila(DokumenttiTila.EI_OLE);
        return dto;
    }

    @Override
    public void setStarted(DokumenttiDto dto) {
    }

    @Override
    public void generateWithDto(DokumenttiDto dto) throws DokumenttiException {
    }

    @Override
    public void generateWithDto(DokumenttiDto dto, OpetussuunnitelmaExportDto opsDto) throws DokumenttiException {
    }

    @Override
    public DokumenttiDto getDto(Long id) {
        return null;
    }

    @Override
    public byte[] get(Long id) {
        return new byte[0];
    }

    @Override
    public Long getLatestValmisDokumenttiId(Long opsId, Kieli kieli) {
        return null;
    }

    @Override
    public DokumenttiDto getLatestDokumentti(Long opsId, Kieli kieli) {
        return null;
    }

    @Override
    public DokumenttiDto getJulkaistuDokumentti(Long opsId, Kieli kieli, Integer revision) {
        return null;
    }

    @Override
    public boolean hasPermission(Long id) {
        return true;
    }

    @Override
    public DokumenttiDto query(Long id) {
        return null;
    }

    @Override
    public void cleanStuckPrintings() {
    }

    @Override
    public void updateDokumenttiTila(DokumenttiTila tila, Long dokumenttiId) {
    }

    @Override
    public void updateDokumenttiPdfData(byte[] pdfData, Long dokumenttiId) {
    }
}
