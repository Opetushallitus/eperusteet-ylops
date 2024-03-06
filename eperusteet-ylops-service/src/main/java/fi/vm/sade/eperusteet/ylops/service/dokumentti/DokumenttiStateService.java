package fi.vm.sade.eperusteet.ylops.service.dokumentti;

import fi.vm.sade.eperusteet.ylops.domain.dokumentti.Dokumentti;
import fi.vm.sade.eperusteet.ylops.dto.dokumentti.DokumenttiDto;
import org.springframework.security.access.prepost.PreAuthorize;

public interface DokumenttiStateService {

    @PreAuthorize("isAuthenticated()")
    Dokumentti save(DokumenttiDto dto);

}
