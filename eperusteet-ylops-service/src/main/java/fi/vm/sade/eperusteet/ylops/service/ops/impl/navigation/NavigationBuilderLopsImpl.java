package fi.vm.sade.eperusteet.ylops.service.ops.impl.navigation;

import com.google.common.collect.Sets;
import fi.vm.sade.eperusteet.utils.dto.peruste.lops2019.tutkinnonrakenne.KoodiDto;
import fi.vm.sade.eperusteet.ylops.domain.KoulutustyyppiToteutus;
import fi.vm.sade.eperusteet.ylops.domain.lops2019.Lops2019OppiaineJarjestys;
import fi.vm.sade.eperusteet.ylops.domain.ops.Opetussuunnitelma;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.Lops2019OpintojaksoDto;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.Lops2019OpintojaksonOppiaineDto;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.Lops2019OppiaineKevytDto;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.Lops2019PaikallinenOppiaineKevytDto;
import fi.vm.sade.eperusteet.ylops.dto.lukio.LukioOpetussuunnitelmaRakenneOpsDto;
import fi.vm.sade.eperusteet.ylops.dto.lukio.LukioOppiaineRakenneListausDto;
import fi.vm.sade.eperusteet.ylops.dto.navigation.NavigationNodeDto;
import fi.vm.sade.eperusteet.ylops.dto.navigation.NavigationType;
import fi.vm.sade.eperusteet.ylops.dto.peruste.lops2019.oppiaineet.Lops2019OppiaineKaikkiDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.lops2019.oppiaineet.moduuli.Lops2019ModuuliDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import fi.vm.sade.eperusteet.ylops.repository.lops2019.Lops2019SisaltoRepository;
import fi.vm.sade.eperusteet.ylops.repository.ops.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.ylops.service.external.EperusteetService;
import fi.vm.sade.eperusteet.ylops.service.lops2019.Lops2019OpintojaksoService;
import fi.vm.sade.eperusteet.ylops.service.lops2019.Lops2019OppiaineService;
import fi.vm.sade.eperusteet.ylops.service.lops2019.Lops2019Service;
import fi.vm.sade.eperusteet.ylops.service.lops2019.impl.Lops2019Utils;
import fi.vm.sade.eperusteet.ylops.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.ylops.service.ops.NavigationBuilder;
import fi.vm.sade.eperusteet.ylops.service.ops.OpsDispatcher;
import fi.vm.sade.eperusteet.ylops.service.ops.lukio.LukioOpetussuunnitelmaService;
import fi.vm.sade.eperusteet.ylops.service.util.Pair;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import static fi.vm.sade.eperusteet.ylops.service.util.Nulls.assertExists;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toSet;

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
