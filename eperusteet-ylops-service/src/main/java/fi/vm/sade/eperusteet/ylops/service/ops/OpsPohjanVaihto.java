package fi.vm.sade.eperusteet.ylops.service.ops;

import fi.vm.sade.eperusteet.ylops.dto.navigation.NavigationNodeDto;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;

public interface OpsPohjanVaihto extends OpsToteutus {
    @Override
    default Class getImpl() {
        return OpsPohjanVaihto.class;
    }

    @PreAuthorize("isAuthenticated()")
    void vaihdaPohja(Long opsId, Long pohjaId);
}
