package fi.vm.sade.eperusteet.ylops.service.util;

import fi.vm.sade.eperusteet.ylops.domain.KoulutusTyyppi;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Set;

public interface MaintenanceService {

    @PreAuthorize("hasPermission(null, 'pohja', 'LUONTI')")
    void teeJulkaisut(boolean julkaiseKaikki, Set<KoulutusTyyppi> koulutustyypit);

}
