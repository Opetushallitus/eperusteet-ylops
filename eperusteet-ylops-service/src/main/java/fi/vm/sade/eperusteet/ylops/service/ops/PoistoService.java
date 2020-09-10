package fi.vm.sade.eperusteet.ylops.service.ops;

import fi.vm.sade.eperusteet.ylops.domain.Poistettava;
import fi.vm.sade.eperusteet.ylops.domain.lops2019.PoistetunTyyppi;
import fi.vm.sade.eperusteet.ylops.domain.ops.Opetussuunnitelma;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.Lops2019PoistettuDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OppiainePalautettuDto;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;

public interface PoistoService {

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    void restore(Long opsId, Long poistettuId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    OppiainePalautettuDto restoreOppiaine(Long opsId, Long oppiaineId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    List<Lops2019PoistettuDto> getRemoved(Long opsId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    List<Lops2019PoistettuDto> getRemoved(Long opsId, PoistetunTyyppi tyyppi);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    Lops2019PoistettuDto getRemoved(Long opsId, Long poistettuId, PoistetunTyyppi tyyppi);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    Lops2019PoistettuDto remove(Opetussuunnitelma ops, Poistettava poistettava);

}
