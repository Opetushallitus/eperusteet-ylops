package fi.vm.sade.eperusteet.ylops.service.ops;

import fi.vm.sade.eperusteet.ylops.dto.navigation.NavigationNodeDto;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;

public interface NavigationBuilderPublic extends NavigationBuilder {
    @Override
    default Class getImpl() {
        return NavigationBuilderPublic.class;
    }

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    default NavigationNodeDto buildNavigation(@P("opsId") Long opsId, String kieli) {
        return buildNavigation(opsId, kieli, false);
    }

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    default NavigationNodeDto buildNavigation(@P("opsId") Long opsId) {
        return buildNavigation(opsId, false);
    }

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    default NavigationNodeDto buildNavigation(@P("opsId") Long opsId, String kieli, boolean esikatselu) {
        return buildNavigation(opsId, esikatselu);
    }

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    NavigationNodeDto buildNavigation(@P("opsId") Long opsId, boolean esikatselu);
}
