package fi.vm.sade.eperusteet.ylops.service.dokumentti;

import fi.vm.sade.eperusteet.ylops.domain.dokumentti.DokumenttiTila;
import fi.vm.sade.eperusteet.ylops.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.ylops.dto.OpetussuunnitelmaExportDto;
import fi.vm.sade.eperusteet.ylops.dto.dokumentti.DokumenttiDto;
import fi.vm.sade.eperusteet.ylops.dto.pdf.PdfData;
import fi.vm.sade.eperusteet.ylops.service.exception.DokumenttiException;
import org.springframework.security.access.prepost.PreAuthorize;

public interface DokumenttiService {

    @PreAuthorize("isAuthenticated()")
    DokumenttiDto createDtoFor(Long id, Kieli kieli);

    @PreAuthorize("isAuthenticated()")
    void setStarted(DokumenttiDto dto);

    @PreAuthorize("isAuthenticated()")
    void generateWithDto(DokumenttiDto dto) throws DokumenttiException;

    @PreAuthorize("isAuthenticated()")
    void generateWithDto(DokumenttiDto dto, OpetussuunnitelmaExportDto opsDto) throws DokumenttiException;

    @PreAuthorize("permitAll()")
    DokumenttiDto getDto(Long id);

    @PreAuthorize("permitAll()")
    byte[] getData(Long id);

    @PreAuthorize("permitAll()")
    byte[] getHtml(Long id);

    @PreAuthorize("permitAll()")
    Long getLatestValmisDokumenttiId(Long opsId, Kieli kieli);

    @PreAuthorize("permitAll()")
    DokumenttiDto getLatestDokumentti(Long opsId, Kieli kieli);

    @PreAuthorize("permitAll()")
    DokumenttiDto getJulkaistuDokumentti(Long opsId, Kieli kieli, Integer revision);

    @PreAuthorize("permitAll()")
    boolean hasPermission(Long id);

    @PreAuthorize("isAuthenticated()")
    DokumenttiDto query(Long id);

    @PreAuthorize("hasPermission(null, 'pohja', 'LUONTI')")
    void cleanStuckPrintings();

    void updateDokumenttiTila(DokumenttiTila tila, Long dokumenttiId);

    void updateDokumenttiPdfData(PdfData pdfData, Long dokumenttiId);
}
