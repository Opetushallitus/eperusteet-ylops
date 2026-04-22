package fi.vm.sade.eperusteet.ylops.service.util;

import fi.vm.sade.eperusteet.ylops.dto.navigation.NavigationNodeDto;
import fi.vm.sade.eperusteet.ylops.dto.navigation.NavigationType;
import lombok.experimental.UtilityClass;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@UtilityClass
public class NavigationUtil {

    public static final String POST_SEPARATOR = "post_separator";
    private static final Set<NavigationType> NUMEROITAVAT_TYYPIT = Set.of(NavigationType.viite);
    private static final Set<NavigationType> UUSI_TYYPIT = Set.of(NavigationType.uusi_tekstikappale, NavigationType.uusi_opintojakso, NavigationType.uusi_oppimaara);

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

    public static NavigationNodeDto tarkistaOikeudet(NavigationNodeDto navigationNodeDto, boolean hasModifyPermission) {
        navigationNodeDto.setChildren(navigationNodeDto.getChildren().stream()
                .filter(naviDto -> hasModifyPermission || !UUSI_TYYPIT.contains(naviDto.getType()))
                .map(naviDto -> tarkistaOikeudet(naviDto, hasModifyPermission))
                .sorted(Comparator.comparing(naviDto -> naviDto.getType().equals(NavigationType.uusi_tekstikappale)))
                .collect(Collectors.toList()));
        return navigationNodeDto;
    }
}
