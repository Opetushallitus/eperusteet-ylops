package fi.vm.sade.eperusteet.ylops.service.ops.impl.navigationpublic;

import fi.vm.sade.eperusteet.ylops.domain.KoulutustyyppiToteutus;
import fi.vm.sade.eperusteet.ylops.dto.OpetussuunnitelmaExportDto;
import fi.vm.sade.eperusteet.ylops.dto.TekstiKappaleViiteExportDto;
import fi.vm.sade.eperusteet.ylops.dto.navigation.NavigationNodeDto;
import fi.vm.sade.eperusteet.ylops.dto.navigation.NavigationType;
import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import fi.vm.sade.eperusteet.ylops.service.ops.NavigationBuilderPublic;
import fi.vm.sade.eperusteet.ylops.service.ops.OpetussuunnitelmaService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class NavigationBuilderDefaultPublicImpl implements NavigationBuilderPublic {

    @Autowired
    private OpetussuunnitelmaService opetussuunnitelmaService;

    @Override
    public Set<KoulutustyyppiToteutus> getTyypit() {
        return Collections.emptySet();
    }

    private NavigationNodeDto buildTekstinavi(TekstiKappaleViiteExportDto.Puu root) {
        LokalisoituTekstiDto nimi = root.getTekstiKappale() != null
                ? root.getTekstiKappale().getNimi()
                : null;

        return NavigationNodeDto
                .of(root.isLiite() ? NavigationType.liite : NavigationType.viite, nimi, root.getId())
                .addAll(Optional.ofNullable(root.getLapset())
                        .map(lapset -> lapset.stream()
                                .filter(tkv -> !tkv.isPiilotettu())
                                .map(this::buildTekstinavi)
                                .collect(Collectors.toList()))
                        .orElse(new ArrayList<>()));
    }

    @Override
    public NavigationNodeDto buildNavigation(Long opsId, boolean esikatselu) {
        OpetussuunnitelmaExportDto opetussuunnitelmaDto = opetussuunnitelmaService.getOpetussuunnitelmaJulkaistuSisalto(opsId, esikatselu);
        return NavigationNodeDto.of(NavigationType.root)
                .addAll(buildTekstinavi(opetussuunnitelmaDto.getTekstit()).getChildren());
    }

}
