package fi.vm.sade.eperusteet.ylops.service.dokumentti;

import fi.vm.sade.eperusteet.ylops.domain.dokumentti.DokumenttiTila;
import fi.vm.sade.eperusteet.ylops.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.ylops.dto.dokumentti.DokumenttiDto;
import fi.vm.sade.eperusteet.ylops.service.exception.DokumenttiException;
import org.springframework.security.access.prepost.PreAuthorize;

public interface DokumenttiService {

    @PreAuthorize("isAuthenticated()")
    DokumenttiDto createDtoFor(Long id, Kieli kieli);

    @PreAuthorize("isAuthenticated()")
    void autogenerate(Long id, Kieli kieli) throws DokumenttiException;

    @PreAuthorize("isAuthenticated()")
    void setStarted(DokumenttiDto dto);

    @PreAuthorize("isAuthenticated()")
    void generateWithDto(DokumenttiDto dto) throws DokumenttiException;

    @PreAuthorize("permitAll()")
    DokumenttiDto getDto(Long id);

    @PreAuthorize("permitAll()")
    byte[] get(Long id);

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

    @PreAuthorize("isAuthenticated()")
    void updateDokumenttiTila(DokumenttiTila tila, Long dokumenttiId);

    @PreAuthorize("isAuthenticated()")
    void updateDokumenttiPdfData(byte[] pdfData, Long dokumenttiId);
}
