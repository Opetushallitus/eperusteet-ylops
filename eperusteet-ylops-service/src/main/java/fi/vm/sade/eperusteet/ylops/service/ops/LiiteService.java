package fi.vm.sade.eperusteet.ylops.service.ops;

import fi.vm.sade.eperusteet.ylops.dto.liite.LiiteDto;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.UUID;

public interface LiiteService {

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    UUID add(@P("opsId") final Long opsId, String tyyppi, String nimi, long length, InputStream is);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU') or isAuthenticated()")
    LiiteDto get(@P("opsId") Long opsId, UUID id);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU') or isAuthenticated()")
    List<LiiteDto> getAll(@P("opsId") Long opsId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    void delete(@P("opsId") Long opsId, UUID id);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU') or isAuthenticated()")
    void export(@P("opsId") final Long opsId, UUID id, OutputStream os);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU') or isAuthenticated()")
    void exportLiitePerusteelta(@P("opsId") final Long opsId, UUID id, OutputStream os);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    InputStream export(@P("opsId") final Long opsId, UUID id, final Long perusteId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    InputStream exportLiitePerusteelta(@P("opsId") final Long opsId, UUID id, final Long perusteId);
}
