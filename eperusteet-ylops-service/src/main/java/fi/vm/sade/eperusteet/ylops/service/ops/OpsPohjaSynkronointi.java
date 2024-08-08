package fi.vm.sade.eperusteet.ylops.service.ops;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;

public interface OpsPohjaSynkronointi extends OpsToteutus {
    @Override
    default Class getImpl() {
        return OpsPohjaSynkronointi.class;
    }

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS') or hasPermission(#pohjaId, 'opetussuunnitelma', 'MUOKKAUS')")
    void syncTekstitPohjasta(@P("opsId") Long opsId, @P("pohjaId") Long pohjaId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    boolean opetussuunnitelmanPohjallaUusiaTeksteja(@P("opsId") Long opsId);
}
