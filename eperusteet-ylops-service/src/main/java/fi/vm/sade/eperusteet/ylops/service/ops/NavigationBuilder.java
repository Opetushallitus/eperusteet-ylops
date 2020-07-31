package fi.vm.sade.eperusteet.ylops.service.ops;

import fi.vm.sade.eperusteet.ylops.dto.navigation.NavigationNodeDto;
import fi.vm.sade.eperusteet.ylops.service.exception.BusinessRuleViolationException;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;

public interface NavigationBuilder extends OpsToteutus {
    @Override
    default Class getImpl() {
        return NavigationBuilder.class;
    }

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    default NavigationNodeDto buildNavigation(@P("opsId") Long opsId, String kieli) {
        return buildNavigation(opsId);
    }

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    NavigationNodeDto buildNavigation(@P("opsId") Long opsId);
}
