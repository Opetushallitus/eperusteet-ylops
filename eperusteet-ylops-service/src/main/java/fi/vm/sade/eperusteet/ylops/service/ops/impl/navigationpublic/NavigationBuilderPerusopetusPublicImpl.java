package fi.vm.sade.eperusteet.ylops.service.ops.impl.navigationpublic;

import com.google.common.collect.Sets;
import fi.vm.sade.eperusteet.ylops.domain.KoulutustyyppiToteutus;
import fi.vm.sade.eperusteet.ylops.domain.oppiaine.OppiaineTyyppi;
import fi.vm.sade.eperusteet.ylops.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.ylops.dto.navigation.NavigationNodeDto;
import fi.vm.sade.eperusteet.ylops.dto.navigation.NavigationType;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaLaajaDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OppiaineExportDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OppiaineenVuosiluokkakokonaisuusDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpsOppiaineExportDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpsVuosiluokkakokonaisuusDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.VuosiluokkakokonaisuusDto;
import fi.vm.sade.eperusteet.ylops.service.ops.NavigationBuilderPublic;
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
public class NavigationBuilderPerusopetusPublicImpl implements NavigationBuilderPublic {

    @Autowired
    private OpsDispatcher dispatcher;

    @Autowired
    private OpetussuunnitelmaService opetussuunnitelmaService;

    @Override
    public Set<KoulutustyyppiToteutus> getTyypit() {
        return Sets.newHashSet(KoulutustyyppiToteutus.PERUSOPETUS);
    }

    @Override
    public NavigationNodeDto buildNavigation(Long opsId, boolean esikatselu) {
        return buildNavigation(opsId, "fi", esikatselu);
    }

    @Override
    public NavigationNodeDto buildNavigation(Long opsId, String kieli, boolean esikatselu) {

        OpetussuunnitelmaLaajaDto opetussuunnitelmaDto = (OpetussuunnitelmaLaajaDto) opetussuunnitelmaService.getOpetussuunnitelmaJulkaistuSisalto(opsId, esikatselu);

        List<VuosiluokkakokonaisuusDto> vuosiluokkakokonaisuudet = opetussuunnitelmaDto.getVuosiluokkakokonaisuudet().stream()
                .sorted(Comparator.comparing(vlk -> vlk.getVuosiluokkakokonaisuus().getNimi().getOrDefault(Kieli.of(kieli))))
                .map(OpsVuosiluokkakokonaisuusDto::getVuosiluokkakokonaisuus)
                .collect(Collectors.toList());

        List<OppiaineExportDto> oppiaineet = opetussuunnitelmaDto.getOppiaineet().stream()
                .map(OpsOppiaineExportDto::getOppiaine)
                .sorted(Comparator.comparing(o -> o.getNimi().getOrDefault(Kieli.of(kieli))))
                .sorted(Comparator.comparing(o -> o.getJnro() != null ? o.getJnro() : Long.MAX_VALUE))
                .collect(Collectors.toList());

        return NavigationNodeDto.of(NavigationType.root)
                .addAll(dispatcher.get(NavigationBuilderPublic.class).buildNavigation(opsId).getChildren())
                .addAll(vuosiluokkakokonaisuudet(vuosiluokkakokonaisuudet, oppiaineet, kieli))
                .add(perusopetusOppiaineet(oppiaineet, kieli));
    }

    private List<NavigationNodeDto> vuosiluokkakokonaisuudet(List<VuosiluokkakokonaisuusDto> vuosiluokkakokonaisuudet, List<OppiaineExportDto> oppiaineet, String kieli) {
        return vuosiluokkakokonaisuudet.stream()
                .map(vlk ->
                        NavigationNodeDto.of(NavigationType.vuosiluokkakokonaisuus, vlk.getNimi(), vlk.getId())
                                .addAll(perusopetusOppiaine(oppiaineet.stream()
                                                .filter(oppiaine -> oppiaine.getTyyppi() == OppiaineTyyppi.YHTEINEN)
                                                .filter(oppiaine -> oppiaine.getVuosiluokkakokonaisuudet().stream()
                                                        .map(OppiaineenVuosiluokkakokonaisuusDto::getVuosiluokkakokonaisuus)
                                                        .collect(Collectors.toList())
                                                        .contains(vlk.getTunniste()))
                                                .collect(Collectors.toList()),
                                        kieli,
                                        vlk.getId()))
                                .add(valinnaisetOppiaineet(oppiaineet.stream()
                                                .filter(oppiaine -> oppiaine.getVuosiluokkakokonaisuudet().stream()
                                                        .map(OppiaineenVuosiluokkakokonaisuusDto::getVuosiluokkakokonaisuus)
                                                        .collect(Collectors.toList())
                                                        .contains(vlk.getTunniste()))
                                                .collect(Collectors.toList()),
                                        kieli,
                                        vlk.getId()))
                )
                .collect(Collectors.toList());

    }

    private NavigationNodeDto perusopetusOppiaineet(List<OppiaineExportDto> oppiaineet, String kieli) {
        List<OppiaineExportDto> eiValinnaiset = oppiaineet.stream().filter(oppiaine -> oppiaine.getTyyppi() == OppiaineTyyppi.YHTEINEN).collect(Collectors.toList());
        return NavigationNodeDto.of(NavigationType.perusopetusoppiaineet)
                .addAll(perusopetusOppiaine(eiValinnaiset, kieli, null))
                .add(valinnaisetOppiaineet(oppiaineet, kieli, null));
    }

    private NavigationNodeDto valinnaisetOppiaineet(List<OppiaineExportDto> oppiaineet, String kieli, Long vlkId) {
        List<OppiaineExportDto> valinnaiset = oppiaineet.stream().filter(oppiaine -> oppiaine.getTyyppi() != OppiaineTyyppi.YHTEINEN).collect(Collectors.toList());
        if (valinnaiset.isEmpty()) {
            return null;
        }

        return NavigationNodeDto.of(NavigationType.valinnaisetoppiaineet).meta("vlkId", vlkId)
                .addAll(perusopetusOppiaine(valinnaiset, kieli, vlkId));
    }

    private Collection<NavigationNodeDto> perusopetusOppiaine(Collection<OppiaineExportDto> oppiaineet, String kieli, Long vlkId) {
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
                                                .sorted(Comparator.comparing(o -> o.getJnro() != null ? o.getJnro() : Long.MAX_VALUE))
                                                .collect(Collectors.toList()),
                                        kieli,
                                        vlkId)));
                    }
                    return oppiaineNavigationNode;
                })
                .collect(Collectors.toList());
    }
}
