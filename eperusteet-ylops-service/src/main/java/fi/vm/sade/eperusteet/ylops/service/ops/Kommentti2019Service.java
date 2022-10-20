package fi.vm.sade.eperusteet.ylops.service.ops;

import fi.vm.sade.eperusteet.ylops.dto.ops.Kommentti2019Dto;
import fi.vm.sade.eperusteet.ylops.dto.ops.KommenttiKahvaDto;
import org.springframework.security.core.parameters.P;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.UUID;

public interface Kommentti2019Service {

    @PreAuthorize("isAuthenticated()")
    List<Kommentti2019Dto> get(@P("uuid") final UUID uuid);

    @PreAuthorize("isAuthenticated()")
    void asetaNimi(Kommentti2019Dto kommentti);

    @PreAuthorize("isAuthenticated()")
    Kommentti2019Dto add(UUID uuid, final Kommentti2019Dto kommenttiDto);

    @PreAuthorize("isAuthenticated()")
    KommenttiKahvaDto addKommenttiKahva(final KommenttiKahvaDto kahvaDto);

    @PreAuthorize("isAuthenticated()")
    Kommentti2019Dto update(Kommentti2019Dto kommenttiUpdateDto);

    @PreAuthorize("isAuthenticated()")
    void remove(@P("uuid") final UUID uuid);

}
