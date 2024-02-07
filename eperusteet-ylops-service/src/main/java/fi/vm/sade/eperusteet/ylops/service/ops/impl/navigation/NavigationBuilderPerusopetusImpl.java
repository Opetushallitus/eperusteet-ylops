package fi.vm.sade.eperusteet.ylops.service.ops.impl.navigation;

import com.google.common.collect.Sets;
import fi.vm.sade.eperusteet.ylops.domain.KoulutustyyppiToteutus;
import fi.vm.sade.eperusteet.ylops.domain.oppiaine.OppiaineTyyppi;
import fi.vm.sade.eperusteet.ylops.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.ylops.dto.navigation.NavigationNodeDto;
import fi.vm.sade.eperusteet.ylops.dto.navigation.NavigationType;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaKevytDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OppiaineSuppeaDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OppiaineenVuosiluokkakokonaisuusSuppeaDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpsOppiaineKevytDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpsVuosiluokkakokonaisuusKevytDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.VuosiluokkakokonaisuusSuppeaDto;
import fi.vm.sade.eperusteet.ylops.service.ops.NavigationBuilder;
import fi.vm.sade.eperusteet.ylops.service.ops.OpetussuunnitelmaService;
import fi.vm.sade.eperusteet.ylops.service.ops.OpsDispatcher;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Transactional
public class NavigationBuilderPerusopetusImpl implements NavigationBuilder {

    @Autowired
    private OpsDispatcher dispatcher;

    @Autowired
    private OpetussuunnitelmaService opetussuunnitelmaService;

    @Override
    public Set<KoulutustyyppiToteutus> getTyypit() {
        return Sets.newHashSet(KoulutustyyppiToteutus.PERUSOPETUS);
    }

    @Override
    public NavigationNodeDto buildNavigation(Long opsId) {
        return buildNavigation(opsId, "fi");
    }

    @Override
    public NavigationNodeDto buildNavigation(Long opsId, String kieli) {

        OpetussuunnitelmaKevytDto ops = opetussuunnitelmaService.getOpetussuunnitelmaKevyt(opsId);

        List<VuosiluokkakokonaisuusSuppeaDto> vuosiluokkakokonaisuudet = ops.getVuosiluokkakokonaisuudet().stream()
                .sorted(Comparator.comparing(vlk -> vlk.getVuosiluokkakokonaisuus().getNimi().getOrDefault(Kieli.of(kieli))))
                .map(OpsVuosiluokkakokonaisuusKevytDto::getVuosiluokkakokonaisuus)
                .collect(Collectors.toList());

        List<OppiaineSuppeaDto> oppiaineet = ops.getOppiaineet().stream()
                .sorted(Comparator.comparing(o -> o.getOppiaine().getNimi().getOrDefault(Kieli.of(kieli))))
                .sorted(Comparator.comparing(o -> o.getJnro() != null ? o.getJnro() : Long.MAX_VALUE))
                .map(OpsOppiaineKevytDto::getOppiaine)
                .collect(Collectors.toList());

        return NavigationNodeDto.of(NavigationType.root)
                .addAll(dispatcher.get(getNavigationBuilderClass()).buildNavigation(opsId).getChildren())
                .addAll(vuosiluokkakokonaisuudet(vuosiluokkakokonaisuudet, oppiaineet, kieli))
                .add(perusopetusOppiaineet(oppiaineet, kieli));
    }

    protected Class<? extends NavigationBuilder> getNavigationBuilderClass() {
        return NavigationBuilder.class;
    }

    private List<NavigationNodeDto> vuosiluokkakokonaisuudet(List<VuosiluokkakokonaisuusSuppeaDto> vuosiluokkakokonaisuudet, List<OppiaineSuppeaDto> oppiaineet, String kieli) {
        return vuosiluokkakokonaisuudet.stream()
                .map(vlk ->
                        NavigationNodeDto.of(NavigationType.vuosiluokkakokonaisuus, vlk.getNimi(), vlk.getId())
                                .addAll(perusopetusOppiaine(oppiaineet.stream()
                                                .filter(oppiaine -> oppiaine.getTyyppi() == OppiaineTyyppi.YHTEINEN)
                                                .filter(oppiaine -> oppiaine.getVuosiluokkakokonaisuudet().stream()
                                                        .map(OppiaineenVuosiluokkakokonaisuusSuppeaDto::getVuosiluokkakokonaisuus)
                                                        .collect(Collectors.toList())
                                                        .contains(vlk.getTunniste()))
                                                .collect(Collectors.toList()),
                                        kieli,
                                        vlk.getId()))
                                .add(valinnaisetOppiaineet(oppiaineet.stream()
                                                .filter(oppiaine -> oppiaine.getVuosiluokkakokonaisuudet().stream()
                                                        .map(OppiaineenVuosiluokkakokonaisuusSuppeaDto::getVuosiluokkakokonaisuus)
                                                        .collect(Collectors.toList())
                                                        .contains(vlk.getTunniste()))
                                                .collect(Collectors.toList()),
                                        kieli,
                                        vlk.getId()))
                )
                .collect(Collectors.toList());

    }

    private NavigationNodeDto perusopetusOppiaineet(List<OppiaineSuppeaDto> oppiaineet, String kieli) {
        List<OppiaineSuppeaDto> eiValinnaiset = oppiaineet.stream().filter(oppiaine -> oppiaine.getTyyppi() == OppiaineTyyppi.YHTEINEN).collect(Collectors.toList());
        return NavigationNodeDto.of(NavigationType.perusopetusoppiaineet)
                .addAll(perusopetusOppiaine(eiValinnaiset, kieli, null))
                .add(valinnaisetOppiaineet(oppiaineet, kieli, null));
    }

    private NavigationNodeDto valinnaisetOppiaineet(List<OppiaineSuppeaDto> oppiaineet, String kieli, Long vlkId) {
        List<OppiaineSuppeaDto> valinnaiset = oppiaineet.stream().filter(oppiaine -> oppiaine.getTyyppi() != OppiaineTyyppi.YHTEINEN).collect(Collectors.toList());
        if (valinnaiset.isEmpty()) {
            return null;
        }

        return NavigationNodeDto.of(NavigationType.valinnaisetoppiaineet).meta("vlkId", vlkId)
                .addAll(perusopetusOppiaine(valinnaiset, kieli, vlkId));
    }

    private Collection<NavigationNodeDto> perusopetusOppiaine(Collection<OppiaineSuppeaDto> oppiaineet, String kieli, Long vlkId) {
        if (oppiaineet == null) {
            return Collections.emptyList();
        }

        return oppiaineet.stream()
                .map(oppiaine -> {
                    NavigationNodeDto oppiaineNavigationNode = NavigationNodeDto.of(NavigationType.perusopetusoppiaine, oppiaine.getNimi(), oppiaine.getId())
                            .meta("vlkId", vlkId);

                    if (!CollectionUtils.isEmpty(oppiaine.getOppimaarat())) {
                        oppiaineNavigationNode.add(NavigationNodeDto.of(NavigationType.oppimaarat).meta("navigation-subtype", true)
                                .addAll(perusopetusOppiaine(oppiaine.getOppimaarat().stream()
                                                .sorted(Comparator.comparing(o -> o.getNimi().getOrDefault(Kieli.of(kieli))))
                                                .collect(Collectors.toList()),
                                        kieli,
                                        vlkId)));
                    }
                    return oppiaineNavigationNode;
                })
                .collect(Collectors.toList());
    }

}
