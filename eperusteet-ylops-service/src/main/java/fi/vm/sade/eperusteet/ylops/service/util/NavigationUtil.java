package fi.vm.sade.eperusteet.ylops.service.util;

import fi.vm.sade.eperusteet.ylops.dto.navigation.NavigationNodeDto;
import fi.vm.sade.eperusteet.ylops.dto.navigation.NavigationType;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@UtilityClass
public class NavigationUtil {

    public static final String POST_SEPARATOR = "post_separator";
    private static final Set<NavigationType> NUMEROITAVAT_TYYPIT = Set.of(NavigationType.viite);

    public static NavigationNodeDto initPublic() {
        return NavigationNodeDto.of(NavigationType.root)
                .add(NavigationNodeDto.of(NavigationType.tiedot)
                        .meta(POST_SEPARATOR, true));
    }

    public static NavigationNodeDto asetaNumerointi(NavigationNodeDto node) {
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
