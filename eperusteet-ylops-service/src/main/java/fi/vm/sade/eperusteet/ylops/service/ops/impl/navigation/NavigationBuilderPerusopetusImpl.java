package fi.vm.sade.eperusteet.ylops.service.ops.impl.navigation;

import com.google.common.collect.Sets;
import fi.vm.sade.eperusteet.ylops.domain.KoulutustyyppiToteutus;
import fi.vm.sade.eperusteet.ylops.domain.oppiaine.OppiaineTyyppi;
import fi.vm.sade.eperusteet.ylops.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.ylops.dto.navigation.NavigationNodeDto;
import fi.vm.sade.eperusteet.ylops.dto.navigation.NavigationType;
import fi.vm.sade.eperusteet.ylops.dto.ops.*;
import fi.vm.sade.eperusteet.ylops.service.ops.NavigationBuilder;
import fi.vm.sade.eperusteet.ylops.service.ops.OpetussuunnitelmaService;
import fi.vm.sade.eperusteet.ylops.service.ops.OpsDispatcher;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.*;
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

        OpetussuunnitelmaKevytDto ops = opetussuunnitelmaService.getOpetussuunnitelma(opsId);

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
                .addAll(vuosiluokkakokonaisuudet(vuosiluokkakokonaisuudet, oppiaineet, kieli));
    }

    protected Class<? extends NavigationBuilder> getNavigationBuilderClass() {
        return NavigationBuilder.class;
    }

    private List<NavigationNodeDto> vuosiluokkakokonaisuudet(List<VuosiluokkakokonaisuusSuppeaDto> vuosiluokkakokonaisuudet, List<OppiaineSuppeaDto> oppiaineet, String kieli) {
        return vuosiluokkakokonaisuudet.stream()
                .map(vlk ->
                        NavigationNodeDto.of(NavigationType.vuosiluokkakokonaisuus, vlk.getNimi(), vlk.getId())
                              .addAll(perusteenOppiaineet(oppiaineet, kieli, vlk))
                              .addAll(valinnaisetOppiaineet(oppiaineet, kieli, vlk))
                              .add(NavigationNodeDto.of(NavigationType.valinnaisetoppiaineet).meta("vlkId", vlk.getId()))
                      )                
                .collect(Collectors.toList());
    }

    private Collection<NavigationNodeDto> perusteenOppiaineet(List<OppiaineSuppeaDto> oppiaineet, String kieli, VuosiluokkakokonaisuusSuppeaDto vlk) {
        List<OppiaineSuppeaDto> perusteenOppiaineet = oppiaineet.stream()
          .filter(oppiaine -> oppiaine.getTyyppi() == OppiaineTyyppi.YHTEINEN)
          .filter(oppiaine -> ObjectUtils.isEmpty(oppiaine.getVuosiluokkakokonaisuudet()) 
              || oppiaine.getVuosiluokkakokonaisuudet().stream()
                .map(OppiaineenVuosiluokkakokonaisuusSuppeaDto::getVuosiluokkakokonaisuus)
                .collect(Collectors.toList())
                .contains(vlk.getTunniste()))
          .collect(Collectors.toList());
        if (perusteenOppiaineet.isEmpty()) {
            return null;
        }

        return oppiaineet(perusteenOppiaineet, NavigationType.perusopetusoppiaine, kieli, vlk);
    }

    private Collection<NavigationNodeDto> valinnaisetOppiaineet(List<OppiaineSuppeaDto> oppiaineet, String kieli, VuosiluokkakokonaisuusSuppeaDto vlk) {
      List<OppiaineSuppeaDto> valinnaiset = oppiaineet.stream()
        .filter(oppiaine -> oppiaine.getTyyppi() != OppiaineTyyppi.YHTEINEN)
        .filter(oppiaine -> oppiaine.getVuosiluokkakokonaisuudet().stream()
                .map(OppiaineenVuosiluokkakokonaisuusSuppeaDto::getVuosiluokkakokonaisuus)
                .collect(Collectors.toList())
                .contains(vlk.getTunniste()))
        .collect(Collectors.toList());
      if (valinnaiset.isEmpty()) {
          return null;
      }

      return oppiaineet(valinnaiset, NavigationType.perusopetuspaikallinenoppiaine, kieli, vlk);

    }

    private Collection<NavigationNodeDto> oppiaineet(
        Collection<OppiaineSuppeaDto> oppiaineet, 
        NavigationType navigationType,
        String kieli, 
        VuosiluokkakokonaisuusSuppeaDto vlk) {
        
        if (oppiaineet == null) {
            return Collections.emptyList();
        }

        return oppiaineet.stream()
                .map(oppiaine -> {
                    NavigationNodeDto oppiaineNavigationNode = NavigationNodeDto.of(
                        navigationType, 
                        oppiaine.getNimi(), 
                        oppiaine.getId())
                      .meta("vlkId", vlk.getId());

                    if (!CollectionUtils.isEmpty(oppiaine.getOppimaarat())) {
                        oppiaineNavigationNode.addAll(
                            oppiaineet(oppiaine.getOppimaarat().stream()
                                                .sorted(Comparator.comparing(o -> o.getNimi().getOrDefault(Kieli.of(kieli))))
                                                .collect(Collectors.toList()),
                            navigationType,        
                            kieli,
                            vlk));
                    }

                    if (oppiaine.isKoosteinen()) {
                      oppiaineNavigationNode.add(NavigationNodeDto.of(NavigationType.uusi_oppimaara)
                      .meta("navigation-sub-type", "add")
                      .meta("oppiaine-id", oppiaine.getId()));
                    }

                    Optional<OppiaineenVuosiluokkakokonaisuusSuppeaDto> vuosiluokkakokonaisuus = oppiaine.getVuosiluokkakokonaisuudet().stream()
                      .filter(oppiaineenVlk -> oppiaineenVlk.getVuosiluokkakokonaisuus().equals(vlk.getTunniste()))
                      .findFirst();

                    if (vuosiluokkakokonaisuus.isPresent() && !vuosiluokkakokonaisuus.get().getVuosiluokat().isEmpty()) {
                      oppiaineNavigationNode.add(NavigationNodeDto.of(NavigationType.tavoitteet_ja_sisallot).meta("navigation-sub-type", "subtype"));
                      vuosiluokkakokonaisuus.get().getVuosiluokat().stream()
                        .sorted(Comparator.comparing(vuosiluokka -> vuosiluokka.getVuosiluokka()))
                        .map(v -> NavigationNodeDto.of(
                          NavigationType.oppiaineenvuosiluokka)
                          .meta("vuosiluokka", v.getVuosiluokka())
                          .meta("vlkId", vlk.getId())
                          .meta("oppiaine-id", oppiaine.getId())
                          .meta("vlId", v.getId())
                        )
                      .collect(Collectors.toList())
                      .forEach(navigationNode -> oppiaineNavigationNode.add(navigationNode));
                    }
                    
                    return oppiaineNavigationNode;
                })
                .collect(Collectors.toList());
    }

}
