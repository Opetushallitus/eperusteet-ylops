package fi.vm.sade.eperusteet.ylops.service.ohje;

import fi.vm.sade.eperusteet.ylops.dto.ohje.OhjeDto;

import java.util.List;
import java.util.UUID;

import org.springframework.security.core.parameters.P;
import org.springframework.security.access.prepost.PreAuthorize;

public interface OhjeService {
    @PreAuthorize("isAuthenticated()")
    OhjeDto getOhje(@P("id") Long id);

    @PreAuthorize("isAuthenticated()")
    List<OhjeDto> getTekstiKappaleOhjeet(@P("uuid") UUID uuid);

    @PreAuthorize("isAuthenticated()")
    OhjeDto addOhje(OhjeDto ohjeDto);

    @PreAuthorize("isAuthenticated()")
    OhjeDto updateOhje(OhjeDto ohjeDto);

    @PreAuthorize("isAuthenticated()")
    void removeOhje(@P("id") Long id);
}
