package fi.vm.sade.eperusteet.ylops.service.dokumentti;

import fi.vm.sade.eperusteet.ylops.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.ylops.dto.dokumentti.LokalisointiDto;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Map;

public interface LokalisointiService {

    @PreAuthorize("permitAll()")
    LokalisointiDto get(String key, String locale);

    @PreAuthorize("isAuthenticated()")
    Map<Kieli, List<LokalisointiDto>> getAll();
}
