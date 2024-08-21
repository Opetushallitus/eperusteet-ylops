package fi.vm.sade.eperusteet.ylops.service.ops;

import fi.vm.sade.eperusteet.ylops.domain.teksti.TekstiKappaleViite;
import fi.vm.sade.eperusteet.ylops.dto.navigation.NavigationNodeDto;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;

import java.util.function.Predicate;

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

    default Predicate<TekstiKappaleViite> tekstikappaleFilter() {
        return tkv -> true;
    }

}
