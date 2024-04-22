package fi.vm.sade.eperusteet.ylops.service.teksti;

import fi.vm.sade.eperusteet.ylops.domain.MuokkausTapahtuma;
import fi.vm.sade.eperusteet.ylops.domain.teksti.TekstiKappaleViite;
import fi.vm.sade.eperusteet.ylops.dto.teksti.TekstiKappaleDto;
import org.springframework.security.core.parameters.P;
import org.springframework.security.access.prepost.PreAuthorize;

public interface TekstiKappaleService {

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    TekstiKappaleDto get(@P("opsId") Long opsId, Long id);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    TekstiKappaleDto add(@P("opsId") Long opsId, TekstiKappaleViite viite, TekstiKappaleDto tekstiKappaleDto);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    TekstiKappaleDto update(@P("opsId") Long opsId, TekstiKappaleDto tekstiKappaleDto, MuokkausTapahtuma tapahtuma);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    TekstiKappaleDto update(@P("opsId") Long opsId, TekstiKappaleDto tekstiKappaleDto, boolean requiredLock, MuokkausTapahtuma tapahtuma);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    TekstiKappaleDto mergeNew(@P("opsId") Long opsId, TekstiKappaleViite viite, TekstiKappaleDto tekstiKappaleDto);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    void delete(@P("opsId") Long opsId, Long id);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    void removeTekstiKappaleFromOps(@P("opsId") Long opsId, Long id);
}
