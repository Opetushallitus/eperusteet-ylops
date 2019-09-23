package fi.vm.sade.eperusteet.ylops.service.ops;

import fi.vm.sade.eperusteet.ylops.dto.ops.Kommentti2019Dto;
import fi.vm.sade.eperusteet.ylops.dto.ops.Kommentti2019LuontiDto;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.UUID;

public interface Kommentti2019Service {

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'KOMMENTOINTI')")
    Kommentti2019Dto get(@P("opsId") final Long opsId, @P("uuid") final UUID uuid);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'KOMMENTOINTI')")
    Kommentti2019Dto add(@P("opsId") final Long opsId, final Kommentti2019LuontiDto kommenttiDto);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'KOMMENTOINTI')")
    Kommentti2019Dto update(@P("opsId") final Long opsId, final UUID uuid, Kommentti2019Dto kommenttiUpdateDto);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'KOMMENTOINTI')")
    void remove(@P("opsId") final Long opsId, @P("uuid") final UUID uuid);

}
