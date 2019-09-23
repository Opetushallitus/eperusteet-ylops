package fi.vm.sade.eperusteet.ylops.service.ops;

import fi.vm.sade.eperusteet.ylops.dto.lops2019.Validointi.Lops2019ValidointiDto;
import org.springframework.security.access.prepost.PreAuthorize;

public interface ValidointiService {
    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    Lops2019ValidointiDto getValidointi(Long opsId);
}
