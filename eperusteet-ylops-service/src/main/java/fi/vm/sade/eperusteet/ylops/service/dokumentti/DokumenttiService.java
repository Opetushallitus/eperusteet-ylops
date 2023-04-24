package fi.vm.sade.eperusteet.ylops.service.dokumentti;

import fi.vm.sade.eperusteet.ylops.domain.dokumentti.DokumenttiTila;
import fi.vm.sade.eperusteet.ylops.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.ylops.dto.dokumentti.DokumenttiDto;
import fi.vm.sade.eperusteet.ylops.service.exception.DokumenttiException;
import org.springframework.security.access.prepost.PreAuthorize;

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

    @PreAuthorize("permitAll()")
    byte[] get(Long id);

    @PreAuthorize("permitAll()")
    boolean hasPermission(Long id);

    @PreAuthorize("permitAll()")
    Long getDokumenttiId(Long opsId, Kieli kieli);

    @PreAuthorize("isAuthenticated()")
    DokumenttiDto query(Long id);

    @PreAuthorize("permitAll()")
    DokumenttiTila getTila(Long opsId, Kieli kieli);
}
