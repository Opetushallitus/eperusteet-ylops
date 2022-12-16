package fi.vm.sade.eperusteet.ylops.service.ops;

import org.springframework.security.core.parameters.P;
import org.springframework.security.access.prepost.PreAuthorize;

public interface OpsPohjaSynkronointi extends OpsToteutus {
    @Override
    default Class getImpl() {
        return OpsPohjaSynkronointi.class;
    }

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    void syncTekstitPohjasta(@P("opsId") Long opsId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    boolean opetussuunnitelmanPohjallaUusiaTeksteja(@P("opsId") Long opsId);
}
