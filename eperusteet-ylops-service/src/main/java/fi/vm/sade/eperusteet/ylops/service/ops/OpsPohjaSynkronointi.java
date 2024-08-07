package fi.vm.sade.eperusteet.ylops.service.ops;

import fi.vm.sade.eperusteet.ylops.domain.ops.Opetussuunnitelma;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;

public interface OpsPohjaSynkronointi extends OpsToteutus {
    @Override
    default Class getImpl() {
        return OpsPohjaSynkronointi.class;
    }

    @PreAuthorize("hasPermission(#opetussuunnitelma.getId(), 'opetussuunnitelma', 'MUOKKAUS') or (#opetussuunnitelma.getPohja() != null and hasPermission(#opetussuunnitelma.getPohja().getId(), 'opetussuunnitelma', 'MUOKKAUS'))")
    void syncTekstitPohjasta(@P("opetussuunnitelma") Opetussuunnitelma opetussuunnitelma);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    boolean opetussuunnitelmanPohjallaUusiaTeksteja(@P("opsId") Long opsId);
}
