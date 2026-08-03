package fi.vm.sade.eperusteet.ylops.service.util;

import fi.vm.sade.eperusteet.ylops.dto.navigation.NavigationNodeDto;
import fi.vm.sade.eperusteet.ylops.dto.navigation.NavigationType;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.Comparator;

@UtilityClass
public class NavigationUtil {

    public static final String POST_SEPARATOR = "post_separator";
    private static final Set<NavigationType> NUMEROITAVAT_TYYPIT = Set.of(NavigationType.viite);
    private static final Set<NavigationType> UUSI_TYYPIT = Set.of(
      NavigationType.uusi_tekstikappale, 
      NavigationType.uusi_opintojakso, 
      NavigationType.uusi_oppimaara,
      NavigationType.uusi_paikallinen_oppiaine,
      NavigationType.uusi_taiteenala
    );

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
                .collect(Collectors.toList()));
        return navigationNodeDto;
    }

    public static NavigationNodeDto siirraLiitteetLoppuun(NavigationNodeDto navigationNodeDto) {
        Stack<NavigationNodeDto> stack = new Stack<>();
        stack.push(navigationNodeDto);

        List<NavigationNodeDto> liitteet = new ArrayList<>();

        while (!stack.empty()) {
            NavigationNodeDto head = stack.pop();

            liitteet.addAll(head.getChildren().stream()
                    .filter(child -> Objects.equals(child.getType(), NavigationType.liite))
                    .collect(Collectors.toList()));

            head.setChildren(head.getChildren().stream()
                    .filter(child -> !Objects.equals(child.getType(), NavigationType.liite))
                    .collect(Collectors.toList()));

            stack.addAll(head.getChildren());
        }

        navigationNodeDto.getChildren().addAll(liitteet);

        return navigationNodeDto;
    }

    public static NavigationNodeDto siirraLisayksetLoppuun(NavigationNodeDto navigationNodeDto) {
        navigationNodeDto.setChildren(navigationNodeDto.getChildren().stream()
                .sorted(Comparator.comparing(naviDto -> naviDto.getType().equals(NavigationType.uusi_tekstikappale)))
                .map(naviDto -> siirraLisayksetLoppuun(naviDto))
                .collect(Collectors.toList()));
        return navigationNodeDto;
    }
}
