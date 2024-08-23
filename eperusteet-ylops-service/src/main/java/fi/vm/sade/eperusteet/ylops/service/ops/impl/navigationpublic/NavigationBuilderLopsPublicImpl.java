package fi.vm.sade.eperusteet.ylops.service.ops.impl.navigationpublic;

import com.google.common.collect.Sets;
import fi.vm.sade.eperusteet.utils.dto.peruste.lops2019.tutkinnonrakenne.KoodiDto;
import fi.vm.sade.eperusteet.ylops.domain.KoulutustyyppiToteutus;
import fi.vm.sade.eperusteet.ylops.dto.export.OpetussuunnitelmaExportLopsDto;
import fi.vm.sade.eperusteet.ylops.dto.lukio.LukioOppiaineTiedotExportDto;
import fi.vm.sade.eperusteet.ylops.dto.navigation.NavigationNodeDto;
import fi.vm.sade.eperusteet.ylops.dto.navigation.NavigationType;
import fi.vm.sade.eperusteet.ylops.service.ops.NavigationBuilder;
import fi.vm.sade.eperusteet.ylops.service.ops.NavigationBuilderPublic;
import fi.vm.sade.eperusteet.ylops.service.ops.OpetussuunnitelmaService;
import fi.vm.sade.eperusteet.ylops.service.ops.OpsDispatcher;
import fi.vm.sade.eperusteet.ylops.service.util.NavigationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Optional;
import java.util.Set;

@Component
@Transactional
public class NavigationBuilderLopsPublicImpl implements NavigationBuilderPublic {

    @Autowired
    protected OpsDispatcher dispatcher;

    @Autowired
    private OpetussuunnitelmaService opetussuunnitelmaService;

    @Override
    public Set<KoulutustyyppiToteutus> getTyypit() {
        return Sets.newHashSet(KoulutustyyppiToteutus.LOPS);
    }

    @Override
    public NavigationNodeDto buildNavigation(Long opsId, Integer revision) {
        return NavigationUtil.initPublic()
                .addAll(dispatcher.get(NavigationBuilderPublic.class).buildNavigation(opsId, revision).getChildren())
                .add(oppiaineet(opsId, revision));
    }

    protected Class<? extends NavigationBuilder> getNavigationBuilderClass() {
        return NavigationBuilder.class;
    }

    protected NavigationNodeDto oppiaineet(Long opsId, Integer revision) {
        OpetussuunnitelmaExportLopsDto opetussuunnitelmaDto = (OpetussuunnitelmaExportLopsDto) opetussuunnitelmaService.getOpetussuunnitelmaJulkaistuSisalto(opsId, revision);
        return NavigationNodeDto.of(NavigationType.oppiaineet)
                .addAll(opetussuunnitelmaDto.getOppiaineet().stream()
                        .map(oppiaine -> mapOppiaine(oppiaine)));

    }

    private NavigationNodeDto mapOppiaine(
            LukioOppiaineTiedotExportDto oa) {
        NavigationNodeDto result = NavigationNodeDto
                .of(NavigationType.lukiooppiaine_2015, oa.getTiedot().getNimi(), oa.getTiedot().getId())
                .meta("koodi", KoodiDto.builder().arvo(oa.getTiedot().getKoodiArvo()).build());

        if (!CollectionUtils.isEmpty(oa.getOppimaarat())) {
            Optional.ofNullable(oa.getOppimaarat())
                    .ifPresent(oppimaarat -> result.add(NavigationNodeDto.of(NavigationType.lukiooppimaarat_2015).meta("navigation-subtype", true)
                            .addAll(oppimaarat.stream().map(om -> mapOppiaine(LukioOppiaineTiedotExportDto.builder().tiedot(om).build())))));
        }

        if (!CollectionUtils.isEmpty(oa.getTiedot().getKurssit())) {
            Optional.ofNullable(oa.getTiedot().getKurssit())
                    .ifPresent(kurssit -> result.add(NavigationNodeDto.of(NavigationType.lukiokurssit).meta("navigation-subtype", true)
                            .addAll(kurssit.stream().map(kurssi -> NavigationNodeDto.
                                    of(NavigationType.lukiokurssi, kurssi.getNimi(), kurssi.getId())
                                    .meta("tyyppi", kurssi.getTyyppi())
                                    .meta("koodi", KoodiDto.builder().arvo(kurssi.getKoodiArvo()).build())
                                    .meta("oppiaine", oa.getTiedot().getId())
                            ))));
        }

        return result;
    }


}
