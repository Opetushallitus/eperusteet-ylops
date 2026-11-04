package fi.vm.sade.eperusteet.ylops.service.ops.impl.navigation;

import com.google.common.collect.Sets;
import fi.vm.sade.eperusteet.ylops.domain.KoulutustyyppiToteutus;
import fi.vm.sade.eperusteet.ylops.dto.navigation.NavigationNodeDto;
import fi.vm.sade.eperusteet.ylops.dto.navigation.NavigationType;
import fi.vm.sade.eperusteet.ylops.dto.peruste.tpo.TpoPerusteenTaiteenalaDto;
import fi.vm.sade.eperusteet.ylops.dto.tpo.TaiteenalaKevytDto;
import fi.vm.sade.eperusteet.ylops.dto.tpo.TaiteenalanVaiheKevytDto;
import fi.vm.sade.eperusteet.ylops.service.ops.NavigationBuilder;
import fi.vm.sade.eperusteet.ylops.service.ops.OpsDispatcher;
import fi.vm.sade.eperusteet.ylops.service.tpo.TaiteenperusopetusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Transactional
public class NavigationBuilderTaiteenperusopetusImpl implements NavigationBuilder {

    @Autowired
    private OpsDispatcher dispatcher;

    @Autowired
    private TaiteenperusopetusService taiteenperusopetusService;

    @Override
    public Set<KoulutustyyppiToteutus> getTyypit() {
        return Sets.newHashSet(KoulutustyyppiToteutus.TPO);
    }

    @Override
    public NavigationNodeDto buildNavigation(Long opsId) {
        return NavigationNodeDto.of(NavigationType.root)
                .addAll(dispatcher.get(NavigationBuilder.class).buildNavigation(opsId).getChildren())
                .addAll(taiteenalat(opsId));
    }

    private List<NavigationNodeDto> taiteenalat(Long opsId) {
        List<TaiteenalaKevytDto> taiteenalat = taiteenperusopetusService.getTaiteenalat(opsId);
        List<NavigationNodeDto> nodes = taiteenalat.stream()
                .map(taiteenala -> mapTaiteenala(taiteenala))
                .collect(Collectors.toList());

        Set<Long> lisatyt = taiteenalat.stream().map(TaiteenalaKevytDto::getPerusteenTaiteenalaId).collect(Collectors.toSet());
        boolean lisattavia = taiteenperusopetusService.getPerusteTaiteenalat(opsId).stream()
                .map(TpoPerusteenTaiteenalaDto::getId)
                .anyMatch(id -> !lisatyt.contains(id));
        if (lisattavia) {
            nodes.add(NavigationNodeDto.of(NavigationType.uusi_taiteenala)
                    .meta("navigation-sub-type", "add"));
        }
        return nodes;
    }

    private NavigationNodeDto mapTaiteenala(TaiteenalaKevytDto taiteenala) {
        NavigationNodeDto node = NavigationNodeDto.of(NavigationType.taiteenala, taiteenala.getNimi(), taiteenala.getId());
        taiteenala.getVaiheet().forEach(vaihe -> node.add(mapVaihe(vaihe, taiteenala.getId())));
        return node;
    }

    private NavigationNodeDto mapVaihe(TaiteenalanVaiheKevytDto vaihe, Long taiteenalaId) {
        NavigationNodeDto node = NavigationNodeDto.of(NavigationType.taiteenalanvaihe, vaihe.getNimi(), vaihe.getId())
                .meta("taiteenalaId", taiteenalaId);
        vaihe.getOpintokokonaisuudet().forEach(ok -> node.add(
                NavigationNodeDto.of(NavigationType.taiteenopintokokonaisuus, ok.getNimi(), ok.getId())
                        .meta("taiteenalaId", taiteenalaId)
                        .meta("vaiheId", vaihe.getId())));
        node.add(NavigationNodeDto.of(NavigationType.uusi_opintokokonaisuus)
                .meta("navigation-sub-type", "add")
                .meta("taiteenalaId", taiteenalaId)
                .meta("vaiheId", vaihe.getId()));
        return node;
    }
}
