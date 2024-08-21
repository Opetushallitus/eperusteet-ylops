package fi.vm.sade.eperusteet.ylops.service.ops;

import fi.vm.sade.eperusteet.ylops.dto.navigation.NavigationNodeDto;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;

public interface NavigationBuilderPublic extends NavigationBuilder {
    @Override
    default Class getImpl() {
        return NavigationBuilderPublic.class;
    }

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    default NavigationNodeDto buildNavigation(@P("opsId") Long opsId, String kieli) {
        return buildNavigation(opsId, kieli, null);
    }

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    default NavigationNodeDto buildNavigation(@P("opsId") Long opsId) {
        return buildNavigation(opsId, null, null);
    }

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    default NavigationNodeDto buildNavigation(@P("opsId") Long opsId, String kieli, Integer revision) {
        return buildNavigation(opsId, revision);
    }

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    NavigationNodeDto buildNavigation(@P("opsId") Long opsId, Integer revision);
}
