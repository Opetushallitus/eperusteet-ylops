package fi.vm.sade.eperusteet.ylops.service.dokumentti;

import fi.vm.sade.eperusteet.ylops.domain.dokumentti.DokumenttiTila;
import fi.vm.sade.eperusteet.ylops.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.ylops.dto.dokumentti.DokumenttiDto;
import fi.vm.sade.eperusteet.ylops.service.exception.DokumenttiException;
import org.springframework.security.core.parameters.P;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface DokumenttiService {

    @PreAuthorize("isAuthenticated()")
    DokumenttiDto getDto(Long opsId, Kieli kieli);

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

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    byte[] getImage(@P("opsId") Long opsId, String tyyppi, Kieli kieli);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    DokumenttiDto addImage(@P("opsId") Long opsId, DokumenttiDto dto, String tyyppi, Kieli kieli, MultipartFile image) throws IOException;

    @PreAuthorize("permitAll()")
    byte[] get(Long id);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    void deleteImage(@P("opsId") Long opsId, String tyyppi, Kieli kieli);

    @PreAuthorize("permitAll()")
    boolean hasPermission(Long id);

    @PreAuthorize("permitAll()")
    Long getDokumenttiId(Long opsId, Kieli kieli);

    @PreAuthorize("isAuthenticated()")
    DokumenttiDto query(Long id);

    @PreAuthorize("permitAll()")
    DokumenttiTila getTila(Long opsId, Kieli kieli);

//    @PreAuthorize("isAuthenticated()")
    void updateDokumenttiTila(DokumenttiTila tila, Long dokumenttiId);

//    @PreAuthorize("isAuthenticated()")
    void updateDokumenttiPdfData(byte[] pdfData, Long dokumenttiId);
}
