package fi.vm.sade.eperusteet.ylops.service.ops;

import fi.vm.sade.eperusteet.ylops.dto.navigation.NavigationNodeDto;
import org.springframework.security.core.parameters.P;
import org.springframework.security.access.prepost.PreAuthorize;

public interface OpsCopier extends OpsToteutus {
    @Override
    default Class getImpl() {
        return OpsCopier.class;
    }

    @PreAuthorize("isAuthenticated()")
    NavigationNodeDto copy(@P("opsId") Long opsId);
}
