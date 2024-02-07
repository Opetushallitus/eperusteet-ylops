package fi.vm.sade.eperusteet.ylops.service.ops.impl.navigation;

import com.google.common.collect.Sets;
import fi.vm.sade.eperusteet.utils.dto.peruste.lops2019.tutkinnonrakenne.KoodiDto;
import fi.vm.sade.eperusteet.ylops.domain.KoulutustyyppiToteutus;
import fi.vm.sade.eperusteet.ylops.dto.lukio.LukioOpetussuunnitelmaRakenneOpsDto;
import fi.vm.sade.eperusteet.ylops.dto.lukio.LukioOppiaineRakenneListausDto;
import fi.vm.sade.eperusteet.ylops.dto.navigation.NavigationNodeDto;
import fi.vm.sade.eperusteet.ylops.dto.navigation.NavigationType;
import fi.vm.sade.eperusteet.ylops.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.ylops.service.ops.NavigationBuilder;
import fi.vm.sade.eperusteet.ylops.service.ops.OpsDispatcher;
import fi.vm.sade.eperusteet.ylops.service.ops.lukio.LukioOpetussuunnitelmaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Optional;
import java.util.Set;

@Component
@Transactional
public class NavigationBuilderLopsImpl implements NavigationBuilder {

    @Autowired
    protected OpsDispatcher dispatcher;

    @Autowired
    protected DtoMapper mapper;

    @Autowired
    private LukioOpetussuunnitelmaService lukioOpetussuunnitelmaService;

    @Override
    public Set<KoulutustyyppiToteutus> getTyypit() {
        return Sets.newHashSet(KoulutustyyppiToteutus.LOPS);
    }

    @Override
    public NavigationNodeDto buildNavigation(Long opsId) {
        return NavigationNodeDto.of(NavigationType.root)
                .addAll(dispatcher.get(getNavigationBuilderClass()).buildNavigation(opsId).getChildren())
                .add(oppiaineet(opsId));
    }

    protected Class<? extends NavigationBuilder> getNavigationBuilderClass() {
        return NavigationBuilder.class;
    }

    protected NavigationNodeDto oppiaineet(Long opsId) {
        LukioOpetussuunnitelmaRakenneOpsDto opsRakenne = lukioOpetussuunnitelmaService.getRakenne(opsId);
        return NavigationNodeDto.of(NavigationType.oppiaineet)
                .addAll(opsRakenne.getOppiaineet().stream()
                        .map(oppiaine -> mapOppiaine(oppiaine)));

    }

    private NavigationNodeDto mapOppiaine(
            LukioOppiaineRakenneListausDto oa) {
        NavigationNodeDto result = NavigationNodeDto
                .of(NavigationType.lukiooppiaine_2015, oa.getNimi(), oa.getId())
                .meta("koodi", KoodiDto.builder().arvo(oa.getKoodiArvo()).build());

        if (!CollectionUtils.isEmpty(oa.getOppimaarat())) {
            Optional.ofNullable(oa.getOppimaarat())
                    .ifPresent(oppimaarat -> result.add(NavigationNodeDto.of(NavigationType.lukiooppimaarat_2015).meta("navigation-subtype", true)
                            .addAll(oppimaarat.stream().map(om -> mapOppiaine(om)))));
        }

        if (!CollectionUtils.isEmpty(oa.getKurssit())) {
            Optional.ofNullable(oa.getKurssit())
                    .ifPresent(kurssit -> result.add(NavigationNodeDto.of(NavigationType.lukiokurssit).meta("navigation-subtype", true)
                            .addAll(kurssit.stream().map(kurssi -> NavigationNodeDto.
                                    of(NavigationType.lukiokurssi, kurssi.getNimi(), kurssi.getId())
                                    .meta("tyyppi", kurssi.getTyyppi())
                                    .meta("koodi", KoodiDto.builder().arvo(kurssi.getKoodiArvo()).build())
                                    .meta("oppiaine", oa.getId())
                            ))));
        }

        return result;
    }

}
