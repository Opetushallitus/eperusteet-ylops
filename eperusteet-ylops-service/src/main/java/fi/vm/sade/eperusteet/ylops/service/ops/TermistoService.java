package fi.vm.sade.eperusteet.ylops.service.ops;

import fi.vm.sade.eperusteet.ylops.dto.ops.TermiDto;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;

import java.util.List;

public interface TermistoService {

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    List<TermiDto> getTermit(@P("opsId") Long opsId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU') or isAuthenticated()")
    TermiDto getTermi(@P("opsId") Long opsId, String avain);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS') or hasPermission(#opsId, 'opetussuunnitelma', 'KORJAUS')")
    TermiDto addTermi(@P("opsId") Long opsId, TermiDto dto);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS') or hasPermission(#opsId, 'opetussuunnitelma', 'KORJAUS')")
    TermiDto updateTermi(@P("opsId") Long opsId, TermiDto dto);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS') or hasPermission(#opsId, 'opetussuunnitelma', 'KORJAUS')")
    void deleteTermi(@P("opsId") Long opsId, Long id);
}
