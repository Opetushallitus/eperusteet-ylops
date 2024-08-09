package fi.vm.sade.eperusteet.ylops.service.ops;

import fi.vm.sade.eperusteet.ylops.domain.ops.Opetussuunnitelma;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;

public interface OpetussuunnitelmaHierarkiaKopiointiService {

    @PreAuthorize("hasPermission(#opetussuunnitelma.getId(), 'opetussuunnitelma', 'MUOKKAUS') or (#opetussuunnitelma.getPohja() != null and hasPermission(#opetussuunnitelma.getPohja().getId(), 'opetussuunnitelma', 'MUOKKAUS'))")
    void kopioiPohjanRakenne(@P("opetussuunnitelma") Opetussuunnitelma opetussuunnitelma, Opetussuunnitelma pohja);
}
