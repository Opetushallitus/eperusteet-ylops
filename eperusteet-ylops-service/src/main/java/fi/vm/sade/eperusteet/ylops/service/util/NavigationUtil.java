package fi.vm.sade.eperusteet.ylops.service.util;

import fi.vm.sade.eperusteet.ylops.dto.navigation.NavigationNodeDto;
import fi.vm.sade.eperusteet.ylops.dto.navigation.NavigationType;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaKevytDto;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@UtilityClass
public class NavigationUtil {

    private static final Set<NavigationType> NUMEROITAVAT_TYYPIT = Set.of(NavigationType.viite);

    public static NavigationNodeDto asetaNumerointi(OpetussuunnitelmaKevytDto opetussuunnitelma, NavigationNodeDto node) {
        asetaNumerointi(node.getChildren(), "");
        return node;
    }

    public static void asetaNumerointi(List<NavigationNodeDto> nodes, String taso) {
        AtomicInteger nro = new AtomicInteger(0);
        nodes.stream()
                .filter(node -> NUMEROITAVAT_TYYPIT.contains(node.getType()))
                .forEach(node -> {
                    node.meta("numerointi", taso + nro.incrementAndGet());
                    asetaNumerointi(node.getChildren(), taso + nro.get() + ".");
                });
    }
}
