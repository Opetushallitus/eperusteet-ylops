package fi.vm.sade.eperusteet.ylops.service.ops;

import fi.vm.sade.eperusteet.ylops.domain.ops.Opetussuunnitelma;
import org.springframework.security.access.prepost.PreAuthorize;

public interface OpetussuunnitelmaHierarkiaKopiointiService {

    @PreAuthorize("hasPermission(#ops.id, 'opetussuunnitelma', 'MUOKKAUS')")
    void kopioiPohjanRakenne(Opetussuunnitelma ops, Opetussuunnitelma pohja);
}
